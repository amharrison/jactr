package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class NumberParameterProcessor extends ParameterProcessor<Number>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NumberParameterProcessor.class);

  public NumberParameterProcessor(String parameterName,
      Consumer<Number> setFunction, Supplier<Number> getFunction)
  {
    super(parameterName, Double::parseDouble, setFunction, Number::toString,
        getFunction);
  }

}
