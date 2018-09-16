package org.jactr.scripting.condition;

/*
 * default logging
 */

import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.match.ExceptionMatchFailure;
import org.jactr.core.production.condition.match.IMatchFailure;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptSupport;

/**
 * delegate for scripted conditions to extend, allowing the generic
 * {@link ScriptableCondition} to handle the majority of the heavy lifting,
 * leaving this to just evaluate the script. If the {@link #clone(IModel, VariableBindings)}
 * or {@link #bind(ScriptSupport, IModel, VariableBindings, boolean)} methods throw a
 * {@link CannotMatchException}, they should use {@link IMatchFailure}s such as
 * {@link ScriptExecutionFailure} or {@link ExceptionMatchFailure}. If the
 * script code itself throws a CME, it can merely be passed up.
 * 
 * @author harrison
 */
public interface IConditionScript
{

  public String getScript();

  /**
   * clone a copy of this condition for use in binding.
   * 
   * @param model
   * @param variableBindings
   * @return
   * @throws CannotMatchException
   */
  public IConditionScript clone(IModel model,
      VariableBindings variableBindings) throws CannotMatchException;

  /**
   * iteratively bind this condition.
   * 
   * @param model
   * @param variableBindings
   * @param isIterative
   *          false if this is the final call. If so, any unresolved bindings
   *          should result in a CannotMatchException
   * @return the number of variables still unresolved
   * @throws CannotMatchException
   */
  public int bind(ScriptSupport scriptSupport, IModel model,
      VariableBindings variableBindings, boolean isIterative)
      throws CannotMatchException;

  public void dispose();

  public IScriptableFactory getFactory();
}
