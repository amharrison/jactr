package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LongParameterProcessor extends
    ParameterProcessor<Long>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LongParameterProcessor.class);

  public LongParameterProcessor(String parameterName,
      Consumer<Long> setFunction, Supplier<Long> getFunction)
  {
    super(parameterName, Long::parseLong, setFunction, (i) -> {
      return Long.toString(i);
    },
        getFunction);
  }

}
