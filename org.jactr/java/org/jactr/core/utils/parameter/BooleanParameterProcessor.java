package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BooleanParameterProcessor extends ParameterProcessor<Boolean>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BooleanParameterProcessor.class);

  public BooleanParameterProcessor(String parameterName,
      Consumer<Boolean> setFunction, Supplier<Boolean> getFunction)
  {
    super(parameterName, Boolean::parseBoolean, setFunction, b -> b.toString(),
        getFunction);
  }

}
