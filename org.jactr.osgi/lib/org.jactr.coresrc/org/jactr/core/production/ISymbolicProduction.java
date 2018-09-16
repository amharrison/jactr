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

package org.jactr.core.production;

import java.util.Collection;

import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.utils.IAdaptable;

/*
 * Not thread safe this once added to memory, the production cannot be changed.
 */
/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ISymbolicProduction extends IAdaptable
{

  /**
   * Gets the productionName attribute of the ISymbolicProduction object
   * 
   * @return The productionName value
   */
  public String getName();

  /**
   * Sets the productionName attribute of the ISymbolicProduction object
   * 
   * @param str
   *            The new productionName value
   */
  public void setName(String str);

  /**
   * Adds a feature to the ICondition attribute of the ISymbolicProduction
   * object
   * 
   * @param cond
   *            The feature to be added to the ICondition attribute
   */
  public void addCondition(ICondition cond);

  /**
   * Description of the Method
   * 
   * @param retr
   *            Description of the Parameter
   */
  public void removeCondition(ICondition retr);

  /**
   * Gets the conditions attribute of the ISymbolicProduction object
   * 
   * @return The conditions value
   */
  public Collection<ICondition> getConditions();

  /**
   * Gets the numberOfConditions attribute of the ISymbolicProduction object
   * 
   * @return The numberOfConditions value
   */
  public int getNumberOfConditions();

  /**
   * Adds a feature to the IAction attribute of the ISymbolicProduction object
   * 
   * @param cons
   *            The feature to be added to the IAction attribute
   */
  public void addAction(IAction cons);

  /**
   * Description of the Method
   * 
   * @param cons
   *            Description of the Parameter
   */
  public void removeAction(IAction cons);

  /**
   * Gets the actions attribute of the ISymbolicProduction object
   * 
   * @return The actions value
   */
  public Collection<IAction> getActions();

  /**
   * Gets the numberOfActions attribute of the ISymbolicProduction object
   * 
   * @return The numberOfActions value
   */
  public int getNumberOfActions();

  /**
   * Description of the Method
   */
  public void dispose();

  public void encode();
}
