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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.IEfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.concurrent.GeneralThreadFactory;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.IModule;
import org.jactr.core.module.asynch.AbstractAsynchronousModule;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.modules.pm.common.symbol.DefaultStringSymbolGrounder;
import org.jactr.modules.pm.common.symbol.ISymbolGrounder;

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

  private ISymbolGrounder _symbolGrounder;

  public AbstractPerceptualModule(String name)
  {
    super(name);
    _modelListener = new ModelListenerAdaptor() {

      @Override
      public void modelConnected(ModelEvent event)
      {
        connectToCommonReality();
      }

      @Override
      public void modelStopped(ModelEvent event)
      {
        disconnectFromCommonReality();
      }
    };
  }

  public ISymbolGrounder getSymbolGrounder()
  {
    return _symbolGrounder;
  }

  /**
   * set the symbol grounder for this module and, if it can, set it for all the
   * other installed perceptual modules
   * 
   * @param grounder
   */
  public void setSymbolGrounder(ISymbolGrounder grounder)
  {
    if (_symbolGrounder == grounder) return;

    _symbolGrounder = grounder;

    for (IModule module : getModel().getModules())
      if (module != this && module instanceof AbstractPerceptualModule)
        ((AbstractPerceptualModule) module).setSymbolGrounder(grounder);
  }

  /**
   * called immediately after common reality connection has been established.
   * This is used to connect various listeners to the {@link IAgent} so that the
   * model can be aware of different {@link IAfferentObject} and
   * {@link IEfferentObject}s
   */
  protected void connectToCommonReality()
  {
    if (_symbolGrounder == null)
      setSymbolGrounder(new DefaultStringSymbolGrounder());
  }

  /**
   * called on model termination to give modules a chance to remove their
   * listeners. We just shutdown the CR executor (if it is not shared)
   */
  protected void disconnectFromCommonReality()
  {
    boolean useShared = Boolean.getBoolean("jactr.pm.useSharedExecutor");

    if (!useShared)
    {
      String exName = "CR-";
      if (useShared)
        exName += "shared";
      else
        exName += getModel().getName();

      ExecutorService es = ExecutorServices.getExecutor(exName);
      if (es != null && !es.isTerminated() && !es.isShutdown())
      {
        es.shutdown();
        ExecutorServices.removeExecutor(exName);
      }
    }
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

  @Override
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
    return getPerceptualExecutor();
  }

  /**
   * get or create the shared executor for this model.
   * 
   * @return
   */
  protected ExecutorService getPerceptualExecutor()
  {
    boolean useShared = Boolean.getBoolean("jactr.pm.useSharedExecutor");

    String exName = "CR-";
    if (useShared)
      exName += "shared";
    else
      exName += getModel().getName();

    ExecutorService es = ExecutorServices.getExecutor(exName);
    if (es == null)
    {
      boolean usePooled = Boolean.getBoolean("jactr.pm.useThreadPool");
      if (usePooled)
        es = Executors.newCachedThreadPool(new GeneralThreadFactory(exName));
      else
        es = Executors
            .newSingleThreadExecutor(new GeneralThreadFactory(exName));

      ExecutorServices.addExecutor(exName, es);
    }
    return es;
  }

  @Override
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
