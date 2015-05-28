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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.handler.IMessageHandler;
import org.commonreality.net.session.ISessionInfo;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.BreakpointType;
import org.jactr.core.runtime.controller.debug.IDebugController;
import org.jactr.tools.async.message.command.breakpoint.BreakpointCommand;
import org.jactr.tools.async.message.command.breakpoint.IBreakpointCommand;

/**
 * take the requested breakpoint action and apply it. currently we only support
 * all clear, add/remove of time, cycle, and production
 * 
 * @author developer
 */
public class BreakpointHandler implements IMessageHandler<BreakpointCommand>
{
  /**
   * logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                              .getLog(BreakpointHandler.class);



  public BreakpointHandler()
  {
  }

  // /**
  // * @see
  // org.apache.mina.handler.demux.MessageHandler#messageReceived(org.apache.mina.common.IoSession,
  // * java.lang.Object)
  // */
  // public void handleMessage(IoSession session, IBreakpointCommand command)
  // throws Exception
  // {
  // // only the owner can change break points
  // }

  @Override
  public void accept(ISessionInfo session, BreakpointCommand command)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got " + command);

    IDebugController controller = (IDebugController) ACTRRuntime.getRuntime()
        .getController();
    switch (command.getAction())
    {
      case CLEAR:
        clearBreakpoints(controller, command);
        break;
      case ADD:
        addBreakpoint(controller, command);
        break;
      case REMOVE:
        removeBreakpoint(controller, command);
        break;
    }

  }

  protected IModel getModel(String modelName, IDebugController controller)
  {
    IModel rtn = null;
    for (IModel model : ACTRRuntime.getRuntime().getModels())
      if (model.getName().equals(modelName))
      {
        rtn = model;
        continue;
      }
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning " + rtn + " for name " + modelName);

    return rtn;
  }

  protected void clearBreakpoints(IDebugController controller,
      IBreakpointCommand command)
  {
    String modelName = command.getModelName();
    Collection<IModel> models = new ArrayList<IModel>();

    if (modelName.equals("all"))
      models.addAll(ACTRRuntime.getRuntime().getModels());
    else
      models.add(getModel(modelName, controller));

    for (IModel model : models)
      controller.clearBreakpoints(getModel(command.getModelName(), controller),
          command.getType());
  }

  protected void addBreakpoint(IDebugController controller,
      IBreakpointCommand command)
  {
    String modelName = command.getModelName();
    Collection<IModel> models = new ArrayList<IModel>();

    if (modelName.equals("all"))
      models.addAll(ACTRRuntime.getRuntime().getModels());
    else
      models.add(getModel(modelName, controller));

    BreakpointType type = command.getType();
    for (IModel model : models)
      if (type == BreakpointType.TIME || type == BreakpointType.CYCLE)
      {
        // resolve to a number
        Double value = Double.valueOf(command.getDetails());
        controller.addBreakpoint(model, type, value);
      }
      else if (type == BreakpointType.PRODUCTION)
      {

        String productionName = command.getDetails();
        /*
         * get the production
         */
        IProduction production = null;
        try
        {
          production = model.getProceduralModule()
              .getProduction(productionName).get();
          controller.addBreakpoint(model, type, production);
        }
        catch (Exception e)
        {
          LOGGER.warn("Could not add production breakpoint " + productionName
              + " for " + model + " since no production matching was found", e);
        }
      }
  }

  protected void removeBreakpoint(IDebugController controller,
      IBreakpointCommand command)
  {
    String modelName = command.getModelName();
    Collection<IModel> models = new ArrayList<IModel>();

    if (modelName.equals("all"))
      models.addAll(ACTRRuntime.getRuntime().getModels());
    else
      models.add(getModel(modelName, controller));
    BreakpointType type = command.getType();
    for (IModel model : models)
      if (type == BreakpointType.TIME || type == BreakpointType.CYCLE)
      {
        // resolve to a number
        Double value = Double.valueOf(command.getDetails());
        controller.removeBreakpoint(model, type, value);
      }
      else if (type == BreakpointType.PRODUCTION)
      {
        String productionName = command.getDetails();
        /*
         * get the production
         */
        IProduction production = null;
        try
        {
          production = model.getProceduralModule()
              .getProduction(productionName).get();
          controller.removeBreakpoint(model, type, production);
        }
        catch (Exception e)
        {
          LOGGER.warn("Could not remove production breakpoint " + productionName
              + " for " + model + " since no production matching was found", e);
        }
      }
  }


  
}
