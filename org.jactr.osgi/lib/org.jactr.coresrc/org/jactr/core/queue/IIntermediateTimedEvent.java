/*
 * Created on Oct 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.queue;

/**
 * A timed event that needs to be notified with intermittant time updates For
 * instance, a long running event (say moving a limb) might need intermediate
 * updates so that its internal model can be updated. Once inserted into the
 * TimedEventQueue, each cycle will fire currentTime() until the event's fire
 * (end) time is reached
 * 
 * @author developer
 */
public interface IIntermediateTimedEvent extends ITimedEvent
{

  /**
   * fired at each cycle of the timed event queue until this events fire time is
   * reached
   * 
   * @param time
   */
  public void currentTime(double time);
}
