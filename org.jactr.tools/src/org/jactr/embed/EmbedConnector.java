package org.jactr.embed;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.time.IClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.IClockConfigurator;
import org.jactr.core.reality.connector.IConnector;

public class EmbedConnector implements IConnector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EmbedConnector.class);

  public EmbedConnector()
  {

  }

  @Override
  public void start()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void stop()
  {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isRunning()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void connect(IModel model)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void disconnect(IModel model)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public IAgent getAgent(IModel model)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IClock getClock(IModel model)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public IClockConfigurator getClockConfigurator()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setClockConfigurator(IClockConfigurator clockConfig)
  {
    // TODO Auto-generated method stub

  }

}
