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


import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IllegalProductionStateException;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.IAction;

/**
 * @author developer
 */
@Deprecated
public class ResponseAction implements IAction
{

  /**
   * @see org.jactr.core.production.action.IAction#dispose()
   */
  public void dispose()
  {
    /*
     * noop
     */
  }

  /**
   * @see org.jactr.core.production.action.IAction#fire(IInstantiation)
   */
  public double fire(IInstantiation instantiation, double firingTime)
  {
    ResponseCollector collector = ResponseCollector.getResponseCollector();
    if (collector == null)
      throw new IllegalProductionStateException(
          "No response collector is installed");

    collector.handleResponse(instantiation.getModel(), instantiation
        .getVariableBindings());
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
