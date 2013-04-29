/**
 * Copyright (C) 2001, Anthony Harrison anh23@pitt.edu This library is free
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

package org.jactr.core.extensions;

import org.jactr.core.utils.IInitializable;
import org.jactr.core.utils.IInstallable;

/**
 * IExtension API. Most extensions will typically implement numerous other
 * listener interfaces in order to actually do their work.
 * 
 * @author harrison
 * @created April 25, 2003
 */
public interface IExtension extends
    org.jactr.core.utils.parameter.IParameterized, IInstallable, IInitializable
{

  /**
   * Install this extension into this model.
   * 
   * @param model
   *            Description of the Parameter
   */
  public void install(org.jactr.core.model.IModel model);

  /**
   * remove this extension from the model
   * 
   * @param model
   *            Description of the Parameter
   */
  public void uninstall(org.jactr.core.model.IModel model);

  /**
   * return the IModel
   * 
   * @return The model value
   */
  public org.jactr.core.model.IModel getModel();

  /**
   * returns the unique name of the extension.
   * 
   * @return The name value
   */
  public String getName();

}
