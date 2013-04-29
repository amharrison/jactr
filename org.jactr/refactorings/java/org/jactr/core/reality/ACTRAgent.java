/*
 * Created on Jul 13, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.reality;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.AbstractAgent;
import org.commonreality.executor.GeneralThreadFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;

public class ACTRAgent extends AbstractAgent
{

  /**
   * 
   */
  private static final long      serialVersionUID = -7399812083846339199L;

  static public final String     MODEL_NAME       = "ACTRAgent.ModelName";

  /**
   * logger definition
   */
  static public final Log        LOGGER           = LogFactory
                                                      .getLog(ACTRAgent.class);

  protected String               _modelName;

  protected ExecutorService      _commonRealityExecutor;

  protected GeneralThreadFactory _commonRealityThreadFactory;

  public ACTRAgent()
  {
    super();
  }

  public void setModelName(String modelName)
  {
    _modelName = modelName;
  }

  public String getModelName()
  {
    return _modelName;
  }

  @Override
  public String getName()
  {
    if (_modelName == null) return "actrAgent";
    return _modelName;
  }

  public void stop() throws Exception
  {
    try
    {
      /*
       * if we are still running, we need to force the controller to shutdown
       */
      IController controller = ACTRRuntime.getRuntime().getController();
      if (controller != null && controller.isRunning())
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Stopping controller");
        try
        {
          controller.stop().get(500, TimeUnit.MILLISECONDS);
        }
        catch (Exception e)
        {

        }
        
        /*
         * there should be a wait here.. to give it a chance to cleanly quit
         */
        if(controller.isRunning())
          controller.terminate().get();
      }
    }
    finally
    {
      super.stop();
    }
  }

  @Override
  public void shutdown() throws Exception
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Shuttingdown " + getName());
    try
    {
      /*
       * kill the executor
       */
      if (_commonRealityExecutor != null
          && !_commonRealityExecutor.isShutdown())
        _commonRealityExecutor.shutdown();
      _commonRealityExecutor = null;

      if (_commonRealityThreadFactory != null)
        _commonRealityThreadFactory.dispose();
      _commonRealityThreadFactory = null;

    }
    finally
    {
      ExecutorServices.removeExecutor(getExecutorName());
      super.shutdown();
    }
  }

  public ExecutorService getExecutorService()
  {
    return _commonRealityExecutor;
  }

  @Override
  public void configure(Map<String, String> options) throws Exception
  {
    super.configure(options);
    if (options.containsKey(MODEL_NAME)) setModelName(options.get(MODEL_NAME));
  }

  private String getExecutorName()
  {
    return getModelName() + ":CommonReality-Thread";
  }

  @Override
  public void initialize() throws Exception
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Initializing " + getName());

    _commonRealityThreadFactory = new GeneralThreadFactory(getName() + "-CR",
        getCentralThreadFactory().getThreadGroup());
    _commonRealityExecutor = Executors
        .newSingleThreadExecutor(_commonRealityThreadFactory);

    ExecutorServices.addExecutor(getExecutorName(), _commonRealityExecutor);

    super.initialize();
  }
}
