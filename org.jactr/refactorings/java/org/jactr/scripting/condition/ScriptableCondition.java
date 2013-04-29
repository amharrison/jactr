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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;
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
 * ScriptableCondition allows for custom actions. You set the script via
 * setScript(String ) - it must contain a function matches(model, chunk,
 * bindings) { return {true | false}; }
 * 
 * @author harrison
 * @created April 18, 2003
 */

public class ScriptableCondition implements ICondition
{

  static private transient Log LOGGER           = LogFactory
                                                    .getLog(ScriptableCondition.class);

  /**
   * Description of the Field
   */
  public final static String   DEFAULT_SCRIPT   =
                                                    "/* match with this custom condition \n*/\n"
                                                        + "function matches()\n"
                                                        + "{\n//insert custom code here\n"
                                                        + "\treturn true; //return true iff the match succeeds\n"
                                                    + "}";

  /**
   * Description of the Field
   */
  protected String             _scriptString;

  /**
   * Description of the Field
   */
  protected transient Script   _script;

  protected boolean            _hasFiredCleanly = false;

  protected boolean            _cleanResult;

  /**
   * Constructor for the ScriptableCondition object
   */
  public ScriptableCondition()
  {
    this(DEFAULT_SCRIPT);
  }

  /**
   * Constructor for the ScriptableCondition object
   * 
   * @param script
   *          Description of the Parameter
   */
  public ScriptableCondition(String script)
  {
    setScriptString(script);
  }

  /**
   * Constructor for the ScriptableCondition object
   * 
   * @param scriptString
   *          Description of the Parameter
   * @param script
   *          Description of the Parameter
   */
  protected ScriptableCondition(String scriptString, Script script)
  {
    _scriptString = scriptString;
    setScript(script);
  }

  public ScriptableCondition clone(IModel model, Map<String, Object> bindings)
      throws CannotMatchException
  {
    return new ScriptableCondition(_scriptString, _script);
  }

  /**
   * Description of the Method
   */
  public void dispose()
  {
    _script = null;
    _scriptString = null;
  }

  /**
   * Gets the script attribute of the ScriptableCondition object
   * 
   * @return The script value
   */
  public String getScript()
  {
    return _scriptString;
  }

  /**
   * Sets the script attribute of the ScriptableCondition object
   * 
   * @param str
   *          The new script value
   */
  public void setScriptString(String str)
  {
    _scriptString = str.trim();
    compileScript();
  }

  private void setScript(Script script)
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
      _script = cx.compileString(_scriptString, "ScriptableCondition", 0, null);
    }
    catch (Exception ioe)
    {
      Context.exit();
      LOGGER.error("Error in scriptable condition", ioe);
      throw new ModelerException("Error in Scriptable ICondition", ioe,
          "double check your sytanx. The script parser detected an error");
    }
  }

  /**
   * ok, this really needs to be rewritten - this is hideous TODO rewrite
   * 
   * @param model
   * @param variableBindings
   * @throws CannotMatchException
   */
  protected boolean execute(IModel model, Map<String, Object> variableBindings)
      throws CannotMatchException
  {
    /*
     * we cache the result if it has fired cleanly (no exceptions). This allows
     * us to avoid multiple calls during iterative instantiation process where
     * bind will be called repeatedly.
     */
    if (_hasFiredCleanly) return _cleanResult;
    _cleanResult = false;

    if (_script == null) compileScript();
    // enter the context for the thread and get the shared scope for
    // the model..
    Context cx = Context.enter();
    Scriptable scope = ScopeManager.newScope(ScopeManager
        .getScopeForModel(model));
    ScopeManager.defineVariable(scope, "jactr", new ScriptSupport(model,
        variableBindings));

    try
    {
      // is matches(model, bindings) is already defined, this will
      // overwrite it..
      _script.exec(cx, scope);
    }
    catch (WrappedException we)
    {
      Throwable cause = we.getWrappedException();
      if (cause instanceof CannotMatchException)
        throw (CannotMatchException) cause;

      if (cause instanceof RuntimeException) throw (RuntimeException) cause;

      throw new RuntimeException(cause);
    }
    catch (JavaScriptException jse)
    {
      Context.exit();
      LOGGER.error("Error in scriptable condition", jse);
      throw new ModelerException("Error in Scriptable ICondition", jse,
          "double check your sytanx in " + variableBindings.get("=production")
              + ". The script was unable to be run..");
    }

    // let's get the function fire(model, prod, bindings)
    Object matches = ScriptableObject.getProperty(scope, "matches");
    if (!(matches instanceof Function))
    {
      Context.exit();
      throw new ModelerException("Could not find matches() in script", null,
          "ScriptableActions must defined function matches(model, bindings)");
    }

    // and fire that beatch
    boolean matched = false;
    try
    {
      Object[] args = {};
      Object result = ((Function) matches).call(cx, scope, scope, args);
      try
      {
        matched = Context.toBoolean(result);
        _hasFiredCleanly = true;
        _cleanResult = matched;
      }
      catch (Exception e)
      {
        matched = false;
      }

      return matched;
    }
    catch (WrappedException we)
    {
      Throwable cause = we.getWrappedException();
      if (cause instanceof CannotMatchException)
        throw (CannotMatchException) cause;

      if (cause instanceof RuntimeException) throw (RuntimeException) cause;

      throw new RuntimeException(cause);
    }
    catch (JavaScriptException jse2)
    {
      LOGGER.error("Error in scriptable condition", jse2);
      throw new ModelerException(
          "Error in Scriptable ICondition",
          jse2,
          "double check your sytanx in "
              + variableBindings.get("=production")
              + ". Scripting failed to execute fire(model, production, bindings)");
    }
    finally
    {
      Context.exit();
    }
  }

  // /**
  // * @see
  // org.jactr.core.production.condition.ICondition#bind(org.jactr.core.model.IModel,
  // * java.util.Map)
  // */
  // public ICondition bind(IModel model, Map<String, Object> variableBindings)
  // throws CannotMatchException
  // {
  // ScriptableCondition sc = new ScriptableCondition(_scriptString, _script);
  // if(!sc.execute(model, variableBindings))
  // throw new CannotMatchException("Script evaluated to false");
  // return sc;
  // }
  //

  public int bind(IModel model, Map<String, Object> variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    try
    {
      if (!execute(model, variableBindings))
        throw new CannotMatchException("Script evaluated to false");
    }
    catch (CannotMatchException cme)
    {
      if (!isIterative) throw cme;
      return 1;
    }
    return 0;
  }
}