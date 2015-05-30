package org.jactr.tools.async.sync;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.handler.IMessageHandler;
import org.commonreality.net.session.ISessionInfo;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.instrument.IInstrument;
import org.jactr.tools.async.controller.RemoteInterface;
import org.jactr.tools.misc.ModelsLock;

/**
 * The wonder of asynchronous messaging is that you don't have to wait, giving
 * the runtime greater throughput. Unfortunately, it can get too fast - drowning
 * the other side. This allows us to specify the frequency of synchronizations.
 * If we could detect load, it'd be even better.. <br/>
 * 
 * @bug we should time out for waiting for the synchronization reply.
 * @author harrison
 */
public class SynchronizationManager implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(SynchronizationManager.class);

  // static public final String SYNC_AT_START = "SynchronizeOnStartUp";

  static public final String         INTERVAL = "SynchronizationDelayInMS";

  private ModelsLock                 _modelsLock;

  private double                     _delay   = 30;                                     // s

  private Runnable                   _blockProcessor;

  private Runnable                   _messageProcessor;

  private SynchronizationMessage     _message;

  private IACTRRuntimeListener       _runtimeListener;

  public SynchronizationManager()
  {
    _modelsLock = new ModelsLock();
    _modelsLock.initialize();
  }

  synchronized public void initialize()
  {
    if (_runtimeListener == null)
    {
      _runtimeListener = new ACTRRuntimeAdapter() {

        @Override
        public void runtimeStarted(ACTRRuntimeEvent event)
        {
          // do the real work
          if (_blockProcessor == null && _messageProcessor == null)
          {
            /*
             * try the interface, which we need for catching the sync message
             */
            RemoteInterface ri = RemoteInterface.getActiveRemoteInterface();

            if (ri == null)
              LOGGER
                  .warn(String
                      .format(
                          "%s requires the RemoteInterface to be running, will not attempt to synchronize",
                          getClass().getName()));
            else
            {
              /*
               * install our handler into the active session (if connected) or
               * the default handler set
               */
              IMessageHandler<SynchronizationMessage> handler = (s, m) -> synchronizationPointReached(m);

              ISessionInfo session = ri.getActiveSession();
              if (session != null)
                session.addHandler(SynchronizationMessage.class, handler);
              else
                ri.getDefaultHandlers().put(SynchronizationMessage.class,
                    handler);
            }

            _blockProcessor = new Runnable() {

              public void run()
              {
                synchronize();
              }

            };

            _messageProcessor = new Runnable() {

              public void run()
              {
                try
                {
                  RemoteInterface.getActiveRemoteInterface().getActiveSession()
                      .write(_message);
                }
                catch (Exception e)
                {
                  // TODO Auto-generated catch block
                  LOGGER.error(".run threw Exception : ", e);
                }
              }

            };

            //

            scheduleProcess();
          }
        }

      };
      ACTRRuntime.getRuntime().addListener(_runtimeListener, null);
    }

  }

  public void install(IModel model)
  {
    _modelsLock.install(model);
  }

  public void uninstall(IModel model)
  {
    // this will stop the processor from rescheduling
    _blockProcessor = null;
    ACTRRuntime.getRuntime().removeListener(_runtimeListener);
    _modelsLock.uninstall(model);
  }

  public String getParameter(String key)
  {
    // meh, too lazy.
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(/* SYNC_AT_START, */INTERVAL);
  }

  public void setParameter(String key, String value)
  {
    // if (SYNC_AT_START.equalsIgnoreCase(key))
    // _syncAtStart = ParameterHandler.booleanInstance().coerce(value)
    // .booleanValue();
    // else
    if (INTERVAL.equalsIgnoreCase(key))
      _delay = ParameterHandler.numberInstance().coerce(value).doubleValue() / 1000.0;
  }

  protected void scheduleProcess()
  {
    if (_blockProcessor != null
        && RemoteInterface.getActiveRemoteInterface() != null)
      /*
       * now we just pick a model and attach to it. If that model is removed, we
       * reschedule
       */
      try
      {
        IModel model = ACTRRuntime.getRuntime().getController()
            .getRunningModels().iterator().next();
        double when = model.getAge() + _delay;
        RunnableTimedEvent rte = new RunnableTimedEvent(when, _blockProcessor);
        model.getTimedEventQueue().enqueue(rte);
      }
      catch (Exception e)
      {
        LOGGER.debug("No model to attach to, irrelevant ", e);
      }
  }

  synchronized protected void synchronize()
  {
    if (_message != null)
    {
      // message is nulled when we wake back up?
      // redundant, we already are
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("We are already synchronizing"));
      return;
    }

    if (!_modelsLock.isClosed() && _modelsLock.areAllFree())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Attempting to synchronize"));
      // signal
      CompletableFuture<Boolean> closeFuture = _modelsLock.close();

      // send out the message when all have reached the synch point.
      _message = new SynchronizationMessage();

      closeFuture.thenRunAsync(_messageProcessor,
          ExecutorServices.getExecutor(ExecutorServices.BACKGROUND));

      // try
      // {
      // /*
      // * wait until everyone has blocked and then send the message.
      // */
      // if (LOGGER.isDebugEnabled())
      // LOGGER.debug(String.format("Attempting to get result of close"));
      //
      // closeFuture.get(1000, TimeUnit.MILLISECONDS);
      //
      // if (LOGGER.isDebugEnabled())
      // LOGGER.debug(String.format("Sending message"));
      // /*
      // * we send the message on the back ground thread since it is the thread
      // * that likely has all the pending requests on it. If we sent from here,
      // * there synch message would arrive before all the pending messages on
      // * the background thread, negating the intended purpose
      // */
      // Executor executor = ExecutorServices
      // .getExecutor(ExecutorServices.BACKGROUND);
      // if (executor != null) executor.execute(_messageProcessor);
      //
      // }
      // catch (TimeoutException e)
      // {
      // LOGGER
      // .error("Waiting for all models to close took too long, stumbling forward");
      // _message = null;
      // }
      // catch (Exception e)
      // {
      // LOGGER
      // .error(
      // "SynchronizationManager.synchronize threw InterruptedException. Aborting synchronization",
      // e);
      //
      // _message = null;
      // }

    }
    else
    {
      /*
       * some are blocked? that's weird, lets reschedule
       */
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(String
                .format("Deferring synchronization request until all models are free"));

      scheduleProcess();
    }

  }

  protected void synchronizationPointReached(SynchronizationMessage reply)
  {
    if (_message == null)
    {
      LOGGER.warn("Reply received, but we didn't send the request. WTF?");
      return;
    }

    if (reply.inResponseTo() != _message.getID())
    {
      LOGGER.warn("WTF? reply was not to recently sent request.");
      return;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(
          "Synchronized, releasing the hounds. Synchronization took %d ms",
          reply.getTimestamp() - _message.getTimestamp()));

    _modelsLock.open();

    _message = null;

    /*
     * and once more..
     */
    scheduleProcess();
  }
}
