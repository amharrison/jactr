package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;

public class EndTrigger extends ImmediateTrigger
{
  public EndTrigger(IExperiment experiment)
  {
    super(experiment);
    // TODO Auto-generated constructor stub
  }

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EndTrigger.class);

}
