package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;

public class StartTrigger extends ImmediateTrigger
{
  public StartTrigger(IExperiment experiment)
  {
    super(experiment);
  }

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StartTrigger.class);

  

}
