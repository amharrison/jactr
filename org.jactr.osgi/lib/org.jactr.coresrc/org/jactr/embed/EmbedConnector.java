package org.jactr.embed;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
/*
 * default logging
 */
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.agents.ThinAgent;
import org.commonreality.time.IClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.LocalConnector;
import org.jactr.core.runtime.ACTRRuntime;

public class EmbedConnector extends LocalConnector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(EmbedConnector.class);

  static private final String        EMBED_AGENT_KEY = "embedConnector.thinAgent";

  private boolean                    _running        = false;

  private ReentrantReadWriteLock     _lock           = new ReentrantReadWriteLock();

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

    try
    {
      runLocked(_lock.writeLock(), () -> {
        if (!isConnected(model))
        {
          super.connect(model);
          startThinAgent(model, getClock(model));
        }
      });
    }
    catch (InterruptedException e)
    {
      // totally expected if we are halting, but very unlikely
      LOGGER.debug("EmbedConnector.connect threw InterruptedException : ", e);
    }
    catch (Exception e)
    {
      LOGGER.error("EmbedConnector.connect threw Exception : ", e);
    }

  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  @Override
  public void disconnect(IModel model)
  {
    try
    {
      runLocked(_lock.writeLock(), () -> {
        if (isConnected(model))
        {
          super.disconnect(model);
          stopThinAgent(model);
        }
      });
    }
    catch (InterruptedException e)
    {
      LOGGER.debug("EmbedConnector.connect threw InterruptedException : ", e);
    }
    catch (Exception e)
    {
      LOGGER.error("EmbedConnector.connect threw Exception : ", e);
    }
  }

  public boolean isConnected(IModel model)
  {
    return getAgent(model) != null;
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
   * We create and start the thin agents at start, not connect, so that we are
   * sure they are available immediately. It is common to require the thinagent
   * slightly before the model has fully started.
   * 
   * @see org.jactr.core.reality.connector.IConnector#start()
   */
  @Override
  public void start()
  {
    try
    {
      runLocked(_lock.writeLock(), () -> {
        if (!_running)
          for (IModel model : ACTRRuntime.getRuntime().getModels())
            connect(model);
        _running = true;
      });
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("EmbedConnector.start threw InterruptedException : ", e);
    }
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#stop()
   */
  @Override
  public void stop()
  {
    try
    {
      runLocked(_lock.writeLock(), () -> {
        if (_running) for (IModel model : ACTRRuntime.getRuntime().getModels())
          disconnect(model);
        _running = false;
      });
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("EmbedConnector.start threw InterruptedException : ", e);
    }
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
    model.setMetaData(EMBED_AGENT_KEY, null);

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

  static public void runLocked(Lock lock, Runnable runnable)
      throws InterruptedException
  {
    boolean locked = false;
    try
    {
      locked = attemptLock(lock, runnable);
      if (locked)
        runnable.run();
      else
        throw new InterruptedException();
    }
    finally
    {
      if (locked) attemptUnlock(lock, runnable);
    }
  }

  static public <T> T runLocked(Lock lock, Callable<T> callable)
      throws InterruptedException, Exception
  {
    boolean locked = false;
    try
    {
      locked = attemptLock(lock, callable);
      if (locked)
        return callable.call();
      else
        throw new InterruptedException();
    }
    finally
    {
      if (locked) attemptUnlock(lock, callable);
    }
  }

  static protected void attemptUnlock(Lock lock, Object with)
  {
    lock.unlock();
  }

  static protected boolean attemptLock(Lock lock, Object with)
      throws InterruptedException
  {
    lock.lock();
    return true;
  }
}
