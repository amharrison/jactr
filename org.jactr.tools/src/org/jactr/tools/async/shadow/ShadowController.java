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
package org.jactr.tools.async.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.controller.debug.BreakpointType;
import org.jactr.tools.async.common.BaseIOHandler;
import org.jactr.tools.async.common.MINAEndpoint;
import org.jactr.tools.async.message.command.breakpoint.BreakpointCommand;
import org.jactr.tools.async.message.command.breakpoint.IBreakpointCommand;
import org.jactr.tools.async.message.command.breakpoint.IProductionCommand;
import org.jactr.tools.async.message.command.breakpoint.ProductionCommand;
import org.jactr.tools.async.message.command.state.IStateCommand;
import org.jactr.tools.async.message.command.state.ModelStateCommand;
import org.jactr.tools.async.message.command.state.RuntimeStateCommand;

/**
 * a mock controller that is to be used to interface with the real one
 * controller.NetworkedDebugController
 * 
 * @author developer
 */
public class ShadowController extends MINAEndpoint
{
  /**
   * logger definition
   */
  static private final Log        LOGGER = LogFactory
                                             .getLog(ShadowController.class);

  private Set<String>             _runningModels;

  private Set<String>             _terminatedModels;

  private Set<String>             _suspendedModels;

  private Map<String, CommonTree> _modelDescriptors;

  /*
   * keyed on model descriptor with value being the break point data
   */
  private Map<String, CommonTree> _breakpointData;

  private ReentrantLock           _lock  = new ReentrantLock();

  private Condition               _state = _lock.newCondition();

  private ShadowIOHandler         _handler;

  /**
   * keeps track of the number of state changes that have occured
   */
  private volatile long           _stateCounter;

  /**
   * what was the state counter when a request (start, resume, stop, suspend)
   * was issued, so that we can escape the waits at the right time.
   */
  private long                    _stateAtSuspend;

  private long                    _stateAtResume;

  private double                  _lastSimulationTime;

  public ShadowController()
  {
    _modelDescriptors = new HashMap<String, CommonTree>();
    _breakpointData = new HashMap<String, CommonTree>();
    _runningModels = new TreeSet<String>();
    _terminatedModels = new TreeSet<String>();
    _suspendedModels = new TreeSet<String>();
    _handler = new ShadowIOHandler(this);
  }

  public Collection<String> getModelNames()
  {
    try
    {
      _lock.lock();
      return new ArrayList<String>(_modelDescriptors.keySet());
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Collection<String> getRunningModels()
  {
    try
    {
      _lock.lock();
      return new ArrayList<String>(_runningModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Collection<String> getTerminatedModels()
  {
    try
    {
      _lock.lock();
      return new ArrayList<String>(_terminatedModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public Collection<String> getSuspendedModels()
  {
    try
    {
      _lock.lock();
      return new ArrayList<String>(_suspendedModels);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public CommonTree getModelDescriptor(String modelName)
  {
    try
    {
      _lock.lock();
      return _modelDescriptors.get(modelName);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void setModelDescriptor(String modelName, CommonTree modelDescriptor)
  {
    try
    {
      _lock.lock();
      _modelDescriptors.put(modelName, modelDescriptor);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public CommonTree getBreakpointData(String modelName)
  {
    try
    {
      _lock.lock();
      return _breakpointData.get(modelName);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void setBreakpointData(String modelName, CommonTree breakpointData)
  {
    try
    {
      _lock.lock();
      _breakpointData.put(modelName, breakpointData);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public boolean isConnected()
  {
    return _handler.isConnected();
  }

  /**
   * after attach is called (which will start the configured service) you will
   * often need to wait for the connection to be completed (established with the
   * remote)
   * 
   * @param timeout
   *          milliseconds to wait
   * @return
   * @throws InterruptedException
   */
  public boolean waitForConnection(long timeout) throws InterruptedException
  {
    return _handler.waitForConnection(timeout);
  }

  protected void checkConnection()
  {
    if (!isConnected())
      throw new IllegalStateException(
          "Can only manipulate a remote controller if the connection is alive");
  }

  /**
   * start up the service
   * 
   * @see org.jactr.core.runtime.controller.IController#attach()
   */
  public void attach()
  {
    try
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Attaching " + this);
      connect();
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not start service ", e);
    }
  }

  public ShadowIOHandler getHandler()
  {
    return _handler;
  }

  @Override
  public BaseIOHandler getIOHandler()
  {
    return getHandler();
  }

  public void detach(boolean force)
  {
    try
    {
      if (isSuspended()) resume();

      if (isRunning()) stop();

      // allow the other side to disconnect first
      if (!force)
      {
        getIOHandler().waitForPendingWrites();
        getIOHandler().waitForDisconnect();
      }

      disconnect(force);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not stop service ", e);
    }
  }

  public double getCurrentSimulationTime()
  {
    return _lastSimulationTime;
  }

  public void setCurrentSimulationTime(double time)
  {
    _lastSimulationTime = time;
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#isRunning()
   */
  public boolean isRunning()
  {
    try
    {
      _lock.lock();
      return _runningModels.size() != 0;
    }
    finally
    {
      _lock.unlock();
    }
  }

  public boolean isRunning(String modelName)
  {
    try
    {
      _lock.lock();
      return _runningModels.contains(modelName);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#isSuspended()
   */
  public boolean isSuspended()
  {
    try
    {
      _lock.lock();
      return _suspendedModels.containsAll(_runningModels) && isRunning();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public boolean isSuspended(String modelName)
  {
    try
    {
      _lock.lock();
      return _suspendedModels.contains(modelName);
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void addBreakpoint(BreakpointType type, String modelName,
      String details)
  {
    checkConnection();
    _handler.write(
        new BreakpointCommand(IBreakpointCommand.Action.ADD, type, modelName,
            details)).awaitUninterruptibly();
  }

  public void removeBreakpoint(BreakpointType type, String modelName,
      String details)
  {
    checkConnection();
    _handler.write(
        new BreakpointCommand(IBreakpointCommand.Action.REMOVE, type,
            modelName, details)).awaitUninterruptibly();
  }

  public void clearBreakpoints()
  {
    checkConnection();
    _handler.write(
        new BreakpointCommand(IBreakpointCommand.Action.CLEAR,
            BreakpointType.ALL)).awaitUninterruptibly();
  }

  public void setProductionEnabled(String modelName, String productionName,
      boolean enabled)
  {
    checkConnection();
    _handler.write(new ProductionCommand(modelName, productionName,
        enabled ? IProductionCommand.Action.ENABLE
            : IProductionCommand.Action.DISABLE));
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#reset()
   */
  public void reset()
  {
    checkConnection();
    if (isRunning())
    {
      // clear breakpoints
      clearBreakpoints();
      if (isSuspended()) resume();

      try
      {
        waitForCompletion();
      }
      catch (InterruptedException ie)
      {
        LOGGER.error("Could not wait for completion ", ie);
      }
    }

    try
    {
      _lock.lock();
      _stateCounter = 0;
      _modelDescriptors.clear();
      _breakpointData.clear();
      _terminatedModels.clear();
      _suspendedModels.clear();
      _runningModels.clear();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#resume()
   */
  public void resume()
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtResume = getStateCounter();
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Sending resume");
      _handler.write(new RuntimeStateCommand(IStateCommand.State.RESUME))
          .awaitUninterruptibly();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void resume(String modelName)
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtResume = getStateCounter();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Sending resume to " + modelName);
      _handler.write(new ModelStateCommand(modelName,
          IStateCommand.State.RESUME));
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#start()
   */
  public void start()
  {
    start(false);
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#start(boolean)
   */
  public void start(boolean suspendImmediately)
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtResume = Long.MAX_VALUE;
      _stateAtSuspend = Long.MAX_VALUE;
      _handler.write(new RuntimeStateCommand(suspendImmediately))
          .awaitUninterruptibly();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#stop()
   */
  public void stop()
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtResume = _stateAtSuspend = getStateCounter();
      _handler.write(new RuntimeStateCommand(IStateCommand.State.STOP))
          .awaitUninterruptibly();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void stop(String modelName)
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtResume = _stateAtSuspend = getStateCounter();
      _handler
          .write(new ModelStateCommand(modelName, IStateCommand.State.STOP));
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#suspend()
   */
  public void suspend()
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtSuspend = getStateCounter();
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Sending suspend");
      _handler.write(new RuntimeStateCommand(IStateCommand.State.SUSPEND))
          .awaitUninterruptibly();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void suspend(String modelName)
  {
    checkConnection();
    try
    {
      _lock.lock();
      _stateAtSuspend = getStateCounter();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Sending suspend to " + modelName);
      _handler.write(
          new ModelStateCommand(modelName, IStateCommand.State.SUSPEND))
          .awaitUninterruptibly();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public long waitForStateChange() throws InterruptedException
  {
    return waitForStateChange(0);
  }

  /**
   * wait at most maxWait milliseconds for some change of state in the system.
   * This is much safer than waitForResumption() and waitForSuspension() as they
   * may miss events if the notifications are coming in faster than
   * ShadowController can respond to them.
   * 
   * @param maxWait
   * @return the number of state changes that have elapsed since start of wait
   * @throws InterruptedException
   */
  public long waitForStateChange(long maxWait) throws InterruptedException
  {
    try
    {
      _lock.lock();
      long startTime = System.currentTimeMillis();
      long startState = getStateCounter();
      while (startState == getStateCounter())
      {
        if (maxWait > 0 && System.currentTimeMillis() - startTime > maxWait)
          return getStateCounter() - startState;

        if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for state change");
        if (maxWait > 0)
          _state.await(maxWait, TimeUnit.MILLISECONDS);
        else
          _state.await();
      }

      return getStateCounter() - startState;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * should only be called from within the lock blocks
   * 
   * @return
   */
  protected long getStateCounter()
  {
    return _stateCounter;
  }

  public boolean waitForCompletion() throws InterruptedException
  {
    return waitForCompletion(0);
  }

  /**
   * @return if the runtime is running
   * @see org.jactr.core.runtime.controller.IController#waitForCompletion()
   */
  public boolean waitForCompletion(long maxWait) throws InterruptedException
  {
    checkConnection();
    try
    {
      _lock.lock();
      long startTime = System.currentTimeMillis();
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for completion");
      while (isRunning())
      {
        // time has elapsed
        if (maxWait > 0 && System.currentTimeMillis() - startTime > maxWait)
          return isRunning();

        if (maxWait > 0)
          _state.await(maxWait, TimeUnit.MILLISECONDS);
        else
          _state.await();
      }

      return isRunning();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * called by the io handler only when the actual runtime has resumed.
   */
  public void resumed(String modelName)
  {
    if (!isSuspended(modelName))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(modelName + " is currently resumed, ignoring");
      return;
    }

    try
    {
      _lock.lock();
      _breakpointData.clear();
      _suspendedModels.remove(modelName);
      _stateCounter++;
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Resumed, signalling");
      _state.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see #waitForResumption(long)
   * @return
   * @throws InterruptedException
   */
  public boolean waitForResumption() throws InterruptedException
  {
    return waitForResumption(0);
  }

  /**
   * Will wait until one of three things happens: the state is now resumed, the
   * state has changed otherwise, or maxWait milliseconds have elapsed
   * 
   * @param maxWait
   *          milliseconds to block, 0 is indefinite
   * @return if the runtime has resumed, if false, you should explicitly check
   *         the state
   * @see #waitForStateChange(long)
   */
  public boolean waitForResumption(long maxWait) throws InterruptedException
  {
    checkConnection();
    try
    {
      _lock.lock();

      /*
       * if the state has changed from the time resume() was called
       */
      if (getStateCounter() - 1 > _stateAtResume)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("State has already changed since resume was called, returning false");
        return false;
      }

      long delta = -1;
      while (isSuspended() && delta < 0)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("waiting for resumption");
        // delta 0 means time has elapsed w/ no change
        delta = waitForStateChange(maxWait);
      }

      if (delta <= 1) return !isSuspended();
      return false;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * called by the io handler only to signal that the runtime (where ever it is)
   * has been suspended.
   */
  public void suspended(String modelName)
  {
    if (isSuspended(modelName))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(modelName + " already suspended, ignoring");
      return;
    }

    try
    {
      _lock.lock();
      _suspendedModels.add(modelName);
      _stateCounter++;
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Suspended, signalling");
      _state.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see #waitForSuspension(long)
   * @throws InterruptedException
   */
  public boolean waitForSuspension() throws InterruptedException
  {
    return waitForSuspension(0);
  }

  /**
   * Will wait until one of three things happens: the state is now suspended,
   * the state has changed otherwise, or maxWait milliseconds have elapsed
   * 
   * @param maxWait
   *          milliseconds to block, 0 is indefinite
   * @return if the runtime has suspended, if false, you should explicitly check
   *         the state
   * @see #waitForStateChange(long)
   */
  public boolean waitForSuspension(long maxWait) throws InterruptedException
  {
    checkConnection();
    if (!isRunning()) return false;

    try
    {
      _lock.lock();

      if (getStateCounter() - 1 > _stateAtSuspend)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("State has already changed since suspend was called, returning false");
        return false;
      }

      long delta = -1;
      while (!isSuspended() && delta < 0)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("waiting for suspension");
        // delta 0 means time has elapsed w/ no change
        delta = waitForStateChange(maxWait);
      }

      if (delta <= 1) return isSuspended();
      return false;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * @see org.jactr.core.runtime.controller.IController#waitForStart()
   */
  public boolean waitForStart() throws InterruptedException
  {
    return waitForStart(0);
  }

  /**
   * Will wait until one of three things happens: the state is now running, the
   * state has changed otherwise, or maxWait milliseconds have elapsed
   * 
   * @param maxWait
   *          milliseconds to block, 0 is indefinite
   * @return if the runtime has started, if false, you should explicitly check
   *         the state
   * @see #waitForStateChange(long)
   */
  public boolean waitForStart(long maxWait) throws InterruptedException
  {
    checkConnection();
    try
    {
      _lock.lock();
      long delta = -1;
      while (!isRunning() && delta < 0)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for start");
        // delta 0 means that the time elapsed but there was no change
        delta = waitForStateChange(maxWait);
      }

      if (delta <= 1) return isRunning();
      return false;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * called by the io handler only when the runtime has actually started.
   */
  public void started(Collection<String> modelNames)
  {
    if (isRunning())
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("already started, ignoring");
      return;
    }

    try
    {
      _lock.lock();
      _modelDescriptors.clear();
      for (String modelName : modelNames)
        setModelDescriptor(modelName, null);

      _runningModels.addAll(modelNames);
      _suspendedModels.clear();
      _terminatedModels.clear();
      _breakpointData.clear();
      _stateCounter++;
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Started, signalling");
      _state.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * called by the io handler only when the runtime has stopped.
   */
  public void stopped(String modelName)
  {
    if (!isRunning(modelName))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(modelName + " already stopped, ignoring");
      return;
    }

    try
    {
      _lock.lock();
      _terminatedModels.add(modelName);
      _runningModels.remove(modelName);
      _suspendedModels.remove(modelName);
      _stateCounter++;
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Stopped, signalling runningModels:" + _runningModels);
      _state.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }

}
