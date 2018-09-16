package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class IntegerParameterProcessor extends
 ParameterProcessor<Integer>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(IntegerParameterProcessor.class);

  public IntegerParameterProcessor(String parameterName,
      Consumer<Integer> setFunction, Supplier<Integer> getFunction)
  {
    super(parameterName, Integer::parseInt, setFunction, (i) -> {
      return Integer.toString(i);
    },
        getFunction);
  }

}
