package org.jactr.tools.goalfeeder.timedevents;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;

/**
 * @author harrison
 */
@Deprecated
public class GatingTimedEvent extends AbstractTimedEvent
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER             = LogFactory
                                                      .getLog(GatingTimedEvent.class);

  private Lock                 _lock              = new ReentrantLock();

  private Condition            _modelBlocked      = _lock.newCondition();

  private Condition            _modelReleased     = _lock.newCondition();

  private volatile boolean     _shouldBlock       = true;

  private volatile boolean     _isBlocked         = false;

  private volatile boolean     _isExternalWaiting = false;

  private Object               _creator;

  private Runnable             _onBlock;

  public GatingTimedEvent(double start, double end)
  {
    this(start, end, null, null);
  }

  public GatingTimedEvent(double start, double end, Object creator)
  {
    this(start, end, creator, null);
  }

  public GatingTimedEvent(double start, double end, Object creator,
      Runnable onBlock)
  {
    super(start, end);
    _creator = creator;
    _onBlock = onBlock;
  }

  public Object getCreator()
  {
    return _creator;
  }

  @Override
  public void abort()
  {
    /**
     * we need to release before the abort because abort is a synchronized call
     * and if fire has been called then the timedevent queue will have
     * synchronized this event.. releasing first allows the block to be released
     * within the synch call. If they were reversed, we'd block the abort until
     * the fire returned, which it wont since release wouldnt be called until
     * after the abort returns
     */
    release();
    super.abort();
  }

  @Override
  public void fire(double now)
  {
    if (hasAborted())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(this + " was aborted, ignoring");
      return;
    }

    super.fire(now);
    try
    {
      _lock.lock();

      if (willBlock() && _onBlock != null) _onBlock.run();

      /*
       * block until release is called
       */
      while (willBlock())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(this + " Blocking model @ " + now);
        _isBlocked = true;
        _modelBlocked.signalAll();
        _modelReleased.await();
      }

      if (_isBlocked && LOGGER.isDebugEnabled())
        LOGGER.debug(this + " Resuming model");
    }
    catch (InterruptedException ie)
    {

    }
    finally
    {
      _isBlocked = false;
      _lock.unlock();
    }
  }

  /**
   * wait until either release is called, or the model reaches fire.
   * 
   * @throws InterruptedException
   */
  public void waitForModel() throws InterruptedException
  {
    try
    {
      _lock.lock();
      while (willBlock() && !isBlocked())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(this + " Waiting for model to block");
        _isExternalWaiting = true;
        _modelBlocked.await();
      }
    }
    finally
    {
      _isExternalWaiting = false;
      _lock.unlock();
    }
  }

  public boolean isExternalWaiting()
  {
    try
    {
      _lock.lock();
      return _isExternalWaiting;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * is there currently a model blocked?
   * 
   * @return
   */
  public boolean isBlocked()
  {
    try
    {
      _lock.lock();
      return _isBlocked;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * will a model reaching the fire method block?
   * 
   * @return
   */
  public boolean willBlock()
  {
    try
    {
      _lock.lock();
      return _shouldBlock;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * release the gate. if the model is currently blocked, it will be signaled
   * and permitted to run on. if it isnt blocked, when the model makes it to
   * fire, it will pass over the blocking.
   */
  public void release()
  {
    try
    {
      _lock.lock();
      if (LOGGER.isDebugEnabled()) LOGGER.debug(this + " Releasing block");
      _shouldBlock = false;
      _modelReleased.signalAll();
      _modelBlocked.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  @Override
  public String toString()
  {
    return super.toString() + " creator:" + _creator;
  }
}
