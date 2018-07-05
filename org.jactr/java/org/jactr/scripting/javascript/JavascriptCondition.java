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
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.match.ExceptionMatchFailure;
import org.jactr.core.utils.ModelerException;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptSupport;
import org.jactr.scripting.ScriptingManager;
import org.jactr.scripting.condition.IConditionScript;
import org.jactr.scripting.condition.ScriptExecutionFailure;

public class JavascriptCondition implements IConditionScript
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
      .getLog(JavascriptCondition.class);

  private final IScriptableFactory   _factory;

  private final String               _script;

  private final CompiledScript       _compiledScript;

  private boolean                    _hasFiredCleanly = false;

  private boolean                    _cleanResult     = false;

  public JavascriptCondition(String script, IScriptableFactory factory)
  {
    _factory = factory;
    _script = script;

    try
    {
      _compiledScript = ((Compilable) ScopeManager.getEngine())
          .compile(_script);
    }
    catch (Exception ioe)
    {
      LOGGER.error("Error in scriptable condition", ioe);

      throw new ModelerException("Error in Scriptable ICondition", ioe,
          "double check your sytanx. The script parser detected an error");
    }
  }

  private JavascriptCondition(String script, CompiledScript compiled,
      IScriptableFactory factory)
  {
    _script = script;
    _factory = factory;
    _compiledScript = compiled;
  }

  public void dispose()
  {
    // noop since we reuse this object in clone
  }

  public IScriptableFactory getFactory()
  {
    return _factory;
  }

  public IConditionScript clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    return new JavascriptCondition(_script, _compiledScript, _factory);
  }

  public int bind(ScriptSupport scriptSupport, IModel model,
      VariableBindings variableBindings, boolean isIterative)
      throws CannotMatchException
  {
    try
    {
      if (!execute(scriptSupport, model, variableBindings))
        throw new CannotMatchException(
            new ScriptExecutionFailure("Script evaluated to false"));
    }
    catch (CannotMatchException cme)
    {
      if (!isIterative) throw cme;
      return 1;
    }
    return 0;
  }

  public String getScript()
  {
    return _script;
  }

  protected boolean execute(ScriptSupport scriptSupport, IModel model,
      VariableBindings variableBindings) throws CannotMatchException
  {
    /*
     * we cache the result if it has fired cleanly (no exceptions). This allows
     * us to avoid multiple calls during iterative instantiation process where
     * bind will be called repeatedly.
     */
    if (_hasFiredCleanly) return _cleanResult;
    _cleanResult = false;

    // enter the context for the thread and get the shared scope for
    // the model..

    try
    {
      // enter the context for the thread and get the shared scope for
      // the model..

      ScriptContext scope = ScopeManager
          .newScope(ScopeManager.getScopeForModel(model));
      ScopeManager.defineVariable(scope, "jactr", scriptSupport);

      ScriptingManager.configureScripting(_factory, model, scriptSupport,
          scope);

      _compiledScript.eval(scope);

      // and fire that beatch
      boolean matched = false;
      Object[] args = {};
      Boolean result = (Boolean) ((Invocable) ScopeManager.getEngine())
          .invokeFunction("matches", args);
      try
      {
        matched = result.booleanValue();
        _hasFiredCleanly = true;
        _cleanResult = matched;
      }
      catch (Exception e)
      {
        matched = false;
      }

      return matched;

    }
    catch (NoSuchMethodException nse)
    {
      throw new CannotMatchException(
          new ScriptExecutionFailure("Could not find function matches()"));
    }
    catch (ScriptException jse)
    {
      LOGGER.error("Error in scriptable condition", jse);

      throw new CannotMatchException(
          new ExceptionMatchFailure(null, String.format("%s.script syntax",
              variableBindings.get("=production")), jse));
    }

  }
}
