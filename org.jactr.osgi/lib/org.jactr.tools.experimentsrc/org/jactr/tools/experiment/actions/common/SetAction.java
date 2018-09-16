package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class SetAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SetAction.class);
  
  private IExperiment _experiment;
  private String _name;
  private String _value;
  
  public SetAction(String variableName, String value, IExperiment experiment)
  {
    _experiment = experiment;
    _name = variableName;
    _value = value;
  }

  public void fire(IVariableContext context)
  {
    String value = _experiment.getVariableResolver().resolve(_value, context).toString();
    _experiment.getVariableResolver().addAlias(_name, value);
  }

}
