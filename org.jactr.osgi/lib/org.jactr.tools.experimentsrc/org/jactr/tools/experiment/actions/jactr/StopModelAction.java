package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.AbstractAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class StopModelAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StopModelAction.class);

  public StopModelAction(IExperiment experiment)
  {
    super(experiment);
  }

  @Override
  protected void fire(IModel model, IExperiment experiment,
      IVariableContext context)
  {
    if (model == null)
    {
      LOGGER.error("No model provided, ignoring");
      return;
    }

    double when = ACTRRuntime.getRuntime().getClock(model).getTime();

    RunnableTimedEvent stopEvent = new RunnableTimedEvent(when, () -> {
      if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("Stopping [%s]", model.getName()));
      throw new ModelTerminatedException("at experiment request");
    });

    model.getTimedEventQueue().enqueue(stopEvent);
  }

}
