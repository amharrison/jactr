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

package org.jactr.instrument;

import org.jactr.core.model.IModel;
import org.jactr.core.utils.IInitializable;
import org.jactr.core.utils.IInstallable;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created March 25, 2003
 */
public interface IInstrument extends IInstallable, IInitializable
{

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   */
  public void install(IModel model);

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   */
  public void uninstall(IModel model);
  
  
  /**
   * perform any dependent initialization. this is called before the model starts,
   * on the model thread. All model data will be available at this time. this 
   * method should excecute as quickly as possible so that all the model can start
   * running sooner. Any long running actions should likely be started during install
   * and harvested by initialize()
   *
   */
  public void initialize();

}

