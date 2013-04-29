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

/**
 * TimedEvent is any event that will occur after X amount of model time. The
 * TimedEvents are queued up on the TimedEventQueue which is processed for each
 * cycle of the model run.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ITimedEvent
{

  /**
   * when the TimedEvent was posted in simulated time.
   * 
   * @return The startTime value
   */
  public double getStartTime();

  /**
   * When the event should be fired. When the model's simulated time has reached
   * this point, the event will be fired by calling fire
   * 
   * @return The endTime value
   */
  public double getEndTime();

  /**
   * called when this event should be fired.. This is always called on the
   * model's thread, and so unless someone has violated the accessibility
   * contract, no other thread should be monkeying around with the model's
   * internals
   */
  public void fire(double currentTime);

  /**
   * called when the event is to be aborted. While the event will be marked as
   * unfirable, it will remain in the timed event queue until its end time is
   * reached
   */
  public void abort();

  /**
   * has this been aborted? aborted events will not be fired
   * 
   * @return
   */
  public boolean hasAborted();
  
  public boolean hasFired();
}
