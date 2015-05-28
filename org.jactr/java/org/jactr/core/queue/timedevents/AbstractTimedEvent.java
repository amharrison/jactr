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

package org.jactr.core.queue.timedevents;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.impl.BasicClock;
import org.jactr.core.queue.ITimedEvent;

/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class AbstractTimedEvent implements ITimedEvent
{
  static private transient final Log LOGGER                = LogFactory
                                                               .getLog(AbstractTimedEvent.class
                                                                   .getName());

  protected boolean                  _hasAborted           = false;

  protected boolean                  _hasFired             = false;

  /**
   * Description of the Field
   */
  protected double                   _startTime;

  /**
   * Description of the Field
   */
  protected double                   _endTime;

  protected String                   _toString;

  static private boolean             _shouldWarnOnSlippage = !Boolean
                                                               .getBoolean("jactr.ignoreTimeSlips");

  static private double              _timeSlipThreshold    = 0.05;
  static
  {
    try
    {
      _timeSlipThreshold = Double.parseDouble(System
          .getProperty("jactr.timeSlipThreshold"));
    }
    catch (Exception e)
    {
      _timeSlipThreshold = 0.05;
    }
  }

  public AbstractTimedEvent()
  {

  }

  public AbstractTimedEvent(double start, double end)
  {
    setTimes(start, end);
  }

  /**
   * Sets the times attribute of the AbstractTimedEvent object
   * 
   * @param start
   *          The new times value
   * @param end
   *          The new times value
   */
  public void setTimes(double start, double end)
  {
    _startTime = BasicClock.constrainPrecision(start);
    _endTime = BasicClock.constrainPrecision(end);
  }

  /**
   * when the TimedEvent was posted in simulated time.
   * 
   * @return The startTime value
   */
  public double getStartTime()
  {
    return _startTime;
  }

  /**
   * When the event should be fired. When the model's simulated time has reached
   * this point, the event will be fired by calling timeHasElapsed
   * 
   * @return The endTime value
   */
  public double getEndTime()
  {
    return _endTime;
  }

  /**
   * @return
   */
  protected boolean shouldWarnOnTimeSlips()
  {
    return _shouldWarnOnSlippage;
  }

  /**
   * called when this event should be fired..
   * 
   * @param currentTime
   *          Description of the Parameter
   */

  synchronized public void fire(double currentTime)
  {
    if (hasAborted())
      throw new IllegalStateException(
          "timed event has been aborted, cannot fire");

    if (hasFired())
      throw new IllegalStateException("timed event has already been fired");

    if (Math.abs(getEndTime() - currentTime) > _timeSlipThreshold
        && shouldWarnOnTimeSlips())
 timeSlipExceedsTolerance(currentTime);

    _hasFired = true;
  }

  protected void timeSlipExceedsTolerance(double currentTime)
  {
    if (LOGGER.isWarnEnabled())
      LOGGER
          .warn(String
              .format(
                  "%s (%s) : Time slippage (%.4f) detected. Event should have fired at %.2f, actually fired at %.2f",
                  this, getClass().getSimpleName(), currentTime - getEndTime(),
                  getEndTime(), currentTime));
  }

  synchronized public boolean hasFired()
  {
    return _hasFired;
  }

  /**
   * called when the event is to be aborted
   */
  synchronized public void abort()
  {
    _hasAborted = true;
  }

  synchronized public boolean hasAborted()
  {
    return _hasAborted;
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  @Override
  public String toString()
  {
    /*
     * no need to synchornize
     */
    if (_toString == null)
      _toString = String.format("%s(@ %.2f)", getClass().getName(), _endTime);
    return _toString;
  }
}