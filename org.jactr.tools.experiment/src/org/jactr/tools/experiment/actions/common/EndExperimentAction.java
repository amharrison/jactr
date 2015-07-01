package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class EndExperimentAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EndExperimentAction.class);
  
  private final IExperiment _experiment;
  
  public EndExperimentAction(IExperiment experiment)
  {
    _experiment = experiment;
  }

  public void fire(IVariableContext context)
  {
    _experiment.stop();
  }

}
