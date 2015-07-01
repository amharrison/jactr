package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class TerminateAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TerminateAction.class);
  
  public TerminateAction()
  {
  }

  public void fire(IVariableContext context)
  {
    IController controller = ACTRRuntime.getRuntime().getController();
    if(controller!=null && controller.isRunning())
      controller.stop();
  }

}
