/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.bridge;


import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IllegalProductionStateException;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.IAction;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * This merely snags the current goal feeder, calls it's createNextGoal() and
 * sets it as the goal
 * 
 * @author developer
 */
@Deprecated
public class NextGoalAction implements IAction
{

  /**
   * @see org.jactr.core.production.action.IAction#dispose()
   */
  public void dispose()
  {
    /*
     * again, nothing to do
     */
  }

  /**
   * @see org.jactr.core.production.action.IAction#fire(IInstantiation)
   */
  public double fire(IInstantiation instantiation, double firingTime)
  {
    GoalFeeder feeder = GoalFeeder.getGoalFeeder();
    if (feeder == null)
      throw new IllegalProductionStateException(
          "GoalFeeder has not been defined!");

    IModel model = instantiation.getModel();

    final IChunk nextGoal = feeder.getNextGoal(model, instantiation
        .getVariableBindings());
    final IActivationBuffer buffer = model
        .getActivationBuffer(IActivationBuffer.GOAL);
    

    ITimedEvent event = new AbstractTimedEvent(firingTime, firingTime
        + feeder.getGoalDelay(nextGoal)) {
      @Override
      public void fire(double now)
      {
        /*
         * remove the first - not really necessary. this is implicit
         */
        buffer.removeSourceChunk(buffer.getSourceChunk());

        if (nextGoal != null) buffer.addSourceChunk(nextGoal);
      }
    };

    model.getTimedEventQueue().enqueue(event);

    return 0;
  }

  /**
   * @see org.jactr.core.production.action.IAction#bind(VariableBindings)
   */
  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return this;
  }

}
