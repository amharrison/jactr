package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class NamedTrigger implements ITrigger
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(NamedTrigger.class);

  static public final String NAME_ATTR = "name";
  
  private IExperiment                _experiment;

  private boolean                    _isArmed = false;
  
  private Collection<IAction> _actions = new ArrayList<IAction>();
  private Collection<ITrigger> _triggers = new ArrayList<ITrigger>();
  
  private String _name;
  
  public NamedTrigger(String name, IExperiment experiment)
  {
    _experiment = experiment;
    _name = _experiment.getVariableResolver().resolve(name, experiment.getVariableContext()).toString();
  }

  public void add(IAction action)
  {
    _actions.add(action);
  }
  
  public void add(ITrigger trigger)
  {
    _triggers.add(trigger);
  }
  
  public void fire(IVariableContext context)
  {
    for(ITrigger trigger : _triggers)
      trigger.setArmed(true);
    
    for(IAction action : _actions)
      action.fire(context);
    
  }
  
  public String getName()
  {
    return _name;
  }

 
  public boolean isArmed()
  {
    return _isArmed;
  }

  public void setArmed(boolean arm)
  {
    if (arm)
    {
      if (isArmed()) return;
      _experiment.getTriggerManager().add(this);
      _isArmed = true;
    }
    else
    {
      _experiment.getTriggerManager().remove(this);
      _isArmed = false;
    }
  }

}
