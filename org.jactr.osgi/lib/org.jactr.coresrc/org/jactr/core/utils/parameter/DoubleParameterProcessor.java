package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DoubleParameterProcessor extends
 ParameterProcessor<Double>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DoubleParameterProcessor.class);

  public DoubleParameterProcessor(String parameterName,
      Consumer<Double> setFunction, Supplier<Double> getFunction)
  {
    super(parameterName, Double::parseDouble, setFunction, Number::toString,
        getFunction);
  }

}
