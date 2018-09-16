package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.AbstractAction;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.misc.ModelUtilities;

public class ProxyAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProxyAction.class);

  private AbstractAction             _actualAction;

  private String                     _models;

  private IExperiment                _experiment;

  public ProxyAction(String className, String models, IExperiment experiment)
  {
    _experiment = experiment;
    _models = models;
    try
    {
      _actualAction = (AbstractAction) getClass().getClassLoader().loadClass(
          className).newInstance();
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException(
          "Could not create AbstractAction from " + className, e);
    }
  }

  public void fire(IVariableContext context)
  {
    IVariableContext child = new VariableContext(_experiment.getVariableContext());
    
    child.set("=experiment", _experiment);

    Collection<IModel> models = VariableResolver.getModels(_models,
        _experiment.getVariableResolver(), context);
    if (_models.length() == 0)
    {
      /*
       * local execution
       */
      _actualAction.fire(context);
    }
    else
    {
      for (IModel model : models)
      {
        final IVariableContext finalContext = new VariableContext(child);
        finalContext.set("=model", model);

        ModelUtilities.executeLater(model, new Runnable() {

          public void run()
          {
            try
            {
              _actualAction.fire(finalContext);
            }
            catch (Exception e)
            {
              LOGGER.error("Failed to execute "
                  + _actualAction.getClass().getName(), e);
            }
          }
        });
      }
    }
  }

}
