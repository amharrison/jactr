package org.jactr.modules.pm.motor.six;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.command.IMotorTimeEquation;
import org.jactr.modules.pm.motor.command.IMovement;

/**
 * default processing time equation. checks to see if the module
 * has DefaultProcessingTime set, otherwise uses 0.05
 * @author harrison
 *
 */
public class DefaultProcessingTimeEquation implements
    IMotorTimeEquation
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultProcessingTimeEquation.class);
  
  static public final String PROCESSING_TIME = "DefaultProcessingTime";

  private double _modulesProcessingTime = Double.NaN;
  
  public double compute(IMovement movement, IMotorModule module)
  {
    /*
     * havent tried to snag the value..
     */
    if(Double.isNaN(_modulesProcessingTime))
    {
      String val = module.getParameter(PROCESSING_TIME);
      try
      {
        _modulesProcessingTime = Double.parseDouble(val);
      }
      catch(Exception e)
      {
        _modulesProcessingTime = 0.05;
      }
    }
    
    
    return _modulesProcessingTime;
  }

}
