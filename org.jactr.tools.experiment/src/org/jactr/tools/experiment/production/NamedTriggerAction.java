package org.jactr.tools.experiment.production;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.IAction;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableContext;
import org.jactr.tools.experiment.misc.ExperimentUtilities;

public class NamedTriggerAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NamedTriggerAction.class);

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return this;
  }

  public void dispose()
  {
    // noop
  }

  public double fire(IInstantiation instantiation, double firingTime)
  {
    VariableBindings bindings = instantiation.getVariableBindings();

    if (!bindings.isBound("trigger"))
      throw new IllegalStateException("slot trigger must be defined");

    IExperiment experiment = ExperimentUtilities
        .getModelsExperiment(instantiation.getModel());
    if (experiment == null)
      throw new IllegalStateException(String.format(
          "Could not find experiment for this model %s",
          instantiation.getModel()));

    String triggerName = bindings.get("trigger").toString();

    /*
     * create a new context..
     */
    IVariableContext child = new VariableContext(
        experiment.getVariableContext());
    for (String key : bindings.getVariables())
      child.set(key, bindings.get(key));

    if (!experiment.getTriggerManager().fire(triggerName, child))
      throw new IllegalArgumentException("No triggers could be found named "
          + triggerName);

    return 0;
  }

}
