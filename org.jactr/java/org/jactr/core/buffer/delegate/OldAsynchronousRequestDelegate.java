package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.timedevents.BlockingTimedEvent;

/**
 * Asynchronous request delegate will queue a blocking timed event to fire just
 * before the finishing timed event. This will block the model, preventing it
 * from reaching the finish method until {@link #release()} is called. This is
 * used as a synchronization mechanism for asynchronous modules.
 * 
 * @author harrison
 */
@Deprecated
public abstract class OldAsynchronousRequestDelegate extends
    AsynchronousRequestDelegate
{
  static private transient final Log LOGGER = LogFactory
                                                .getLog(OldAsynchronousRequestDelegate.class);

  private BlockingTimedEvent         _previousBlockingTimedEvent;

  
  protected void preStart(IRequest request, IActivationBuffer buffer, double startTime, double finishTime)
  {
    if(!isAsynchronous()) return;
    
    release();
    
    _previousBlockingTimedEvent = new BlockingTimedEvent(this, startTime, finishTime);
    
    buffer.getModel().getTimedEventQueue().enqueue(_previousBlockingTimedEvent);
  }

  /**
   * make sure we release the blocking timed event
   * 
   * @param request
   * @param buffer
   * @param startValue
   * @see org.jactr.core.buffer.delegate.AsynchronousRequestDelegate#abortRequest(org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer, java.lang.Object)
   */
  protected void abortRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    release();
  }

  /**
   * release the current blocking timed event, if any
   */
  protected void release()
  {
    if (_previousBlockingTimedEvent != null
        && !_previousBlockingTimedEvent.hasAborted())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Releaseing " + _previousBlockingTimedEvent);
      _previousBlockingTimedEvent.abort();
    }
  }
}
