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

package org.jactr.core.production.action;


import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;

/**
 * BindAction is used merely for the assignment of variables that are not
 * explicitly imbedded in any other action. The constructor accepts a
 * variableName (?=variableName?) and any value that will be bound to. When the
 * bind action is bound, the variable binding will be added to the Map making it
 * accessible for subsequent Actions to utilize.
 * 
 * @author harrison
 * @created April 18, 2003
 */

public class BindAction extends DefaultAction
{

  

  /**
   * Description of the Field
   */
  public String                _variableName;

  /**
   * Description of the Field
   */
  public Object                _object;

  /**
   * Constructor for the BindAction object
   * 
   * @param variableName
   *            Description of the Parameter
   * @param someValue
   *            Description of the Parameter
   */
  public BindAction(String variableName, Object someValue)
  {
    setVariableName(variableName);
    setObject(someValue);
  }

  public IAction bind(VariableBindings variableBindings)
  {
    Object obj = getObject();

    if (obj instanceof String && ((String) obj).startsWith("=")
        && variableBindings.isBound((String) obj))
      obj = resolve((String) obj, variableBindings);

    BindAction copy = new BindAction(getVariableName(), obj);
    return copy;
  }

  /**
   * Gets the variableName attribute of the BindAction object
   * 
   * @return The variableName value
   */
  public String getVariableName()
  {
    return _variableName;
  }

  /**
   * Sets the variableName attribute of the BindAction object
   * 
   * @param name
   *            The new variableName value
   */
  public void setVariableName(String name)
  {
    _variableName = name;
  }

  /**
   * Gets the object attribute of the BindAction object
   * 
   * @return The object value
   */
  public Object getObject()
  {
    return _object;
  }

  /**
   * Sets the object attribute of the BindAction object
   * 
   * @param ob
   *            The new object value
   */
  public void setObject(Object ob)
  {
    _object = ob;
  }

  /**
   * 
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    instantiation.getVariableBindings().bind(getVariableName(), getObject());
    return 0.0;
  }
}
