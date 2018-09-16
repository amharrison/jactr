package org.jactr.core.runtime.controller;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.concurrent.GeneralThreadFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.DefaultModelRunner;
import org.jactr.core.runtime.controller.impl.ModelListener;
import org.jactr.core.runtime.controller.impl.RuntimeListener;
import org.jactr.core.runtime.controller.impl.RuntimeState;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.profile.ProfilingModelRunner;

public class DefaultController implements IController
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER         = LogFactory
                                                        .getLog(DefaultController.class);

  final private RuntimeState         _state;

  final private RuntimeListener      _runtimeListener;

  final private ModelListener        _modelListener;

  final protected Lock               _lock          = new ReentrantLock();

  final private Set<Thread>          _activeThreads = new HashSet<Thread>();

  final private Set<ExecutorService> _executors     = new HashSet<ExecutorService>();

  public DefaultController()
  {
    _state = new RuntimeState();
    _runtimeListener = new RuntimeListener(_state) {
      @Override
      public void modelAdded(ACTRRuntimeEvent event)
      {
        super.modelAdded(event);
        if (isRunning())
          startModel(event.getModel());
      }

      @Override
      public void modelRemoved(ACTRRuntimeEvent event)
      {
        super.modelRemoved(event);
      }

      @Override
      public void modelStopped(ACTRRuntimeEvent event)
      {
        IModel model = event.getModel();
        model.removeListener(_modelListener);

        try
        {
          _lock.lock();
          _activeThreads.remove(Thread.currentThread());
        }
        finally
        {
          _lock.unlock();
        }

        _state.stopped(model);
      }

    };
    _modelListener = new ModelListener(_state);
  }

  public void attach()
  {
    ACTRRuntime.getRuntime().addListener(_runtimeListener,
        ExecutorServices.INLINE_EXECUTOR);
  }

  public void detach()
  {
    ACTRRuntime.getRuntime().removeListener(_runtimeListener);
  }

  public Collection<IModel> getRunningModels()
  {
    ArrayList<IModel> models = new ArrayList<IModel>(2);
    _state.getRunning(models);
    return models;
  }

  public Collection<IModel> getSuspendedModels()
  {
    ArrayList<IModel> models = new ArrayList<IModel>(2);
    _state.getSuspended(models);
    return models;
  }

  public Collection<IModel> getTerminatedModels()
  {
    ArrayList<IModel> models = new ArrayList<IModel>(2);
    _state.getTerminated(models);
    return models;
  }

  public boolean isRunning()
  {
    return _state.isRunning();
  }

  public boolean isSuspended()
  {
    return _state.isSuspended();
  }

  public Future<Boolean> start()
  {
    return start(false);
  }

  public Future<Boolean> start(boolean suspendImmediately)
  {
    if (_state.isRunning()) return _runtimeListener.getStartFuture();

    ArrayList<IModel> models = new ArrayList<IModel>(ACTRRuntime.getRuntime()
        .getModels());

    if (models.size() == 0 && LOGGER.isWarnEnabled())
      LOGGER.warn("No models to execute");

    try
    {
      _lock.lock();
      _state.clear();
      _modelListener.setShouldSuspend(suspendImmediately);
    }
    finally
    {
      _lock.unlock();
    }

    /**
     * bring up the connector and fire onStart
     */
    _state.starting();

    for (IModel model : models)
      startModel(model);

    _state.started();

    return _runtimeListener.getStartFuture();
  }

  protected void startModel(final IModel model)
  {
    final ExecutorService service = createExecutorService(model);
    final Runnable runner = createModelRunnable(model, service);
    Runnable actual = new Runnable() {
      public void run()
      {
        ACTRRuntime runtime = ACTRRuntime.getRuntime();
        try
        {
          _lock.lock();
          _activeThreads.add(Thread.currentThread());
        }
        finally
        {
          _lock.unlock();
        }

        _state.starting(model);

        try
        {
          model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

          // if (runtime.hasListeners())
          // runtime.dispatch(new ACTRRuntimeEvent(model,
          // ACTRRuntimeEvent.Type.MODEL_STARTED, null));
          //
          // we rely upon the model runner to fire that first event.

          runner.run();
        }
        finally
        {
          if (runtime.hasListeners())
            runtime.dispatch(new ACTRRuntimeEvent(model,
                ACTRRuntimeEvent.Type.MODEL_STOPPED, null));

          destroyModelRunnable(model, runner);
          destroyExecutorService(model, service);
          model.removeListener(_modelListener);
        }
      }
    };

    /*
     * actually start it up
     */
    service.execute(actual);
  }

  public Future<Boolean> complete()
  {
    return _runtimeListener.getStopFuture();
  }

  public Future<Boolean> stop()
  {
    if (!_state.isRunning()) return _runtimeListener.getStopFuture();

    try
    {
      _lock.lock();

      for (ExecutorService service : _executors)
        service.shutdown();
    }
    finally
    {
      _lock.unlock();
    }

    /*
     * wake them up..
     */
    _modelListener.setShouldSuspend(false);
    return _runtimeListener.getStopFuture();
  }

  /**
   * provides access to suspension mechanism. this is here so that extenders can
   * use the suspend mechanism in the debug controller
   */
  protected void suspendLocally(IModel model)
  {
    _modelListener.suspendModel(model);
  }

  public Future<Boolean> suspend()
  {
    if (_state.isRunning() && !_state.isSuspended())
      _modelListener.setShouldSuspend(true);
    return _runtimeListener.getSuspendFuture();
  }

  public Future<Boolean> resume()
  {
    if (_state.isRunning() && _state.isSuspended())
      _modelListener.setShouldSuspend(false);
    return _runtimeListener.getResumeFuture();
  }

  public Future<Boolean> terminate()
  {
    stop();

    try
    {
      _lock.lock();

      for (ExecutorService service : _executors)
        service.shutdownNow();
      /*
       * and just in case, let's interrupt
       */
      for (Thread thread : _activeThreads)
        thread.interrupt();
    }
    finally
    {
      _activeThreads.clear();
      _executors.clear();
      _lock.unlock();
    }

    return _runtimeListener.getStopFuture();
  }

  protected Runnable createModelRunnable(IModel model, ExecutorService service)
  {
    if (Boolean.getBoolean("jactr.profiling"))
      return new ProfilingModelRunner(service, model, model.getCycleProcessor());

    return new DefaultModelRunner(service, model, model.getCycleProcessor());
  }

  protected void destroyModelRunnable(IModel model, Runnable runnable)
  {
    if (runnable instanceof ProfilingModelRunner)
    {
      ProfilingModelRunner prm = (ProfilingModelRunner) runnable;

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

      LOGGER.debug(String.format("Profiling stats for %s", model.getName()));
      LOGGER.debug(String.format("Simulated processing cycles\t%d",
          model.getCycle()));
      LOGGER.debug(String.format("Total real time\t\t%.4fs",
          prm.getTotalCycleTime()));
      LOGGER.debug(String.format("Simulated time\t\t%.4fs",
          prm.getSimulatedTime()));
      LOGGER.debug(String.format("Avg sleep time (clock)\t%.4fms",
          prm.getActualWaitTime() / prm.getTotalCycles() * 1000d));
      LOGGER.debug(String.format("Avg event processing time\t%.4fms",
          prm.getActualEventTime() / prm.getTotalCycles() * 1000d));
      LOGGER.debug(String.format("Avg production cycle time\t%.4fms",
          prm.getActualCycleTime() / prm.getTotalCycles() * 1000d));
      LOGGER.debug(String.format("Avg production time + waits\t%.4fms",
          prm.getTotalCycleTime() / prm.getTotalCycles() * 1000d));
      LOGGER.debug(String.format("Realtime factor\t\t\t%.2fx",
          prm.getRealTimeFactor()));
    }
  }

  private ExecutorService createExecutorService(IModel model)
  {
    ExecutorService service = Executors
        .newSingleThreadExecutor(new GeneralThreadFactory(model.getName()));

    _executors.add(service);
    ExecutorServices.addExecutor(model.getName(), service);

    return service;
  }

  /**
   * do not wait or trigger an immediate shutdown of the executor as it make
   * sure you call super if overriding
   * 
   * @param model
   * @param service
   */
  private void destroyExecutorService(IModel model, ExecutorService service)
  {
    if (!service.isShutdown()) service.shutdown();

    if (service instanceof ThreadPoolExecutor)
    {
      ThreadFactory factory = ((ThreadPoolExecutor) service).getThreadFactory();
      if (factory instanceof GeneralThreadFactory)
        ((GeneralThreadFactory) factory).dispose();
    }
    _executors.remove(service);
    ExecutorServices.removeExecutor(model.getName());
  }

  public Future<Boolean> waitForCompletion()
  {
    return _runtimeListener.getStopFuture();
  }

  public Future<Boolean> waitForResumption()
  {
    return _runtimeListener.getResumeFuture();
  }

  public Future<Boolean> waitForStart()
  {
    return _runtimeListener.getStartFuture();
  }

  public Future<Boolean> waitForSuspension()
  {
    return _runtimeListener.getSuspendFuture();
  }
}
