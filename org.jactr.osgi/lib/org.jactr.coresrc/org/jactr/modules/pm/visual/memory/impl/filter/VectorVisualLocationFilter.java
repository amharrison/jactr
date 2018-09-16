package org.jactr.modules.pm.visual.memory.impl.filter;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;

/**
 * enables search along a path defined by one visual-location, an angle (from
 * vertical, -PI - PI), and a threshold. The filter will prioritize
 * visual-location candidates based on their distance from the line defined by
 * visual-location and angle, culling any of those beyond the threshold.
 * 
 * @author harrison
 */
public class VectorVisualLocationFilter extends
    AbstractVisualLocationIndexFilter<Double>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(VectorVisualLocationFilter.class);

  static public final String         ANGLE_SLOT       = ":vector-angle";

  static public final String         ORIGIN_SLOT      = ":vector-origin";

  static public final String         DESTINATION_SLOT = ":vector-destination";

  static public final String         THRESHOLD_SLOT   = ":vector-threshold";

  private double[]                   _v0;

  private double[]                   _v1;

  private double                     _threshold       = Double.POSITIVE_INFINITY;

  /**
   * while the distance from the line-segment is our primary criterion, it is
   * possible that a point outside of search area could be the closest to the
   * line segment, so we still need some bounding points
   */
  private double[]                   _minimumPosition;

  private double[]                   _maximumPosition;

  public VectorVisualLocationFilter()
  {

  }

  protected VectorVisualLocationFilter(double[] origin, double[] destination,
      double threshold, double angle)
  {
    _threshold = threshold;
    _v0 = origin;
    _v1 = destination;

    if (_threshold == Double.POSITIVE_INFINITY) _threshold = 180;

    /*
     * compute bounds... irrelevant
     */
    computeBounds(_v0, _v1, _threshold, angle, 0.001);
  }

  @Override
  protected Double compute(ChunkTypeRequest request)
  {
    IChunk visualLocation = getVisualLocation(request);
    if (visualLocation != null)
    {
      double[] ref = getCoordinates(visualLocation);

      double a = ref[0] - _v0[0];
      double b = ref[1] - _v0[1];
      double c = _v1[0] - _v0[0];
      double d = _v1[1] - _v0[1];

      /*
       * distance = |AD - CB| / sqrt(d^2 + c^2) divisor is the length of the
       * base vector, which is 1.
       */
      double distance = Math.abs(a * d - c * b) / Math.sqrt(d * d + c * c);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("(" + ref[0] + "," + ref[1] + ") is " + distance
            + " from unit line segment defined by (" + _v0[0] + "," + _v0[1]
            + ")-(" + _v1[0] + "," + _v1[1] + ")");

      return distance;
    }

    return null;
  }

  public boolean accept(ChunkTypeRequest visualLocationTemplate)
  {
    Double distance = get(visualLocationTemplate);
    if (distance == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Rejecting %s because no distance was calculated",
            visualLocationTemplate));
      return false;
    }

    IChunk visualLocation = getVisualLocation(visualLocationTemplate);
    if (visualLocation == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Rejecting %s because no visual-location could be accessed",
            visualLocationTemplate));
      return false;
    }

    if (distance > _threshold)
    {
      /*
       * let's notify when it's close
       */
      if (distance < _threshold * 1.3)
      {
        IModel model = visualLocation.getModel();
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.VISUAL, String.format(
              "%s was rejected but it's awfully close at %.2f", visualLocation,
              distance));
      }

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Rejecting %s because %.2f is too far away from threshold %.2f",
            visualLocation, distance, _threshold));
      return false;
    }

    double[] ref = getCoordinates(visualLocation);

    /*
     * is it _v0 or _v1? We want to exclude the boundaries..
     */
    if (ref[0] == _v0[0] && ref[1] == _v0[1] || ref[0] == _v1[0]
        && ref[1] == _v1[1])
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Rejecting %s because it is at the bounds of the visual field",
            visualLocation));
      return false;
    }

    /**
     * finally, we need to make sure the point is not only near the line, but
     * within the tolerance bounds of the line segment (prevents false positives
     * of targets that are closer to the line, but not the line segment)
     */
    boolean rtn = _minimumPosition[0] <= ref[0]
        && ref[0] <= _maximumPosition[0] && _minimumPosition[1] <= ref[1]
        && ref[1] <= _maximumPosition[1];

    if (LOGGER.isDebugEnabled()) if(rtn)
      LOGGER.debug(String.format("Accepting %s because it is within maximum bounds (%.2f, %.2f)-(%.2f,%.2f)",
                visualLocation, _minimumPosition[0], _minimumPosition[1],
                _maximumPosition[0], _maximumPosition[1]));
    else
    LOGGER
        .debug(String
            .format(
                "Rejecting %s because it is outside maximum bounds (%.2f, %.2f)-(%.2f,%.2f)",
                visualLocation, _minimumPosition[0], _minimumPosition[1],
                _maximumPosition[0], _maximumPosition[1]));

    return rtn;
  }

  public Comparator<ChunkTypeRequest> getComparator()
  {
    return new Comparator<ChunkTypeRequest>() {
      public int compare(ChunkTypeRequest o1, ChunkTypeRequest o2)
      {
        if (o1 == o2) return 0;
        Double d1 = get(o1);
        Double d2 = get(o2);

        if (d1 < d2) return -1;
        if (d1 > d2) return 1;
        return 0;
      }
    };
  }

  public IIndexFilter instantiate(ChunkTypeRequest request)
  {
    double angle = Double.NaN;
    double threshold = Double.POSITIVE_INFINITY;
    double[] origin = null;
    double[] destination = null;

    int index = 0;
    int weight = 0;
    VectorVisualLocationFilter filter = null;
    for (IConditionalSlot cSlot : request.getConditionalSlots())
    {
      index++;
      if (cSlot.getCondition() == IConditionalSlot.EQUALS)
        if (cSlot.getName().equals(ORIGIN_SLOT))
        {
          origin = getCoordinates((IChunk) cSlot.getValue());
          weight = index;
        }
        else if (cSlot.getName().equals(DESTINATION_SLOT))
          destination = getCoordinates((IChunk) cSlot.getValue());
        else if (cSlot.getName().equals(ANGLE_SLOT))
          angle = ((Number) cSlot.getValue()).doubleValue();
        else if (cSlot.getName().equals(THRESHOLD_SLOT))
          threshold = ((Number) cSlot.getValue()).doubleValue();
    }

    /*
     * nothing was defined..
     */
    if (origin == null && destination == null && Double.isNaN(angle))
      return null;

    if (origin == null || destination == null && Double.isNaN(angle))
    {
      String msg = "Cannot search along vector without both " + ORIGIN_SLOT
          + " and either " + ANGLE_SLOT + " or " + DESTINATION_SLOT
          + " defined. Ignoring.";

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
      IModel model = getVisualMemory().getModule().getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
    }
    else
    {
      if (destination == null)
        destination = computeDestination(origin, angle);
      else
      {
        double dx = destination[0] - origin[0];
        double dy = destination[1] - origin[1];
        // intentionally swapped since we are measuring from the vertical
        angle = Math.toDegrees(Math.atan2(dx, dy));
      }

      filter = new VectorVisualLocationFilter(origin, destination, threshold,
          angle);
      filter.setWeight(weight);
      filter.setPerceptualMemory(getVisualMemory());

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Will search from (" + origin[0] + "," + origin[1]
            + ") along " + angle + " (" + destination[0] + "," + destination[1]
            + ") +/- " + threshold);
    }

    return filter;
  }

  /**
   * computes the destination point that is the intersection of the line
   * (origin-angle) with the bounds of the visual field
   * 
   * @param origin
   * @param angle
   * @return
   */
  private double[] computeDestination(double[] origin, double angle)
  {
    /*
     * and compute a new point from origin along angle
     */
    double[] tmp = new double[2];
    tmp[0] = origin[0] + Math.sin(Math.toRadians(angle));
    tmp[1] = origin[1] + Math.cos(Math.toRadians(angle));

    /*
     * compute the intersection of v0,tmp and the bounds of the visual field and
     * use that as the destination point
     */

    double w2 = getVisualMemory().getHorizontalSpan() / 2.0;
    double h2 = getVisualMemory().getVerticalSpan() / 2.0;
    double[][] otherLine = null;
    // top
    if (Math.abs(angle) <= 45)
      otherLine = new double[][] { { -w2, h2 }, { w2, h2 } };
    else
    // bottom
    if (Math.abs(angle) >= 135)
      otherLine = new double[][] { { -w2, -h2 }, { w2, -h2 } };
    else
    // right
    if (angle > 0)
      otherLine = new double[][] { { w2, -h2 }, { w2, h2 } };
    else
      // left
      otherLine = new double[][] { { -w2, -h2 }, { -w2, h2 } };

    return intersection(new double[][] { origin, tmp }, otherLine, 0.01);
  }

  /**
   * computes the bounds of the visual search area
   * 
   * @param v0
   * @param v1
   * @param threshold
   * @param angle
   * @param epsilon
   */
  private void computeBounds(double[] v0, double[] v1, double threshold,
      double angle, double epsilon)
  {
    // double minX = Math.min(v0[0], v1[0]);
    // double minY = Math.min(v0[1], v1[1]);
    // double maxX = Math.max(v0[0], v1[0]);
    // double maxY = Math.max(v0[1], v1[1]);

    // double xSign = 1;
    // double ySign = 1;
    //
    // double absAngle = Math.abs(angle);
    //
    // if (Math.abs(absAngle - 0) <= epsilon
    // || Math.abs(absAngle - 180) <= epsilon)
    // {
    // /*
    // * vertical
    // */
    // xSign = -1;
    // ySign = 0;
    // }
    // else if (Math.abs(absAngle - 90) <= epsilon)
    // {
    // /*
    // * horizontal
    // */
    // xSign = 0;
    // ySign = -1;
    // }
    // else
    // {
    // // angle > 0, subtract
    // xSign = -Math.signum(angle);
    // if (absAngle > 90)
    // ySign = -1;
    // else
    // ySign = 1;
    // }

    // we measure from the vertical CW, so a slight correction
    double rotatedAngle = 90 - angle;
    double rad = Math.toRadians(rotatedAngle);
    // how much we need to grow the bounds
    double xShift = Math.abs(Math.sin(rad) * threshold);
    double yShift = Math.abs(Math.cos(rad) * threshold);

    double minX = Math.min(v0[0], v1[0]) - xShift;
    double minY = Math.min(v0[1], v1[1]) - yShift;

    double maxX = Math.max(v0[0], v1[0]) + xShift;
    double maxY = Math.max(v0[1], v1[1]) + yShift;

    _minimumPosition = new double[] { Math.min(minX, maxX),
        Math.min(minY, maxY) };
    _maximumPosition = new double[] { Math.max(minX, maxX),
        Math.max(minY, maxY) };
  }

  /**
   * compute the intersection of two lines. adapted from
   * http://www.pdas.com/lineint.htm
   * 
   * @param l1
   * @param l2
   * @return intersecting point or null if parallel
   */
  private double[] intersection(double[][] l1, double[][] l2, double resolution)
  {
    double a1 = l1[1][1] - l1[0][1];
    double b1 = l1[0][0] - l1[1][0];
    double c1 = l1[1][0] * l1[0][1] - l1[0][0] * l1[1][1];
    // l1 : a1*x + b1*y + c1 =0;
    double a2 = l2[1][1] - l2[0][1];
    double b2 = l2[0][0] - l2[1][0];
    double c2 = l2[1][0] * l2[0][1] - l2[0][0] * l2[1][1];

    double denom = a1 * b2 - a2 * b1;
    // parallel
    if (Math.abs(denom) <= resolution) return null;

    double x = (b1 * c2 - b2 * c1) / denom;
    double y = (a2 * c1 - a1 * c2) / denom;

    return new double[] { x, y };
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub

  }
}
