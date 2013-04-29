package org.jactr.modules.pm.motor.buffer.processor;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.object.IEfferentObject;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.SimpleRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.command.ICommandTranslator;
import org.jactr.modules.pm.motor.command.IMovement;
import org.jactr.modules.pm.motor.managers.MotorCommandManager;

public class AbortRequestDelegate extends SimpleRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbortRequestDelegate.class);

  public AbortRequestDelegate(IChunkType chunkType)
  {
    super(chunkType);
  }

  public boolean request(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    ChunkTypeRequest cRequest = (ChunkTypeRequest) request;
    String muscleName = null;
    for (IConditionalSlot slot : cRequest.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase("muscle")
          && slot.getCondition() == IConditionalSlot.EQUALS)
      {
        muscleName = (String) slot.getValue();
        break;
      }

    IModel model = buffer.getModel();
    AbstractMotorModule amm = (AbstractMotorModule) buffer.getModule();
    ICommandTranslator translator = amm.getCommandTranslator();
    MotorCommandManager manager = amm.getCommandManager();

    String msg = null;
    IMovement toAbort = null;

    if (muscleName != null)
    {
      IEfferentObject muscle = translator.getMuscle(muscleName, model);

      if (muscle == null)
        msg = "No muscle found corresponding to " + muscleName
            + ", cannot abort movement of it";
      else
      {
        IMovement movement = manager.getMovementFromMuscle(muscle
            .getIdentifier());

        if (movement != null)
          toAbort = movement;
        else
        {
          movement = manager.getPreparedMovement(muscle.getIdentifier());
          if (movement != null)
            toAbort = movement;
          else
            msg = "No currently executing movement of " + muscleName;
        }
      }
    }
    else
    {
      /*
       * no name provided, snag the last executed movement
       */
      IMovement movement = manager.getMovementFromMuscle(null);

      if (movement != null)
        toAbort = movement;
      else
      {
        movement = manager.getPreparedMovement(null);
        if (movement != null)
          toAbort = movement;
        else
          msg = "No currently executing movement";
      }
    }

    if (toAbort != null)
    {
      msg = "Requesting abort of movement of "
          + MotorUtilities.getName(manager.getMuscle(toAbort
              .getMuscleIdentifier()));
      amm.abort(toAbort, requestTime);
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR, msg);

    return toAbort != null;
  }

  /**
   * returns the current movement associated with the muscle defined in this
   * request
   * 
   * @param request
   * @return
   */
  static public IMovement getMovement(ChunkTypeRequest cRequest,
      AbstractMotorModule motor)
  {
    String muscleName = null;
    for (IConditionalSlot slot : cRequest.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase("muscle")
          && slot.getCondition() == IConditionalSlot.EQUALS)
      {
        muscleName = (String) slot.getValue();
        break;
      }

    if (muscleName == null)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("No muscle name found");
      return null;
    }

    ICommandTranslator translator = motor.getCommandTranslator();
    MotorCommandManager manager = motor.getCommandManager();

    IEfferentObject muscle = translator.getMuscle(muscleName, motor.getModel());
    if (muscle == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("No muscle found named " + muscleName);
      return null;
    }

    IMovement movement = manager.getMovementFromMuscle(muscle.getIdentifier());
    if (movement == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No active movement for " + muscle
            + ", checking for prepared movement");
      movement = manager.getPreparedMovement(muscle.getIdentifier());
    }

    return movement;
  }

  /**
   * noop. aborts are immediately processed
   */
  public void clear()
  {
  }
}
