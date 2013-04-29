package org.jactr.core.queue.timedevents;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.IRequestableBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.IRequest;

public class DelayedBufferRequestTimedEvent extends AbstractTimedEvent implements IBufferBasedTimedEvent
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DelayedBufferRequestTimedEvent.class);

  IRequest _request;
  IRequestableBuffer _buffer;
  
  public DelayedBufferRequestTimedEvent(IRequest request, IRequestableBuffer buffer, double start, double end)
  {
    super(start, end);
    _request = request;
    _buffer = buffer;
  }
  
  
  public void fire(double currentTime)
  {
    super.fire(currentTime);
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Inserting " + _request + " into buffer " +
          _buffer.getName());
    _buffer.request(_request, currentTime);
  }

  public IChunk getBoundChunk()
  {
    return null;
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }
  
  
}
