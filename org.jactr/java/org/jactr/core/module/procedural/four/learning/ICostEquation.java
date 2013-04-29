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

package org.jactr.core.module.procedural.four.learning;

import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ICostEquation
{

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   * @param p
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeCost(IModel model, IProduction p);
}