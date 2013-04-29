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

package org.jactr.core.production.action;


import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;

/**
 * The action interface provides three methods that all actions must specify.
 * The action is used by the IProduction when all its conditions can be matched
 * and the production is actually selected to fire.
 * 
 * @author harrison
 * @created April 18, 2003
 */

public interface IAction
{

  /**
   * Bind is called during the creation of an instantiation of a produciton. By
   * the time this gets called, we know that all the conditions are matched and
   * all the variables have been assigned. This method merely creates a copy of
   * this action and applies those bindings. the IAction returned by this method
   * will be the actual one that is fired. Normally, an action should not change
   * the contents of variableBindings
   * 
   * @param variableBindings
   * @return
   */
  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException;

  /**
   * One the IInstantiation is fully bound and has been selected for firing, all
   * of its Actions? fire methods are called. In simulated time, all the Actions
   * are fired in parallel. However, in real time they are fired in the order of
   * their definition. IModel is the current model, IProduction is the firing
   * production (IInstantiation, actually), bindings contains the current
   * bindings to that point (since the IAction might create additional bindings
   * for subsequent ones). The method returns the simulated firing time of the
   * action. This is not currently used as the IProduction itself overrides the
   * firing time. It is in place now as a place keeper for the future when we
   * get to the point where we can empirically derive firing times for
   * individual actions.
   * @param firingTime when this production was fired
   */
  public double fire(IInstantiation instantiation, double firingTime);

  /**
   * Dispose is called to explicitly release resources when we are done with the
   * IAction. Why not rely upon the finalization mechanism and garbage
   * collection? Many of the classes contain circular linkages: parent to child
   * and child to parent. Since the garbage collection contract is not
   * standardized, circular links might prevent objects from being collected.
   * This method should never be called by the programmer. It is called by the
   * containing IProduction, which in turn has its dispose method called by the
   * IModel. You should only call IModel.dispose() when you are done with the
   * model.
   */
  public void dispose();
}
