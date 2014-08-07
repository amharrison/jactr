package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;

public class ACTRParameterProcessor extends ParameterProcessor<Object>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ACTRParameterProcessor.class);

  public ACTRParameterProcessor(String parameterName,
      Consumer<Object> setFunction, Supplier<Object> getFunction,
      final IModel model)
  {
    super(parameterName, (String element) -> {
      if (element == null) return null;

      try
      {
        Object rtn = null;

        rtn = model.getDeclarativeModule().getChunk(element).get();

        if (rtn == null)
          rtn = model.getDeclarativeModule().getChunkType(element).get();

        if (rtn == null)
          rtn = model.getProceduralModule().getProduction(element).get();

        if (rtn == null) rtn = model.getActivationBuffer(element);

        return rtn;
      }
      catch (Exception e)
      {
        LOGGER.error(String.format(
            "Could not coerce %s into theoretical object", element), e);
        throw new ParameterException(e);
      }
    }, setFunction, b -> b.toString(), getFunction);
  }

}
