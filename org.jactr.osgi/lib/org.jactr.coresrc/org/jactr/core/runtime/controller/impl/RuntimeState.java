package org.jactr.core.runtime.controller.impl;

/*
 * default logging
 */
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;

public class RuntimeState
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RuntimeState.class);

  final private Collection<IModel>   _startingModels;

  final private Collection<IModel>   _activeModels;

  final private Collection<IModel>   _inactiveModels;

  final private Collection<IModel>   _suspendedModels;

  final private Lock                 _lock  = new ReentrantLock();

  public RuntimeState()
  {
    _startingModels = new HashSet<IModel>();
    _activeModels = new HashSet<IModel>();
    _inactiveModels = new HashSet<IModel>();
    _suspendedModels = new HashSet<IModel>();
  }

  public void clear()
  {
    try
    {
      _lock.lock();
      _activeModels.clear();
      _inactiveModels.clear();
      _suspendedModels.clear();
      _startingModels.clear();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void getRunning(Collection<IModel> running)
  {
    try
    {
      _lock.lock();
      running.addAll(_activeModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void getTerminated(Collection<IModel> terminated)
  {
    try
    {
      _lock.lock();
      terminated.addAll(_inactiveModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void getSuspended(Collection<IModel> suspended)
  {
    try
    {
      _lock.lock();
      suspended.addAll(_suspendedModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * true if any models are currently running
   * 
   * @return
   */
  public boolean isRunning()
  {
    try
    {
      _lock.lock();
      return _activeModels.size() != 0;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * true if all the models are suspended
   * 
   * @return
   */
  public boolean isSuspended()
  {
    try
    {
      _lock.lock();
      return _suspendedModels.containsAll(_activeModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * called just before the full system starts. we use this to start up the
   * connector and onStart
   */
  public void starting()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();

    /*
     * connect to CR or local
     */
    runtime.getConnector().start();

    /*
     * and the custom start up
     */
    Runnable runnable = runtime.getOnStart();
    if (runnable != null) runnable.run();
  }

  /**
   * called after all the models have started
   */
  public void started()
  {
    /*
     * if there is any model, runtime start will be called once the first once
     * starts. If there are no models, we need to call it ourselves
     */
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    if (runtime.hasListeners() && runtime.getModels().size() == 0)
      runtime.dispatch(new ACTRRuntimeEvent(ACTRRuntimeEvent.Type.STARTED));
  }

  /**
   * called just before a model starts to execute..
   * 
   * @param model
   */
  public void starting(IModel model)
  {
    // boolean shouldFireStart = false;
    /*
     * we dont flag as started until the model is done with its set up
     */
    IModelListener actualStart = new ModelListenerAdaptor() {
      @Override
      public void modelStarted(ModelEvent event)
      {
        IModel model = event.getSource();
        actuallyStarted(model);
        /*
         * remove the listener
         */
        model.removeListener(this);
      }
    };

    model.addListener(actualStart, ExecutorServices.INLINE_EXECUTOR);

    try
    {
      _lock.lock();
      _startingModels.add(model);
      // shouldFireStart = _startingModels.size() == 1
      // && _activeModels.size() == 0;
    }
    finally
    {
      _lock.unlock();
    }

    /*
     * call the runnable and connect reality.. but we dont fire the runtime
     * started until actuallyStarted()
     */
    // if(shouldFireStart)
    // {
    // ACTRRuntime runtime = ACTRRuntime.getRuntime();
    //
    // /*
    // * connect to CR or local
    // */
    // runtime.getConnector().start();
    //
    // /*
    // * and the custom start up
    // */
    // Runnable runnable = runtime.getOnStart();
    // if (runnable != null) runnable.run();
    // }
  }

  private void actuallyStarted(IModel model)
  {
    boolean shouldFireEvent = false;
    try
    {
      _lock.lock();
      if (_startingModels.remove(model)) _activeModels.add(model);
      shouldFireEvent = _activeModels.size() == 1;
    }
    finally
    {
      _lock.unlock();
    }

    if (shouldFireEvent)
    {
      ACTRRuntime runtime = ACTRRuntime.getRuntime();
      if (runtime.hasListeners())
        runtime.dispatch(new ACTRRuntimeEvent(ACTRRuntimeEvent.Type.STARTED));
    }
  }

  // public void started(IModel model)
  // {
  // boolean shouldFireStart = false;
  //
  // /*
  // * we don't mark the runtime as actually starting until there is a model
  // * in the running state, which means it must have connected to CR and fully
  // initialized
  // * first.
  // */
  // IModelListener actualStart = new ModelListenerAdaptor() {
  // public void modelStarted(ModelEvent event)
  // {
  // IModel model = event.getSource();
  // try
  // {
  // _lock.lock();
  // if (_startingModels.remove(model)) _activeModels.add(model);
  // }
  // finally
  // {
  // _lock.unlock();
  // }
  // /*
  // * remove the listener
  // */
  // model.removeListener(this);
  // }
  // };
  //
  //
  //
  // try
  // {
  // _lock.lock();
  // _startingModels.add(model);
  // shouldFireStart = _startingModels.size() == 1;
  // }
  // finally
  // {
  // _lock.unlock();
  // }
  //
  // /*
  // * we can fire this late because nothing will have happened in the model
  // * yet. If we fire early, we run the risk of duplicate start events since we
  // * cant run from within the lock
  // */
  // if (shouldFireStart)
  // {
  // ACTRRuntime runtime = ACTRRuntime.getRuntime();
  //
  // /*
  // * connect to CR or local
  // */
  // runtime.getConnector().start();
  //
  // /*
  // * and the custom start up
  // */
  // Runnable runnable = runtime.getOnStart();
  // if (runnable != null) runnable.run();
  //
  // /*
  // * finally notify
  // */
  // if (runtime.hasListeners())
  // runtime.dispatch(new ACTRRuntimeEvent(ACTRRuntimeEvent.Type.STARTED));
  // }
  // }

  public void stopped(IModel model)
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    boolean fireStopped = false;
    try
    {
      _lock.lock();
      if (_activeModels.remove(model) || _startingModels.remove(model))
      {
        _inactiveModels.add(model);

        fireStopped = _inactiveModels.containsAll(runtime.getModels());
      }
      else if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format(
            "%s has stopped, but we have no record of it running or starting",
            model.getName()));
    }
    finally
    {
      _lock.unlock();
    }

    /*
     * last model gets to onStop and notify
     */
    if (fireStopped)
    {
      Exception deferred = null;
      /*
       * on stop..
       */
      try
      {
        if (runtime.getOnStop() != null) runtime.getOnStop().run();
      }
      catch (Exception e)
      {
        LOGGER.error("Could not fire onStop ", e);
        deferred = e;
      }

      try
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Stopping connector");
        runtime.getConnector().stop();
      }
      catch (Exception e)
      {
        LOGGER.error("Could not stop connector ", e);
        deferred = e;
      }

      if (runtime.hasListeners())
        runtime.dispatch(new ACTRRuntimeEvent(null,
            ACTRRuntimeEvent.Type.STOPPED, deferred));
    }
  }

  public void suspended(IModel model)
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    boolean fireSuspended = false;
    try
    {
      _lock.lock();
      if (_suspendedModels.add(model))
        fireSuspended = _suspendedModels.containsAll(_activeModels);
    }
    finally
    {
      _lock.unlock();
    }

    if (fireSuspended && runtime.hasListeners())
      runtime.dispatch(new ACTRRuntimeEvent(ACTRRuntimeEvent.Type.SUSPENDED));
  }

  public void resumed(IModel model)
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    boolean fireResumed = false;
    try
    {
      _lock.lock();
      if (_suspendedModels.remove(model))
        fireResumed = _suspendedModels.size() == 0;
    }
    finally
    {
      _lock.unlock();
    }

    if (fireResumed && runtime.hasListeners())
      runtime.dispatch(new ACTRRuntimeEvent(ACTRRuntimeEvent.Type.RESUMED));
  }
}
