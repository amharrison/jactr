package org.jactr.modules.pm.motor.six;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.command.IMotorTimeEquation;
import org.jactr.modules.pm.motor.command.IMovement;

/**
 * @author harrison
 */
public class DefaultPreparationTimeEquation implements IMotorTimeEquation
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(DefaultPreparationTimeEquation.class);

  static public final String         TIME_PER_FEATURE = "TimePerFeature";

  private double                     _timePerFeature  = 0.05;

  private boolean                    _firstRun        = true;

  public double compute(IMovement movement, IMotorModule module)
  {
    if (_firstRun)
    {
      try
      {
        _timePerFeature = Double.parseDouble(module
            .getParameter(TIME_PER_FEATURE));
      }
      catch (Exception e)
      {
        _timePerFeature = 0.05;
      }
      _firstRun = false;
    }

    Map<String, Object> features = generateSlotMap(movement);

    double prepTime = features.size() * _timePerFeature;

    IMovement last = module.getLastMovement(movement.getMuscleIdentifier());

    if (last != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Comparing current movement " + movement
            + " to last movement " + last);
      /*
       * calculate the savings
       */
      Map<String, Object> oldFeatures = generateSlotMap(last);

      if (movement.getChunkTypeRequest().getChunkType() == last.getChunkTypeRequest()
          .getChunkType())
      {
        prepTime -= _timePerFeature;

        if (features.get(IMotorModule.MUSCLE_SLOT).equals(
            oldFeatures.get(IMotorModule.MUSCLE_SLOT)))
        {
          prepTime -= _timePerFeature;
          features.remove(IMotorModule.MUSCLE_SLOT);
          oldFeatures.remove(IMotorModule.MUSCLE_SLOT);

          /*
           * now we iterate through the remaining features. any non-null matches
           * are a further reudction
           */
          for (Map.Entry<String, Object> entry : features.entrySet())
            if (entry.getValue() != null)
              if (entry.getValue().equals(oldFeatures.get(entry.getKey())))
                prepTime -= _timePerFeature;
        }
      }
    }

    return Math.max(_timePerFeature, prepTime);
  }

  protected Map<String, Object> generateSlotMap(IMovement movement)
  {
    Map<String, Object> rtn = new TreeMap<String, Object>();

    IChunkType movementType = movement.getChunkTypeRequest().getChunkType();
    for (ISlot slot : movementType.getSymbolicChunkType().getSlots())
      rtn.put(slot.getName(), slot.getValue());

    for (ISlot slot : movement.getChunkTypeRequest().getSlots())
      if (((IConditionalSlot)slot).getCondition()==IConditionalSlot.EQUALS)
      {
        if (rtn.containsKey(slot.getName()))
          rtn.put(slot.getName(), slot.getValue());
        else if (LOGGER.isWarnEnabled())
          LOGGER.warn("Ignoring invalid slot " + slot + " for chunk type "
              + movementType);
      }
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Feature set for "+movement+" : "+rtn);

    return rtn;
  }
}
