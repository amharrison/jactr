/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module;

import org.jactr.core.model.IModel;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.IInitializable;
import org.jactr.core.utils.IInstallable;

/**
 * module definition. All theoretical additions to jACT-R are made through these
 * interfaces. All event listeners and buffers etc should be installed during
 * the call to install(IModel). initialize() is called after all the modules
 * have been installed - signaling that they can accurate request access to the
 * other modules within the model to satisfy dependencies. The constructor for
 * the module should <b>not</b> do any costly initializations or resource
 * intensive processes - hold these off for install() and initialize(), or by
 * listening the the model start event.
 * 
 * @author developer
 */
public interface IModule extends IInstallable, IInitializable, IAdaptable
{

  /**
   * called from the model during IModel.install(IModule). The module should
   * handle any nondependent initialization here. Since no chunks will have been
   * installed yet, the module should make not attempt to make reference to any
   * until just before the model runs. Likewise, you should not install
   * listeners into modules that this one is dependent upon until initialize()
   * is called.
   * 
   * @see org.jactr.core.utils.IInstallable#install(org.jactr.core.model.IModel)
   */
  public void install(IModel model);

  /**
   * this will be called after all the modules have been installed permitting
   * the module to attach listeners to other modules. if you want to get access
   * to chunks,types, or productions before the model runs, attach a model
   * listener and do that during the modelStarted call
   */
  public void initialize();

  /**
   * the name of the module
   * 
   * @return
   */
  public String getName();

  /**
   * return the model this module is installed into
   */
  public IModel getModel();

  /**
   * release any resources. this should only be called by the model during its
   * own dispose method (assuming that this module is still installed) and
   * should not result in the propogation of ANY events. The module should
   * dispose of all its resources, including buffers
   */
  public void dispose();
  
  /**
   * reset the module. This will typically clear the owned buffers and abort
   * any module specific operations
   */
  public void reset();
}
