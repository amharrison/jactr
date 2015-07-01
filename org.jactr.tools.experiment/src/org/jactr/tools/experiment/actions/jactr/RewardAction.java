package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tools.experiment.misc.ModelUtilities;

public class RewardAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RewardAction.class);

  private String                     _models;

  private String                     _reward;

  private IExperiment                _experiment;

  public RewardAction(String models, String reward, IExperiment experiment)
  {
    _experiment = experiment;
    _models = models;
    _reward = reward;
  }

  

  public void fire(IVariableContext context)
  {
    Collection<IModel> models = null;
    if (!_models.equals(""))
      models = VariableResolver.getModels(_models,
          _experiment.getVariableResolver(), context);
    else
    {
      IModel model = ExperimentUtilities.getExperimentsModel(_experiment);
      if (model != null) models = Collections.singleton(model);
    }
    double reward = getReward(context);

    if (models.size() == 0)
      LOGGER.error("No clue what models to reward. " + _models
          + " did not resolve to any recognized models.");

    for (IModel model : models)
      ModelUtilities.reward(model, reward);
  }

  private double getReward(IVariableContext context)
  {
    String rewardString = _experiment.getVariableResolver().resolve(_reward,
        context).toString();
    double reward = Double.NaN;
    try
    {
      reward = Double.parseDouble(rewardString);
    }
    catch (NumberFormatException nfe)
    {
      LOGGER.warn(String.format("Could not convert %1$s to number for reward, using NaN",
          rewardString));
    }
    return reward;
  }

}
