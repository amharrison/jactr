/*
 * Created on Oct 21, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.model;

public interface ICycleProcessor
{
  /**
   * run a single cycle of the model returning the time that the next cycle
   * should start at. or if NaN if there is nothing that can be done and there
   * are no pending events
   * @param eventsHaveFired TODO
   */
  public double cycle(IModel model, boolean eventsHaveFired);

  /**
   * called at start up
   * 
   * @param model
   */
  public void initialize(IModel model);

  /**
   * execute this before the next cycle starts
   * 
   * @param runner
   */
  public void executeBefore(Runnable runner);

  /**
   * execute this after the current cycle finishes
   * 
   * @param runner
   */
  public void executeAfter(Runnable runner);

  /**
   * is the cycle currently executing? True between cycle start and stop events.
   * 
   * @return
   */
  public boolean isExecuting();

}
