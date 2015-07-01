package org.jactr.tools.experiment.actions;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.misc.ExperimentUtilities;

/**
 * abstract action implementation meant primarily for actions that require access to
 * the model that is invoking it.
 * 
 * 
 * @author harrison
 *
 */
public abstract class AbstractAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractAction.class);

  protected final IExperiment _experiment;
  
  protected AbstractAction(IExperiment experiment)
  {
   _experiment = experiment;  
  }
  /**
   * called when the action fires
   * @param model, may be null if the action wasn't called from a model
   * @param experiment
   * @param context
   */
  abstract protected void fire(IModel model, IExperiment experiment, IVariableContext context);
  
  public void fire(IVariableContext context)
  {
    IModel model = getModel(context); //via context
    if(model==null)
       model = ExperimentUtilities.getExperimentsModel(_experiment);
    
    fire(model, _experiment, context);
  }

  public IModel getModel(IVariableContext context)
  {
    return (IModel) context.get("=model"); //set by jACT-R
  }
  
 
}
