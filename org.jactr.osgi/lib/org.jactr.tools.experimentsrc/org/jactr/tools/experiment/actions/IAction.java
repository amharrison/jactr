package org.jactr.tools.experiment.actions;

import org.jactr.tools.experiment.impl.IVariableContext;

/*
 * default logging
 */

public interface IAction
{
  public void fire(IVariableContext context);
}
