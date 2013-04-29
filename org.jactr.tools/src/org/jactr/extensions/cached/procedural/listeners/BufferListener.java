package org.jactr.extensions.cached.procedural.listeners;

/*
 * default logging
 */
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.extensions.cached.procedural.invalidators.IInvalidator;

public class BufferListener extends ActivationBufferListenerAdaptor
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BufferListener.class);

  private final IActivationBuffer    _buffer;

  /**
   * invalidators that are flipped on any change to the buffer contents
   */
  private Collection<IInvalidator>   _generalInvalidators;

  public BufferListener(IActivationBuffer buffer)
  {
    _generalInvalidators = new FastList<IInvalidator>();

    _buffer = buffer;
    _buffer.addListener(this, null);

  }

  public void dispose()
  {
    _generalInvalidators.clear();
    _buffer.removeListener(this);
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

  public void sourceChunkAdded(ActivationBufferEvent abe)
  {
    invalidateAll();
  }

  public void sourceChunkRemoved(ActivationBufferEvent abe)
  {
    invalidateAll();
  }

  public void sourceChunksCleared(ActivationBufferEvent abe)
  {
    invalidateAll();
  }

  public void statusSlotChanged(ActivationBufferEvent abe)
  {
    /*
     * we could use this for the slot change handling, but instead we'll use the
     * general slot listener
     */
  }

  public void register(IInvalidator invalidator)
  {
    _generalInvalidators.add(invalidator);
  }

  public void unregister(IInvalidator invalidator)
  {
    _generalInvalidators.remove(invalidator);
  }

  private void invalidateAll()
  {

    FastList<IInvalidator> invalidators = FastList.newInstance();

    invalidators.addAll(_generalInvalidators);

    if (invalidators.size() > 0)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Invalidating due to content change of %s",
            _buffer));
      for (IInvalidator invalidator : invalidators)
        invalidator.invalidate();
    }

    FastList.recycle(invalidators);
  }

}
