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

package org.jactr.core.queue.event;

import java.util.EventListener;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ITimedEventListener extends EventListener
{

  /**
   * an event has been fired (its time has expired)
   */
  public void eventFired(TimedEventEvent tee);

  /**
   * an event has been queued
   */
  public void eventQueued(TimedEventEvent tee);

  /**
   * an event has been aborted and dequeued
   */
  public void eventAborted(TimedEventEvent tee);
  
  
  /**
   * the timed event is an IIntermediateTimedEvent and it has
   * received an update 
   * @param tee
   */
  public void eventUpdated(TimedEventEvent tee);

}

