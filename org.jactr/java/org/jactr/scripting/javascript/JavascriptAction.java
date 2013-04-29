package org.jactr.scripting.javascript;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.utils.ModelerException;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptSupport;
import org.jactr.scripting.action.IActionScript;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;

public class JavascriptAction implements IActionScript
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(JavascriptAction.class);

  private final IScriptableFactory   _factory;

  private final String               _script;

  private final Script               _compiledScript;

  public JavascriptAction(String script, IScriptableFactory factory)
  {
    _script = script;
    _factory = factory;

    ScopeManager.getPublicScope();
    Context cx = Context.enter();
    try
    {
      _compiledScript = cx.compileString(_script, "ScriptableAction", 0, null);
    }
    catch (Exception ioe)
    {
      LOGGER.error("Could not compile script: " + _script);
      throw new ModelerException("Error in Scriptable IAction", ioe,
          "double check your sytanx. The script parser detected an error");
    }
    finally
    {
      Context.exit();
    }
  }

  private JavascriptAction(String script, Script compiled,
      IScriptableFactory factory)
  {
    _script = script;
    _compiledScript = compiled;
    _factory = factory;
  }

  public IActionScript bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    // recycle
    return new JavascriptAction(_script, _compiledScript, _factory);
  }

  public void dispose()
  {
    // noop since we recycle
  }

  public double fire(ScriptSupport scriptSupport, IInstantiation instantiation,
      double firedAt)
  {
    IModel model = instantiation.getModel();

    // enter the context for the thread and get the shared scope for
    // the model..
    Context cx = Context.enter();
    Scriptable scope = ScopeManager.newScope(ScopeManager
        .getScopeForModel(model));
    ScopeManager.defineVariable(scope, "jactr", scriptSupport);

    try
    {
      _compiledScript.exec(cx, scope);
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

  public IScriptableFactory getFactory()
  {
    return _factory;
  }

  public String getScript()
  {
    return _script;
  }

}
