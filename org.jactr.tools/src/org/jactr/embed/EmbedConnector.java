package org.jactr.embed;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.agents.ThinAgent;
import org.commonreality.time.IClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.LocalConnector;

public class EmbedConnector extends LocalConnector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(EmbedConnector.class);


  static private final String        EMBED_AGENT_KEY = "embedConnector.thinAgent";


  private boolean                    _running        = false;

  public EmbedConnector()
  {
    super();
  }

  public EmbedConnector(boolean useIndependentClocks)
  {
    super(useIndependentClocks);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#connect(org.jactr.core.model.IModel)
   */
  @Override
  public void connect(IModel model)
  {
    super.connect(model);

    startThinAgent(model, getClock(model));
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  @Override
  public void disconnect(IModel model)
  {
    super.disconnect(model);

    stopThinAgent(model);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#getAgentInterface(org.jactr.core.model.IModel)
   */
  @Override
  public IAgent getAgent(IModel model)
  {
    return (IAgent) model.getMetaData(EMBED_AGENT_KEY);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#isRunning()
   */
  @Override
  public boolean isRunning()
  {
    return _running;
  }



  /**
   * @see org.jactr.core.reality.connector.IConnector#start()
   */
  @Override
  public void start()
  {
    _running = true;
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#stop()
   */
  @Override
  public void stop()
  {
    _running = false;
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
