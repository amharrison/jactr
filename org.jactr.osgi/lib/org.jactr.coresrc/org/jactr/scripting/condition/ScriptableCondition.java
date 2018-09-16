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

package org.jactr.scripting.condition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.match.IMatchFailure;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptSupport;
import org.jactr.scripting.ScriptingManager;

/**
 * ScriptableCondition allows for custom actions. You set the script via
 * setScript(String ) - it must contain a function matches(model, chunk,
 * bindings) { return {true | false}; }
 * 
 * @author harrison
 * @created April 18, 2003
 */

public class ScriptableCondition implements ICondition
{

  static private transient Log   LOGGER = LogFactory
                                            .getLog(ScriptableCondition.class);

  private final IConditionScript _conditionScript;

  private ScriptSupport          _scriptableSupport;

  /**
   * Constructor for the ScriptableCondition object
   */
  public ScriptableCondition(IConditionScript script)
  {
    _conditionScript = script;
  }

  public ScriptableCondition clone(IModel model, VariableBindings bindings)
      throws CannotMatchException
  {
    ScriptableCondition condition = new ScriptableCondition(
        _conditionScript.clone(model, bindings));
    condition._scriptableSupport = ScriptingManager.newScriptSupport(
        _conditionScript.getFactory(), model, bindings);
    return condition;
  }

  /**
   * Description of the Method
   */
  public void dispose()
  {
    _conditionScript.dispose();
  }

  /**
   * Gets the script attribute of the ScriptableCondition object
   * 
   * @return The script value
   */
  public String getScript()
  {
    return _conditionScript.getScript();
  }

  public IScriptableFactory getFactory()
  {
    return _conditionScript.getFactory();
  }

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    try
    {
      return _conditionScript.bind(_scriptableSupport, model, variableBindings,
          isIterative);
    }
    catch (CannotMatchException cme)
    {
      IMatchFailure mf = cme.getMismatch();
      if (mf != null) mf.setCondition(this);
      throw cme;
    }
  }
}