/*
 * Created on Feb 20, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.IDebugController;
import org.jactr.core.runtime.controller.debug.event.BreakpointEvent;
import org.jactr.core.runtime.controller.debug.event.IBreakpointListener;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.async.common.BaseIOHandler;
import org.jactr.tools.async.common.MINAEndpoint;
import org.jactr.tools.async.message.command.login.LogoutCommand;
import org.jactr.tools.async.message.event.data.BreakpointReachedEvent;
import org.jactr.tools.async.message.event.data.ModelDataEvent;
import org.jactr.tools.async.message.event.state.IStateEvent;
import org.jactr.tools.async.message.event.state.ModelStateEvent;
import org.jactr.tools.async.message.event.state.RuntimeStateEvent;

/**
 * An instrument that permits the remote control of a runtime. The remote
 * interface sets up the connection and networking on the runtime side so that a
 * ShadowController can issue commands and receive run events
 * 
 * @author developer
 */
public class RemoteInterface extends MINAEndpoint implements IInstrument,
    IParameterized
{
  /**
   * logger definition
   */
  static final Log               LOGGER                      = LogFactory
                                                                 .getLog(RemoteInterface.class);

  static public final String     EXECUTOR_PARAM              = "Executor";

  static public final String     SEND_MODEL_ON_SUSPEND_PARAM = "SendModelOnSuspend";

  protected RemoteIOHandler      _handler;

  /*
   * for start, stop, etc
   */
  protected IACTRRuntimeListener _runtimeListener;

  protected IBreakpointListener  _breakpointListener;

  protected IModelListener       _modelListener;

  private String                 _executorName               = ExecutorServices.BACKGROUND;

  private Executor               _executor                   = ExecutorServices
                                                                 .getExecutor(_executorName);

  private boolean                _sendOnSuspend;

  private boolean                _connectedToRuntime;

  private Collection<IModel>     _installedModels;

  static private RemoteInterface _activeInterface;

  static public RemoteInterface getActiveRemoteInterface()
  {
    synchronized (RemoteInterface.class)
    {
      return _activeInterface;
    }
  }

  static protected void setActiveRemoteInterface(RemoteInterface remoteInterface)
  {
    synchronized (RemoteInterface.class)
    {
      if (remoteInterface != null && _activeInterface != null)
        throw new RuntimeException("There is already a RemoteInterface active");
      _activeInterface = remoteInterface;
    }
  }

  /**
   * 
   */
  public RemoteInterface()
  {
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    _handler = new RemoteIOHandler(runtime.getController());

    _installedModels = new ArrayList<IModel>();

    _modelListener = new ModelListenerAdaptor() {

      @Override
      public void modelStarted(ModelEvent me)
      {
        String name = me.getSource().getName();
        if (LOGGER.isDebugEnabled()) LOGGER.debug(name + " has started");
        _handler.write(new ModelStateEvent(name, IStateEvent.State.STARTED, me
            .getSimulationTime()));
      }

      @Override
      public void modelStopped(ModelEvent me)
      {
        String name = me.getSource().getName();
        if (LOGGER.isDebugEnabled()) LOGGER.debug(name + " has stopped");
        if (_sendOnSuspend) _handler.write(new ModelDataEvent(me.getSource()));
        if (me.getException() != null)
          _handler.write(new ModelStateEvent(name, me.getException(), me
              .getSimulationTime()));
        else
          _handler.write(new ModelStateEvent(name, IStateEvent.State.STOPPED,
              me.getSimulationTime()));
      }

      @Override
      public void modelSuspended(ModelEvent me)
      {
        String name = me.getSource().getName();
        if (LOGGER.isDebugEnabled()) LOGGER.debug(name + " has suspended");
        if (_sendOnSuspend) _handler.write(new ModelDataEvent(me.getSource()));
        _handler.write(new ModelStateEvent(name, IStateEvent.State.SUSPENDED,
            me.getSimulationTime()));
      }

      @Override
      public void modelResumed(ModelEvent me)
      {
        String name = me.getSource().getName();
        if (LOGGER.isDebugEnabled()) LOGGER.debug(name + " has resumed");
        _handler.write(new ModelStateEvent(name, IStateEvent.State.RESUMED, me
            .getSimulationTime()));
      }
    };

    _runtimeListener = new IACTRRuntimeListener() {

      public void modelAdded(ACTRRuntimeEvent event)
      {
        // noop
      }

      public void modelRemoved(ACTRRuntimeEvent event)
      {
      }

      public void runtimeResumed(ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Runtime resumed");

        _handler.write(new RuntimeStateEvent(IStateEvent.State.RESUMED, event
            .getSimulationTime()));
      }

      public void runtimeStarted(ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Runtime started");
        _handler.write(new RuntimeStateEvent(ACTRRuntime.getRuntime()
            .getModels(), event.getSimulationTime()));
      }

      public void runtimeStopped(final ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime stopped");

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Sending stop notice");
        try
        {
          if (_handler.isConnected())
            if (event.getException() != null)
              _handler.write(new RuntimeStateEvent(event.getException(), event
                  .getSimulationTime()));
            else
              _handler.write(new RuntimeStateEvent(IStateEvent.State.STOPPED,
                  event.getSimulationTime()));

          // send the logout, shadow controller will echo it back and we'll
          // disconnect then.
          // we could send now, which could result in immediate termination
          // or we can send it on the back ground executor so that everyone
          // else has a chance to finish up first.
          try
          {
          ExecutorServices.getExecutor(ExecutorServices.BACKGROUND).execute(
              new Runnable() {
                public void run()
                {
                  _handler.write(new LogoutCommand());
                }
              });
          }
          catch (Exception e)
          {
            if (LOGGER.isDebugEnabled()) LOGGER.debug(e);
          }

        }
        catch (IllegalStateException ise)
        {
          /*
           * can happen if we've disconnected already
           */
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Could not send stop notice, already disconnected");
        }
      }

      public void runtimeSuspended(ACTRRuntimeEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("runtime suspended");
        _handler.write(new RuntimeStateEvent(IStateEvent.State.SUSPENDED, event
            .getSimulationTime()));
      }

      public void modelStarted(ACTRRuntimeEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void modelStopped(ACTRRuntimeEvent event)
      {
        // TODO Auto-generated method stub

      }

    };

    _breakpointListener = new IBreakpointListener() {

      public void breakpointReached(BreakpointEvent be)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Breakpoint reached");

        Object value = be.getDetails();
        CommonTree details = null;
        if (value instanceof IProduction)
          details = ASTResolver.toAST((IProduction) value);
        else
          details = new CommonTree(new CommonToken(JACTRBuilder.STRING, ""
              + value));

        BreakpointReachedEvent bpre = new BreakpointReachedEvent(be.getSource()
            .getName(), be.getType(), be.getSimulationTime(), details);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Sending breakpoint reached " + bpre);
        _handler.write(bpre);
      }

    };

    /*
     * we can't install just yet because we need the executor
     */
  }

  public RemoteIOHandler getHandler()
  {
    return _handler;
  }

  @Override
  public BaseIOHandler getIOHandler()
  {
    return getHandler();
  }

  public Executor getExecutor()
  {
    return _executor;
  }

  public void setExecutor(Executor executor)
  {
    _executor = executor;
  }

  /**
   * @see org.jactr.instrument.IInstrument#initialize()
   */
  public void initialize()
  {

  }

  /**
   * @see org.jactr.instrument.IInstrument#install(org.jactr.core.model.IModel)
   */
  public void install(IModel model)
  {
    _installedModels.add(model);
    model.addListener(_modelListener, getExecutor());
    if (!_connectedToRuntime) try
    {
      /*
       * now let's start this beatch
       */
      connect();
    }
    catch (RuntimeException re)
    {
      throw re;
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not start RemoteInterface ", e);
    }
  }

  /**
   * @see org.jactr.instrument.IInstrument#uninstall(org.jactr.core.model.IModel)
   */
  public void uninstall(IModel model)
  {
    _installedModels.remove(model);
    model.removeListener(_modelListener);
  }

  @Override
  protected void disconnect(boolean force) throws Exception
  {
    try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Disconnecting remote interface");
      ACTRRuntime runtime = ACTRRuntime.getRuntime();
      runtime.removeListener(_runtimeListener);
      IController controller = runtime.getController();
      if (controller instanceof IDebugController)
        ((IDebugController) controller).removeListener(_breakpointListener);

      super.disconnect(force);
    }
    finally
    {
      setActiveRemoteInterface(null);

      /*
       * now we remove our listeners
       */
      _connectedToRuntime = false;
    }
  }

  /**
   * safely disconnect asynchronously.. this queues up the disconnect on the
   * background executor, allowing other processing that has already been queued
   * to finish up
   */
  public void disconnectSafe(final boolean force)
  {
    Runnable disconnect = new Runnable() {
      public void run()
      {
        try
        {
          disconnect(force);
        }
        catch (Exception e)
        {
          LOGGER.error("Could not disconnect", e);
        }
      }
    };

    try
    {
      getExecutor().execute(disconnect);
    }
    catch (RejectedExecutionException e)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(String
                .format("Execution of clean disconnect was rejected. Already shutting down."));
    }
  }

  @Override
  protected void connect() throws Exception
  {
    try
    {
      /*
       * now we can install the listeners
       */
      ACTRRuntime runtime = ACTRRuntime.getRuntime();
      runtime.addListener(_runtimeListener, getExecutor());
      IController controller = runtime.getController();
      if (controller instanceof IDebugController)
        ((IDebugController) controller).addListener(_breakpointListener,
            getExecutor());
      super.connect();
      setActiveRemoteInterface(this);
      _connectedToRuntime = true;
    }
    catch (Exception e)
    {
      setActiveRemoteInterface(null);
      throw e;
    }
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>(super.getPossibleParameters());
    rtn.add(EXECUTOR_PARAM);
    rtn.add(SEND_MODEL_ON_SUSPEND_PARAM);
    return rtn;
  }

  @Override
  public String getParameter(String key)
  {
    if (EXECUTOR_PARAM.equalsIgnoreCase(key)) return _executorName;
    if (SEND_MODEL_ON_SUSPEND_PARAM.equalsIgnoreCase(key))
      return "" + _sendOnSuspend;
    return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (EXECUTOR_PARAM.equalsIgnoreCase(key))
    {
      Executor ex = ExecutorServices.getExecutor(value);
      if (ex == null)
        throw new RuntimeException("Could not find executor named " + value);
      _executorName = value;
      setExecutor(ex);
    }
    else if (SEND_MODEL_ON_SUSPEND_PARAM.equalsIgnoreCase(key))
      _sendOnSuspend = Boolean.parseBoolean(value);
    else
      super.setParameter(key, value);
  }

  public boolean willSendOnSuspend()
  {
    return _sendOnSuspend;
  }

  public void setSendOnSuspend(boolean send)
  {
    _sendOnSuspend = send;
  }
}
