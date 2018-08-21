/*
 * Created on Oct 21, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IAuthoritativeClock;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.BasicClock;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.ICycleProcessor;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.utils.Diagnostics;

/**
 * basic model runner, handles all events except disconnected which will be
 * handled by the controller
 * 
 * @author developer
 */
public class DefaultModelRunner implements Runnable
{
  /**
   * logger definition
   */
  static private final Log  LOGGER = LogFactory
                                       .getLog(DefaultModelRunner.class);

  static boolean            _enableTimeDiagnostics = Boolean
                                                       .getBoolean("jactr.enableTimeDiagnostics");

  protected ExecutorService _service;

  protected BasicModel      _model;

  protected ICycleProcessor _cycleRunner;

  private NumberFormat      _format;

  private boolean           _firstRun              = true;

  public DefaultModelRunner(ExecutorService service, IModel model,
      ICycleProcessor cycleRunner)
  {
    if (!(model instanceof BasicModel))
      throw new RuntimeException(getClass().getSimpleName()
          + " only supports subclasses of " + BasicModel.class.getName());
    _model = (BasicModel) model;
    _cycleRunner = cycleRunner;
    _service = service;

    _format = NumberFormat.getNumberInstance();
    _format.setMaximumFractionDigits(3);
    _format.setMinimumFractionDigits(3);
  }

  protected void startUp()
  {

    /**
     * this will connect, and once it has gotten the go ahead to initialize from
     * common reality, it will call model.initialize(). we then block until it
     * gets the start command
     */
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.getConnector().connect(_model);

    IClock clock = runtime.getClock(_model);

    double timeShift = _model.getAge() - clock.getTime();

    Optional<IAuthoritativeClock> auth = clock.getAuthority();
    if (auth.isPresent())
    {
      auth.get().setLocalTimeShift(timeShift);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Shifted clock to " + timeShift);
    }
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format("Cannot set time shift"));

    /**
     * let anyone who depends on commonreality know that we have connected
     */
    _model.dispatch(new ModelEvent(_model, ModelEvent.Type.CONNECTED));

    _cycleRunner.initialize(_model);

    if (runtime.hasListeners())
      runtime.dispatch(new ACTRRuntimeEvent(_model,
          ACTRRuntimeEvent.Type.MODEL_STARTED, null));
  }

  protected void shutDown(Exception deferred)
  {

    try
    {
      _model
          .dispatch(new ModelEvent(_model, ModelEvent.Type.STOPPED, deferred));

      double age = ACTRRuntime.getRuntime().getClock(_model).getTime();
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Set model age to " + age);
      _model.setAge(age);

      ACTRRuntime.getRuntime().getConnector().disconnect(_model);
      Thread.currentThread().setName("dead-" + _model.getName());
    }
    catch (Exception e)
    {
      deferred = e;
    }
    finally
    {
      _model.dispatch(new ModelEvent(_model, ModelEvent.Type.DISCONNECTED,
          deferred));
    }
  }

  protected void preEventFiring()
  {

  }

  protected boolean firePendingEvents(double now)
  {
    /*
     * fire all the events that may have transpired by now
     */
    TimedEventQueue queue = _model.getTimedEventQueue();
    boolean anyFired = false;
    while (queue.fireExpiredEvents(now))
      anyFired = true;
    return anyFired;
  }

  protected void postEventFiring()
  {

  }

  /**
   * run a single cycle of the model
   * 
   * @return
   */
  protected double cycle(boolean eventsHaveFired)
  {
    // make sure we're within the precision bounds bounds.
    return _cycleRunner.cycle(_model, eventsHaveFired);
  }

  /**
   * wait for the clock to reach this time
   * 
   * @param waitForTime
   * @throws InterruptedException
   */
  protected double waitForClock(double waitForTime)
      throws InterruptedException, ExecutionException
  {
    if (Double.isNaN(waitForTime))
    {
      LOGGER
          .error("Requested NaN? This should not happen unless we have no time control.");
      return 0;
    }

    IClock clock = ACTRRuntime.getRuntime().getClock(_model);
    double now = clock.getTime();
    if (waitForTime <= now && !_firstRun)
    {
      LOGGER
          .warn(String
              .format(
                  "WARNING: Time discrepancy detected. Clock regression requested"
                      + ": %.10f(desired) < %.10f(current). Should be >=. Incrementing request by 0.05",
                  waitForTime, now));

      waitForTime = now + 0.05;

      if (_enableTimeDiagnostics) Diagnostics.timeSanityCheck(waitForTime);
    }
    _firstRun = false;

    waitForTime = BasicClock.constrainPrecision(waitForTime);
    Optional<IAuthoritativeClock> auth = clock.getAuthority();

    double rtn = waitForTime;
    if (auth.isPresent())
      rtn = auth.get().requestAndWaitForTime(waitForTime, _model).get();
    else
    {
      LOGGER.warn("Cannot fully participant in time control ");
      rtn = clock.waitForTime(waitForTime).get();
    }

    if (_enableTimeDiagnostics) Diagnostics.timeSanityCheck(rtn);

    // double rtn = ACTRRuntime.getRuntime().getClock(_model)
    // .waitForTime(waitForTime).get();

    if (rtn < waitForTime)
    {
      LOGGER
          .error(String
              .format(
                  "WARNING: Time discrepancy detected. Clock regression : %.10f(returned) < %.10f(desired). Should be >=",
                  rtn, waitForTime));
      rtn = Math.nextAfter(rtn, 1);
    }

    return rtn;
  }

  /**
   * called before blocking on waitForClock
   */
  protected void preClock()
  {

  }

  /**
   * called after waitForClock returns
   */
  protected void postClock(double currentSimulatedTime)
  {

  }

  /**
   * called before each cycle starts
   */
  protected void preCycle(double currentSimulatedTime)
  {

  }

  /**
   * called after each cycle
   */
  protected void postCycle(double nextTime)
  {

  }

  /**
   * run the model in loop
   * 
   * @param clock
   */
  protected void runModelLoop()
  {
    try
    {
      double startTime = waitForClock(_model.getAge());
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Starting @ " + startTime + " age:" + _model.getAge());

      /*
       * signal the start to the runtime and the model
       */
      _model.dispatch(new ModelEvent(_model, ModelEvent.Type.STARTED));

      double nextTime = startTime;

      while (!_service.isShutdown() && !Double.isNaN(nextTime))
      {
        /*
         * fire the time event
         */
        if (Logger.hasLoggers(_model))
          Logger.log(_model, Logger.Stream.TIME, _format.format(nextTime));

        preEventFiring();

        boolean eventsHaveFired = firePendingEvents(nextTime);

        postEventFiring();

        preCycle(nextTime);

        if (LOGGER.isDebugEnabled()) LOGGER.debug("set age to " + nextTime);
        _model.setAge(nextTime);

        double priorTime = nextTime;
        nextTime = cycle(eventsHaveFired);

        if (nextTime <= priorTime)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER
                .warn(String
                    .format(
                        "WARNING: Time discrepancy detected. Cycle time error : %.6f(next) <= %.6f(prior). Should be >. Incrementing",
                        nextTime, priorTime));

          if (_enableTimeDiagnostics) Diagnostics.timeSanityCheck(nextTime);

          // nextTime = priorTime + 0.001;
        }

        postCycle(priorTime);

        if (!Double.isNaN(nextTime))
        {
          preClock();
          nextTime = waitForClock(nextTime);
          postClock(nextTime);
        }
        else
          postClock(Double.NaN);
      }
    }
    catch (ModelTerminatedException mte)
    {
      LOGGER.debug("Model threw termination");
      // perfectly normal
      // thi
    }
    catch (InterruptedException ie)
    {
      // perfectly normal
      LOGGER.warn("Interrupted, assuming termination ", ie);
    }
    catch (ExecutionException ee)
    {
      LOGGER.error("Execution exception, terminating ", ee);
    }
    catch (Exception e)
    {
      LOGGER.error("Unknown exception, terminating ", e);
    }
    finally
    {
      /*
       * pop all the buffer contents
       */
      for (IActivationBuffer buffer : _model.getActivationBuffers())
        buffer.clear();
    }
  }

  public void run()
  {
    Exception deferred = null;
    try
    {
      startUp();
      runModelLoop();
    }
    catch (Exception e)
    {
      deferred = e;
      if (LOGGER.isErrorEnabled())
        LOGGER.error("Model:" + _model + " has thrown an exception", e);

      if (Logger.hasLoggers(_model))
      {
        /*
         * build the message..
         */
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        Logger.log(_model, Logger.Stream.EXCEPTION, sw.toString());
      }

      try
      {
        _model.dispatch(new ModelEvent(_model, e));
      }
      catch (Exception e2)
      {
        // just in case
      }
    }
    finally
    {
      shutDown(deferred);
    }
  }
}
