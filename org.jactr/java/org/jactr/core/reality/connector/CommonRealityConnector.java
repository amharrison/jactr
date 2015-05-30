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

  // protected Map<IModel, IClock> _disconnectedModels;

  protected BasicClock             _defaultClock;

  protected Map<IModel, IClock>    _allClocks;

  protected IClockConfigurator     _clockConfig;

  public CommonRealityConnector()
  {
    _defaultClock = new BasicClock();
    _agentInterfaces = new ConcurrentHashMap<IModel, ACTRAgent>();
    // _disconnectedModels = new ConcurrentHashMap<IModel, IClock>();
    _allClocks = new ConcurrentHashMap<IModel, IClock>();
    setClockConfigurator(new IClockConfigurator() {

      public IClock getClockFor(IModel model, IAgent agent)
      {
        return agent.getClock();
      }

      /*
       * by default, we don't support nested clock (non-Javadoc)
       * @see
       * org.jactr.core.reality.connector.IClockConfigurator#getClockFor(org
       * .jactr.core.model.IModel, org.commonreality.time.IClock)
       */
      public IClock getClockFor(IModel model, IClock defaultClock)
      {
        return null;
      }

      public void release(IModel model, IClock clock)
      {
        // noop
      }

    });
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
        {
          String idModelName = a.getIdentifier().getName();
          String agentName = ((ACTRAgent) a).getModelName();

          /*
           * short term test as we migrate from ACTRAgent explicitly.
           */
          if (!idModelName.equals(agentName))
            LOGGER.error(String.format(
                "Model name (%s) isn't same as agent name(%s)?", idModelName,
                agentName));

          if (modelName.equals(agentName)) agentInterface = (ACTRAgent) a;
        }

      if (agentInterface != null)
      {
        _agentInterfaces.put(model, agentInterface);

        _allClocks.put(model, _clockConfig.getClockFor(model, agentInterface));

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("connected, waiting for start");

        // we dont need to do anything here since the modules will attach
        // to common reality during the modelStarted() call

        agentInterface.waitForState(State.STARTED);
      }
      else
      {
        IClock baseClock = _defaultClock;

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "Unexpected model %s, not common reality agent configured.",
              model.getName()));

        IClock assignedClock = _clockConfig.getClockFor(model, baseClock);

        if (assignedClock != null)
        {
          _allClocks.put(model, assignedClock);
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String
                .format("Running the model using provided clock"));
        }
        else
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Unable to run this model. Exception"));

          throw new RuntimeException(String.format(
              "No clue how to hook up %s using current IConnector",
              model.getName()));
        }

      }

      if (LOGGER.isDebugEnabled()) LOGGER.debug("started!!");
    }
    catch (Exception e)
    {
      _agentInterfaces.remove(model);
      _allClocks.remove(model);

      throw new RuntimeException("Could not connect " + model + " to reality",
          e);
    }
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  public void disconnect(IModel model)
  {
    IClock clock = _allClocks.remove(model);
    if (clock != null) getClockConfigurator().release(model, clock);

    // now we can disconnect from reality
    IAgent agentInterface = getAgent(model);
    if (agentInterface != null)
      try
      {
        CommonReality.removeAgent(agentInterface);
        _agentInterfaces.remove(model);
        cleanDisconnect(agentInterface);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not disconnect " + model + " from reality", e);
        throw new RuntimeException("Could not disconnect " + model
            + " from reality", e);
      }
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

    IClock rtn = _allClocks.get(model);

    if (rtn == null) rtn = _defaultClock;

    return rtn;
  }

  public IClockConfigurator getClockConfigurator()
  {
    return _clockConfig;
  }

  public void setClockConfigurator(IClockConfigurator clockConfig)
  {
    _clockConfig = clockConfig;
  }

}
