package org.jactr.tools.throttle;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.instrument.IInstrument;

/**
 * simple runtime throttler. Attaches to a model and ensure that the full model
 * cycle takes at least MinimumCycleRealTime seconds.
 * 
 * @author harrison
 */
public class RuntimeThrottler implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RuntimeThrottler.class);

  static public final String         MINIMUM_CYCLE_TIME = "MinimumCycleRealTime";

  private double                     _minimumCycleTime  = 0.05;

  public void setParameter(String key, String value)
  {
    if (MINIMUM_CYCLE_TIME.equalsIgnoreCase(key))
      _minimumCycleTime = ParameterHandler.numberInstance().coerce(value)
          .doubleValue();

  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.singleton(MINIMUM_CYCLE_TIME);
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void install(IModel model)
  {
    model.addListener(new ThreadSleepingListener(_minimumCycleTime), null);
  }

  public void uninstall(IModel model)
  {
    // noop - probably need to fix this.
  }

  public void initialize()
  {
    // noop
  }

}
