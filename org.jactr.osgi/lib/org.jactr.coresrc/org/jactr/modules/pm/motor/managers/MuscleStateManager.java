package org.jactr.modules.pm.motor.managers;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.motor.MotorConstants;
import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.commonreality.object.manager.IEfferentObjectManager;
import org.commonreality.object.manager.event.IEfferentListener;
import org.commonreality.object.manager.event.IObjectEvent;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.event.MotorModuleEvent;

public class MuscleStateManager implements IEfferentListener
{
  /**
   * Logger definition
   */
  static private final transient Log          LOGGER = LogFactory
                                                         .getLog(MuscleStateManager.class);

  final private IMotorModule                  _motor;

  final private Map<IIdentifier, MuscleState> _muscleStates;

  private IAgent                              _currentAgent;

  public MuscleStateManager(IMotorModule motor)
  {
    _motor = motor;
    _muscleStates = new HashMap<IIdentifier, MuscleState>();
  }

  public void install(IAgent agent)
  {
    _currentAgent = agent;
    /*
     * attach
     */
    agent.getEfferentObjectManager().addListener(this,
        ExecutorServices.INLINE_EXECUTOR);

    IEfferentObjectManager eManager = agent.getEfferentObjectManager();
    for (IIdentifier id : eManager.getIdentifiers())
    {
      IEfferentObject object = eManager.get(id);
      if (object == null) continue;
      if (MotorUtilities.isMotor(object))
      {
        MuscleState state = getMuscleState(object.getIdentifier());
        updateState(state, object);
      }
    }
  }

  public void uninstall(IAgent agent)
  {
    _currentAgent = null;
    agent.getEfferentObjectManager().removeListener(this);
  }

  public MuscleState getMuscleState(String muscleName)
  {
    IEfferentObjectManager manager = _currentAgent.getEfferentObjectManager();
    synchronized (_muscleStates)
    {
      for (Map.Entry<IIdentifier, MuscleState> entry : _muscleStates.entrySet())
      {
        IEfferentObject muscle = manager.get(entry.getKey());
        if (muscle == null) continue;
        String name = MotorUtilities.getName(muscle);
        if (muscleName.equalsIgnoreCase(name)) return entry.getValue();
      }
    }

    /*
     * not already in our cache.. scan all
     */
    for (IIdentifier id : manager.getIdentifiers())
    {
      IEfferentObject muscle = _currentAgent.getEfferentObjectManager().get(id);

      if (!MotorUtilities.isMotor(muscle)) continue;

      String name = MotorUtilities.getName(muscle);
      if (!muscleName.equalsIgnoreCase(name)) continue;

      MuscleState state = new MuscleState(id, MotorUtilities.getName(muscle),
          _motor.getModel().getDeclarativeModule().getFreeChunk());
      synchronized (_muscleStates)
      {
        _muscleStates.put(id, state);
      }
    }

    return null;
  }

  public MuscleState getMuscleState(IIdentifier muscleIdentifier)
  {
    MuscleState state = null;
    synchronized (_muscleStates)
    {
      state = _muscleStates.get(muscleIdentifier);
    }

    if (state == null)
    {
      IEfferentObject muscle = _currentAgent.getEfferentObjectManager().get(
          muscleIdentifier);

      if (muscle == null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("No muscle found for " + muscleIdentifier);
        return null;
      }

      if (!MotorUtilities.isMotor(muscle))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(muscleIdentifier + " is not a motor");
        return null;
      }

      state = new MuscleState(muscleIdentifier, MotorUtilities.getName(muscle),
          _motor.getModel().getDeclarativeModule().getFreeChunk());

      synchronized (_muscleStates)
      {
        _muscleStates.put(muscleIdentifier, state);
      }
      /*
       * dispatch
       */
      if (_motor.hasListeners())
        _motor.dispatch(new MotorModuleEvent(_motor, state,
            MotorModuleEvent.Type.ADDED));
    }

    return state;
  }

  public void objectsAdded(IObjectEvent<IEfferentObject, ?> addEvent)
  {
    for (IEfferentObject object : addEvent.getObjects())
      if (MotorUtilities.isMotor(object))
      {
        MuscleState state = getMuscleState(object.getIdentifier());
        updateState(state, object);
      }

  }

  public void objectsRemoved(IObjectEvent<IEfferentObject, ?> removeEvent)
  {
    for (IEfferentObject object : removeEvent.getObjects())
    {
      MuscleState state = _muscleStates.remove(object.getIdentifier());

      if (state != null)
        if (_motor.hasListeners())
          _motor.dispatch(new MotorModuleEvent(_motor, state,
              MotorModuleEvent.Type.ADDED));
    }
  }

  public void objectsUpdated(IObjectEvent<IEfferentObject, ?> updateEvent)
  {
    for (IObjectDelta delta : updateEvent.getDeltas())
    {
      MuscleState state = getMuscleState(delta.getIdentifier());
      if (state == null) continue;
      updateState(state, delta);
    }
  }

  protected void updateState(MuscleState state, IEfferentObject object)
  {
    double[] position = MotorUtilities.getPosition(object);
    if (position == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn("No position information available for "
            + object.getIdentifier());
      return;
    }
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Updating %s position : %s", state.getName(),
          Arrays.toString(position)));

    state.setPosition(position);
  }

  protected void updateState(MuscleState state, IObjectDelta delta)
  {
    if (delta.getChangedProperties().contains(MotorConstants.POSITION))
      try
      {
        double[] position = (double[]) delta
            .getNewValue(MotorConstants.POSITION);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Updating %s position : %s",
              state.getName(), Arrays.toString(position)));

        state.setPosition(position);
      }
      catch (Exception e)
      {
        LOGGER.error("failed to set position ", e);
      }
  }

}
