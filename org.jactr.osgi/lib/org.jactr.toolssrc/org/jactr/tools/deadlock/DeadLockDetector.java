package org.jactr.tools.deadlock;

/*
 * default logging
 */
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.instrument.IInstrument;

public class DeadLockDetector implements IInstrument
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER       = LogFactory
                                                      .getLog(DeadLockDetector.class);

  final private Runnable             _detector;

  final private IModelListener       _modelListener;

  final private IDeadLockListener    _listener;

  final private long                 _inactivityTime;

  final private long                 _delay;

  // number of missed checks
  private int                        _counter;

  private double                     _lastTime    = -1;

  private double                     _currentTime = 0;

  private long                       _lastTimeChange;

  private boolean                    _scheduled   = false;

  /**
   * zero arg for installing straight into models
   */
  public DeadLockDetector()
  {
    this(null, 100000);
  }

  public DeadLockDetector(IDeadLockListener listener, long checkIntervalMS)
  {
    _listener = listener;
    _inactivityTime = checkIntervalMS;
    _delay = _inactivityTime / 10;

    /**
     * run periodically to test to see if the model time has changed
     */
    _detector = new Runnable() {

      public void run()
      {
        synchronized (DeadLockDetector.this)
        {
          _scheduled = false;
        }
        check();
      }
    };

    /**
     * flags the model time as having changed
     */
    _modelListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStarted(ModelEvent me)
      {
        touch(me); // accidental pun
      }
    };
  }

  public void initialize()
  {

  }

  public void install(IModel model)
  {
    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    schedule();
  }

  public void uninstall(IModel model)
  {
    model.removeListener(_modelListener);
  }

  private void check()
  {
    IController controller = ACTRRuntime.getRuntime().getController();
    if (controller == null || !controller.isRunning()
        || controller.isSuspended())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("runtime not started or suspended, ignoring");

      if (controller != null && controller.isSuspended()) synchronized (this)
      {
        // if suspended, we need to reset this, just in case
        _counter = 0;
      }

      schedule();
      return;
    }

    boolean deadlocked = false;

    synchronized (this)
    {
      if (_lastTime == _currentTime)
      {
        _counter++;

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Time hasn't changed " + _counter + " times");

        long delta = System.currentTimeMillis() - _lastTimeChange;
        if (_counter >= 10 && delta >= _inactivityTime)
        {
          LOGGER.error("Deadlock has been detected after " + delta + "ms");
          deadlocked = true;
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("only " + delta
              + "ms have elapsed, still some time left");
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug("Time has changed [" + _currentTime + "/" + _lastTime
            + "]. All is good in the universe.");

      _lastTime = _currentTime;
    }

    if (deadlocked)
    {
      DeadLockUtilities.dumpThreads("deadlock-threads.txt");
      DeadLockUtilities.dumpHeap("deadlock-heap.hprof", true);

      if (_listener != null) _listener.deadlockDetected();
    }
    else
      schedule();
  }

  private void touch(ModelEvent event)
  {
    synchronized (this)
    {
      if (_currentTime == event.getSimulationTime()) return;

      _counter = 0;
      _currentTime = event.getSimulationTime();
      _lastTimeChange = event.getSystemTime();
    }
  }

  private void schedule()
  {
    try
    {
      if (_scheduled) return;
      ((ScheduledExecutorService) ExecutorServices
          .getExecutor(ExecutorServices.PERIODIC)).schedule(_detector, _delay,
          TimeUnit.MILLISECONDS);
      _scheduled = true;
    }
    catch (RejectedExecutionException ree)
    {
      // ignore
      _scheduled = false;
    }
  }
}
