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
import org.jactr.tools.experiment.lock.LockManager;
import org.jactr.tools.experiment.misc.ExperimentUtilities;

public class IsUnlockedCondition implements ICondition
{

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    clone(model, variableBindings);

    if (!variableBindings.isBound("lock"))
      throw new CannotMatchException("No lock name was found");

    String lockName = variableBindings.get("lock").toString();

    IExperiment experiment = ExperimentUtilities.getModelsExperiment(model);

    if (experiment == null)
      throw new CannotMatchException(String.format(
          "No experiment defined for %s", model));

    LockManager manager = experiment.getLockManager();

    if (!manager.lockExists(lockName))
      throw new CannotMatchException(lockName + " does not exist");

    if (manager.isLocked(lockName))
      throw new CannotMatchException(lockName + " is still locked");

    return 0;
  }

  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    IExperiment experiment = ExperimentUtilities.getModelsExperiment(model);

    if (experiment == null)
      throw new CannotMatchException("No experiment accessible");

    return this;
  }

  public void dispose()
  {

  }

}
