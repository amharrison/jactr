package org.jactr.embed;

/*
 * default logging
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.agents.ThinAgent;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.OwnedClock;
import org.commonreality.time.impl.OwnedClock.OwnedAuthoritativeClock;
import org.commonreality.time.impl.WrappedClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.reality.connector.IClockConfigurator;
import org.jactr.core.reality.connector.IConnector;

public class EmbedConnector implements IConnector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(EmbedConnector.class);

  static private final String        EMBED_AGENT_KEY = "embedConnector.thinAgent";

  private final OwnedClock           _defaultClock;

  private Map<IModel, IClock>        _clocks;

  private IClockConfigurator         _configurator;

  private boolean                    _running        = false;

  public EmbedConnector()
  {
    _defaultClock = new OwnedClock(0.05);
    _clocks = new ConcurrentHashMap<IModel, IClock>();
    setClockConfigurator(new IClockConfigurator() {

      public void release(IModel model, IClock clock)
      {

      }

      public IClock getClockFor(IModel model, IClock defaultClock)
      {
        return new WrappedClock(defaultClock);
      }

      public IClock getClockFor(IModel model, ACTRAgent agent)
      {
        return agent.getClock();
      }
    });
    // _defaultClock.setTime(-1);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#connect(org.jactr.core.model.IModel)
   */
  public void connect(IModel model)
  {

    OwnedAuthoritativeClock auth = (OwnedAuthoritativeClock) _defaultClock
        .getAuthority().get();

    auth.addOwner(model);

    IClock clock = getClockConfigurator().getClockFor(model, _defaultClock);

    _clocks.put(model, clock);

    startThinAgent(model, clock);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  public void disconnect(IModel model)
  {
    OwnedAuthoritativeClock auth = (OwnedAuthoritativeClock) _defaultClock
        .getAuthority().get();

    auth.removeOwner(model);

    IClock defined = _clocks.remove(model);

    getClockConfigurator().release(model, defined);

    stopThinAgent(model);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#getAgentInterface(org.jactr.core.model.IModel)
   */
  public IAgent getAgent(IModel model)
  {
    return (IAgent) model.getMetaData(EMBED_AGENT_KEY);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#isRunning()
   */
  public boolean isRunning()
  {
    return _running;
  }

  public IClock getClock(IModel model)
  {
    // concurrent hash cant deal w/ null
    if (model == null) return _defaultClock;
    IClock rtn = _clocks.get(model);
    if (rtn == null) rtn = _defaultClock;
    return rtn;
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#start()
   */
  public void start()
  {
    _running = true;
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#stop()
   */
  public void stop()
  {
    _running = false;
  }

  public IClockConfigurator getClockConfigurator()
  {
    return _configurator;
  }

  public void setClockConfigurator(IClockConfigurator clockConfig)
  {
    _configurator = clockConfig;
  }

  protected void startThinAgent(IModel model, IClock clock)
  {
    ThinAgent agent = new ThinAgent(model.getName(), clock);

    try
    {

      agent.connect(); // noop

      agent.initialize();

      agent.start();

      model.setMetaData(EMBED_AGENT_KEY, agent);
    }
    catch (Exception e)
    {
      LOGGER
          .error(String.format("Failed to start thin agent for %s", model), e);
    }
  }

  protected void stopThinAgent(IModel model)
  {
    ThinAgent agent = (ThinAgent) model.getMetaData(EMBED_AGENT_KEY);
    if (agent != null)
      try
      {
        agent.stop();
        agent.disconnect();
        agent.shutdown();
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to stop thin agent for %s", model),
            e);
      }
  }
}
