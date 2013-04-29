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
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.tools.async.common.BaseIOHandler;
import org.jactr.tools.async.common.MINAEndpoint;
import org.jactr.tools.async.iterative.message.DeadLockMessage;
import org.jactr.tools.async.iterative.message.ExceptionMessage;
import org.jactr.tools.async.iterative.message.StatusMessage;
import org.jactr.tools.deadlock.IDeadLockListener;

/**
 * @author developer
 */
public class IterativeRunTracker extends MINAEndpoint
{
  /**
   * logger definition
   */
  static private final Log LOGGER               = LogFactory
                                                    .getLog(IterativeRunTracker.class);

  private BaseIOHandler    _ioHandler;

  private int              _totalIterations;

  private int              _currentIteration;

  private long             _startTime           = 0;

  private long             _summedDurations;

  private long             _estimatedIterationDuration;

  private long             _estimatedCompletionTime;

  private Set<Integer>     _exceptionIterations = new TreeSet<Integer>();
  
  private IDeadLockListener _listener;

  public IterativeRunTracker()
  {
    _ioHandler = new BaseIOHandler();
    _ioHandler.addReceivedMessageHandler(StatusMessage.class, new MessageHandler<StatusMessage>() {

      public void handleMessage(IoSession arg0, StatusMessage message)
          throws Exception
      {
        update(message);
        
        if (LOGGER.isDebugEnabled())
        {
          StringBuilder sb = new StringBuilder();
          sb.append(message.getIteration()).append("/").append(
              message.getTotalIterations());
          if (message.isStart())
            sb.append(" started @ ");
          else
            sb.append(" stopped @ ");
          
          DateFormat format = DateFormat.getTimeInstance(DateFormat.LONG);
          sb.append(format.format(new Date(message.getWhen())));
          LOGGER.debug(sb.toString());
          LOGGER.debug("Will finish @ " + format.format(new Date(getETA())) +
              " in " + getTimeToCompletion() + "ms");
        }
      }
    });

    _ioHandler.addReceivedMessageHandler(ExceptionMessage.class, new MessageHandler<ExceptionMessage>() {

      public void handleMessage(IoSession arg0, ExceptionMessage message)
          throws Exception
      {
        update(message);
        if (LOGGER.isDebugEnabled())
        {
          StringBuilder sb = new StringBuilder(
          "Exception thrown during iteration ");
          sb.append(message.getIteration()).append(" by ").append(
              message.getModelName()).append(" ");
          LOGGER.debug(sb.toString(), message.getThrown());
        }
      }
    });
    
    _ioHandler.addReceivedMessageHandler(DeadLockMessage.class, new MessageHandler<DeadLockMessage>(){

      public void handleMessage(IoSession session, DeadLockMessage message)
          throws Exception
      {
        /**
         * Error : error
         */
        LOGGER.error("Deadlock has been detected, notifying");
        if(_listener!=null)
          _listener.deadlockDetected();
      }
    });
  }

  protected void update(StatusMessage message)
  {
    _totalIterations = message.getTotalIterations();
    _currentIteration = message.getIteration();
    if (_startTime == 0) _startTime = message.getWhen(); // when it actually started

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

      long remainingTime = (_totalIterations - _currentIteration + 1) *
          _estimatedIterationDuration;

      _estimatedCompletionTime = remainingTime + message.getWhen();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("estimated duration : " +
            _estimatedIterationDuration +
            " r:" +
            (_totalIterations - _currentIteration) +
            " remainingTime : " +
            remainingTime +
            "ms eta " +
            DateFormat.getTimeInstance(DateFormat.LONG).format(
                _estimatedCompletionTime));
    }
  }

  protected void update(ExceptionMessage message)
  {
    _exceptionIterations.add(message.getIteration());
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

  /**
   * @see org.jactr.tools.async.common.MINAEndpoint#getIOHandler()
   */
  @Override
  public BaseIOHandler getIOHandler()
  {
    return _ioHandler;
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
