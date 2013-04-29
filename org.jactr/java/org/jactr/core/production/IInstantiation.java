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


/*
 * IInstantiation is not thread safe - only one thread should work with a given
 * instantiation at a time
 */
/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IInstantiation extends IProduction
{

  /**
   * Gets the variableBindings attribute of the IInstantiation object
   * 
   * @return The variableBindings value
   */
  public VariableBindings getVariableBindings();

  /**
   * Gets the production attribute of the IInstantiation object
   * 
   * @return The production value
   */
  public IProduction getProduction();

  /**
   * Gets the timeFired attribute of the IInstantiation object
   * 
   * @return The timeFired value
   */
  public double getTimeFired();

  /**
   * Gets the actionLatency attribute of the IInstantiation object
   * 
   * @return The actionLatency value
   */
  public double getActionLatency();

  /**
   * fire the instantiation returning the amount of time it should take
   * @param firingTime TODO
   * 
   * @return
   */
  public double fire(double firingTime);
}
