/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.tools.async.message.command.state.RuntimeStateCommand;

/**
 * @author developer
 */
public class RuntimeStateHandler implements
    IMessageHandler<RuntimeStateCommand>
{
  /**
   * logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                      .getLog(RuntimeStateHandler.class);

  public RuntimeStateHandler()
  {
  }

  @Override
  public void accept(ISessionInfo session, RuntimeStateCommand command)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got " + command);

    IController controller = ACTRRuntime.getRuntime().getController();

    switch (command.getState())
    {
      case START:
        start(controller, command.shouldSuspendImmediately());
        break;
      case STOP:
        stop(controller);
        break;
      case RESUME:
        resume(controller);
        break;
      case SUSPEND:
        suspend(controller);
        break;
    }

  }

  final protected void start(IController controller, boolean suspend)
  {
    if (!controller.isRunning())
      controller.start(suspend);
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("controller is already running, ignoring request");
  }

  final protected void stop(IController controller)
  {
    if (controller.isRunning())
      controller.stop();
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("controller is not running, ignoring request");
  }

  final protected void suspend(IController controller)
  {
    if (!controller.isSuspended() && controller.isRunning())
      controller.suspend();
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("controller is suspended, ignoring request");
  }

  final protected void resume(IController controller)
  {
    if (controller.isSuspended() && controller.isRunning())
      controller.resume();
    // else if (controller instanceof IDebugController)
    // {
    // IDebugController debugger = (IDebugController) controller;
    // Collection<IModel> suspended = controller.getSuspendedModels();
    // if (suspended.size() != 0)
    // {
    // for (IModel model : suspended)
    // debugger.resume(model);
    // }
    // else if (LOGGER.isWarnEnabled())
    // LOGGER.warn("no models are suspended, ignoring request");
    // }
    else if (LOGGER.isDebugEnabled())
      LOGGER.debug("controller is already running, ignoring request");
  }

}
