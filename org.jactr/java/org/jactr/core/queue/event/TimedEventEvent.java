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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * TimedEvent events are sent whenever a TimedEvent's state has changed.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class TimedEventEvent extends
    AbstractACTREvent<TimedEventQueue, ITimedEventListener>
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(TimedEventEvent.class);

  static public enum Type {
    ABORTED, FIRED, QUEUED, UPDATED;
  }

  private Type        _type;

  private ITimedEvent _timedEvent;

  public TimedEventEvent(TimedEventQueue queue, ITimedEvent te, Type type)
  {
    super(queue, ACTRRuntime.getRuntime().getClock(queue.getModel()).getTime());
    _type = type;
    _timedEvent = te;
  }

  /**
   * 
   */
  public ITimedEvent getTimedEvent()
  {
    return _timedEvent;
  }

  public Type getType()
  {
    return _type;
  }

  @Override
  public void fire(final ITimedEventListener listener)
  {
    switch (this.getType())
    {
      case QUEUED:
        listener.eventQueued(this);
        break;
      case ABORTED:
        listener.eventAborted(this);
        break;
      case FIRED:
        listener.eventFired(this);
        break;
      case UPDATED:
        listener.eventUpdated(this);
        break;
      default:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("No clue what to do with type " + this.getType());
    }

  }

}
