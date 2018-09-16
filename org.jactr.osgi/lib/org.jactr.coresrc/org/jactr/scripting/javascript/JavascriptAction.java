package org.jactr.scripting.javascript;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptException;

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
import org.jactr.scripting.ScriptingManager;
import org.jactr.scripting.action.IActionScript;

public class JavascriptAction implements IActionScript
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(JavascriptAction.class);

  private final IScriptableFactory   _factory;

  private final String               _script;

  private final CompiledScript       _compiledScript;

  public JavascriptAction(String script, IScriptableFactory factory)
  {
    _script = script;
    _factory = factory;

    try
    {
      _compiledScript = ((Compilable) ScopeManager.getEngine())
          .compile(_script);
    }
    catch (Exception ioe)
    {
      LOGGER.error("Could not compile script: " + _script);
      throw new ModelerException("Error in Scriptable IAction", ioe,
          "double check your sytanx. The script parser detected an error");
    }
  }

  private JavascriptAction(String script, CompiledScript compiled,
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

    ScriptContext scope = ScopeManager
        .newScope(ScopeManager
        .getScopeForModel(model));
    ScopeManager.defineVariable(scope, "jactr", scriptSupport);

    ScriptingManager.configureScripting(_factory, model, scriptSupport, scope);

    try
    {
      _compiledScript.eval(scope);
    }
    catch (ScriptException jse)
    {
      LOGGER.error("Error in scriptable condition", jse);
      throw new ModelerException("Error in Scriptable IAction", jse,
          "double check your sytanx in " + instantiation
              + ". The script was unable to be run..");
    }

    // and fire that beatch
    double fireTime = 0;
    try
    {
      Object[] args = {};
      Number result = (Number) ((Invocable) ScopeManager.getEngine())
          .invokeFunction("fire", args);

      fireTime = result.doubleValue();
      return fireTime;
    }
    catch (NoSuchMethodException e)
    {
      throw new ModelerException("Could not find fire() in script", null,
          "ScriptableActions must defined function fire(instantiation)");
    }
    catch (ScriptException jse2)
    {
      LOGGER.error("Error in scriptable action", jse2);
      throw new ModelerException(
          "Error in Scriptable IAction",
          jse2,
          "double check your sytanx in "
              + instantiation
              + ". Scripting failed to execute fire(model, production, bindings)");
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
