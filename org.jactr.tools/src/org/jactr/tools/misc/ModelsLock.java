package org.jactr.tools.misc;

/*
 * default logging
 */
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.instrument.IInstrument;

/**
 * locking mechanism, that when closed, will block all the models it is
 * installed into.
 * 
 * @author harrison
 */
public class ModelsLock extends ModelListenerAdaptor implements IInstrument
{
  /**
   * a simple future that has a callable to determine how long the get() call
   * should block. If the block is released (returns false), the second callable
   * will be executed for the return value.
   * 
   * @author harrison
   * @param <T>
   */
  private final class SimpleFuture<T> implements Future<T>
  {
    private boolean           _canceled = false;

    private boolean           _done     = false;

    private Callable<T>       _runner;

    private Callable<Boolean> _blockingTest;

    public SimpleFuture(Callable<Boolean> block, Callable<T> runner)
    {
      _runner = runner;
      _blockingTest = block;
    }

    public boolean cancel(boolean mayInterruptIfRunning)
    {
      _canceled = true;
      return _canceled;
    }

    public T get() throws InterruptedException, ExecutionException
    {
      try
      {
        return get(-1l, null);
      }
      catch (TimeoutException e)
      {
        // won't actually happen
        throw new ExecutionException(e);
      }
    }

    public T get(long timeout, TimeUnit unit) throws InterruptedException,
        ExecutionException, TimeoutException
    {
      try
      {
        _lock.lock();
        while (_blockingTest.call())
          if (timeout == -1 && unit == null)
            _lockCondition.await();
          else if (!_lockCondition.await(timeout, unit))
            throw new TimeoutException();

        return _runner.call();
      }
      catch (TimeoutException e)
      {
        throw e;
      }
      catch (InterruptedException e)
      {
        throw e;
      }
      catch (ExecutionException e)
      {
        throw e;
      }
      catch (Exception e)
      {
        throw new ExecutionException("Failed to execute callable", e);
      }
      finally
      {
        _lock.unlock();
      }
    }

    public boolean isCancelled()
    {
      return _canceled;
    }

    public boolean isDone()
    {
      return _done;
    }
  }

  /**
   * Logger definition
   */
  static private final transient Log LOGGER         = LogFactory
                                                        .getLog(ModelsLock.class);

  private ReentrantLock              _lock          = new ReentrantLock();

  private Condition                  _lockCondition = _lock.newCondition();

  private volatile boolean           _shouldBlock   = false;

  private Set<IModel>                _installed     = new HashSet<IModel>();

  /**
   * who are we going to lock
   */
  private Set<IModel>                _modelsToLock  = new HashSet<IModel>();

  /**
   * the models that are currently blocking
   */
  private Set<IModel>                _modelsLocked  = new HashSet<IModel>();

  /*
   * we actually block at the start of the next cycle. We do it at the start,
   * because the normal cycle blocks on the clock after the cycle stops (since
   * that is when we now the next timestep). It is also safe if the close()
   * calls are received at different points in related models. (non-Javadoc)
   * @see org.jactr.instrument.IInstrument#initialize()
   */

  public void initialize()
  {
  }

  @Override
  public void modelStarted(ModelEvent me)
  {
    // add to the set
    try
    {
      _lock.lock();

      /*
       * if we should enable locking, add it now. the model will block almost
       * immediately after this for the first cycle.
       */
      if (_installed.contains(me.getSource()))
        _modelsToLock.add(me.getSource());

      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  @Override
  public void modelStopped(ModelEvent me)
  {
    // remove from the set
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(
          "%s has stopped, removing from lock set @ %.2f", me.getSource(),
          ACTRRuntime.getRuntime().getClock(me.getSource()).getTime()));
    try
    {
      _lock.lock();
      _modelsToLock.remove(me.getSource());
      _modelsLocked.remove(me.getSource());

      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  @Override
  public void cycleStarted(ModelEvent me)
  {
    // yup.
    checkAndBlock(me.getSource());
  }

  public void install(IModel model)
  {
    try
    {
      _lock.lock();
      _installed.add(model);

      // inline
      model.addListener(this, null);

      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void uninstall(IModel model)
  {
    try
    {
      _lock.lock();
      _installed.remove(model);
      model.removeListener(this);
      _modelsToLock.remove(model);
      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * returns true if the gate is closed, but not necessarily if everyone is.
   * 
   * @return
   */
  public boolean isClosed()
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

  public boolean allAreFree()
  {
    try
    {
      _lock.lock();
      return _modelsLocked.size() == 0;
    }
    finally
    {
      _lock.unlock();
    }
  }

  public boolean allAreBlocked()
  {
    try
    {
      _lock.lock();
      return _modelsToLock.size() > 0
          && _modelsLocked.containsAll(_modelsToLock);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * close the lock on all the installed models. To block until all the models,
   * just call {@link Future#get()}. The return boolean will be true, unless
   * there are no models. i.e., If no models are running, it will return false.
   * 
   * @return
   */
  public Future<Boolean> close()
  {
    try
    {
      _lock.lock();
      _shouldBlock = true;
      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }

    return new SimpleFuture<Boolean>(new Callable<Boolean>() {

      public Boolean call() throws Exception
      {
        return _modelsToLock.size() > 0
            && !_modelsToLock.containsAll(_modelsLocked);
      }

    }, new Callable<Boolean>() {

      public Boolean call() throws Exception
      {
        if (_modelsToLock.size() == 0) return false;
        return _modelsToLock.containsAll(_modelsLocked);
      }

    });

  }

  /**
   * release the models.
   * 
   * @return
   */
  public Future<Boolean> open()
  {
    try
    {
      _lock.lock();
      _shouldBlock = false;
      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }

    return new SimpleFuture<Boolean>(new Callable<Boolean>() {

      public Boolean call() throws Exception
      {
        return _modelsToLock.size() != 0 || _modelsLocked.size() > 0;
      }

    }, new Callable<Boolean>() {

      public Boolean call() throws Exception
      {
        if (_modelsToLock.size() == 0) return false;
        return _modelsLocked.size() == 0;
      }

    });
  }

  /**
   * do the blocking.
   * 
   * @param model
   */
  protected void checkAndBlock(IModel model)
  {
    try
    {
      _lock.lock();

      long start = System.currentTimeMillis();
      boolean blocked = false;

      while (_shouldBlock && _modelsToLock.contains(model))
      {
        blocked = true;
        _modelsLocked.add(model);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Blocking %s @ %.2f", model, ACTRRuntime
              .getRuntime().getClock(model).getTime()));
        _lockCondition.await(250, TimeUnit.MILLISECONDS);
      }

      if (LOGGER.isDebugEnabled() && blocked)
      {
        long total = System.currentTimeMillis() - start;
        LOGGER.debug(String.format("blocked %s for %dms", model, total));
      }

    }
    catch (InterruptedException ie)
    {
      return;
    }
    finally
    {
      _modelsLocked.remove(model);

      _lock.unlock();
    }
  }

}
