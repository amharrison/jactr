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

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.utils.ModelerException;

/**
 * The ExecuteAction is a convenience IAction that permits the execution of an
 * arbitrary Runnable class?s run method. When the ExecuteAction is fired, the
 * class is instantiated and the reflection mechanism is used to get the run
 * method and calls it.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class ExecuteAction extends DefaultAction
{

  // logger class
  private static transient Log LOGGER = LogFactory.getLog(ExecuteAction.class
                                          .getName());

  /*
   * Class name
   */
  /**
   * Description of the Field
   */
  public String                _className;

  /**
   * Constructor that takes the fully qualified class name of the Runnable
   * class. Note: the class must use an empty constructor. Later implementations
   * will support the specification of specific class instances. [Why not
   * already? Everything needs to be serializable to XML. Providing access to
   * arbitrary objects violates this requirement]
   * 
   * @param className
   *            Description of the Parameter
   */
  public ExecuteAction(String className)
  {
    _className = className;
  }

  /**
   * Constructor for the ExecuteAction object
   */
  public ExecuteAction()
  {
    this(null);
  }



  public IAction bind(VariableBindings variableBindings)
  {
    return new ExecuteAction(getClassName());
  }

  /**
   * Gets the className attribute of the ExecuteAction object
   * 
   * @return The className value
   */
  public String getClassName()
  {
    return _className;
  }

  /**
   * Sets the className attribute of the ExecuteAction object
   * 
   * @param name
   *            The new className value
   */
  public void setClassName(String name)
  {
    _className = name;
  }

  /**
   * Description of the Method
   * @param instantiation
   *            Description of the Parameter
   * 
   * @return Description of the Return Value
   */
  @SuppressWarnings("unchecked")
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    try
    {
      String[] arguments = new String[0];
      Class c = getClass().getClassLoader().loadClass(_className);
      // get the class
      Class[] args = new Class[1];
      args[0] = arguments.getClass();
      Method method = c.getMethod("main", args);
      // get main method
      Object[] params = new Object[1];
      params[0] = arguments;
      method.invoke(null, params);
      // invoke
    }
    catch (Exception e)
    {
      LOGGER.error("Execute error", e);
      throw new ModelerException(_className
          + ".main(String[]) invocation error.", e,
          "Are you sure you spelled the class name correctly? Is it in your path?");
    }
    return 0.0;
  }

}
