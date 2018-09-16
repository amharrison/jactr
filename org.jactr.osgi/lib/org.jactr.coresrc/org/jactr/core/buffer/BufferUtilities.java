package org.jactr.core.buffer;

import java.util.ArrayList;
/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;

public class BufferUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                   = LogFactory
                                                                  .getLog(BufferUtilities.class);

  static private final String        CONTAINMENT_MAP_META_KEY = BufferUtilities.class
                                                                  + ".containmentMap";

  /**
   * marks this chunk as being contained within this buffer
   * 
   * @param chunk
   * @param buffer
   */
  @SuppressWarnings("unchecked")
  static public void markContained(IChunk chunk, IActivationBuffer buffer,
      double activation)
  {
    try
    {
      chunk.getWriteLock().lock();
      ConcurrentHashMap<IActivationBuffer, Double> containmentMap = (ConcurrentHashMap<IActivationBuffer, Double>) chunk
          .getMetaData(CONTAINMENT_MAP_META_KEY);

      if (containmentMap == null)
      {
        containmentMap = new ConcurrentHashMap<IActivationBuffer, Double>();
        chunk.setMetaData(CONTAINMENT_MAP_META_KEY, containmentMap);
      }

      containmentMap.putIfAbsent(buffer, activation);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(chunk + " is contained within " + containmentMap.keySet());
    }
    finally
    {
      chunk.getWriteLock().unlock();
    }
  }

  static public void unmarkContained(IChunk chunk, IActivationBuffer buffer)
  {
    try
    {
      chunk.getWriteLock().lock();
      @SuppressWarnings("unchecked")
      ConcurrentHashMap<IActivationBuffer, Double> containmentMap = (ConcurrentHashMap<IActivationBuffer, Double>) chunk
          .getMetaData(CONTAINMENT_MAP_META_KEY);

      if (containmentMap != null && containmentMap.remove(buffer) != null
          && containmentMap.size() == 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(chunk
              + " is not contained by anyone, removing meta container");
        chunk.setMetaData(CONTAINMENT_MAP_META_KEY, null);
      }

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(chunk + " is contained within "
            + (containmentMap != null ? containmentMap.keySet() : "[]"));
    }
    finally
    {
      chunk.getWriteLock().unlock();
    }
  }

  static public void getContainingBuffers(IChunk chunk, boolean isStrict,
      Collection<IActivationBuffer> container)
  {

    if (!isStrict)
    {
      IModel model = chunk.getModel();

      for (IActivationBuffer buffer : model.getActivationBuffers())
        for (IChunk sourceChunk : buffer.getSourceChunks())
        {
          boolean equals = false;

          if (isStrict)
            equals = chunk.equals(sourceChunk);
          else
            equals = chunk.equalsSymbolic(sourceChunk);

          if (equals) container.add(buffer);
        }
    }
    else
    {
      @SuppressWarnings("unchecked")
      ConcurrentHashMap<IActivationBuffer, Double> containmentMap = (ConcurrentHashMap<IActivationBuffer, Double>) chunk
          .getMetaData(CONTAINMENT_MAP_META_KEY);

      if (containmentMap != null)
        containmentMap.forEach((buffer, act) -> container.add(buffer));
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(chunk + " is contained by " + container + ", isStrict="
          + isStrict);
  }

  /**
   * return all the buffers that contain this chunk. if isStrict, the
   * containment check will use {@link IChunk#equals(Object)}, otherwise it will
   * use {@link IChunk#equalsSymbolic(IChunk)}
   * 
   * @param chunk
   * @param isStric
   * @return
   */
  static public Collection<IActivationBuffer> getContainingBuffers(
      IChunk chunk, boolean isStrict)
  {
    Collection<IActivationBuffer> container = new ArrayList<>();
    getContainingBuffers(chunk, isStrict, container);
    return container;
  }
}
