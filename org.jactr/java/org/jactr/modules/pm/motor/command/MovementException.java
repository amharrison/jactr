package org.jactr.modules.pm.motor.command;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MovementException extends RuntimeException
{
  /**
   * 
   */
  private static final long          serialVersionUID = -438072006595003276L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MovementException.class);

  final transient private IMovement  _movement;

  public MovementException(String message, IMovement movement)
  {
    super(message);
    _movement = movement;
  }

}
