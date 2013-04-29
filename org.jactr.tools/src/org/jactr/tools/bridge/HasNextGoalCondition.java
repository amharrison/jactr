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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;

/**
 * @author developer
 */
@Deprecated
public class HasNextGoalCondition implements ICondition
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(HasNextGoalCondition.class);

  /**
   * @see org.jactr.core.production.condition.ICondition#bind(org.jactr.core.model.IModel,
   *      java.util.Map)
   */
  public ICondition bind(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    GoalFeeder feeder = GoalFeeder.getGoalFeeder();
    if (feeder == null)
      throw new CannotMatchException(
          "Cannot match because no goal feeder is available");

    if (!feeder.hasNextGoal(model, variableBindings))
      throw new CannotMatchException("GoalFeeder says no goal is available");

    return this;
  }

  /**
   * @see org.jactr.core.production.condition.ICondition#dispose()
   */
  public void dispose()
  {
    /*
     * noop
     */
  }

  private void test(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    GoalFeeder feeder = GoalFeeder.getGoalFeeder();
    if (feeder == null)
      throw new CannotMatchException(
          "Cannot match because no goal feeder is available");

    if (!feeder.hasNextGoal(model, variableBindings))
      throw new CannotMatchException("GoalFeeder says no goal is available");
  }

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    /*
     * if we've been cloned (and we have to have been in order to bind)
     * then we already know that we're in the clear
     */
    return 0;
  }

  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    test(model, variableBindings);

    return this;
  }

}
