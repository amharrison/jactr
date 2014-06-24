package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.six.AbstractActivationBuffer6;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.BlockingTimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;

/**
 * abstract request delegate that splits requests into two part process
 * {@link #startRequest(IRequest, IActivationBuffer, double)} and
 * {@link #finishRequest(IRequest, IActivationBuffer, Object)}. This makes it
 * easier to implement asynchronous requests that start a computation and then
 * later harvest the results.<br/>
 * <br/>
 * To make derived classes asynchronous, merely override
 * {@link #isAsynchronous()} to return true, and use
 * {@link #computeCompletionTime(double, IRequest, IActivationBuffer)} to return
 * when the system should call
 * {@link #finishRequest(IRequest, IActivationBuffer, Object)}.<br/>
 * <br/>
 * If {@link #finishRequest(IRequest, IActivationBuffer, Object)} requires some
 * value from {@link #startRequest(IRequest, IActivationBuffer, double)} they
 * both have an object that can be passed from one to the other.<br/>
 * <br/>
 * The asynchrony is implemented by calling
 * {@link #startRequest(IRequest, IActivationBuffer, double)} and then queueing
 * up a {@link ITimedEvent} that will fire at
 * {@link #computeCompletionTime(double, IRequest, IActivationBuffer)} which
 * will then call {@link #finishRequest(IRequest, IActivationBuffer, Object)}. <br/>
 * <br/>
 * You can also configure the delegate to use blocking timed events to ensure
 * that the model clock does not advance beyond the harvest time (instead of
 * relying upon some blocking mechanism in
 * {@link #finishRequest(IRequest, IActivationBuffer, Object)}. However, if you
 * do enable blocking timed events ({@link #setUseBlockingTimedEvents(boolean)}
 * ), you must be sure that you call {@link #release()} in response to some
 * event, otherwise the model will deadlock.<br/>
 * <br/>
 * <br/>
 * This is for requested that are delayed in time (beyond the current cycle),
 * such as visual requests. As opposed to buffers that accept new chunk
 * insertions directly and immediately (like goal).
 * 
 * @author harrison
 */
public abstract class AsynchronousRequestDelegate implements IRequestDelegate
{

  private boolean            _isAsynchronous         = false;

  private ITimedEvent        _currentTimedEvent;

  private IRequest           _previousRequest;

  /**
   * canonical ACT-R doesn't fire requests until after the production has fired,
   * this will tack 50ms onto the start time
   */
  private boolean            _delayRequestStart      = true;

  private BlockingTimedEvent _previousBlockingTimedEvent;

  private boolean            _useBlockingTimedEvents = false;

  /**
   * release the current blocking timed event, if any
   */
  protected void release()
  {
    if (_previousBlockingTimedEvent != null
        && !_previousBlockingTimedEvent.hasAborted())
      _previousBlockingTimedEvent.abort();
  }

  public void setUseBlockingTimedEvents(boolean use)
  {
    _useBlockingTimedEvents = use;
  }

  public boolean isUsingBlockingTimedEvents()
  {
    return _useBlockingTimedEvents;
  }

  public boolean isDelayingStart()
  {
    return _delayRequestStart;
  }

  public void setDelayStart(boolean delayStart)
  {
    _delayRequestStart = delayStart;
  }

  /**
   * called the be sure the request is valid. If the request is valid but can't
   * be accepted, return false. If the request is invalid, throw an exception
   * 
   * @param request
   * @param buffer
   *          TODO
   * @return
   */
  abstract protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException;

  /**
   * Start the buffer request, optimally returning an object for the
   * {@link #finishRequest(IRequest, IActivationBuffer, Object)} to inspect. If
   * this is an asynchronous request, this will be fired and finish will be
   * fired at
   * {@link #computeCompletionTime(double, IRequest, IActivationBuffer)}
   * 
   * @param request
   * @param buffer
   * @param requestTime
   *          TODO
   */
  abstract protected Object startRequest(IRequest request,
      IActivationBuffer buffer, double requestTime);

  /**
   * finish the request
   * 
   * @param request
   * @param buffer
   * @param startValue
   */
  abstract protected void finishRequest(IRequest request,
      IActivationBuffer buffer, Object startValue);

  /**
   * called if the timedevent is aborted
   * 
   * @param request
   * @param buffer
   * @param startValue
   */
  protected void abortRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    release();
  }

  public void clear()
  {
    ITimedEvent event = getCurrentTimedEvent();
    setCurrentTimedEvent(null);

    if (event != null && !event.hasAborted() && !event.hasFired())
      event.abort();

    release();
  }

  final public void setAsynchronous(boolean isAsynch)
  {
    _isAsynchronous = isAsynch;
  }

  /**
   * override to return true if the request should be split in time. be sure to
   * override
   * {@link #computeCompletionTime(double, IRequest, IActivationBuffer)} as well
   * 
   * @return
   */
  final public boolean isAsynchronous()
  {
    return _isAsynchronous;
  }

  /**
   * default impl returns now +
   * {@link IProceduralModule#getDefaultProductionFiringTime()}
   * 
   * @param now
   * @param request
   * @param buffer
   *          TODO
   * @return
   */
  protected double computeCompletionTime(double startTime, IRequest request,
      IActivationBuffer buffer)
  {
    return startTime
        + buffer.getModel().getProceduralModule()
            .getDefaultProductionFiringTime();
  }

  /**
   * expand requests gives subclassers the chance to modify the request after it
   * has been validated but before the request is actually made. Subclassers are
   * free to change the request in anyway. This impl merely passes the request
   * through unchanged.
   * 
   * @param request
   * @return
   */
  protected IRequest expandRequest(IRequest request)
  {
    return request;
  }

  /**
   * make the buffer request. If synchronous
   * {@link #startRequest(IRequest, IActivationBuffer, double)} and
   * {@link #finishRequest(IRequest, IActivationBuffer, Object)} will be called
   * immediately. If not, start will be called and a timed event will be posted
   * that will handle
   * {@link #finishRequest(IRequest, IActivationBuffer, Object)}
   * 
   * @param request
   * @param buffer
   * @return
   * @see org.jactr.core.buffer.delegate.IRequestDelegate#request(org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer)
   */
  final public boolean request(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    if (!isValid(request, buffer)) return false;

    request = expandRequest(request);

    IModel model = buffer.getModel();
    double start = requestTime;
    /*
     * lisp delays firing requests until after the production has completed, so
     * start times need to be offset by def act time
     */
    if (isDelayingStart())
      start += model.getProceduralModule().getDefaultProductionFiringTime();
    double finish = computeCompletionTime(start, request, buffer);

    preStart(request, buffer, start, finish);

    final Object startReturn = startRequest(request, buffer, requestTime);

    postStart(request, buffer, start, finish, startReturn);

    _previousRequest = request;

    if (!isAsynchronous())
      finishRequest(request, buffer, startReturn);
    else
    {
      ITimedEvent timedEvent = createFinishTimedEvent(start, finish, request,
          buffer, startReturn);

      setCurrentTimedEvent(timedEvent);

      model.getTimedEventQueue().enqueue(timedEvent);
    }

    return true;
  }

  /**
   * called just before
   * {@link #startRequest(IRequest, IActivationBuffer, double)} is called
   * 
   * @param request
   * @param buffer
   * @param startTime
   * @param finishTime
   */
  protected void preStart(IRequest request, IActivationBuffer buffer,
      double startTime, double finishTime)
  {
    if (!isAsynchronous()) return;

    release();

    if (isUsingBlockingTimedEvents())
    {
      /*
       * we want to push finish time forward a smidgen.
       */
      _previousBlockingTimedEvent = new BlockingTimedEvent(this, startTime,
          finishTime);

      buffer.getModel().getTimedEventQueue()
          .enqueue(_previousBlockingTimedEvent);
    }
  }

  protected void postStart(IRequest request, IActivationBuffer buffer,
      double startTime, double finishTime, Object startReturn)
  {

  }

  /**
   * creates the timed event that will fire the finish method. If you want more
   * control, override this method
   * 
   * @param start
   * @param finish
   * @param request
   * @param buffer
   * @param startValue
   * @return
   */
  protected ITimedEvent createFinishTimedEvent(double start, double finish,
      IRequest request, IActivationBuffer buffer, Object startValue)
  {
    return new FinishRequestTimedEvent(start, finish, request, buffer,
        startValue);
  }

  /**
   * returns the last timed event queued. This will only be changed after each
   * request is called. it will not be nulled out after the event has fired
   * 
   * @return
   */
  protected ITimedEvent getCurrentTimedEvent()
  {
    return _currentTimedEvent;
  }

  protected void setCurrentTimedEvent(ITimedEvent event)
  {
    _currentTimedEvent = event;
  }

  protected IRequest getPreviousRequest()
  {
    return _previousRequest;
  }

  /**
   * utility method to test the state, only works if this buffer extends
   * {@link IStatusBuffer}
   * 
   * @param buffer
   * @return
   */
  protected boolean isBusy(IActivationBuffer buffer)
  {
    return ((IStatusBuffer) buffer).isStateBusy();
  }

  /**
   * utility method, only works if buffer extends
   * {@link AbstractActivationBuffer6}
   * 
   * @param buffer
   */
  protected void setBusy(IActivationBuffer buffer)
  {
    AbstractActivationBuffer6 ab = (AbstractActivationBuffer6) buffer;
    ab.setStateChunk(ab.getBusyChunk());
  }

  /**
   * utility method, only works if buffer extends
   * {@link AbstractActivationBuffer6}
   * 
   * @param buffer
   */
  protected void setFree(IActivationBuffer buffer)
  {
    AbstractActivationBuffer6 ab = (AbstractActivationBuffer6) buffer;
    ab.setStateChunk(ab.getFreeChunk());
    ab.setErrorChunk(null);
  }

  /**
   * utility method, only works if buffer extends
   * {@link AbstractActivationBuffer6}
   * 
   * @param buffer
   */
  protected void setError(IActivationBuffer buffer)
  {
    AbstractActivationBuffer6 ab = (AbstractActivationBuffer6) buffer;
    ab.setStateChunk(ab.getErrorChunk());
  }

  protected void setError(IActivationBuffer buffer, IChunk errorCode)
  {
    AbstractActivationBuffer6 ab = (AbstractActivationBuffer6) buffer;
    ab.setStateChunk(ab.getErrorChunk());
    ab.setErrorChunk(errorCode);
  }

  private class FinishRequestTimedEvent extends AbstractTimedEvent implements
      IBufferBasedTimedEvent
  {

    final private IActivationBuffer _buffer;

    final private IRequest          _request;

    final private Object            _startValue;

    public FinishRequestTimedEvent(double start, double stop, IRequest request,
        IActivationBuffer buffer, Object startValue)
    {
      super(start, stop);
      _buffer = buffer;
      _request = request;
      _startValue = startValue;
    }

    public IChunk getBoundChunk()
    {
      return null;
    }

    public IActivationBuffer getBuffer()
    {
      return _buffer;
    }

    @Override
    public void fire(double currentTime)
    {
      super.fire(currentTime);
      finishRequest(_request, _buffer, _startValue);
    }

    @Override
    public void abort()
    {
      super.abort();
      abortRequest(_request, _buffer, _startValue);
    }
  }
}
