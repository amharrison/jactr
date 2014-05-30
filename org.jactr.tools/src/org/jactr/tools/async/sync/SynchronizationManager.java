
package org.jactr.tools.async.sync;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
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

  private long                       _delay   = 180000;                                 // 3

  // minutes
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
      _runtimeListener = new IACTRRuntimeListener() {

        public void runtimeSuspended(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

        }

        public void runtimeStopped(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

        }

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
               * install our handler
               */
              ri.getIOHandler().addSentMessageHandler(
                  SynchronizationMessage.class,
                  new MessageHandler<SynchronizationMessage>() {

                    public void handleMessage(IoSession arg0,
                        SynchronizationMessage arg1) throws Exception
                    {
                      // ignore the out going
                    }
                  });

              /*
               * when we get a reply, check it against the one we sent out
               */
              ri.getIOHandler().addReceivedMessageHandler(
                  SynchronizationMessage.class,
                  new MessageHandler<SynchronizationMessage>() {

                    public void handleMessage(IoSession arg0,
                        SynchronizationMessage arg1) throws Exception
                    {
                      synchronizationPointReached(arg1);
                    }
                  });
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
                RemoteInterface.getActiveRemoteInterface().getHandler()
                    .getOwner().write(_message);
              }

            };

            //

            scheduleProcess();
          }
        }

        public void runtimeResumed(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

        }

        public void modelRemoved(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

        }

        public void modelAdded(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

        }

        public void modelStarted(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

        }

        public void modelStopped(ACTRRuntimeEvent event)
        {
          // TODO Auto-generated method stub

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
      _delay = ParameterHandler.numberInstance().coerce(value).longValue();
  }

  protected void scheduleProcess()
  {
    if (_blockProcessor != null
        && RemoteInterface.getActiveRemoteInterface() != null)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("scheduling"));
      ScheduledExecutorService ses = (ScheduledExecutorService) ExecutorServices
          .getExecutor(ExecutorServices.PERIODIC);
      if (ses != null)
      ses.schedule(_blockProcessor, _delay, TimeUnit.MILLISECONDS);
    }
  }

  protected void synchronize()
  {
    if (_message != null)
    {
      // redundant, we already are
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("We are already synchronizing"));
      return;
    }

    if (!_modelsLock.isClosed() && _modelsLock.allAreFree())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Attempting to synchronize"));
      // signal
      Future<Boolean> closeFuture = _modelsLock.close();

      // send out the message
      _message = new SynchronizationMessage();

      try
      {
        /*
         * wait until everyone has blocked and then send the message.
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Attempting to get result of close"));

        closeFuture.get(1000, TimeUnit.MILLISECONDS);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Sending message"));
        /*
         * we send the message on the back ground thread since it is the thread
         * that likely has all the pending requests on it. If we sent from here,
         * there synch message would arrive before all the pending messages on
         * the background thread, negating the intended purpose
         */
        Executor executor = ExecutorServices
            .getExecutor(ExecutorServices.BACKGROUND);
        if (executor != null) executor.execute(_messageProcessor);

      }
      catch (TimeoutException e)
      {
        LOGGER
            .error("Waiting for all models to close took too long, stumbling forward");
        _message = null;
      }
      catch (Exception e)
      {
        LOGGER
            .error(
                "SynchronizationManager.synchronize threw InterruptedException. Aborting synchronization",
                e);

        _message = null;
      }

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
