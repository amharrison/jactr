package org.jactr.modules.pm.visual.memory.impl;

import java.util.List;
/*
 * default logging
 */
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.memory.impl.IIndexManager;
import org.jactr.modules.pm.visual.IVisualModule;

public class VisualLocationManager implements IIndexManager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisualLocationManager.class);

  private SortedMap<Integer, IChunk> _sparseVisualLocations;

  private int                        _horizontalResolution;

  private int                        _verticalResolution;

  private double                     _horizontalSpan;

  private double                     _verticalSpan;

  private Lock                       _lock  = new ReentrantLock();

  private final IVisualModule        _module;

  public VisualLocationManager(IVisualModule module)
  {
    _module = module;
    _sparseVisualLocations = new TreeMap<Integer, IChunk>();
  }

  public void setDimensions(double horizontalSpan, int horizonalResolution,
      double verticalSpan, int verticalResolution)
  {
    _lock.lock();
    List<IChunk> locations = FastListFactory.newInstance();
    try
    {
      _horizontalResolution = horizonalResolution;
      _verticalResolution = verticalResolution;
      _horizontalSpan = horizontalSpan;
      _verticalSpan = verticalSpan;

      /*
       * recompute the indices
       */
      locations.addAll(_sparseVisualLocations.values());
      _sparseVisualLocations.clear();

      /*
       * grab existing visual-locations, possibly having been written to file
       * already
       */
      for (IChunk location : _module.getVisualLocationChunkType()
          .getSymbolicChunkType().getChunks())
      {
        double x = ((Number) location.getSymbolicChunk().getSlot(
            IVisualModule.SCREEN_X_SLOT).getValue()).doubleValue();
        double y = ((Number) location.getSymbolicChunk().getSlot(
            IVisualModule.SCREEN_Y_SLOT).getValue()).doubleValue();
        location.setMutable(true);
        _sparseVisualLocations.put(getVisualLocationChunkIndex(x, y), location);
      }

      for (IChunk location : locations)
      {
        double x = ((Number) location.getSymbolicChunk().getSlot(
            IVisualModule.SCREEN_X_SLOT).getValue()).doubleValue();
        double y = ((Number) location.getSymbolicChunk().getSlot(
            IVisualModule.SCREEN_Y_SLOT).getValue()).doubleValue();
        location.setMutable(true);
        _sparseVisualLocations.put(getVisualLocationChunkIndex(x, y), location);
      }
    }
    finally
    {
      _lock.unlock();
    }
  }

  public IChunk getIndexChunk(IChunk encodedChunk)
  {
    try
    {
      ISlot screenPos = encodedChunk.getSymbolicChunk().getSlot(
          IVisualModule.SCREEN_POSITION_SLOT);
      return (IChunk) screenPos.getValue();
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No valid screen-pos for " + encodedChunk);
      return null;
    }
  }

  public IChunk getVisualLocationChunkAt(double x, double y)
  {
    int index = getVisualLocationChunkIndex(x, y);
    if (index < 0) return null;

    return getVisualLocationChunk(index, x, y);
  }

  protected IChunk getVisualLocationChunk(int index, double x, double y)
  {
    _lock.lock();
    IChunk locationChunk = null;
    try
    {
      locationChunk = _sparseVisualLocations.get(index);
      if (locationChunk == null)
      {
        /*
         * need to create it
         */
        locationChunk = createVisualLocationChunk(x, y);
        _sparseVisualLocations.put(index, locationChunk);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Could not create visual location ", e);
    }
    finally
    {
      _lock.unlock();
    }

    return locationChunk;
  }

  private double getClosestXLocation(double x)
  {
    double half = _horizontalSpan / 2.0;
    int xIndex = (int) Math.ceil((x + half) / _horizontalSpan
        * _horizontalResolution);

    return xIndex * _horizontalSpan / _horizontalResolution
        - half;
  }

  private double getClosestYLocation(double y)
  {
    double half = _verticalSpan / 2.0;
    int yIndex = (int) Math.ceil((y + half) / _verticalSpan
        * _verticalResolution);

    return yIndex * _verticalSpan / _verticalResolution
        - half;
  }

  private IChunk createVisualLocationChunk(double x, double y) throws Exception
  {
    x = getClosestXLocation(x);
    y = getClosestYLocation(y);

    java.text.NumberFormat nf = java.text.NumberFormat.getNumberInstance();
    nf.setMinimumFractionDigits(2);
    nf.setMaximumFractionDigits(2);

    String name = "Loc:" + nf.format(x) + "x" + nf.format(y);
    Future<IChunk> created = _module.getModel().getDeclarativeModule()
        .createChunk(_module.getVisualLocationChunkType(), name);
    Double xLoc = x;
    Double yLoc = y;
    IChunk locationChunk = created.get();
    locationChunk.getSymbolicChunk().addSlot(
        new BasicSlot(IVisualModule.SCREEN_X_SLOT, xLoc));
    locationChunk.getSymbolicChunk().addSlot(
        new BasicSlot(IVisualModule.SCREEN_Y_SLOT, yLoc));

    /*
     * so that the chunk is marked as being permitted to have its slots changed
     * after encoding
     */
    locationChunk.setMutable(true);
    locationChunk = _module.getModel().getDeclarativeModule().addChunk(
        locationChunk).get();

    return locationChunk;
  }

  protected int getVisualLocationChunkIndex(double x, double y)
  {
    double halfWidth = _horizontalSpan / 2.0;
    double halfHeight = _verticalSpan / 2.0;

    if (x > halfWidth || x < -halfWidth)
    {
      LOGGER
          .warn("requested visual location beyond the available horizontal range. requested:"
              + x + " width:" + _horizontalSpan);
      return -1;
    }

    if (y > halfHeight || y < -halfHeight)
    {
      LOGGER
          .warn("requested visual location beyond the available vertical range. requested:"
              + y + " width:" + _verticalSpan);
      return -1;
    }

    // take care of -0 issue
    if (x == 0) x = 0;
    if (y == 0) y = 0;

    double xRes = _horizontalSpan / _horizontalResolution;
    double yRes = _verticalSpan / _verticalResolution;

    int xIndex = (int) Math.ceil((x + halfWidth) / xRes);
    int yIndex = (int) Math.ceil((y + halfHeight) / yRes);

    return yIndex * _verticalResolution + xIndex;
  }

}
