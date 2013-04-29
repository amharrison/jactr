package org.jactr.scripting.javascript;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.action.IActionScript;
import org.jactr.scripting.condition.IConditionScript;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JavascriptFactory implements IScriptableFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(JavascriptFactory.class);

  public IActionScript createActionScript(String script) throws Exception
  {
    return new JavascriptAction(script, this);
  }

  public IConditionScript createConditionScript(String script) throws Exception
  {
    return new JavascriptCondition(script, this);
  }

  public String getDescription()
  {
    return "Mozilla Rhino Javascript";
  }

  public String getLanguageName()
  {
    return "javascript";
  }

  public Object getGlobalContext()
  {
    return ScopeManager.getPublicScope();
  }

  public Object getVariable(String label, Object variableContext)
  {
    return ScriptableObject.getProperty((Scriptable) variableContext, label);
  }

  public void setVariable(String label, Object value, Object variableContext)
  {
    ScopeManager.defineVariable((Scriptable) variableContext, label, value);
  }

  public boolean supports(String scriptName)
  {
    return scriptName.equalsIgnoreCase("javascript")
        || scriptName.equalsIgnoreCase("js");
  }

}
