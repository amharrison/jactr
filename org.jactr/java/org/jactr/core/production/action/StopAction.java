/*
 * Created on May 24, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.production.action;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;

public class StopAction extends DefaultAction
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(StopAction.class);

  public StopAction()
  {
    super();
  }

  @Override
  public double fire(@SuppressWarnings("unused")
  IInstantiation instantiation, double firingTime)
  {
    // force the model to stop cleanly
    throw new ModelTerminatedException();
  }

  /**
   * @see org.jactr.core.production.action.IAction#bind(VariableBindings)
   */
  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return new StopAction();
  }

}
