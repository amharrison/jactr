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


import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;

/**
 * GoalFeeder is part of a two part general solution for the providing goals to
 * a running model. The second part is the ResponseCollector. There can be only
 * one instanceof of either active at anytime in the runtime, as the custom
 * actions require static access.
 * 
 * @author developer
 */
@Deprecated
public abstract class GoalFeeder
{

  static private GoalFeeder _goalFeeder;

  static public void setGoalFeeder(GoalFeeder feeder)
  {
    _goalFeeder = feeder;
  }

  static public GoalFeeder getGoalFeeder()
  {
    return _goalFeeder;
  }

  /**
   * This is called by the NextGoalAction. A new goal chunk should be created
   * and returned. if null is returned, the model will typically stop.
   * 
   * @param model
   * @return
   */
  abstract public IChunk createNextGoal(IModel model,
      VariableBindings variableBindings);

  abstract public boolean hasNextGoal(IModel model,
      VariableBindings variableBindings);

  /**
   * return how long it will take before the chunk returned by getNextGoal() is
   * inserted into the goal buffer. chunk may be null, but this should still
   * return a valid time, as that will be when the goal buffer contents are
   * removed
   * 
   * @param chunk
   *            may be null
   * @return 0.05
   */
  public double getGoalDelay(IChunk chunk)
  {
    return 0.05;
  }

  public IChunk getNextGoal(IModel model, VariableBindings variableBindings)
  {
    IChunk chunk = createNextGoal(model, variableBindings);

    return chunk;
  }
}
