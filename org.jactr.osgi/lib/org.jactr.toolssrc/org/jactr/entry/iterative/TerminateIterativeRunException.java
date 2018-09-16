package org.jactr.entry.iterative;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TerminateIterativeRunException extends Exception
{
  /**
   * 
   */
  private static final long          serialVersionUID = 4941116438896123697L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TerminateIterativeRunException.class);

  public TerminateIterativeRunException(String message)
  {
    super(message);
  }

}
