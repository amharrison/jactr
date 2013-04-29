package org.jactr.modules.pm.motor.command;

/*
 * default logging
 */
import org.jactr.modules.pm.motor.IMotorModule;

/**
 * general equation for all time calculations
 * @author harrison
 *
 */
public interface IMotorTimeEquation
{
 
  public double compute(IMovement movement, IMotorModule module);
}
