/*
 * Created on Oct 11, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * class that handles the nitty gritty of tracking listeners, executors, and
 * propogating that the firing events correctly
 * 
 * @author developer
 */
public class ACTREventDispatcher<S, L>
{
  /**
   * logger definition
   */
  static public final Log LOGGER              = LogFactory
      .getLog(ACTREventDispatcher.class);

  private List<Pair>      _actualListeners;

  private boolean         _haveEncounteredREE = false;

  public ACTREventDispatcher()
  {

  }

  synchronized public void clear()
  {
    if (_actualListeners != null)
    {
      _actualListeners.clear();
      _actualListeners = null;
    }
  }

  synchronized public void addListener(L listener, Executor executor)
  {
    if (listener == null)
      throw new IllegalArgumentException("Listener must not be null");

    if (executor == null) executor = ExecutorServices.INLINE_EXECUTOR;

    Pair p = new Pair(listener, executor);

    if (_actualListeners == null) _actualListeners = new ArrayList<>();

    _actualListeners.add(p);
  }

  synchronized public void removeListener(L listener)
  {
    if (_actualListeners == null) return;

    ListIterator<Pair> itr = _actualListeners.listIterator();
    while (itr.hasNext())
    {
      Pair pair = itr.next();
      if (pair.hasListener(listener)) itr.remove();
    }

    if (_actualListeners.size() == 0) _actualListeners = null;
  }

  synchronized public boolean hasListeners()
  {
    return _actualListeners != null && _actualListeners.size() != 0;
  }

  public void fire(IACTREvent<S, L> event)
  {
    Collection<Pair> container = null;

    synchronized (this)
    {
      if (_actualListeners == null) return;
      container = FastCollectionFactory.newInstance();
      container.addAll(_actualListeners);
    }

    for (Pair pair : container)
      pair.fire(event);

    FastCollectionFactory.recycle(container);
  }

  private class Pair
  {
    private Executor _executor;

    private L        _listener;

    public Pair(L listener, Executor executor)
    {
      _executor = executor;
      _listener = listener;
    }

    public boolean hasListener(L listener)
    {
      return _listener.equals(listener);
    }

    public void fire(final IACTREvent<S, L> event)
    {
      Runnable runner = new Runnable() {
        public void run()
        {
          try
          {
            event.fire(_listener);
          }
          catch (Exception e)
          {
            LOGGER.error("Uncaught exception during event firing of " + event
                + " to " + _listener, e);
          }
        }
      };

      try
      {
        _executor.execute(runner);
      }
      catch (RejectedExecutionException ree)
      {

        if (!_haveEncounteredREE)
        {
          if (LOGGER.isWarnEnabled())
          {
            LOGGER.warn(String.format(
                "%s rejected processing of actr event (%s) by listener (%s).",
                _executor, event.getClass().getSimpleName(),
                _listener.getClass().getName()));
            LOGGER.warn(
                "This is normal during end-of-run processing if your model produced data faster than it could be processed,");
            LOGGER.warn(
                "by the IDE or background tools. If you see this warning mid-run, something may be wrong, please see the exception.");
            LOGGER.warn(String.format(
                "Suppressing further rejection warnings for this event source (%s).",
                event.getSource().getClass().getName()));
            LOGGER.warn(ree);
          }

          _haveEncounteredREE = true;
        }
      }
    }
  }

  /**
   * @return
   */
  synchronized public Collection<L> getListeners()
  {
    if (_actualListeners == null) return Collections.EMPTY_LIST;

    List<L> listeners = new ArrayList<>(_actualListeners.size());
    for (Pair pair : _actualListeners)
      listeners.add(pair._listener);

    return listeners;
  }
}
