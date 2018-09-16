package org.jactr.tools.experiment.bootstrap;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.misc.ExperimentUtilities;

/**
 * starts the experiment
 * 
 * @author harrison
 */
public class StopModelExperiments implements Runnable
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER             = LogFactory
                                                            .getLog(StopModelExperiments.class);


  /*
   * for experiment variable context
   */
  static public final String         EXPERIMENT_MODEL   = "experiment.model";

  /*
   * for model metadata
   */
  static public final String         MODELS_EXPERIMENT  = "models.experiment";

  public void run()
  {

    /*
     * we create a separate experiment for each model
     */
    for (IModel model : ACTRRuntime.getRuntime().getModels())
    {

      IExperiment experiment = ExperimentUtilities.getModelsExperiment(model);
      experiment.stop();

      model.setMetaData(MODELS_EXPERIMENT, null);
      experiment.getVariableContext().set(EXPERIMENT_MODEL, null);

    }


  }


}
