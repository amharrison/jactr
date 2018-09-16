package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClassNameParameterProcessor extends ParameterProcessor<Class>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ClassNameParameterProcessor.class);

  public ClassNameParameterProcessor(String parameterName,
      Consumer<Class> setFunction, Supplier<Class> getFunction,
      final ClassLoader classLoader)
  {
    super(parameterName, (String name) -> {
      try
      {
        return classLoader.loadClass(name);
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to load class %s", name), e);

        throw new ParameterException(e);
      }
    }, setFunction, b -> b.getName(),
        getFunction);
  }



}
