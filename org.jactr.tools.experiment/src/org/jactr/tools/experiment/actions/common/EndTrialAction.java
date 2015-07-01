package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.trial.ITrial;

public class EndTrialAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EndTrialAction.class);

  private IExperiment                _experiment;

  public EndTrialAction(IExperiment experiment)
  {
    _experiment = experiment;
  }

  public void fire(IVariableContext context)
  {
    ITrial current = _experiment.getTrial();
    if (current != null) if (current.isRunning()) current.stop();
  }

}
