package org.jactr.tools.experiment.bootstrap;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.scripting.IScriptConfigurator;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptSupport;
import org.jactr.tools.experiment.misc.ExperimentUtilities;

public class ScriptConfig implements IScriptConfigurator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ScriptConfig.class);

  @Override
  public void configure(IScriptableFactory factory, IModel model,
      ScriptSupport support, Object scope)
  {
    // blindly assuming javascript
    support.setGlobal("jactrExperiment",
        ExperimentUtilities.getModelsExperiment(model));
  }
}
