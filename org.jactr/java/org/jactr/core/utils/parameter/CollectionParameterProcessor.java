package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CollectionParameterProcessor<T>
    extends ParameterProcessor<Collection<T>>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(CollectionParameterProcessor.class);


  public CollectionParameterProcessor(String parameterName,
      Consumer<Collection<T>> setFunction, Supplier<Collection<T>> getFunction,
      final ParameterProcessor<T> contentProcessor, final boolean ignoreNulls)
  {
    super(parameterName, (String s) -> {
      String stripped = s.substring(s.indexOf("(") + 1, s.lastIndexOf(")"));

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Stripped " + s + " to " + stripped);

      String[] splits = stripped.split(",");
      List<T> rtn = new ArrayList<T>(splits.length);

      for (String split : splits)
      {
        split = split.trim();
        if (split.length() != 0) try
        {
          T secondary = contentProcessor.getFromStringFunction().apply(split);
          if (secondary != null || !ignoreNulls) rtn.add(secondary);
        }
        catch (Exception e)
        {
          LOGGER.error("Failed to coerce " + split + " with secondary handler "
              + contentProcessor, e);
        }
      }

      return rtn;
    }, setFunction, (Collection<T> c) -> {
      StringBuilder sb = new StringBuilder("(");
      int appended = 0;
      for (T item : c)
      {
        String asString = contentProcessor.getToStringFunction().apply(item);
        if (asString != null && asString.length() != 0 || !ignoreNulls)
        {
          sb.append(asString).append(", ");
          appended++;
        }
      }

      if (appended > 0) sb.delete(sb.length() - 2, sb.length());
      sb.append(")");

      return sb.toString();
    }, getFunction);

  }


}
