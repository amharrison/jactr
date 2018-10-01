package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParameterProcessor<T>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ParameterProcessor.class);

  private Function<String, T>        _fromString;

  private Function<T, String>        _toString;

  private Consumer<T>                _setFunction;

  private Supplier<T>                _getFunction;

  private final String               _parameterName;

  /**
   * @param parameterName
   * @param fromString
   * @param setFunction
   *          may be null if read only
   * @param toString
   * @param getFunction
   */
  public ParameterProcessor(String parameterName,
      Function<String, T> fromString, Consumer<T> setFunction,
      Function<T, String> toString, Supplier<T> getFunction)
  {
    _parameterName = parameterName;
    _fromString = fromString;
    _toString = toString;
    _setFunction = setFunction;
    _getFunction = getFunction;
  }

  protected void setGetter(Supplier<T> supplier)
  {
    _getFunction = supplier;
  }

  protected void setSetter(Consumer<T> consumer)
  {
    _setFunction = consumer;
  }

  protected void setFromString(Function<String, T> from)
  {
    _fromString = from;
  }

  protected void setToString(Function<T, String> to)
  {
    _toString = to;
  }

  public Function<String, T> getFromStringFunction()
  {
    return _fromString;
  }

  public Function<T, String> getToStringFunction()
  {
    return _toString;
  }

  public Consumer<T> getSetFunction()
  {
    return _setFunction;
  }

  public Supplier<T> getGetFunction()
  {
    return _getFunction;
  }

  public boolean isSetable()
  {
    return _setFunction != null;
  }

  public T setParameter(String value) throws ParameterException
  {
    if (!isSetable()) throw new ReadOnlyParameterException(_parameterName);

    try
    {
      T fromString = getFromStringFunction().apply(value);
      getSetFunction().accept(fromString);
      return fromString;
    }
    catch (Exception e)
    {
      throw new ParameterException(String.format("Failed to set %s to %s ",
          _parameterName, value), e);
    }
  }

  public String getParameter() throws ParameterException
  {
    try
    {
      T value = getGetFunction().get();
      return getToStringFunction().apply(value);
    }
    catch (Exception e)
    {
      throw new ParameterException(String.format("Failed to get %s ",
          _parameterName), e);
    }
  }

  public String getParameterName()
  {
    return _parameterName;
  }
}
