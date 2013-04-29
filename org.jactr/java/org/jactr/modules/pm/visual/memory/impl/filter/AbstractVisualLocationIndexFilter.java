package org.jactr.modules.pm.visual.memory.impl.filter;

/*
 * default logging
 */
import java.util.IdentityHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;

public abstract class AbstractVisualLocationIndexFilter<T> implements IIndexFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractVisualLocationIndexFilter.class);

  private IVisualMemory              _visualMemory;

  private IChunkType                 _visualLocationChunkType;
  
  private int _weight = Integer.MIN_VALUE;
  
  private Map<ChunkTypeRequest, T> _cache;

  public void setPerceptualMemory(IPerceptualMemory memory)
  {
    _cache = new IdentityHashMap<ChunkTypeRequest, T>();
    _visualMemory = (IVisualMemory) memory;
    _visualLocationChunkType = _visualMemory.getVisualModule()
        .getVisualLocationChunkType();
  }
  
  public IPerceptualMemory getPerceptualMemory()
  {
    return _visualMemory;
  }
  
  protected IVisualMemory getVisualMemory()
  {
    return _visualMemory;
  }

  protected void clearCache()
  {
    _cache.clear();
  }
  
  abstract protected T compute(ChunkTypeRequest request);
  
  protected T get(ChunkTypeRequest request)
  {
    T rtn = null;
    if(!_cache.containsKey(request))
    {
      rtn = compute(request);
      _cache.put(request, rtn);
    }
    else
      rtn = _cache.get(request);
    
    return rtn;
  }

  public int getWeight()
  {
    return _weight;
  }
  
  protected void setWeight(int weight)
  {
    _weight = weight;
  }
  
 
  protected IChunk getVisualLocation(IChunk visualChunk)
  {
    Object location = visualChunk.getSymbolicChunk().getSlot(
        IVisualModule.SCREEN_POSITION_SLOT).getValue();
    if (!(location instanceof IChunk)) return null;

    if (((IChunk) location).isA(_visualLocationChunkType))
      return (IChunk) location;

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

    if (!Double.isNaN(x) && !Double.isNaN(y))
      return _visualMemory.getVisualLocationChunkAt(x, y);

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
