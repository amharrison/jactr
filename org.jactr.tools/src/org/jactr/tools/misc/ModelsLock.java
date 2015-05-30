package org.jactr.tools.misc;

/*
 * default logging
 */
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
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
   * Logger definition
   */
  static private final transient Log          LOGGER         = LogFactory
                                                                 .getLog(ModelsLock.class);

  private ReentrantLock                       _lock          = new ReentrantLock();

  private Condition                           _lockCondition = _lock
                                                                 .newCondition();

  private volatile boolean                    _shouldBlock   = false;

  private Set<IModel>                         _installed     = new HashSet<IModel>();

  /**
   * who are we going to lock
   */
  private Set<IModel>                         _modelsToLock  = new HashSet<IModel>();

  /**
   * the models that are currently blocking
   */
  private Set<IModel>                         _modelsLocked  = new HashSet<IModel>();

  private volatile CompletableFuture<Boolean> _currentRequest;

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

  public boolean areAllFree()
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

  public boolean areAllBlocked()
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
  public CompletableFuture<Boolean> close()
  {
    try
    {
      _lock.lock();
      _shouldBlock = true;
      _currentRequest = new CompletableFuture<Boolean>();
      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
    return _currentRequest;
  }

  /**
   * release the models.
   * 
   * @return
   */
  public CompletableFuture<Boolean> open()
  {
    try
    {
      _lock.lock();
      _shouldBlock = false;
      _currentRequest = new CompletableFuture<Boolean>();
      _lockCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }

    return _currentRequest;
  }

  private void allBocked()
  {
    if (_currentRequest != null) _currentRequest.complete(true);
    _currentRequest = null;
  }

  private void allFreed()
  {
    if (_currentRequest != null) _currentRequest.complete(true);
    _currentRequest = null;
  }

  private boolean shouldBlock()
  {
    return _shouldBlock;
  }

  /**
   * do the blocking.
   * 
   * @param model
   */
  protected void checkAndBlock(IModel model)
  {
    long start = System.currentTimeMillis();
    boolean blocked = false;
    boolean allAreBlocked = false;
    try
    {
      _lock.lock();

      if (shouldBlock() && _modelsToLock.contains(model))
      {
        _modelsLocked.add(model);
        blocked = true;

        allAreBlocked = areAllBlocked();
      }
    }
    finally
    {
      _lock.unlock();
    }

    if (allAreBlocked) allBocked();

    if (blocked)
      try
      {
        _lock.lock();
        while (shouldBlock() && _modelsToLock.contains(model))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Blocking %s @ %.2f", model, ACTRRuntime
                .getRuntime().getClock(model).getTime()));
          _lockCondition.await(250, TimeUnit.MILLISECONDS);
        }

        if (LOGGER.isDebugEnabled())
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
        boolean allFree = areAllFree();

        _lock.unlock();

        if (allFree) allFreed();
      }

    // try
    // {
    // _lock.lock();
    //
    // long start = System.currentTimeMillis();
    // boolean blocked = false;
    //
    // while (shouldBlock() && _modelsToLock.contains(model))
    // {
    // blocked = true;
    // _modelsLocked.add(model);
    // if (LOGGER.isDebugEnabled())
    // LOGGER.debug(String.format("Blocking %s @ %.2f", model, ACTRRuntime
    // .getRuntime().getClock(model).getTime()));
    //
    // if (areAllBlocked()) allBocked();
    //
    // _lockCondition.await();
    // }
    //
    // if (LOGGER.isDebugEnabled() && blocked)
    // {
    // long total = System.currentTimeMillis() - start;
    // LOGGER.debug(String.format("blocked %s for %dms", model, total));
    // }
    //
    // }
    // catch (InterruptedException ie)
    // {
    // return;
    // }
    // finally
    // {
    // _modelsLocked.remove(model);
    //
    // if (areAllFree()) allFreed();
    //
    // _lock.unlock();
    // }
  }

}
