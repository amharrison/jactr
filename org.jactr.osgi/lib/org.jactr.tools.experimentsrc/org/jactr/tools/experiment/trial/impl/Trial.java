package org.jactr.tools.experiment.trial.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.triggers.ITrigger;

public class Trial implements ITrial, Runnable
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
                                                           .getLog(Trial.class);

  private final String               _id;

  private ITrigger                   _start;

  private ITrigger                   _end;

  private Collection<ITrigger>       _triggers;

  private volatile boolean           _isRunning;

  private volatile boolean           _shouldStop       = false;

  private Lock                       _lock             = new ReentrantLock();

  private Condition                  _runningCondition = _lock.newCondition();

  private double                     _startTime;

  private double                     _stopTime;

  private IExperiment                _experiment;

  public Trial(String id, IExperiment experiment)
  {
    _experiment = experiment;
    _id = id;
    _triggers = new ArrayList<ITrigger>();
  }

  public IExperiment getExperiment()
  {
    return _experiment;
  }

  public String getId()
  {
    return _id;
  }

  public void addTrigger(ITrigger trigger)
  {
    _triggers.add(trigger);
  }

  public void setEndTrigger(ITrigger trigger)
  {
    _end = trigger;
  }

  public void setStartTrigger(ITrigger trigger)
  {
    _start = trigger;
  }

  public boolean isRunning()
  {
    try
    {
      _lock.lock();
      return _isRunning;
    }
    finally
    {
      _lock.unlock();
    }
  }

  private void setRunning(boolean running)
  {
    try
    {
      _lock.lock();
      _isRunning = running;
      _runningCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void start()
  {
    if (isRunning())
      throw new IllegalStateException(getId() + " is already running");

    setRunning(true);

    started();
    run();
  }

  protected void started()
  {
    _startTime = _experiment.getTime();

    if (_start != null) _start.setArmed(true);

    for (ITrigger trigger : _triggers)
      trigger.setArmed(true);
  }

  public void stop()
  {
    if (!isRunning())
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("%s is not running. Issuing stop anyway",
            getId()));
    }
    try
    {
      _lock.lock();
      _shouldStop = true;


      _runningCondition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  protected boolean shouldStop()
  {
    return _shouldStop;
  }

  public void waitForStop()
  {
    try
    {
      _lock.lock();
      if (!isRunning()) return;
      while (!_isRunning)
        _runningCondition.awaitUninterruptibly();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void run()
  {
    runInternal();
    waitForStopSignal();
    setRunning(false);
    stopped();
  }

  protected void runInternal()
  {

  }

  protected void waitForStopSignal()
  {
    try
    {
      _lock.lock();
      while (!_shouldStop)
        _runningCondition.awaitUninterruptibly();
      _shouldStop = false;
    }
    finally
    {
      _lock.unlock();
    }
  }

  protected void stopped()
  {
    _stopTime = _experiment.getTime();

    if (_end != null) _end.setArmed(true);

    for (ITrigger trigger : _triggers)
      trigger.setArmed(false);

    setRunning(false);
  }

  public double getStartTime()
  {
    return _startTime;
  }

  public double getStopTime()
  {
    return _stopTime;
  }
}
