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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.participant.IParticipant.State;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.IReality;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.BasicClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.ACTRAgent;

/**
 * @author developer
 */
public class CommonRealityConnector implements IConnector
{

  public static final String       TRANSPORT_CLASS = "transportClass";

  public static final String       PROTOCOL_CLASS  = "protocolClass";

  public static final String       ADDRESS         = "address";

  /**
   * logger definition
   */
  static private final Log         LOGGER          = LogFactory
                                                       .getLog(CommonRealityConnector.class);

  protected Map<IModel, ACTRAgent> _agentInterfaces;

  protected BasicClock             _defaultClock;

  public CommonRealityConnector()
  {
    _defaultClock = new BasicClock();
    _defaultClock.setTime(-1);
    _agentInterfaces = new ConcurrentHashMap<IModel, ACTRAgent>();
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
    try
    {
      reality.start();
      reality.waitForState(State.STARTED);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not start common reality ", e);
    }
  }

  public void stop()
  {
    IReality reality = CommonReality.getReality();
    try
    {
      // already shutdown?
      if (reality.getState().equals(State.UNKNOWN))
      {
        if (LOGGER.isWarnEnabled()) LOGGER.warn("CR is already shutdown");
        return;
      }

      if (reality.stateMatches(State.STARTED, State.SUSPENDED))
      {
        reality.stop();
        reality.waitForState(State.STOPPED);
      }

      reality.shutdown();
    }
    catch (Exception e)
    {
      throw new RuntimeException("could not stop common reality ", e);
    }
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

      if (agentInterface == null)
        throw new RuntimeException("No ACTRAgent was found for " + modelName);

      if (LOGGER.isDebugEnabled()) LOGGER.debug("Connecting to reality");

      // we are actually already connected..
      // agentInterface.connect();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("connected, waiting for start");

      _agentInterfaces.put(model, agentInterface);

      // we dont need to do anything here since the modules will attach
      // to common reality during the modelStarted() call

      agentInterface.waitForState(State.STARTED);

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
    if (agentInterface != null) try
    {
      /*
       * we call shutdown instead of disconnect as that will cleanup note this
       * is not strictly correct. as a participant, it should wait for CR to
       * tell it to shutdown and disconnect..
       */
      // agentInterface.stop();
      // agentInterface.shutdown();
      _agentInterfaces.remove(model);
    }
    catch (Exception e)
    {
      LOGGER.error("Could not disconnect " + model + " from reality", e);
      throw new RuntimeException("Could not disconnect " + model
          + " from reality", e);
    }
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#getAgentInterface(org.jactr.core.model.IModel)
   */
  public IAgent getAgent(IModel model)
  {
    if(model==null) return null;
    /*
     * concurrent map will throw npe for a null key
     */
    return _agentInterfaces.get(model);
  }

  public IClock getClock(IModel model)
  {
    if(model == null) return _defaultClock;
    
    IAgent agentInterface = getAgent(model);
    if (agentInterface != null) return agentInterface.getClock();

    return _defaultClock;
  }

}
