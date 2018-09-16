/*
 * Created on Jan 21, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.utils.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class CollectionParameterHandler<T> extends
    ParameterHandler<Collection<T>>
{

  static private transient final Log LOGGER       = LogFactory
                                                      .getLog(CollectionParameterHandler.class);

  IParameterHandler<T>               _secondaryHandler;

  boolean                            _ignoreNulls = false;

  public CollectionParameterHandler()
  {

  }

  public CollectionParameterHandler(IParameterHandler<T> secondaryHandler)
  {
    this(secondaryHandler, false);
  }

  public CollectionParameterHandler(IParameterHandler<T> secondaryHandler,
      boolean ignoreNulls)
  {
    _secondaryHandler = secondaryHandler;
    _ignoreNulls = ignoreNulls;
  }

  @Override
  public Collection<T> coerce(String value)
  {
    if (_secondaryHandler == null)
      throw new ParameterException("Cannot coerce " + value
          + " into an array without a secondary handler",
          new OperationNotSupportedException(
              "Use coerce(String, ParameterHandler) instead"));
    return coerce(value, _secondaryHandler);
  }

  public Collection<T> coerce(String value,
      IParameterHandler<T> secondaryHandler)
  {
    /*
     * strip the first and last ()
     */
    String stripped = value.substring(value.indexOf("(") + 1, value
        .lastIndexOf(")"));

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Stripped " + value + " to " + stripped);

    /*
     * split at ,
     */
    String[] splits = stripped.split(",");
    List<T> rtn = new ArrayList<T>(splits.length);

    for (String split : splits)
    {
      split = split.trim();
      if (split.length() != 0)
        try
        {
          T secondary = secondaryHandler.coerce(split);
          if (secondary != null || !_ignoreNulls) rtn.add(secondary);
        }
        catch (Exception e)
        {
          LOGGER.error("Failed to coerce " + split + " with secondary handler "
              + secondaryHandler, e);
        }
    }

    return rtn;
  }

  public String toString(Collection<T> values,
      IParameterHandler<T> secondaryHandler)
  {
    StringBuilder sb = new StringBuilder("(");
    int appended = 0;

    for (T element : values)
    {
      String secondary = secondaryHandler.toString(element);
      if (secondary.length() > 0)
      {
        sb.append(secondary).append(", ");
        appended++;
      }
    }
    // delete the last two characters
    if (appended != 0)
    {
      sb.deleteCharAt(sb.length() - 1);
      sb.deleteCharAt(sb.length() - 1);
    }

    sb.append(")");
    return sb.toString();
  }

  @Override
  public String toString(Collection<T> values)
  {
    if (_secondaryHandler == null)
      throw new ParameterException(
          "Cannot toString without a secondary parameter handler",
          new OperationNotSupportedException(
              "Using toString(value, ParameterHandler)"));
    return toString(values, _secondaryHandler);
  }

}