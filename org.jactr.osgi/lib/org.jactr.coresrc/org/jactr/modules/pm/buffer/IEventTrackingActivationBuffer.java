/*
 * Created on Feb 6, 2007
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.modules.pm.buffer;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.queue.ITimedEvent;


/**
 * a decorator for activation buffers that allows the buffer
 * to track any events that it is posting that it should track.
 * this allows the visual buffer to cancel pending requests if a clear
 * is called.
 * @author developer
 *
 */
public interface IEventTrackingActivationBuffer extends IActivationBuffer
{

  /**
   * enqueue the timed event to the model's event queue and track
   * said event
   * @param timedEvent
   */
  public void enqueueTimedEvent(ITimedEvent timedEvent);
}


