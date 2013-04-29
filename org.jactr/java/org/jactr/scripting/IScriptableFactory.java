package org.jactr.scripting;

/*
 * default logging
 */
import org.jactr.scripting.action.IActionScript;
import org.jactr.scripting.condition.IConditionScript;

public interface IScriptableFactory
{

  /**
   * description of the scripting language
   * 
   * @return
   */
  public String getDescription();

  public String getLanguageName();

  /**
   * test the language name to see if we support it. Case cannot be assured, so
   * be tolerant
   * 
   * @param scriptName
   * @return
   */
  public boolean supports(String scriptName);

  /**
   * global variable context for the factory. That is, all scripts using this
   * language will have the same global context
   * 
   * @return
   */
  public Object getGlobalContext();

  public void setVariable(String label, Object value, Object variableContext);

  public Object getVariable(String label, Object variableContext);

  public IActionScript createActionScript(String script) throws Exception;

  public IConditionScript createConditionScript(String script) throws Exception;
}
