package org.jactr.modules.pm.common.memory.filter;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

public abstract class AbstractIndexFilter<T> implements IIndexFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER  = LogFactory
                                                 .getLog(AbstractIndexFilter.class);

  private int                        _weight = Integer.MIN_VALUE;

  private Map<ChunkTypeRequest, T>   _cache;

  private IPerceptualMemory          _perceptualMemory;

  public void setPerceptualMemory(IPerceptualMemory memory)
  {
    _cache = new HashMap<ChunkTypeRequest, T>();
    _perceptualMemory = memory;
  }

  public IPerceptualMemory getPerceptualMemory()
  {
    return _perceptualMemory;
  }

  protected void clearCache()
  {
    _cache.clear();
  }

  abstract protected T compute(ChunkTypeRequest request);

  protected T get(ChunkTypeRequest request)
  {
    T rtn = null;
    if (!_cache.containsKey(request))
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

  public void normalizeRequest(ChunkTypeRequest request)
  {
    
  }
}
