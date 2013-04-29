package org.jactr.core.module.procedural.map.template;

/*
 * default logging
 */
import java.util.Collections;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.module.procedural.map.instance.IInstaniationMap;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.IBufferCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;

public class BufferInstantiationMapTemplate extends
    AbstractInstantiationMapTemplate<IActivationBuffer, Object, GeneralInstantiationMapTemplate>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(BufferInstantiationMapTemplate.class);

  static public final String        QUERY_KEY = QueryCondition.class
                                                         .getName();

  public BufferInstantiationMapTemplate(IActivationBuffer root)
  {
    super(root);
  }
  
  public GeneralInstantiationMapTemplate getContentTemplate(Object key)
  {
    if(key==null) return null;

    return getSubMap(key, false);
  }

  @Override
  public boolean add(IProduction production)
  {
    String bufferName = getRoot().getName();
    boolean added = false;

    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition)
      {
        IBufferCondition bufferCondition = (IBufferCondition) condition;
        if (bufferCondition.getBufferName().equalsIgnoreCase(bufferName))
        {
          /*
           * relevant, is it query or match condition
           */
          Object key = getGeneralMapKey(bufferCondition);
          if(key==null) continue;

          GeneralInstantiationMapTemplate specificMap = getSubMap(key, true);
          added |= specificMap.add(production, bufferCondition);
        }
      }
    return added;
  }

  @Override
  public void remove(IProduction production)
  {
    String bufferName = getRoot().getName();

    for (ICondition condition : production.getSymbolicProduction()
        .getConditions())
      if (condition instanceof IBufferCondition)
      {
        IBufferCondition bufferCondition = (IBufferCondition) condition;
        if (bufferCondition.getBufferName().equalsIgnoreCase(bufferName))
        {
          Object key = getGeneralMapKey(bufferCondition);
          
          if(key==null) continue;

          GeneralInstantiationMapTemplate specificMap = getSubMap(key, false);
          if (specificMap != null)
          {
            specificMap.remove(production, bufferCondition);
            if (specificMap.getSize() == 0)
              removeSubMap(getRoot(), key);
          }
        }
      }
  }

  protected Object getGeneralMapKey(IBufferCondition condition)
  {
    if (condition instanceof ChunkTypeCondition)
      return ((ChunkTypeCondition) condition).getChunkType();
    else
      if(condition instanceof QueryCondition)
        return QUERY_KEY;
    
    return null;
  }

  /**
   * we only return the query key and not the chunktype, as those will be
   * handled by add & remove
   */
  @Override
  protected Set<Object> getSubMapKeys(IActivationBuffer root)
  {
    return Collections.singleton((Object) QUERY_KEY);
  }

  @Override
  protected GeneralInstantiationMapTemplate instantiateSubMap(IActivationBuffer root, Object key)
  {
    return new GeneralInstantiationMapTemplate(key);
  }

  @Override
  public IInstaniationMap<IActivationBuffer> instantiate(Object... params)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
