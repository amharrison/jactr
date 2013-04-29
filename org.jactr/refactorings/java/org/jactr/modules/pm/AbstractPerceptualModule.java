/*
 * Created on Jul 17, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm;

import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.IEfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.asynch.AbstractAsynchronousModule;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;

public abstract class AbstractPerceptualModule extends
    AbstractAsynchronousModule implements IPerceptualModule, IParameterized,
    IAsynchronousModule
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(AbstractPerceptualModule.class);

  private IModelListener  _modelListener;

  private IChunk          _freeChunk;

  private IChunk          _errorChunk;

  private IChunk          _busyChunk;

  private IChunk          _requestedChunk;

  private IChunk          _unrequestedChunk;

  public AbstractPerceptualModule(String name)
  {
    super(name);
    _modelListener = new ModelListenerAdaptor() {

      public void modelConnected(ModelEvent event)
      {
        connectToCommonReality();
      }

      public void modelStopped(ModelEvent event)
      {
        disconnectFromCommonReality();
      }
    };
  }

  /**
   * called immediately after common reality connection has been established.
   * This is used to connect various listeners to the {@link IAgent} so that the
   * model can be aware of different {@link IAfferentObject} and
   * {@link IEfferentObject}s
   */
  protected void connectToCommonReality()
  {

  }

  /**
   * called on model termination to give modules a chance to remove their
   * listeners
   */
  protected void disconnectFromCommonReality()
  {

  }

  @Override
  public void initialize()
  {
    // attach the listener is necessary
    getModel().addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    _freeChunk = getNamedChunk("free");
    _errorChunk = getNamedChunk("error");
    _busyChunk = getNamedChunk("busy");
    _unrequestedChunk = getNamedChunk("unrequested");
    _requestedChunk = getNamedChunk("requested");
  }

  public void dispose()
  {
    getModel().removeListener(_modelListener);
    super.dispose();
  }

  /**
   * return the executor that should shared by all common reality listeners.
   * this call is only valid after common reality has been connected
   * 
   * @return
   */
  public Executor getCommonRealityExecutor()
  {
    IAgent agentInterface = ACTRRuntime.getRuntime().getConnector().getAgent(
        getModel());
    if (agentInterface instanceof ACTRAgent)
      return ((ACTRAgent) agentInterface).getExecutorService();

    throw new IllegalModuleStateException(
        "Could not get ACTRAgentInterface which is required for perceptual modules to access CommonReality executor");
  }

  public Executor getExecutor()
  {
    return getCommonRealityExecutor();
  }

  /**
   * utility to snag a named chunk from DM
   * 
   * @param name
   * @return
   */
  protected IChunk getNamedChunk(String name)
  {
    try
    {
      return getModel().getDeclarativeModule().getChunk(name).get();
    }
    catch (Exception e)
    {
      LOGGER.warn("Could not get chunk " + name, e);
      return null;
    }
  }

  protected IChunkType getNamedChunkType(String name)
  {
    try
    {
      return getModel().getDeclarativeModule().getChunkType(name).get();
    }
    catch (Exception e)
    {
      LOGGER.warn("Could not get chunktype " + name, e);
      return null;
    }
  }

  public IChunk getFreeChunk()
  {
    return _freeChunk;
  }

  public IChunk getErrorChunk()
  {
    return _errorChunk;
  }

  public IChunk getBusyChunk()
  {
    return _busyChunk;
  }
  
  public IChunk getRequestedChunk()
  {
    return _requestedChunk;
  }
  
  public IChunk getUnrequestedChunk()
  {
    return _unrequestedChunk;
  }

}
