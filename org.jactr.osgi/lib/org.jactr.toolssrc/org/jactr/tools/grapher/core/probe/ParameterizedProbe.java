package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.parameter.IParameterized;

public class ParameterizedProbe extends
    AbstractParameterizedProbe<IParameterized>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ParameterizedProbe.class);

  public ParameterizedProbe(String name, IParameterized parameterized)
  {
    super(name, parameterized);
  }

  @Override
  protected IParameterized asParameterized(IParameterized parameterizedObject)
  {
    return parameterizedObject;
  }

  @Override
  public void install(IParameterized parameterized, Executor executor)
  {
    /*
     * install the listener
     */
    

  }

  @Override
  protected AbstractParameterizedProbe<IParameterized> newInstance(
      IParameterized parameterized)
  {
    return new ParameterizedProbe(parameterized.getClass().getName(),
        parameterized);
  }

}
