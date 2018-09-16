package org.jactr.tools.experiment.actions;

/*
 * default logging
 */

public interface ICompositeAction extends IAction
{

  public void add(IAction action);
}
