package org.jactr.modules.pm.motor.event;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.command.IMovement;

public class MotorModuleEvent extends
    AbstractACTREvent<IMotorModule, IMotorModuleListener>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MotorModuleEvent.class);

  static public enum Type {
    PREPARED, STARTED, COMPLETED, ABORTED, RESET, REJECTED
  };

  private IMovement _movement;

  private Type      _type;

  public MotorModuleEvent(IMotorModule source, IMovement movement, Type type)
  {
    super(source);
    _type = type;
    _movement = movement;
  }

  public Type getType()
  {
    return _type;
  }

  public IMovement getMovement()
  {
    return _movement;
  }

  @Override
  public void fire(final IMotorModuleListener listener)
  {
    switch (this.getType())
    {
      case PREPARED:
        listener.movementPrepared(this);
        break;
      case STARTED:
        listener.movementStarted(this);
        break;
      case ABORTED:
        listener.movementAborted(this);
        break;
      case COMPLETED:
        listener.movementCompleted(this);
        break;
      case REJECTED:
        listener.movementRejected(this);
        break;
    }
  }

}
