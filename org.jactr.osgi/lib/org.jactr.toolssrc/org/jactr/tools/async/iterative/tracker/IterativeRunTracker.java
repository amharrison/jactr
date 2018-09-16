/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.iterative.tracker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.session.ISessionInfo;
import org.jactr.tools.async.common.NetworkedEndpoint;
import org.jactr.tools.async.iterative.listener.NetworkedIterativeRunListener;
import org.jactr.tools.async.iterative.message.DeadLockMessage;
import org.jactr.tools.async.iterative.message.ExceptionMessage;
import org.jactr.tools.async.iterative.message.StatusMessage;
import org.jactr.tools.deadlock.IDeadLockListener;

/**
 * for the receiving of updates from the {@link NetworkedIterativeRunListener}
 * 
 * @author developer
 */
public class IterativeRunTracker extends NetworkedEndpoint
{
  /**
   * logger definition
   */
  static private final Log  LOGGER               = LogFactory
                                                     .getLog(IterativeRunTracker.class);

  private int               _totalIterations;

  private int               _currentIteration;

  private long              _startTime           = 0;

  private long              _summedDurations;

  private long              _estimatedIterationDuration;

  private long              _estimatedCompletionTime;

  private Set<Integer>      _exceptionIterations = new TreeSet<Integer>();

  private IDeadLockListener _listener;

  static private DateFormat _longTimeFormat      = DateFormat
                                                     .getTimeInstance(DateFormat.LONG);

  public IterativeRunTracker()
  {
  }

  @Override
  protected void createDefaultHandlers()
  {
    super.createDefaultHandlers();
    _defaultHandlers.put(DeadLockMessage.class, (s, m) -> {
      LOGGER.error("Deadlock has been detected, notifying");
      if (_listener != null) _listener.deadlockDetected();
    });

    _defaultHandlers.put(ExceptionMessage.class,
        (s, m) -> update((ExceptionMessage) m));

    _defaultHandlers.put(StatusMessage.class,
        (s, m) -> update((StatusMessage) m));

  }

  protected void update(StatusMessage message)
  {
    _totalIterations = message.getTotalIterations();
    _currentIteration = message.getIteration();
    if (_startTime == 0) _startTime = message.getWhen(); // when it actually
                                                         // started

    if (message.isStop())
    {

      long currentDuration = message.getWhen() - _startTime;
      /*
       * we set the next start time to this so that we can accurately measure
       * the cost of any analyses being run outside of the simulation
       */
      _startTime = message.getWhen();

      _summedDurations += currentDuration;

      /*
       * weighted average of everyone up to now and last
       */
      _estimatedIterationDuration = _summedDurations / _currentIteration;

      long remainingTime = (_totalIterations - _currentIteration + 1)
          * _estimatedIterationDuration;

      _estimatedCompletionTime = remainingTime + message.getWhen();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("estimated duration : "
            + _estimatedIterationDuration
            + " r:"
            + (_totalIterations - _currentIteration)
            + " remainingTime : "
            + remainingTime
            + "ms eta "
            + _longTimeFormat.format(
                _estimatedCompletionTime));
    }

    if (LOGGER.isDebugEnabled())
    {
      StringBuilder sb = new StringBuilder();
      sb.append(message.getIteration()).append("/")
          .append(message.getTotalIterations());
      if (message.isStart())
        sb.append(" started @ ");
      else
        sb.append(" stopped @ ");

      sb.append(_longTimeFormat.format(new Date(message.getWhen())));
      LOGGER.debug(sb.toString());
      LOGGER.debug("Will finish @ "
          + _longTimeFormat.format(new Date(getETA()))
          + " in " + getTimeToCompletion() + "ms");
    }
  }

  protected void update(ExceptionMessage message)
  {
    _exceptionIterations.add(message.getIteration());
    if (LOGGER.isDebugEnabled())
    {
      StringBuilder sb = new StringBuilder("Exception thrown during iteration ");
      sb.append(message.getIteration()).append(" by ")
          .append(message.getModelName()).append(" ");
      LOGGER.debug(sb.toString(), message.getThrown());
    }
  }

  @Override
  public ISessionInfo getActiveSession()
  {
    return getSession();
  }

  public void setDeadLockListener(IDeadLockListener listener)
  {
    _listener = listener;
  }

  public int getCurrentIteration()
  {
    return _currentIteration;
  }

  public int getTotalIterations()
  {
    return _totalIterations;
  }

  public long getEstimatedDuration()
  {
    return _estimatedIterationDuration;
  }

  public long getTimeToCompletion()
  {
    long eta = getETA();
    if (eta != 0) return eta - System.currentTimeMillis();
    return 0;
  }

  public long getETA()
  {
    return _estimatedCompletionTime;
  }

  public Collection<Integer> getExceptionCycles()
  {
    return new ArrayList<Integer>(_exceptionIterations);
  }

  public void start() throws Exception
  {
    connect();
  }

  public void stop() throws Exception
  {
    // getIOHandler().waitForPendingWrites();
    // getIOHandler().waitForDisconnect();
    disconnect();
  }

}
