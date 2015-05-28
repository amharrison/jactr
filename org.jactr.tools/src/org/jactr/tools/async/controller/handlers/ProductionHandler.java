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
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.IDebugController;
import org.jactr.tools.async.message.command.breakpoint.IProductionCommand;
import org.jactr.tools.async.message.command.breakpoint.ProductionCommand;

/**
 * take the requested breakpoint action and apply it. currently we only support
 * all clear, add/remove of time, cycle, and production
 * 
 * @author developer
 */
public class ProductionHandler implements IMessageHandler<ProductionCommand>
{
  /**
   * logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                              .getLog(ProductionHandler.class);



  public ProductionHandler()
  {
  }

  @Override
  public void accept(ISessionInfo session, ProductionCommand command)
  {

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got " + command);

    IDebugController controller = (IDebugController) ACTRRuntime.getRuntime()
        .getController();
    switch (command.getAction())
    {
      case ENABLE:
        enable(controller, command);
        break;
      case DISABLE:
        disable(controller, command);
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
  
  protected IProduction getProduction(String production, IModel model)
  {
    try
    {
      return model.getProceduralModule().getProduction(production).get();
    }
    catch(Exception e)
    {
      LOGGER.error("Failed to snag production "+production, e);
      return null;
    }
  }
  
  protected void enable(IDebugController controller, IProductionCommand command)
  {
    IModel model = getModel(command.getModelName(), controller);
    if(model==null) return;
    IProduction production = getProduction(command.getProductionName(), model);
    if(production==null) return;
    controller.setEnabled(production, true);
  }

  protected void disable(IDebugController controller, IProductionCommand command)
  {
    IModel model = getModel(command.getModelName(), controller);
    if(model==null) return;
    IProduction production = getProduction(command.getProductionName(), model);
    if(production==null) return;
    controller.setEnabled(production, false);
  }

  
}
