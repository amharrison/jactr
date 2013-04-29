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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.action.DefaultAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.utils.ModelerException;
import org.jactr.scripting.ScopeManager;
import org.jactr.scripting.ScriptSupport;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

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

  static private transient Log LOGGER         = LogFactory
                                                  .getLog(ScriptableAction.class);

  /**
   * Description of the Field
   */
  public final static String   DEFAULT_SCRIPT = "/* Fire this custom action \n*/\n"
                                                  + "function fire()\n"
                                                  + "{\n//insert custom code here\n"
                                                  + "\t\n" + "}";

  /**
   * Description of the Field
   */
  protected String             _scriptString;

  /**
   * Description of the Field
   */
  protected transient Script   _script;

  /**
   * Constructor for the ScriptableAction object
   */
  public ScriptableAction()
  {
    this(DEFAULT_SCRIPT);
  }

  /**
   * Constructor for the ScriptableAction object
   * 
   * @param script
   *          Description of the Parameter
   */
  public ScriptableAction(String script)
  {
    setScriptString(script);
  }

  /**
   * Constructor for the ScriptableAction object
   * 
   * @param scriptString
   *          Description of the Parameter
   * @param script
   *          Description of the Parameter
   */
  protected ScriptableAction(String scriptString, Script script)
  {
    _scriptString = scriptString;
    setScript(script);
  }

  /**
   * Description of the Method
   */
  @Override
  public void dispose()
  {
    _script = null;
    super.dispose();
  }

  public IAction bind(Map<String, Object> variableBindings)
  {
    ScriptableAction sa = new ScriptableAction(_scriptString, _script);

    return sa;
  }

  /**
   * Gets the script attribute of the ScriptableAction object
   * 
   * @return The script value
   */
  public String getScript()
  {
    return _scriptString;
  }

  /**
   * Sets the script attribute of the ScriptableAction object
   * 
   * @param str
   *          The new script value
   */
  public void setScriptString(String str)
  {
    _scriptString = str.trim();
    compileScript();
  }

  protected void setScript(Script script)
  {
    _script = script;
  }

  /**
   * Description of the Method
   */
  private void compileScript()
  {
    ScopeManager.getPublicScope();
    Context cx = Context.enter();
    try
    {
      _script = cx.compileString(_scriptString, "ScriptableAction", 0, null);
    }
    catch (Exception ioe)
    {
      LOGGER.error("Could not compile script: " + _scriptString);
      throw new ModelerException("Error in Scriptable IAction", ioe,
          "double check your sytanx. The script parser detected an error");
    }
    finally
    {
      Context.exit();
    }
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
    IModel model = instantiation.getModel();
    if (_script == null) compileScript();
    // enter the context for the thread and get the shared scope for
    // the model..
    Context cx = Context.enter();
    Scriptable scope = ScopeManager.newScope(ScopeManager
        .getScopeForModel(model));
    ScopeManager.defineVariable(scope, "jactr", new ScriptSupport(model,
        instantiation));
    try
    {
      // is fire(model, prod, bindings) is already defined, this will
      // overwrite it..
      _script.exec(cx, scope);
    }
    catch (JavaScriptException jse)
    {
      Context.exit();
      LOGGER.error("Error in scriptable condition", jse);
      throw new ModelerException("Error in Scriptable IAction", jse,
          "double check your sytanx in " + instantiation
              + ". The script was unable to be run..");
    }

    // let's get the function fire(model, prod, bindings)
    Object fire = ScriptableObject.getProperty(scope, "fire");
    if (!(fire instanceof Function))
    {
      Context.exit();
      throw new ModelerException("Could not find fire() in script", null,
          "ScriptableActions must defined function fire(instantiation)");
    }

    // and fire that beatch
    double fireTime = 0;
    try
    {
      Object[] args = {};
      Object result = ((Function) fire).call(cx, scope, scope, args);
      fireTime = Context.toNumber(result);
      return fireTime;
    }
    catch (WrappedException we)
    {
      Throwable cause = we.getWrappedException();
      if (cause instanceof RuntimeException) throw (RuntimeException) cause;

      throw new RuntimeException(cause);
    }
    catch (JavaScriptException jse2)
    {
      LOGGER.error("Error in scriptable action", jse2);
      throw new ModelerException(
          "Error in Scriptable IAction",
          jse2,
          "double check your sytanx in "
              + instantiation
              + ". Scripting failed to execute fire(model, production, bindings)");
    }
    finally
    {
      Context.exit();
    }
  }
}