/*
 * Created on Nov 30, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.runtime.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.concurrent.GeneralThreadFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.model.six.DefaultCycleProcessor6;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.DefaultModelRunner;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;
import org.jactr.core.runtime.profile.ProfilingModelRunner;

/**
 * @author developer
 */
public class OldController //implements IController
{
  /**
   * logger definition
   */
  static private final Log               LOGGER          = LogFactory
                                                             .getLog(OldController.class);

  protected Set<IModel>                  _runningModels;

  protected Set<IModel>                  _terminatedModels;

  protected Set<IModel>                  _suspendedModels;

  protected Map<IModel, Suspender>       _suspensionState;

  protected boolean                      _suspendOnStart = false;

  protected Map<IModel, ExecutorService> _executors;

  private IModelListener                 _defaultModelListener;

  private IACTRRuntimeListener           _defaultRuntimeListener;

  protected ReentrantLock                _lock           = new ReentrantLock();

  protected Condition                    _start          = _lock.newCondition();

  protected Condition                    _stop           = _lock.newCondition();

  protected Condition                    _suspend        = _lock.newCondition();

  protected Condition                    _resume         = _lock.newCondition();

  public OldController()
  {
    _executors = new HashMap<IModel, ExecutorService>();
    _runningModels = new HashSet<IModel>();
    _terminatedModels = new HashSet<IModel>();
    _suspendedModels = new HashSet<IModel>();
    _suspensionState = new HashMap<IModel, Suspender>();
    _defaultModelListener = createModelListener();
    _defaultRuntimeListener = createRuntimeListener();
  }

  /**
   * return the model listener that we use in order to control execution
   * 
   * @return
   */
  final protected IModelListener getModelListener()
  {
    return _defaultModelListener;
  }

  /**
   * return the runtime listener that we use in order to signal the controller
   * events (awake from wait)
   * 
   * @return
   */
  final protected IACTRRuntimeListener getRuntimeListener()
  {
    return _defaultRuntimeListener;
  }

  /**
   * create the model listener that will be used to controller the model. if you
   * are subclassing this controller, and need to customize the listener, use
   * new DecoratedModelListener(super.createModelListener()); and please, oh,
   * please, make sure you call the super.XXXX() in your versions too
   * 
   * @return
   */
  protected IModelListener createModelListener()
  {
    return new ModelListenerAdaptor() {
      /**
       * if the controller has called suspend, models will block at the
       * cycleStart
       * 
       * @see org.jactr.core.model.event.ModelListenerAdaptor#cycleStarted(org.jactr.core.model.event.ModelEvent)
       */
      @Override
      public void cycleStarted(ModelEvent event)
      {
        Suspender suspender = getSuspender(event.getSource());
        if (suspender == null)
          LOGGER.warn("No model control block found for " + event.getSource());
        else
          try
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Checking to see if we should suspend");
            suspender.suspendIfNecessary();
          }
          catch (Exception e)
          {
            LOGGER.error("Could not suspend " + event.getSource(), e);
          }
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelResumed(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void modelResumed(ModelEvent event)
      {
        IModel model = event.getSource();

        if (LOGGER.isDebugEnabled()) LOGGER.debug(model + " has been resumed");

        if (!isSuspended(model))
          throw new RuntimeException(model + " isn't suspended, cannot resume");

        try
        {
          _lock.lock();
          _suspendedModels.remove(model);

          /*
           * everyone is awake.. fire the runtime resumed event
           */
          if (_suspendedModels.size() == 0)
          {
            /*
             * condition was originally if(!isSuspended()) which isn't right,
             * since isSuspended only returns true if all the models are
             * suspended, which means if one is resumed, this would have always
             * fired, resulting in a resumed event for every running model. AMH
             * 03.15.07
             */
            ACTRRuntime runtime = ACTRRuntime.getRuntime();
            if (runtime.hasListeners())
              runtime.dispatch(new ACTRRuntimeEvent(
                  ACTRRuntimeEvent.Type.RESUMED));
          }

          _resume.signalAll();
        }
        finally
        {
          _lock.unlock();
        }

      }

      /**
       * we use this to snag the model thread for this model's suspender
       * 
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelStarted(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void modelStarted(ModelEvent event)
      {
        IModel model = event.getSource();
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(model + " has been started on " + Thread.currentThread());

        try
        {
          _lock.lock();

          if (!isRunning(model))
          {
            _runningModels.add(model);
            getSuspender(model).setModelThread(Thread.currentThread());
            //make sure word gets out
            _start.signalAll();
            _stop.signalAll();
          }
        }
        finally
        {
          _lock.unlock();
        }
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelStopped(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void modelStopped(ModelEvent event)
      {
        /*
         * logic moved to disconnected so that we can be sure all events are
         * delivered before killing the executor - also it ensures that the
         * runtime doesn't hit stopped until everything is done..
         */
      }

      @Override
      public void modelDisconnected(ModelEvent event)
      {
        IModel model = event.getSource();

        if (LOGGER.isDebugEnabled()) LOGGER.debug(model + " has stopped");

        try
        {
          _lock.lock();

          _terminatedModels.add(model);
          _runningModels.remove(model);

          /*
           * remove the listener
           */
          model.removeListener(getModelListener());

          getSuspender(model).setModelThread(null);

          /*
           * this will just change the suspender state..
           */
          resumeModel(model);

          /*
           * everyone has stopped... fire the onStop and signal. I'd rather
           * these not fire on the modelThread - but there is nothing that I can
           * do about this short of starting another thread just for the
           * controller
           */
          if (!isRunning())
          {
            ACTRRuntime runtime = ACTRRuntime.getRuntime();
            if (runtime.hasListeners())
              runtime.dispatch(new ACTRRuntimeEvent(
                  ACTRRuntimeEvent.Type.STOPPED));
          }

          /*
           * and kill the model's executor
           */
          ExecutorService service = _executors.remove(model);
          if (service == null)
          {
            if (LOGGER.isWarnEnabled())
              LOGGER.warn("No valid execution service is associated with "
                  + model);
          }
          else
            destroyExecutorService(service, model);
        }
        finally
        {
          _lock.unlock();
        }
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelSuspended(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void modelSuspended(ModelEvent event)
      {
        IModel model = event.getSource();
        if (LOGGER.isDebugEnabled()) LOGGER.debug(model + " suspended");

        try
        {
          _lock.lock();
          if (!isRunning(model))
            throw new RuntimeException(
                "Cannot suspend a model that isnt running");

          _suspendedModels.add(model);

          /*
           * everyone is suspended..
           */
          if (isSuspended())
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Everyone has suspended, signalling runtime");
            ACTRRuntime runtime = ACTRRuntime.getRuntime();
            if (runtime.hasListeners())
              runtime.dispatch(new ACTRRuntimeEvent(
                  ACTRRuntimeEvent.Type.SUSPENDED));
          }

          _suspend.signalAll();
        }
        finally
        {
          _lock.unlock();
        }

      }
    };
  }

  /**
   * create the runtime listener that handles controller signalling.
   * 
   * @return
   */
  protected IACTRRuntimeListener createRuntimeListener()
  {
    return new ACTRRuntimeAdapter() {

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelRemoved(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void modelRemoved(ACTRRuntimeEvent event)
      {
        /*
         * let's be sure there is no executor
         */
        ExecutorService service = _executors.remove(event.getModel());
        if (service != null) destroyExecutorService(service, event.getModel());
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeResumed(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void runtimeResumed(ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime resumed");
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeStarted(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void runtimeStarted(ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime started");
        try
        {
          _lock.lock();
          _start.signalAll();
          _stop.signalAll();
        }
        finally
        {
          _lock.unlock();
        }
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeStopped(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void runtimeStopped(ACTRRuntimeEvent evnet)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime stopped");
        ACTRRuntime runtime = ACTRRuntime.getRuntime();
        Runnable stop = runtime.getOnStop();
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Running onStop " + stop);
        if (stop != null) stop.run();

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Stopping common reality");
        try
        {
          runtime.getConnector().stop();
        }
        catch (Exception e)
        {
          LOGGER.warn("Could not cleanly stop connector connection. ", e);
        }

        try
        {
          _lock.lock();
          _start.signalAll();
          _stop.signalAll();
        }
        finally
        {
          _lock.unlock();
        }
      }

      /**
       * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeSuspended(org.jactr.core.runtime.event.ACTRRuntimeEvent)
       */
      @Override
      public void runtimeSuspended(ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime suspended");
      }
    };
  }

  /**
   * if true, after start() is called, the models will immediately suspend
   * 
   * @param suspendImmediately
   */
  final public void setSuspendImmediately(boolean suspendImmediately)
  {
    _suspendOnStart = suspendImmediately;
  }

  /**
   * return all the models that are currently running
   * 
   * @see org.jactr.core.runtime.controller.IController#getRunningModels()
   */
  final public Collection<IModel> getRunningModels()
  {
    try
    {
      _lock.lock();
      return new ArrayList<IModel>(_runningModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * return all the models that are suspended
   * 
   * @see org.jactr.core.runtime.controller.IController#getSuspendedModels()
   */
  final public Collection<IModel> getSuspendedModels()
  {
    try
    {
      _lock.lock();
      return new ArrayList<IModel>(_suspendedModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * return all the models that have termianted
   * 
   * @see org.jactr.core.runtime.controller.IController#getTerminatedModels()
   */
  final public Collection<IModel> getTerminatedModels()
  {
    try
    {
      _lock.lock();
      return new ArrayList<IModel>(_terminatedModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * return the suspender which is responsible for flaging a model for
   * suspension, doing it and propogating the events.
   * 
   * @param model
   * @return
   */
  final protected Suspender getSuspender(IModel model)
  {
    try
    {
      _lock.lock();
      return _suspensionState.get(model);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#isRunning()
   */
  final public boolean isRunning()
  {
    try
    {
      _lock.lock();
      return _runningModels.size() != 0;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#isSuspended()
   */
  final public boolean isSuspended()
  {
    try
    {
      _lock.lock();
      return isRunning() && _suspendedModels.containsAll(_runningModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * reset a competed runtime to its prefrun state
   * 
   * @see org.jactr.core.runtime.controller.IController#reset()
   */
  final public void reset()
  {
    if (isRunning()) try
    {
      waitForCompletion();
    }
    catch (InterruptedException ie)
    {
      LOGGER.error("interrupted while waiting for completion", ie);
    }

    try
    {
      _lock.lock();
      _runningModels.clear();
      _terminatedModels.clear();
      _suspendedModels.clear();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * request that a suspended runtime resume
   * 
   * @see org.jactr.core.runtime.controller.IController#resume()
   */
  final public void resume()
  {
    if (!isRunning())
      throw new RuntimeException(
          "Cannot resume a runtime that isnt even running");

    /*
     * note: this is resuming any models - whether they have been suspended or
     * not. We could be more strict and only resume suspended models, but it
     * doesn't really make a difference given the way Suspender works
     */
    try
    {
      _lock.lock();
      for (IModel model : _runningModels)
        resumeModel(model);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * attach this controller to the runtime. called by ACRRuntime.setController()
   * If you override, please call super.attach()
   * 
   * @see org.jactr.core.runtime.controller.IController#attach()
   */
  public void attach()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    if (runtime.getController() == this)
    {
      if (LOGGER.isWarnEnabled()) LOGGER.warn("already connected to runtime");
      return;
    }

    IACTRRuntimeListener listener = getRuntimeListener();

    runtime.addListener(listener, ExecutorServices.INLINE_EXECUTOR);

    // we may have missed the addModel callbacks..
    for (IModel model : runtime.getModels())
      listener.modelAdded(new ACTRRuntimeEvent(model,
          ACTRRuntimeEvent.Type.MODEL_ADDED));
  }

  /**
   * detach from the runtime. if you override, please call super.detach()
   * 
   * @see org.jactr.core.runtime.controller.IController#detach()
   */
  public void detach()
  {
    if (isRunning())
      throw new RuntimeException(
          "Cannot detach until the models complete their runs");

    /*
     * remove our listener
     */
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.removeListener(getRuntimeListener());

    /*
     * the model listener will be removed when the model stops
     */
    reset();
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#start()
   */
  final public void start()
  {
    start(_suspendOnStart);
  }

  /**
   * this will fire the onStart if present, signal the start of the runtime and
   * then start the models
   * 
   * @see org.jactr.core.runtime.controller.IController#start(boolean)
   */
  final public void start(boolean suspendImmediately)
  {
    if (isRunning())
      throw new RuntimeException("Runtime is currently running");

    ACTRRuntime runtime = ACTRRuntime.getRuntime();

    /*
     * start common reality if necessary
     */
    runtime.getConnector().start();

    /*
     * fire the onStart. we do this outside of the lock in case the onStart
     * needs to access controller
     */
    Runnable start = runtime.getOnStart();
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Running onStart" + start);
    if (start != null) start.run();

    /*
     * fire the runtime event. outside of the lock in case asynch listeners will
     * access the controller
     */
    if (runtime.hasListeners())
      runtime.dispatch(new ACTRRuntimeEvent(ACTRRuntimeEvent.Type.STARTED));

    try
    {
      _lock.lock();

      setSuspendImmediately(suspendImmediately);

      for (IModel model : runtime.getModels())
        startModel(model, _suspendOnStart);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * request that everyone stop.
   * 
   * @see org.jactr.core.runtime.controller.IController#stop()
   */
  final public void stop()
  {
    try
    {
      _lock.lock();
      if (!isRunning()) return;

      /*
       * request that everyone quit..
       */
      for (IModel model : _runningModels)
        stopModel(model);
    }
    finally
    {
      _lock.unlock();
    }
  }
  
  public Future<Boolean> terminate()
  {
    if (LOGGER.isWarnEnabled()) LOGGER.warn("NO OP");
    FutureTask<Boolean> rtn = new FutureTask<Boolean>(new Callable<Boolean>(){

      public Boolean call() throws Exception
      {
        try
        {
           
          return true;
        }
        catch(Exception e)
        {
          LOGGER.error("Failed to terminate runtime correctly ",e);
          return false;
        }
      }
    });
    
    rtn.run();
    return rtn;
  }

  /**
   * request that everyone suspend
   * 
   * @see org.jactr.core.runtime.controller.IController#suspend()
   */
  final public void suspend()
  {
    try
    {
      _lock.lock();
      /*
       * request that everyone suspend
       */
      for (IModel model : _runningModels)
        suspendModel(model);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#waitForCompletion()
   */
  final public void waitForCompletion() throws InterruptedException
  {
    try
    {
      _lock.lock();
      while (isRunning())
        _stop.await();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#waitForResumption()
   */
  final public void waitForResumption() throws InterruptedException
  {
    try
    {
      _lock.lock();
      while (isRunning() && isSuspended())
        _resume.await();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#waitForSuspension()
   */
  final public void waitForSuspension() throws InterruptedException
  {
    try
    {
      _lock.lock();
      while (isRunning() && !isSuspended())
        _suspend.await();
    }
    finally
    {
      _lock.unlock();
    }
  }

  final public void waitForStart() throws InterruptedException
  {
    try
    {
      _lock.lock();
      /*
       * this is a tad complicated since a model might terminate before wait for
       * Start is even called..
       */
      HashSet<IModel> runningOrTerminated = new HashSet<IModel>(_runningModels);
      runningOrTerminated.addAll(_terminatedModels);
      while (!runningOrTerminated.containsAll(ACTRRuntime.getRuntime()
          .getModels()))
      {
        _start.await();
        // update the contents
        runningOrTerminated.addAll(_runningModels);
        runningOrTerminated.addAll(_terminatedModels);
      }
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * override to provide ProfilingModelRunner if you'd like to track performance
   * stats
   * 
   * @param model
   * @param service
   * @return
   */
  protected Runnable createModelRunner(IModel model, ExecutorService service)
  {
    if (Boolean.getBoolean("jactr.profiling"))
      return new ProfilingModelRunner(service, model,
          new DefaultCycleProcessor6());

    return new DefaultModelRunner(service, model, new DefaultCycleProcessor6());
  }

  /**
   * if you override instantiateModelRunner and need to clean up said runner,
   * clean up here
   * 
   * @param runner
   * @param model
   */
  protected void destroyModelRunner(Runnable runner, IModel model)
  {
    if (runner instanceof ProfilingModelRunner)
    {
      ProfilingModelRunner prm = (ProfilingModelRunner) runner;

      StringBuilder sb = new StringBuilder("Profile Stats for ");
      sb.append(model.getName()).append("\n");
      sb.append(" Total actual processing cycles \t").append(
          prm.getTotalCycles()).append("\n");
      sb.append(" Simulated processing cycles \t\t").append(model.getCycle())
          .append("\n");
      sb.append(" Total actual time \t\t\t").append(prm.getTotalCycleTime())
          .append("s\n");
      sb.append(" Simulate time \t\t\t\t").append(prm.getSimulatedTime())
          .append("s\n");
      sb.append(" Average sleep time (wait for clock) \t").append(
          prm.getActualWaitTime() / prm.getTotalCycles() * 1000d)
          .append("ms\n");
      sb.append(" Average time processing events \t").append(
          prm.getActualEventTime() / prm.getTotalCycles() * 1000d).append(
          "ms\n");
      sb.append(" Average production cycle time \t\t").append(
          prm.getActualCycleTime() / prm.getTotalCycles() * 1000d).append(
          "ms\n");
      sb.append(" Average production time + waits \t").append(
          prm.getTotalCycleTime() / prm.getTotalCycles() * 1000d)
          .append("ms\n");
      sb.append(" Realtime factor \t\t\t").append(prm.getRealTimeFactor())
          .append(" X \n");

      System.out.println(sb.toString());
      System.out.flush();
    }
  }

  /**
   * create the executor service that will run the model. The service is created
   * and then added to ExecutorServices under model.getName(). Note, that model
   * threads must be run on ITimeDependentThreads. If you need a thread factory,
   * check ExecutorServices.TIME_DEPENDENT_THREAD_FACTORY
   * 
   * @param model
   * @return
   */
  protected ExecutorService createExecutorService(IModel model)
  {
    ExecutorService service = Executors
        .newSingleThreadExecutor(new GeneralThreadFactory(model.getName()));
    ExecutorServices.addExecutor(model.getName(), service);
    return service;
  }

  /**
   * destory the executor service.
   * 
   * @param service
   * @param model
   */
  protected void destroyExecutorService(ExecutorService service, IModel model)
  {
    if (!service.isShutdown()) service.shutdown();

    if (service instanceof ThreadPoolExecutor)
    {
      ThreadFactory factory = ((ThreadPoolExecutor) service).getThreadFactory();
      if (factory instanceof GeneralThreadFactory)
        ((GeneralThreadFactory) factory).dispose();
    }

    ExecutorServices.removeExecutor(model.getName());
  }

  /**
   * actually start the model
   * 
   * @param model
   * @param suspendOnStart
   */
  final protected void startModel(final IModel model, boolean suspendOnStart)
  {
    try
    {
      _lock.lock();

      /*
       * attach the model listener that lets us block at the start of a cycle..
       */
      model.addListener(getModelListener(), ExecutorServices.INLINE_EXECUTOR);

      /*
       * create the executor on which the model will run
       */
      ExecutorService executor = createExecutorService(model);
      _executors.put(model, executor);

      /*
       * create the suspender that will control the model execution
       */
      Suspender suspender = new Suspender(model);
      suspender.setShouldSuspend(suspendOnStart);
      _suspensionState.put(model, suspender);

      /*
       * now the actual runnable
       */
      final Runnable modelRunner = createModelRunner(model, executor);

      Runnable actual = new Runnable() {
        public void run()
        {
          try
          {
            modelRunner.run();
          }
          finally
          {
            destroyModelRunner(modelRunner, model);
          }
        }
      };

      /*
       * and start the beatch up.
       */
      executor.execute(actual);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * force the model to stop
   */
  final protected void stopModel(IModel model)
  {
    ExecutorService executor = _executors.get(model);
    if (executor != null)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Shuting down " + model);
      executor.shutdown();
    }
    /*
     * wake it up just in case
     */
    resumeModel(model);
  }

  /**
   * is this model running?
   * 
   * @param model
   * @return
   */
  final protected boolean isRunning(IModel model)
  {
    try
    {
      _lock.lock();
      return _runningModels.contains(model);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * is this model suspended
   * 
   * @param model
   * @return
   */
  final protected boolean isSuspended(IModel model)
  {
    try
    {
      _lock.lock();
      return _suspendedModels.contains(model);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * request that this model be suspended
   * 
   * @param model
   */
  final protected void suspendModel(IModel model)
  {
    Suspender suspender = getSuspender(model);
    if (suspender == null)
      LOGGER.warn("Could not find model control block for " + model
          + ", ignoring suspension request");
    else
      suspender.setShouldSuspend(Boolean.TRUE);
  }

  /**
   * request that this model resume
   */
  final protected void resumeModel(IModel model)
  {
    Suspender suspender = getSuspender(model);
    if (suspender == null)
      LOGGER.warn("Could not find model control block for " + model
          + ", ignoring resumption request");
    else
      suspender.setShouldSuspend(Boolean.FALSE);
  }

  /**
   * actual class that will block a model's execution. It is also responsible
   * for firing the modelSuspend/resume events
   * 
   * @author developer
   */
  static protected class Suspender
  {
    private Lock      _lock;

    private boolean   _shouldSuspend;

    private Condition _suspend;

    private IModel    _model;

    private Thread    _thread;

    public Suspender(IModel model)
    {
      _lock = new ReentrantLock();
      _model = model;
      _suspend = _lock.newCondition();
    }

    public Thread getModelThread()
    {
      return _thread;
    }

    protected void setModelThread(Thread thread)
    {
      _thread = thread;
    }

    public void setShouldSuspend(boolean suspend)
    {
      try
      {
        _lock.lock();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Marking " + _model + " should suspend : " + suspend);
        _shouldSuspend = suspend;
        _suspend.signalAll();
      }
      finally
      {
        _lock.unlock();
      }
    }

    public boolean willSuspend()
    {
      try
      {
        _lock.lock();
        return _shouldSuspend;
      }
      finally
      {
        _lock.unlock();
      }
    }

    public void suspendIfNecessary() throws InterruptedException
    {
      boolean wasSuspended = false;

      if (Thread.currentThread() != getModelThread())
        throw new RuntimeException(
            "Cannot suspend current thread since it is not the model thread "
                + getModelThread());

      try
      {
        _lock.lock();

        if (willSuspend())
          _model.dispatch(new ModelEvent(_model, ModelEvent.Type.SUSPENDED));

        while (willSuspend())
        {
          wasSuspended = true;
          _suspend.await();
        }
      }
      finally
      {
        if (wasSuspended)
          _model.dispatch(new ModelEvent(_model, ModelEvent.Type.RESUMED));

        _lock.unlock();
      }
    }
  }

}
