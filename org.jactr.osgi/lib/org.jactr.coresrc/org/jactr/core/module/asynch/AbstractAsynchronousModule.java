package org.jactr.core.module.asynch;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.queue.timedevents.BlockingTimedEvent;
import org.jactr.core.utils.parameter.IParameterized;

/**
 * abstract asynchronous module. Those wishing to add additional parameters
 * should extends {@link #getSetableParameters()}
 * 
 * @author harrison
 */
public abstract class AbstractAsynchronousModule extends AbstractModule
    implements IAsynchronousModule, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                 = LogFactory
                                                                .getLog(AbstractAsynchronousModule.class);

  private boolean                    _strictSynchronization = true;

  public AbstractAsynchronousModule(String name)
  {
    super(name);
  }

  public boolean isStrictSynchronizationEnabled()
  {
    return _strictSynchronization;
  }

  public void setStrictSynchronizationEnabled(boolean enableStrict)
  {
    _strictSynchronization = enableStrict;
  }

  public BlockingTimedEvent synchronizedTimedEvent(double startTime,
      double blockAtTime)
  {
    BlockingTimedEvent bte = new BlockingTimedEvent(this, startTime,
        blockAtTime);

    if (isStrictSynchronizationEnabled())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("Queueing synchronization block to lock at " + blockAtTime);

      getModel().getTimedEventQueue().enqueue(bte);
    }
    else
      bte.abort();

    return bte;
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.singleton(STRICT_SYNCHRONIZATION_PARAM);
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public void setParameter(String key, String value)
  {
    if (STRICT_SYNCHRONIZATION_PARAM.equalsIgnoreCase(key))
      setStrictSynchronizationEnabled(Boolean.parseBoolean(value));
    else
      LOGGER.warn(String.format(
          "%s doesn't recognize %s. Available parameters : %s", getClass()
              .getSimpleName(), key, getSetableParameters()));
  }

  public String getParameter(String key)
  {
    if (STRICT_SYNCHRONIZATION_PARAM.equalsIgnoreCase(key))
      return "" + isStrictSynchronizationEnabled();
    return null;
  }

}
