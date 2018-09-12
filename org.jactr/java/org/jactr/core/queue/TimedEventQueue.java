/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.queue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.collection.IPrioritizer;
import org.jactr.core.queue.collection.PrioritizedQueue;
import org.jactr.core.queue.event.ITimedEventListener;
import org.jactr.core.queue.event.TimedEventEvent;

/**
 * Tracks TimedEvents within the model. A TimedEvent is any event that must
 * occur by a specific time. All buffer actions are created and placed into this
 * queue so that after the production's execution time has expired, the actions
 * will be executed.
 * 
 * @author harrison
 * @created February 17, 2003
 */
public class TimedEventQueue
{

  private static transient Log                              LOGGER  = LogFactory
                                                                        .getLog(TimedEventQueue.class
                                                                            .getName());

  private static Comparator<ITimedEvent>                    _sorter = new Comparator<ITimedEvent>() {

                                                                      public int compare(
                                                                          ITimedEvent arg0,
                                                                          ITimedEvent arg1)
                                                                      {
                                                                        if (arg0 == arg1)
                                                                          return 0;

                                                                        if (arg0
                                                                            .getEndTime() < arg1
                                                                            .getEndTime())
                                                                          return -1;
                                                                        if (arg0
                                                                            .getEndTime() > arg1
                                                                            .getEndTime())
                                                                          return 1;

                                                                        return 0;
                                                                      }

                                                                    };

  ACTREventDispatcher<TimedEventQueue, ITimedEventListener> _eventDispatcher;

  /**
   * events that are currently (at this precise moment) being fired
   */
  Collection<ITimedEvent>                                   _firingEvents;

  IModel                                                    _model;

  final Lock                                                _lock   = new ReentrantLock();

  /**
   * to compute the priority for the priorizied queue
   */
  IPrioritizer<ITimedEvent>                                 _prioritizer;

  PrioritizedQueue<ITimedEvent>                             _priorityQueue;

  Collection<ITimedEvent>                                   _intermediateEvents;

  public TimedEventQueue(IModel model)
  {
    _model = model;

    _eventDispatcher = new ACTREventDispatcher<TimedEventQueue, ITimedEventListener>();
    _firingEvents = new ArrayList<ITimedEvent>();

    _prioritizer = new IPrioritizer<ITimedEvent>() {

      public double getPriority(ITimedEvent object)
      {
        return object.getEndTime();
      }

    };
    _priorityQueue = new PrioritizedQueue<ITimedEvent>(_prioritizer);
    _intermediateEvents = new ArrayList<ITimedEvent>();
  }

  public void clear()
  {
    _priorityQueue.clear();
    _intermediateEvents.clear();
  }

  public void dispose()
  {
    _eventDispatcher.clear();
    // _eventDispatcher = null;

    _priorityQueue.clear();
    // _priorityQueue = null;

    _intermediateEvents.clear();
    // _intermediateEvents = null;

    // _model = null;
  }

  public IModel getModel()
  {
    return _model;
  }

  /**
   * is the queue empty?
   */
  public boolean isEmpty()
  {
    try
    {
      _lock.lock();
      return _priorityQueue.isEmpty();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * returns the time of the next expiring event, you should check the size
   * first..
   */
  public double getNextEndTime()
  {
    try
    {
      _lock.lock();

      return _priorityQueue.getFirstPriority();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * queue up the event. If the event has already elapsed, it will be fired.
   */
  public void enqueue(ITimedEvent te)
  {
    try
    {
      _lock.lock();
      _priorityQueue.add(te);
      if (te instanceof IIntermediateTimedEvent) _intermediateEvents.add(te);
    }
    finally
    {
      _lock.unlock();
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Enqueueing " + te);

    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new TimedEventEvent(this, te,
          TimedEventEvent.Type.QUEUED));

    if (Logger.hasLoggers(_model))
      Logger.log(_model, Logger.Stream.EVENT, "Queued " + te);

  }

  /**
   * return all the events that are pending. this is a costly operation and
   * should only be used when absolutely necessary
   */
  public Collection<ITimedEvent> getPendingEvents()
  {
    return _priorityQueue.get();
  }

  /**
   * return the events that will fire right now. this has a very limited window
   * of viability. This is used to let events that are currently firing get
   * access to the other events that will be (or have been) fired. Once the
   * firing is complete, this will return an empty list.
   * 
   * @return
   */
  public Collection<ITimedEvent> getFiringEvents()
  {
    return Collections.unmodifiableCollection(_firingEvents);
  }

  /**
   * check the queue for events that should be fired. If an event hasn't
   * expired, its timePassed method will be called with the current time. Those
   * that have expired will have their timeHasElapsed() methods called, removed,
   * and returned to the caller
   * 
   * @param currentTime
   * @since
   */
  public boolean fireExpiredEvents(double currentTime)
  {
    boolean firedAny = false;

    try
    {
      _lock.lock();
      _firingEvents.clear();
      _priorityQueue.remove(currentTime, _firingEvents);
      _intermediateEvents.removeAll(_firingEvents);
    }
    finally
    {
      _lock.unlock();
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Will attempt to fire " + _firingEvents);

    for (ITimedEvent expiredEvent : _firingEvents)
    {
      boolean firedEvent = false;
      boolean shouldFire = false;

      /*
       * not genuinely thread safe...
       */
      synchronized (expiredEvent)
      {
        shouldFire = !expiredEvent.hasAborted() && !expiredEvent.hasFired();
      }

      if (shouldFire)
        try
        {
          if (LOGGER.isDebugEnabled()) LOGGER.debug("firing " + expiredEvent);
          expiredEvent.fire(currentTime);
          firedEvent = true;
        }
        catch (IllegalStateException e)
        {
          /*
           * why might an exception occur at this point? we're not going to
           * synchronized on the timed events, so a separate thread may abort a
           * timed event even with that check. so we catch the possiblity here
           * and log it.
           */
          firedEvent = false;
          if (!expiredEvent.hasAborted() && LOGGER.isWarnEnabled())
            LOGGER.warn("Timed event [" + expiredEvent + "] failed to fire. ",
                e);
        }

      // synchronized (expiredEvent)
      // {
      // if (!expiredEvent.hasAborted())
      // try
      // {
      // if (LOGGER.isDebugEnabled())
      // LOGGER.debug("firing " + expiredEvent);
      // expiredEvent.fire(currentTime);
      //
      // firedEvent = true;
      // }
      // catch (IllegalStateException e)
      // {
      // /*
      // * why might an exception occur at this point? we're not going to
      // * synchronized on the timed events, so a separate thread may abort
      // * a timed event even with that check. so we catch the possiblity
      // * here and log it.
      // */
      // firedEvent = false;
      // if (!expiredEvent.hasAborted() && LOGGER.isWarnEnabled())
      // LOGGER.warn(
      // "Timed event [" + expiredEvent + "] failed to fire. ", e);
      // }
      // }

      if (firedEvent)
      {
        if (_eventDispatcher.hasListeners())
          _eventDispatcher.fire(new TimedEventEvent(this, expiredEvent,
              TimedEventEvent.Type.FIRED));

        if (Logger.hasLoggers(_model))
          Logger.log(_model, Logger.Stream.EVENT, "Fired " + expiredEvent);
      }
      else
      {
        if (expiredEvent.hasAborted() && _eventDispatcher.hasListeners())
          _eventDispatcher.fire(new TimedEventEvent(this, expiredEvent,
              TimedEventEvent.Type.ABORTED));

        if (Logger.hasLoggers(_model))
          Logger.log(_model, Logger.Stream.EVENT, "Aborted " + expiredEvent);
      }
    }

    firedAny = _firingEvents.size() != 0;
    _firingEvents.clear();

    /*
     * now we need to notify the intermediates, we should not do this from a
     * synchronized block..
     */
    try
    {
      _lock.lock();
      _firingEvents.addAll(_intermediateEvents);
    }
    finally
    {
      _lock.unlock();
    }

    for (ITimedEvent te : _firingEvents)
    {
      ((IIntermediateTimedEvent) te).currentTime(currentTime);
      if (_eventDispatcher.hasListeners())
        _eventDispatcher.fire(new TimedEventEvent(this, te,
            TimedEventEvent.Type.UPDATED));
    }

    return firedAny;
  }

  public void addTimedEventListener(ITimedEventListener tel, Executor executor)
  {
    _eventDispatcher.addListener(tel, executor);
  }

  public void removeTimedEventListener(ITimedEventListener tel)
  {
    _eventDispatcher.removeListener(tel);
  }

}
