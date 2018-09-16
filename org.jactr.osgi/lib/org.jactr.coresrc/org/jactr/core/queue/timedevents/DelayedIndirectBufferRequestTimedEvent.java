package org.jactr.core.queue.timedevents;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.IRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.IRequest;

public class DelayedIndirectBufferRequestTimedEvent extends AbstractTimedEvent
    implements IBufferBasedTimedEvent
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DelayedIndirectBufferRequestTimedEvent.class);

  IRequest                           _request;

  IActivationBuffer                  _buffer;

  IRequestDelegate                   _delegate;

  public DelayedIndirectBufferRequestTimedEvent(IRequest request,
      IActivationBuffer buffer,
      org.jactr.core.buffer.delegate.IRequestDelegate delegate, double start,
      double end)
  {
    super(start, end);
    _request = request;
    _buffer = buffer;
    _delegate = delegate;
  }
  
  public void fire(double currentTime)
  {
    super.fire(currentTime);
    _delegate.request(_request, getBuffer(), currentTime);
  }

  public IChunk getBoundChunk()
  {
    if(_request instanceof ChunkRequest)
      return ((ChunkRequest)_request).getChunk();
    
    return null;
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

}
