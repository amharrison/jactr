/*
 * Created on Mar 19, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.tools.async.controller.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.handler.IMessageHandler;
import org.commonreality.net.session.ISessionInfo;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.IDebugController;
import org.jactr.tools.async.message.command.state.ModelStateCommand;

/**
 * Largely historic, as we no longer support individual model control, but only
 * runtime control. We continue to support it in case of older clients
 * 
 * @author developer
 */
public class ModelStateHandler implements IMessageHandler<ModelStateCommand>
{
  /**
   * logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelStateHandler.class);


  public ModelStateHandler()
  {
  }

  @Override
  public void accept(ISessionInfo session, ModelStateCommand command)
  {
    IDebugController controller = (IDebugController) ACTRRuntime.getRuntime()
        .getController();
    IModel model = getModel(controller, command.getModelName());
    if (model == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("Could not find model named " + command.getModelName());
      return;
    }

    switch (command.getState())
    {

    // case RESUME:
    // if (controller.getSuspendedModels().contains(model))
    // {
    // if (LOGGER.isDebugEnabled()) LOGGER.debug("Resuming " + model);
    // controller.resume(model);
    // }
    // else if (LOGGER.isDebugEnabled())
    // LOGGER.debug(" cannot resume " + model + " is is not suspended");
    // break;
    // case SUSPEND:
    // if (!controller.getSuspendedModels().contains(model))
    // {
    // if (LOGGER.isDebugEnabled()) LOGGER.debug("Suspending " + model);
    // controller.suspendModel(model);
    // }
    // else if (LOGGER.isDebugEnabled())
    // LOGGER.debug("cannot suspend " + model + " already suspended");
    // break;
      default:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("Requested state " + command.getState()
              + " is not supported for individual models");
        break;
    }
  }

  protected IModel getModel(IController controller, String modelName)
  {
    for (IModel model : controller.getRunningModels())
      if (model.getName().equals(modelName)) return model;
    return null;
  }

}
