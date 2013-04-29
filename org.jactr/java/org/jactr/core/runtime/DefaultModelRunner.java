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
import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.ICycleProcessor;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.queue.TimedEventQueue;

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

  protected ExecutorService _service;

  protected BasicModel      _model;

  protected ICycleProcessor _cycleRunner;

  private NumberFormat      _format;

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
    ACTRRuntime.getRuntime().getConnector().connect(_model);

    ACTRRuntime.getRuntime().getClock(_model).setTimeShift(_model.getAge());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Shifted clock to " + _model.getAge());

    /**
     * let anyone who depends on commonreality know that we have connected
     */
    _model.dispatch(new ModelEvent(_model, ModelEvent.Type.CONNECTED));
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
    return _cycleRunner.cycle(_model, eventsHaveFired);
  }

  /**
   * wait for the clock to reach this time
   * 
   * @param waitForTime
   * @throws InterruptedException
   */
  protected double waitForClock(double waitForTime) throws InterruptedException
  {
    if (Double.isNaN(waitForTime)) return 0;

    double rtn = ACTRRuntime.getRuntime().getClock(_model).waitForTime(
        waitForTime);

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

        nextTime = cycle(eventsHaveFired);

        postCycle(nextTime);

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
      // perfectly normal
    }
    catch (InterruptedException ie)
    {
      // perfectly normal
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
