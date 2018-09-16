package org.jactr.modules.pm.motor.event;

/*
 * default logging
 */
import java.util.EventListener;

public interface IMotorModuleListener extends EventListener
{

  public void movementPrepared(MotorModuleEvent event);
  
  public void movementStarted(MotorModuleEvent event);
  
  public void movementCompleted(MotorModuleEvent event);
  
  public void movementAborted(MotorModuleEvent event);
  
  public void movementRejected(MotorModuleEvent event);

  public void muscleAdded(MotorModuleEvent event);

  public void muscleRemoved(MotorModuleEvent event);
}
