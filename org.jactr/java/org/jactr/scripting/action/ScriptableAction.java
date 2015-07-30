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

package org.jactr.scripting.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultAction;
import org.jactr.core.production.action.IAction;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptingManager;

/**
 * ScriptableAction allows for custom actions. You set the script via
 * setScript(String ) - it must contain a function fire(model, production,
 * bindings) { }
 * 
 * @author harrison
 * @created April 18, 2003
 */

public class ScriptableAction extends DefaultAction
{

  static private transient Log LOGGER = LogFactory
                                          .getLog(ScriptableAction.class);

  private final IActionScript  _script;

  /**
   * Constructor for the ScriptableAction object
   */
  public ScriptableAction(IActionScript script)
  {
    _script = script;
  }

  /**
   * Description of the Method
   */
  @Override
  public void dispose()
  {
    _script.dispose();
    super.dispose();
  }

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    ScriptableAction sa = new ScriptableAction(_script.bind(variableBindings));
    return sa;
  }

  /**
   * Gets the script attribute of the ScriptableAction object
   * 
   * @return The script value
   */
  public String getScript()
  {
    return _script.getScript();
  }

  public IScriptableFactory getFactory()
  {
    return _script.getFactory();
  }

  /**
   * Description of the Method
   * 
   * @param instantiation
   *          Description of the Parameter
   * @param bindings
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    return _script.fire(
        ScriptingManager.newScriptSupport(getFactory(), instantiation),
        instantiation, firingTime);
  }
}