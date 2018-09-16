package org.jactr.extensions.cached.procedural.listeners;

/*
 * default logging
 */
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.utils.collections.FastListFactory;
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
    _generalInvalidators = FastListFactory.newInstance();

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

  @Override
  public void sourceChunkAdded(ActivationBufferEvent abe)
  {
    invalidateAll();
  }

  @Override
  public void sourceChunkRemoved(ActivationBufferEvent abe)
  {
    invalidateAll();
  }

  @Override
  public void sourceChunksCleared(ActivationBufferEvent abe)
  {
    invalidateAll();
  }

  @Override
  public void statusSlotChanged(ActivationBufferEvent abe)
  {
    /*
     * we could use this for the slot change handling, but instead we'll use the
     * general slot listener
     */
  }

  synchronized public void register(IInvalidator invalidator)
  {
    _generalInvalidators.add(invalidator);
  }

  synchronized public void unregister(IInvalidator invalidator)
  {
    _generalInvalidators.remove(invalidator);
  }

  synchronized private List<IInvalidator> getInvalidators()
  {
    List<IInvalidator> invalidators = FastListFactory.newInstance();
    invalidators.addAll(_generalInvalidators);
    return invalidators;
  }

  private void invalidateAll()
  {
    List<IInvalidator> invalidators = getInvalidators();

    try
    {
      if (invalidators.size() > 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "Invalidating due to content change of %s", _buffer));

        for (IInvalidator invalidator : invalidators)
          invalidator.invalidate();
      }
    }
    catch (Exception e)
    {
      LOGGER.error("failed to invalidate ", e);
    }

    FastListFactory.recycle(invalidators);
  }

}
