package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class WaitForACTRAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(WaitForACTRAction.class);

  public void fire(IVariableContext context)
  {
    IController controller = ACTRRuntime.getRuntime().getController();
    if (controller == null)
      throw new IllegalStateException(
          "No runtime controller available to wait on");

    try
    {
      Future<Boolean> future = controller.waitForStart();
      if (!future.get())
        throw new IllegalStateException("Runtime has stopped prematurely");
    }
    catch (IllegalStateException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Was unable to wait for ACTR ", e);
    }
  }

}
