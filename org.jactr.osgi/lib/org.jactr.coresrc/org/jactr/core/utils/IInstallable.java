/*
 * Created on May 10, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.utils;

import org.jactr.core.model.IModel;

/**
 * interface for anything that can be attached to a model before running
 * 
 * @author developer
 */
public interface IInstallable
{

  /**
   * called by the model during the install(IInstallable) call. Any nondependent
   * initialization should be done at this time. This will likely occur before
   * any chunks,type,buffers,or productions have been added so you should avoid
   * attempting to access them until initialize().
   * 
   * @param model
   */
  public void install(IModel model);

  /**
   * remove the element from this model. this is called after the model
   * terminates
   * 
   * @param model
   */
  public void uninstall(IModel model);
}
