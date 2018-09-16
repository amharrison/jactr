package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;

public class ImmediateTrigger implements ITrigger
{
 

  private Collection<IAction>        _actions = new ArrayList<IAction>();
  private Collection<ITrigger> _triggers = new ArrayList<ITrigger>();
  
  private IExperiment _experiment;
  
  public ImmediateTrigger(IExperiment experiment)
  {
    _experiment = experiment;
  }

  public void add(IAction action)
  {
    _actions.add(action);
  }
  
  public void add(ITrigger trigger)
  {
    _triggers.add(trigger);
  }

  
  public boolean isArmed()
  {
    return false;
  }

  public void setArmed(boolean arm)
  {
    if (arm)
    {
      for(ITrigger trigger : _triggers)
        trigger.setArmed(true);
      
      for (IAction action : _actions)
        action.fire(_experiment.getVariableContext());
    }
  }

}
