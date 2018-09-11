/*
 * Created on May 8, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.model.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.impl.BasicClock;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.ICycleProcessor;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * default cycle control for the model
 * 
 * @author developer
 */
public class DefaultCycleProcessor6 implements ICycleProcessor
{
  /**
   * logger definition
   */
  static private final Log     LOGGER                            = LogFactory
      .getLog(DefaultCycleProcessor6.class);

  private double               _nextPossibleProductionFiringTime = 0;

  private Collection<Runnable> _executeBefore;

  private Collection<Runnable> _executeAfter;

  private volatile boolean     _isExecuting                      = false;

  private final double         TEMPORAL_TOLERANCE                = 0.0001;

  public DefaultCycleProcessor6()
  {
    _executeAfter = new ArrayList<>();
    _executeBefore = new ArrayList<>();
  }

  private void execute(Collection<Runnable> source)
  {
    Collection<Runnable> container = FastCollectionFactory.newInstance();
    synchronized (source)
    {
      container.addAll(source);
      source.clear();
    }

    for (Runnable runner : container)
      try
      {
        runner.run();
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to execute " + runner, e);
      }

    FastCollectionFactory.recycle(container);
  }

  @Override
  public void initialize(IModel model)
  {
    _nextPossibleProductionFiringTime = ACTRRuntime.getRuntime().getClock(model)
        .getTime();
    _isExecuting = false;
  }

  /**
   * run a single cycle of the model
   * 
   * @see java.util.concurrent.Callable#call()
   */
  public double cycle(IModel model, boolean eventsHaveFired)
  {
    BasicModel basicModel = (BasicModel) model;
    execute(_executeBefore);

    /*
     * what time is it?
     */
    double now = ACTRRuntime.getRuntime().getClock(basicModel).getTime();

    basicModel.setCycle(basicModel.getCycle() + 1);
    basicModel
        .dispatch(new ModelEvent(basicModel, ModelEvent.Type.CYCLE_STARTED));
    double nextWaitTime = Double.NEGATIVE_INFINITY;

    _isExecuting = true;

    try
    {
      double productionFiringTime = evaluateAndFireProduction(basicModel, now);
      nextWaitTime = calculateNextWaitTime(now, productionFiringTime,
          basicModel, eventsHaveFired);

      if (nextWaitTime <= now) LOGGER.error(String.format(
          "WARNING: Time discrepancy detected. Next cycle time : %.10f(next) <= %.10f(current). Should be >",
          nextWaitTime, now));

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("nextWaitTime : " + nextWaitTime);
    }
    catch (InterruptedException ie)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("Interrupted while executing production. Terminating ");

      nextWaitTime = Double.NaN;
    }
    catch (ExecutionException e)
    {
      LOGGER.error("Failed to fire production ", e);
      throw new RuntimeException(e.getMessage(), e.getCause());
    }
    finally
    {
      _isExecuting = false;

      /*
       * always fire the cycle stopped event
       */
      basicModel
          .dispatch(new ModelEvent(basicModel, ModelEvent.Type.CYCLE_STOPPED));

      execute(_executeAfter);
    }

    return nextWaitTime;
  }

  /**
   * using the current state, guestimate as to the how long this cycle will run
   * assuming that no production actually fired <br/>
   * 
   * @return
   */
  protected double calculateNextWaitTime(double now,
      double productionFiringTime, BasicModel model, boolean eventsHaveFired)
  {
    // IProceduralModule procMod = model.getProceduralModule();
    // double cycleTime = procMod.getDefaultProductionFiringTime();
    TimedEventQueue queue = model.getTimedEventQueue();

    /*
     * if the production queued any events that should fire immediately, we need
     * to fire them before we guess what the next time should be.
     */
    while (queue.fireExpiredEvents(now))
      eventsHaveFired = true;

    // will already be now+cycleTime if a production fired
    double nextProductionFiringTime = _nextPossibleProductionFiringTime;
    double nextEventFiringTime = _nextPossibleProductionFiringTime;

    if (!queue.isEmpty()) nextEventFiringTime = queue.getNextEndTime(); // already
                                                                        // constrained

    double nextWaitTime = Math.min(nextProductionFiringTime,
        nextEventFiringTime);

    /*
     * no production fired
     */
    if (Double.isInfinite(productionFiringTime)
        || Double.isNaN(productionFiringTime))
      if (queue.isEmpty() && !eventsHaveFired)
      {
      if (!model.isPersistentExecutionEnabled())
      {
      /*
       * nothing to do, no production fired, and we aren't required to stay
       * running. lets empty the goal buffer to permit empty productions (w/ no
       * goal) to fire. if the goal buffer is already empty, signal quit
       */
      IActivationBuffer goalBuffer = model
          .getActivationBuffer(IActivationBuffer.GOAL);
      if (goalBuffer != null && goalBuffer.getSourceChunk() != null)
      goalBuffer.clear();
      else
      return Double.NaN; // signal quit
      }
      }
      /*
       * we only skip cycles if no events have fired. If events have fired, then
       * productions might be able to fire..
       */
      else if (model.isCycleSkippingEnabled())
      {
      if (eventsHaveFired)
      nextWaitTime = Math.min(nextEventFiringTime, nextProductionFiringTime);
      else
      {
      nextWaitTime = nextEventFiringTime;
      nextProductionFiringTime = nextEventFiringTime;
      }

      /*
       * increment the cycles
       */
      long cycleDelta = (long) ((nextWaitTime - now)
          / model.getProceduralModule().getDefaultProductionFiringTime());
      cycleDelta--;
      model.setCycle(model.getCycle() + cycleDelta);
      }

    /*
     * if the two are absurdly close, just take the larger of the two. this
     * prevents the occasional situation (w/o cycle skipping) where the
     * production may fire microseconds before the event is to expire. since no
     * production fires, the goal is cleared, but then the event fires and there
     * is no one left to handle it. this prevents the whacky duplicate time
     * display since the time display is rounded to the millisecond, we're
     * missing that these are just ever so slightly different
     */
    if (nextEventFiringTime != nextProductionFiringTime && Math.abs(
        nextProductionFiringTime - nextEventFiringTime) < TEMPORAL_TOLERANCE)
    {
      nextWaitTime = Math.max(nextProductionFiringTime, nextEventFiringTime);
      if (LOGGER.isDebugEnabled()) LOGGER.warn(String.format(
          "Dangerously close timing : nextProd (%.5f) and nextEvent (%.5f) are insanely close, using larger (%.5f)",
          nextProductionFiringTime, nextEventFiringTime, nextWaitTime));
    }

    if (nextWaitTime <= now)
    {
      double newWaitTime = Math.nextAfter(now + 0.001,
          Double.POSITIVE_INFINITY);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(String.format(
          "nextWaitTime (%.5f) is less than or equal to the time (%.5f), incrementing to (%.5f). eventsFired=%s nextEvent=%.2f productionFiringTime=%.2f",
          nextWaitTime, now, newWaitTime, eventsHaveFired,
          queue.getNextEndTime(), productionFiringTime));

      nextWaitTime = newWaitTime;
    }
    /*
     * if we didn't fire a production, we might be able to right after the event
     * has fired
     */

    if (Double.isNaN(productionFiringTime))
      _nextPossibleProductionFiringTime = nextWaitTime;

    return nextWaitTime;
  }

  /**
   * using the current contents of the buffer, derive the conflict set and
   * select the best production. Request it to be fired, and eventually return
   * the result.
   * 
   * @return firing time or -inf if no production can fire yet, NaN if conflict
   *         resolution ran but nothing was fired
   */
  protected double evaluateAndFireProduction(BasicModel model,
      double currentTime) throws InterruptedException, ExecutionException
  {
    /*
     * if current time less than nextpossible (w/ in tolerance) we know no
     * production could fire. We need the tolerance just in case nextpossible is
     * 0.1000000002 and current is 0.100000
     */
    if (_nextPossibleProductionFiringTime - currentTime > TEMPORAL_TOLERANCE)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format(
          "nextPossibleFiringTime (%.4f) is greater than current time (%.4f), no production may fire yet.",
          _nextPossibleProductionFiringTime, currentTime));
      return Double.NEGATIVE_INFINITY;
    }

    /*
     * get the buffers and their contents
     */
    Collection<IActivationBuffer> buffers = model.getActivationBuffers();

    /*
     * and the procedural module since we'll be using him quite frequently
     */
    IProceduralModule procMod = model.getProceduralModule();

    /*
     * snag the conflict set, procMod should fire the notification events itself
     * this and selectInstantiation should not be separate methods, or better
     * yet, provide a third method
     */
    Future<Collection<IInstantiation>> conflictSet = procMod
        .getConflictSet(buffers);

    /*
     * figure out who is the best production..
     */
    IInstantiation instantiation = null;

    instantiation = procMod.selectInstantiation(conflictSet.get()).get();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Conflict resolution selected " + instantiation);

    /*
     * and fire his arse.. and snag his return time Nan if no production was
     * fired
     */
    double firingDuration = Double.NaN;
    if (instantiation != null)
      try
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Firing " + instantiation);
        firingDuration = procMod.fireProduction(instantiation, currentTime)
            .get();

        _nextPossibleProductionFiringTime = BasicClock
            .constrainPrecision(currentTime + firingDuration);
      }
      catch (ExecutionException e)
      {
        throw new ExecutionException("Failed to execute " + instantiation,
            e.getCause());
      }
    else
      _nextPossibleProductionFiringTime = BasicClock.constrainPrecision(
          currentTime + procMod.getDefaultProductionFiringTime());

    return firingDuration;
  }

  public void executeAfter(Runnable runner)
  {
    synchronized (_executeAfter)
    {
      _executeAfter.add(runner);
    }
  }

  public void executeBefore(Runnable runner)
  {
    synchronized (_executeBefore)
    {
      _executeBefore.add(runner);
    }
  }

  public boolean isExecuting()
  {
    return _isExecuting;
  }

}
