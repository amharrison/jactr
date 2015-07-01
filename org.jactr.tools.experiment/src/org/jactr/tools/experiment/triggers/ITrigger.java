package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import org.jactr.tools.experiment.actions.IAction;

public interface ITrigger
{
  public void add(IAction action);
  //triggers, are armed on firing, but not fired.
  public void add(ITrigger trigger);
  
  public void setArmed(boolean arm);
  public boolean isArmed();
}
