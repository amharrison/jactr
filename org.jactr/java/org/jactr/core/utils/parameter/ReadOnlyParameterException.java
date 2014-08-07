package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReadOnlyParameterException extends ParameterException
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ReadOnlyParameterException.class);


  public ReadOnlyParameterException(String arg0)
  {
    super(String.format("%s is a ready only parameter", arg0));
  }



}
