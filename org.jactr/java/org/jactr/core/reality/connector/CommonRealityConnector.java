/*
 * Created on Nov 30, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.reality.connector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.participant.IParticipant.State;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.IReality;
import org.commonreality.reality.control.RealityShutdown;
import org.commonreality.reality.control.RealityStartup;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.BasicClock;
import org.commonreality.time.impl.SlavedClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.ACTRAgent;

/**
 * @author developer
 */
public class CommonRealityConnector implements IConnector
{

  // public static final String TRANSPORT_CLASS = "transportClass";

  // public static final String PROTOCOL_CLASS = "protocolClass";

  // public static final String ADDRESS = "address";

  /**
   * logger definition
   */
  static private final Log         LOGGER = LogFactory
                                              .getLog(CommonRealityConnector.class);

  protected Map<IModel, ACTRAgent> _agentInterfaces;

  protected Map<IModel, IClock>    _disconnectedModels;

  protected BasicClock             _defaultClock;

  public CommonRealityConnector()
  {
    _defaultClock = new BasicClock();
    _defaultClock.setTime(-1);
    _agentInterfaces = new ConcurrentHashMap<IModel, ACTRAgent>();
    _disconnectedModels = new ConcurrentHashMap<IModel, IClock>();
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#isRunning()
   */
  public boolean isRunning()
  {
    return CommonReality.getReality() != null
        && CommonReality.getReality().stateMatches(State.STARTED,
            State.SUSPENDED);
  }

  public void start()
  {
    IReality reality = CommonReality.getReality();
    new RealityStartup(reality).run();
  }

  public void stop()
  {
    IReality reality = CommonReality.getReality();
    // try
    // {
    // // already shutdown?
    // if (reality.getState().equals(State.UNKNOWN))
    // {
    // if (LOGGER.isWarnEnabled()) LOGGER.warn("CR is already shutdown");
    // return;
    // }
    //
    // if (reality.stateMatches(State.STARTED, State.SUSPENDED))
    // {
    // reality.stop();
    // reality.waitForState(State.STOPPED);
    // }
    //
    // reality.shutdown();
    // }
    // catch (Exception e)
    // {
    // throw new RuntimeException("could not stop common reality ", e);
    // }

    if (reality != null) new RealityShutdown(reality, true).run();
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#connect(org.jactr.core.model.IModel)
   */
  public void connect(IModel model)
  {
    String modelName = model.getName();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("connecting to reality inteface for " + modelName);

    try
    {
      ACTRAgent agentInterface = null;

      for (IAgent a : CommonReality.getAgents())
        if (a instanceof ACTRAgent)
          if (modelName.equals(((ACTRAgent) a).getModelName()))
            agentInterface = (ACTRAgent) a;

      if (agentInterface != null)
      {
        _agentInterfaces.put(model, agentInterface);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("connected, waiting for start");

        // we dont need to do anything here since the modules will attach
        // to common reality during the modelStarted() call

        agentInterface.waitForState(State.STARTED);
      }
      else
      {
        /*
         * if no agent was found, then common reality has no knowledge of this
         * particular model. This can occur in master/slave scenarios (model in
         * model) we could create an agent and connect to CR, but that might
         * propogate the agency through the system and the sensors unlikely to
         * be configured for that. Instead, we create a slave clock based off of
         * any existing clock (if none, then use the default clock and warn)
         */
        String clockOwner = "default";
        IClock baseClock = _defaultClock;
        /*
         * find the clock to use
         */
        if (_agentInterfaces.size() > 0)
        {
          Map.Entry<IModel, ACTRAgent> entry = _agentInterfaces.entrySet()
              .iterator().next();
          clockOwner = entry.getKey().getName();
          baseClock = entry.getValue().getClock();
        }

        if (LOGGER.isWarnEnabled())
          LOGGER
              .warn(String
                  .format(
                      "No IAgent configured for %s, connecting as disembodied using the clock from %s",
                      model, clockOwner));

        _disconnectedModels.put(model, new SlavedClock(baseClock));
      }

      if (LOGGER.isDebugEnabled()) LOGGER.debug("started!!");
    }
    catch (Exception e)
    {
      _agentInterfaces.remove(model);

      throw new RuntimeException("Could not connect " + model + " to reality",
          e);
    }
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  public void disconnect(IModel model)
  {
    // now we can disconnect from reality
    IAgent agentInterface = getAgent(model);
    if (agentInterface != null)
      try
      {
        _agentInterfaces.remove(model);
        cleanDisconnect(agentInterface);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not disconnect " + model + " from reality", e);
        throw new RuntimeException("Could not disconnect " + model
            + " from reality", e);
      }
    else
      _disconnectedModels.remove(model);
  }

  protected void cleanDisconnect(IAgent agent)
  {
    boolean force = false;
    if (agent.stateMatches(State.STARTED))
      try
      {
        agent.stop();
      }
      catch (Exception e)
      {
        if (LOGGER.isWarnEnabled() && !(e instanceof InterruptedException))
          LOGGER.warn(String.format("Could not stop agent cleanly, forcing "),
              e);
        force = true;
      }

    try
    {
      agent.shutdown(force);
    }
    catch (Exception e)
    {
      if (LOGGER.isWarnEnabled() && !(e instanceof InterruptedException))
        LOGGER.warn(String.format("Forced shutdown failed ", e));
    }
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#getAgentInterface(org.jactr.core.model.IModel)
   */
  public IAgent getAgent(IModel model)
  {
    if (model == null) return null;
    /*
     * concurrent map will throw npe for a null key
     */
    return _agentInterfaces.get(model);
  }

  public IClock getClock(IModel model)
  {
    if (model == null) return _defaultClock;

    IAgent agentInterface = getAgent(model);
    if (agentInterface != null) return agentInterface.getClock();

    IClock disconnected = _disconnectedModels.get(model);
    if (disconnected != null) return disconnected;

    return _defaultClock;
  }

}
