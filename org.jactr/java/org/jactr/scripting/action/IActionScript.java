package org.jactr.scripting.action;


import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptSupport;

/*
 * default logging
 */

public interface IActionScript
{

  /**
   * return the actual script
   * 
   * @return
   */
  public String getScript();

  /**
   * return an instanceof this action script that has been fully bound. If a new
   * instanceof of the script is not required (that is, it can be reused) this
   * can be returned.
   * 
   * @param variableBindings
   * @return
   */
  public IActionScript bind(VariableBindings variableBindings)
      throws CannotInstantiateException;

  /**
   * Actually execute the script, returning the time it takes to complete (which
   * is added to the production firing time)
   * 
   * @param scriptSupport
   * @param instantiation
   * @param firedAt
   * @return additional time it takes to fire this condition (typically 0)
   */
  public double fire(ScriptSupport scriptSupport, IInstantiation instantiation,
      double firedAt);

  public void dispose();

  public IScriptableFactory getFactory();
}
