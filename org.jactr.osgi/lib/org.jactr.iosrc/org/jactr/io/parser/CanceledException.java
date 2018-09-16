package org.jactr.io.parser;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CanceledException extends RuntimeException
{
  /**
   * 
   */
  private static final long serialVersionUID = -7382746231986528250L;
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(CanceledException.class);

}
