package org.jactr.core.runtime.controller.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;

/**
 * the runtime listener is responsible for triggering the appropriate conditions
 * in the default controller
 * 
 * @author harrison
 */
public class RuntimeListener extends ACTRRuntimeAdapter
{
  /**
   * Logger definition
   */
  static private final transient Log      LOGGER = LogFactory
                                                     .getLog(RuntimeListener.class);

  final private RuntimeState              _state;

  final private Collection<RuntimeFuture> _startFutures;

  final private Collection<RuntimeFuture> _stopFutures;

  final private Collection<RuntimeFuture> _suspendFutures;

  final private Collection<RuntimeFuture> _resumeFutures;

  final private Lock                      _lock  = new ReentrantLock();
  
  private Exception _stopException;

  public RuntimeListener(RuntimeState state)
  {
    _state = state;
    _startFutures = new ArrayList<RuntimeFuture>();
    _stopFutures = new ArrayList<RuntimeFuture>();
    _suspendFutures = new ArrayList<RuntimeFuture>();
    _resumeFutures = new ArrayList<RuntimeFuture>();
  }
  
  

  private Future<Boolean> newFuture(Collection<RuntimeFuture> container)
  {
    RuntimeFuture future = new RuntimeFuture();
    container.add(future);
    return future;
  }

  private void triggerFutures(Collection<RuntimeFuture> container,
      boolean succeeded, Exception exception)
  {
    try
    {
      _lock.lock();
      for (Iterator<RuntimeFuture> iterator = container.iterator(); iterator
          .hasNext();)
      {
        RuntimeFuture futureTask = iterator.next();
        iterator.remove();
        futureTask.set(succeeded, exception);
      }
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Future<Boolean> getStartFuture()
  {
    if (_state.isRunning()) return new RuntimeFuture(true, null);

    try
    {
      _lock.lock();
      return newFuture(_startFutures);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Future<Boolean> getStopFuture()
  {
    if (!_state.isRunning()) return new RuntimeFuture(true, _stopException);

    try
    {
      _lock.lock();
      return newFuture(_stopFutures);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Future<Boolean> getSuspendFuture()
  {
    if (!_state.isRunning())
      return new RuntimeFuture(false, new IllegalStateException(
          "Runtime is not running, cannot be suspended"));

    if (_state.isSuspended()) return new RuntimeFuture(true, null);

    try
    {
      _lock.lock();

      return newFuture(_suspendFutures);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Future<Boolean> getResumeFuture()
  {
    if (!_state.isRunning())
      return new RuntimeFuture(false, new IllegalStateException(
          "Runtime is not running, cannot be resumed"));

    if (!_state.isSuspended()) return new RuntimeFuture(true, null);

    try
    {
      _lock.lock();

      return newFuture(_resumeFutures);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void runtimeResumed(ACTRRuntimeEvent event)
  {
    triggerFutures(_resumeFutures, true, null);
  }

  public void runtimeStarted(ACTRRuntimeEvent event)
  {
    _stopException = null;
    triggerFutures(_startFutures, true, null);
  }

  public void runtimeStopped(ACTRRuntimeEvent event)
  {
    _stopException = event.getException();
    triggerFutures(_stopFutures, true, event.getException());
  }

  public void runtimeSuspended(ACTRRuntimeEvent event)
  {
    triggerFutures(_suspendFutures, true, null);
  }

  private class RuntimeFuture extends FutureTask<Boolean>
  {
    public RuntimeFuture()
    {
      super(new Callable<Boolean>() {
        public Boolean call()
        {
          return null;
        }
      });
    }

    public RuntimeFuture(boolean succeeded, Exception e)
    {
      this();
      set(succeeded, e);
    }

    public void set(boolean succeeded, Exception e)
    {
      if (e != null)
        setException(e);
      else
        set(succeeded);
    }
  }
}
