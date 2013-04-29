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

package org.jactr.core.utils.parameter;

import java.util.Collection;

/**
 * IParameterized interface allows objects to set/get arbitrary parameter values.
 * using strings.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IParameterized
{

  /**
   * Set the named parameter
   * 
   */
  public void setParameter(String key, String value);

  /**
   * return parameter value - null if not defined.
   * 
   * @param key
   *          Description of the Parameter
   * @return The parameter value
   */
  public String getParameter(String key);

  /**
   * Return all parameters that can be read
   * 
   * @return The possibleParameters value
   */
  public Collection<String> getPossibleParameters();

  /**
   * Return list of all parameters that can be set.
   * 
   * @return The setableParameters value
   */
  public Collection<String> getSetableParameters();
}

