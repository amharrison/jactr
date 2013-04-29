package org.jactr.modules.pm.aural.memory.impl.filter;

/*
 * default logging
 */
import java.util.Comparator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.filter.AbstractIndexFilter;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.visual.IVisualModule;

/**
 * provides nearest filtering and also normalizes all references to current,
 * highest,lowest
 * 
 * @author harrison
 */
public class NearestAuralFilter extends
    AbstractIndexFilter<Double>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NearestAuralFilter.class);

  private final double[]             _referenceLocation;
  final private IAuralModule _auralModule;

  public NearestAuralFilter(IAuralModule auralModule)
  {
    this(new double[2],auralModule);
  }

  public NearestAuralFilter(double[] referenceLocation, IAuralModule auralModule)
  {
    _auralModule = auralModule;
    _referenceLocation = new double[referenceLocation.length];
    System.arraycopy(referenceLocation, 0, _referenceLocation, 0,
        referenceLocation.length);
  }
  
  public IAuralModule getAuralModule()
  {
    return _auralModule;
  }

  @Override
  protected Double compute(ChunkTypeRequest request)
  {
    IChunk visualLocation = getVisualLocation(request);
    if (visualLocation != null)
    {
      double[] coords = getCoordinates(visualLocation);

      double sqDistance = 0;
      // length may be 2 or 3 (if depth was provided)
      int len = Math.min(coords.length, _referenceLocation.length);
      for (int i = 0; i < len; i++)
        sqDistance += Math.pow(coords[i] - _referenceLocation[i], 2);

      return sqDistance;
    }
    return null;
  }

  /*
   * if we've gotten this far..
   */
  public boolean accept(ChunkTypeRequest template)
  {
    return get(template)!=null;
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
    int index = 0;
    double[] location = null;
    for (IConditionalSlot cSlot : request.getConditionalSlots())
    {
      /*
       * I should do the validity test here
       */
      index++;
      if (IVisualModule.NEAREST_SLOT.equals(cSlot.getName())
          && cSlot.getCondition() == IConditionalSlot.EQUALS)
      {
        IChunk visualLocation = (IChunk) cSlot.getValue();
        if (visualLocation != null)
        {
          location = getCoordinates(visualLocation);
          break;
        }
        else if (LOGGER.isWarnEnabled())
          LOGGER.warn("nearest was null, ignoring");
      }
    }

    if (location == null) return null;

//    NearestAuralFilter filter = new NearestAuralFilter(
//        location);
//    filter.setWeight(index);
//    filter.setPerceptualMemory(getPerceptualMemory());
//
//    return filter;
    return null;
  }

//  public void normalizeRequest(ChunkTypeRequest searchRequest)
//  {
//    IChunk currentLocation = getVisualMemory().getVisualModule()
//        .getVisualLocationBuffer().getCurrentVisualLocation();
//
//    if (currentLocation == null)
//      currentLocation = getVisualMemory().getVisualLocationChunkAt(0, 0);
//
//    for (IConditionalSlot cSlot : searchRequest.getConditionalSlots())
//    {
//      if (!(cSlot.getValue() instanceof IChunk)) continue;
//
//      String chunkName = ((IChunk) cSlot.getValue()).getSymbolicChunk()
//          .getName();
//
//      if (IVisualModule.CURRENT_CHUNK.equals(chunkName))
//      {
//        /*
//         * resolve the current chunk - but how? for all cases but one, the
//         * conditional slot named "x" will have its value assigned to
//         * currentLocation's slot named "x". Unless the slots name is NEAREST,
//         * in which case the value of the conditional slot will be the current
//         * chunk itself
//         */
//        if (IVisualModule.NEAREST_SLOT.equals(cSlot.getName()))
//          cSlot.setValue(currentLocation);
//        else
//          cSlot.setValue(currentLocation.getSymbolicChunk().getSlot(
//              cSlot.getName()).getValue());
//      }
//      else if (IVisualModule.LESS_THAN_CURRENT_CHUNK.equals(chunkName))
//      {
//        cSlot.setCondition(IConditionalSlot.LESS_THAN);
//        cSlot.setValue(currentLocation.getSymbolicChunk().getSlot(
//            cSlot.getName()).getValue());
//      }
//      else if (IVisualModule.GREATER_THAN_CURRENT_CHUNK.equals(chunkName))
//      {
//        cSlot.setCondition(IConditionalSlot.GREATER_THAN);
//        cSlot.setValue(currentLocation.getSymbolicChunk().getSlot(
//            cSlot.getName()).getValue());
//      }
//    }
//  }
  
  
  
  protected IChunk getVisualLocation(IChunk visualChunk)
  {
    Object location = visualChunk.getSymbolicChunk().getSlot(
        IVisualModule.SCREEN_POSITION_SLOT).getValue();
    if (!(location instanceof IChunk)) return null;

//    if (((IChunk) location).isA(_visualLocationChunkType))
//      return (IChunk) location;

    return null;
  }

  protected IChunk getVisualLocation(ChunkTypeRequest request)
  {
    double x = Double.NaN;
    double y = Double.NaN;

    for (IConditionalSlot cSlot : request.getConditionalSlots())
      if (cSlot.getCondition() == IConditionalSlot.EQUALS)
        if (cSlot.getName().equals(IVisualModule.SCREEN_X_SLOT))
          x = ((Number) cSlot.getValue()).doubleValue();
        else if (cSlot.getName().equals(IVisualModule.SCREEN_Y_SLOT))
          y = ((Number) cSlot.getValue()).doubleValue();

//    if (!Double.isNaN(x) && !Double.isNaN(y))
//      return _visualMemory.getVisualLocationChunkAt(x, y);

    return null;
  }

  protected double[] getCoordinates(IChunk visualLocation)
  {
    double[] rtn = new double[3];
    rtn[0] = ((Number) visualLocation.getSymbolicChunk().getSlot(
        IVisualModule.SCREEN_X_SLOT).getValue()).doubleValue();
    rtn[1] = ((Number) visualLocation.getSymbolicChunk().getSlot(
        IVisualModule.SCREEN_Y_SLOT).getValue()).doubleValue();
    Object depth = visualLocation.getSymbolicChunk().getSlot(
        IVisualModule.SCREEN_Z_SLOT).getValue();

    if (depth instanceof Number) rtn[2] = ((Number) depth).doubleValue();

    return rtn;
  }
}
