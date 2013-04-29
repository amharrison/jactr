package org.jactr.modules.pm.motor.command.translators;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IEfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.DefaultMutableSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.motor.IMotorModule;

public abstract class AbstractManualTranslator extends AbstractTranslator
{
  static public final String                PECK_FITTS_COEFFICIENT = "PeckFittsCoefficient";

  static public final String                MINIMUM_FITTS_TIME     = "MinimumFittsTime";

  static public final String                MINIMUM_MOVEMENT_TIME  = "MinimumMovementTime";

  static private final double               LOG_2                  = Math
                                                                       .log(2);

  /**
   * Logger definition
   */
  static final transient Log                LOGGER                 = LogFactory
                                                                       .getLog(AbstractManualTranslator.class);

  private double                            _motorBurstTime        = Double.NaN;

  private double                            _minimumFittsTime      = Double.NaN;

  private double                            _peckFittsCoeff        = Double.NaN;
  
  private Collection<ISlot> _recycledSlotContainer = new ArrayList<ISlot>(4);

  public AbstractManualTranslator()
  {
    
  }

  /**
   * translates a set of slot values into a {@link IEfferentObject} that
   * represents a muscle defined within the pattern. Since many ACT-R movement
   * commands use multiple slots to define a muscle, this collapses them. (i.e.
   * translates hand right finger index into right-index). In addition to the
   * returned {@link IEfferentObject} this method should also ensure that the
   * slots used to define the muscle are nulled out and the muscle slot is
   * specified.
   * 
   * @param request
   * @param model
   * @return
   * @throws IllegalArgumentException
   */
  public IEfferentObject getMuscle(ChunkTypeRequest request, IModel model)
      throws IllegalArgumentException
  {
    /*
     * special processing to handle finger,hand,device
     */
    IChunk hand = null;
    IChunk finger = null;
    IChunk device = null;
    ISlot muscleSlot = null;
    
    _recycledSlotContainer.clear();
    _recycledSlotContainer = request.getSlots(_recycledSlotContainer);

    for (ISlot slot : _recycledSlotContainer)
    {
      String slotName = slot.getName();
      if (slotName.equalsIgnoreCase("hand"))
      {
        hand = (IChunk) slot.getValue();
        request.removeSlot(slot);
      }
      else if (slotName.equalsIgnoreCase("finger"))
      {
        finger = (IChunk) slot.getValue();
        request.removeSlot(slot);
      }
      else if (slotName.equalsIgnoreCase("device"))
      {
        device = (IChunk) slot.getValue();
        request.removeSlot(slot);
      }
      else if (slotName.equalsIgnoreCase("muscle")) muscleSlot = slot;
    }

    String muscleName = null;
    if (hand != null)
    {
      muscleName = hand.getSymbolicChunk().getName();

      if (finger != null)
        muscleName += "-" + finger.getSymbolicChunk().getName();
    }
    else if (device != null)
      muscleName = device.getSymbolicChunk().getName();
    else if (muscleSlot != null) muscleName = "" + muscleSlot.getValue();

    IEfferentObject muscle = getMuscle(muscleName, model);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("muscle named : " + muscleName
          + " matches to efferent object " + muscle.getIdentifier());

    if (muscle == null)
    {
      muscleName = null;
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("No muscle found, available : " + getCachedMuscleNames());
    }

    /**
     * finally we set the muscle slot value since that is what we will actually
     * be using further down stream in the processing
     */
    if (muscleSlot == null)
    {
      muscleSlot = new DefaultMutableSlot("muscle", muscleName);
      request.addSlot(muscleSlot);
    }
    else
      ((IMutableSlot) muscleSlot).setValue(muscleName);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Final movement pattern : " + request);

    return muscle;
  }

  protected double getMotorBurstTime(IMotorModule module)
  {
    if (Double.isNaN(_motorBurstTime))
      try
      {
        _motorBurstTime = Double.parseDouble(module
            .getParameter(MINIMUM_MOVEMENT_TIME));
      }
      catch (Exception e)
      {
        _motorBurstTime = 0.05;
      }
    return _motorBurstTime;
  }

  protected double getMinimumFittsTime(IMotorModule module)
  {
    if (Double.isNaN(_minimumFittsTime))
      try
      {
        _minimumFittsTime = Double.parseDouble(module
            .getParameter(MINIMUM_FITTS_TIME));
      }
      catch (Exception e)
      {
        _minimumFittsTime = 0.1;
      }
    return _minimumFittsTime;
  }

  protected double getPeckFittsCoefficient(IMotorModule module)
  {
    if (Double.isNaN(_peckFittsCoeff))
      try
      {
        _peckFittsCoeff = Double.parseDouble(module
            .getParameter(PECK_FITTS_COEFFICIENT));
      }
      catch (Exception e)
      {
        _peckFittsCoeff = 0.075;
      }
    return _peckFittsCoeff;
  }

  protected double computeFitts(double fittsCoeff, double distance, double width)
  {
    return fittsCoeff * Math.log(distance / width + 0.5) / LOG_2;
  }

  protected double[] computeRate(double[] origin, double[] target,
      double duration)
  {
    double[] rate = new double[origin.length];
    for (int i = 0; i < rate.length; i++)
    {
      rate[i] = (target[i] - origin[i]) / duration;
      if (Double.isNaN(rate[i])) rate[i] = 0;
    }
    return rate;
  }
}