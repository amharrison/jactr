package org.jactr.tools.experiment.misc;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.bootstrap.StartModelExperiments;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;

public class ExperimentUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ExperimentUtilities.class);

  static public IExperiment getModelsExperiment(IModel model)
  {
    IExperiment exp = (IExperiment) model
        .getMetaData(StartModelExperiments.MODELS_EXPERIMENT);

    if (exp == null) // stored in ACTRRuntime? by StartExperiment
      exp = (IExperiment) ACTRRuntime.getRuntime().getApplicationData();
    return exp;
  }

  static public IModel getExperimentsModel(IExperiment experiment)
  {
    VariableResolver resolver = experiment.getVariableResolver();
    IVariableContext context = experiment.getVariableContext();
    Object resolved = resolver
        .resolve(
            String.format("${%s}", StartModelExperiments.EXPERIMENT_MODEL),
            context);
    IModel model = null;
    
    if(resolved instanceof IModel)
      model = (IModel) resolved;

    if (model == null) model = (IModel) resolver.resolve("${=model}", context);

    return model;
  }
}
