package org.jactr.tools.experiment.production;

/*
 * default logging
 */
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tools.experiment.trial.ITrial;

public class TrialHasStartedCondition implements ICondition
{

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    clone(model, variableBindings);
    return 0;
  }

  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    IExperiment experiment = ExperimentUtilities.getModelsExperiment(model);
    
    if(experiment==null)
      throw new CannotMatchException("No experiment accessible");
    
    ITrial trial = experiment.getTrial();
    if(trial==null)
      throw new CannotMatchException("No trial is available");
    
    if(!trial.isRunning())
      throw new CannotMatchException("Trial is not running");

    return this;
  }

  public void dispose()
  {

  }

}
