package org.jactr.tools.masterslave.clock;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.OwnedClock;

/**
 * @author harrison
 */
public class MasterSlaveClock extends OwnedClock// SharedClock
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(MasterSlaveClock.class);

  private IClock                     _primaryClock;

  private Thread                     _primaryOwner;

  private volatile boolean           _firstOut = true;

  public MasterSlaveClock(IClock primary, Thread primaryOwner)
  {
    super(0.05);
    _primaryClock = primary;
    _primaryOwner = primaryOwner;
    // addOwner(primaryOwner);
  }

  @Override
  public double getTime()
  {
    return _primaryClock.getTime();
  }

  // @Override
  // protected double setTimeInternal(double time)
  // {
  // try
  // {
  // _lock.lock();
  // _firstOut = true;
  // }
  // finally
  // {
  // _lock.unlock();
  // }
  // return super.setTimeInternal(time);
  // }
  //
  // @Override
  // protected boolean requestTime(double requestedTime)
  // throws InterruptedException
  // {
  // boolean rtn = super.requestTime(requestedTime);
  //
  // /*
  // * this was the lowest requested time and has been sent, that means, it's
  // * time to pass this on to primary. first out is necessary if all the shared
  // * owners requested the same time.
  // */
  // boolean shouldRun = false;
  //
  // if (rtn) try
  // {
  // _lock.lock();
  //
  // if (_firstOut)
  // {
  // _firstOut = false;
  // shouldRun = true;
  // }
  // }
  // finally
  // {
  // _lock.unlock();
  // }
  //
  // if (shouldRun)
  // {
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(String.format(
  // "Notifying primary; lowest requested time %f", requestedTime));
  //
  // /*
  // * we will block until this is released. because we are the first out of
  // * the
  // */
  // double rtnValue = Double.isNaN(requestedTime) ? _primaryClock
  // .waitForChange() : _primaryClock.waitForTime(requestedTime);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(String.format("Waiting for %.4f, got %.4f", requestedTime,
  // rtnValue));
  //
  // // this notification may not be necessary at all.
  // // try
  // // {
  // // _lock.lock();
  //
  // if (Double.isNaN(requestedTime))
  // return true;
  // else
  // return requestedTime <= rtnValue;
  // // }
  // // finally
  // // {
  // // //this may be an excessive signal
  // // _timeChangeCondition.signalAll();
  // // _lock.unlock();
  // // }
  //
  // }
  // else // we aren't the first one out, so we need to block on this piece.
  // if (LOGGER.isDebugEnabled())
  // LOGGER
  // .debug(String
  // .format(
  // "Skipping primary notification. rtn: %s, shouldRun: %s, requestedTime %.3f",
  // rtn, shouldRun, requestedTime));
  //
  // return false;
  // }
}
