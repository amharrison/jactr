package org.jactr.modules.pm.motor.managers;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.command.IMovement;

public class Movement implements IMovement
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory.getLog(Movement.class);

  final private AbstractMotorModule  _module;

  final private ChunkTypeRequest     _definingPattern;

  private MuscleState                _muscleState;

  private IIdentifier                _muscleIdentifier;

  private IIdentifier                _commandIdentifier;

  private State                      _state;

  private double[]                   _timingInfo;

  public Movement(ChunkTypeRequest pattern, IIdentifier muscleId,
      AbstractMotorModule module)
  {
    _definingPattern = pattern;
    _module = module;
    _muscleIdentifier = muscleId;
  }

  protected void configure(MuscleState muscleState,
      IIdentifier commandIdentifier)
  {
    _commandIdentifier = commandIdentifier;
    _muscleState = muscleState;
  }

  protected void setTimingInfo(double[] timing)
  {
    _timingInfo = timing;
  }

  protected double[] getTimingInfo()
  {
    return _timingInfo;
  }

  public ChunkTypeRequest getChunkTypeRequest()
  {
    return _definingPattern;
  }

  public IIdentifier getCommandIdentifier()
  {
    return _commandIdentifier;
  }

  public MuscleState getMuscleState()
  {
    return _muscleState;
  }

  public IIdentifier getMuscleIdentifier()
  {
    return _muscleIdentifier;
  }

  public void setState(State state, boolean updateNow)
  {
    if (_state != state)
    {
      _state = state;
      if (updateNow)
        updateMuscleState();
      else
        _module.getModel().getCycleProcessor().executeBefore(new Runnable() {

          public void run()
          {
            updateMuscleState();
          }
        });
    }
  }

  public State getState()
  {
    return _state;
  }

  protected void updateMuscleState()
  {
    switch (_state)
    {
      case INITIALIZED:
        _muscleState.set(_module.getFreeChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PREPARATION_SLOT,
            IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);
        break;
      case PREPARING:
        _muscleState.set(_module.getBusyChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PREPARATION_SLOT);
        break;
      case PREPARED:
        _muscleState.set(_module.getFreeChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PREPARATION_SLOT);
        break;
      case PROCESSING:
        _muscleState.set(_module.getBusyChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);
        break;
      case EXECUTING:
        _muscleState.set(_module.getFreeChunk(),
            IPerceptualBuffer.PROCESSOR_SLOT);
        _muscleState.set(_module.getBusyChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.EXECUTION_SLOT);
        break;
      case ABORTING:
        _muscleState.set(_module.getAbortingChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PREPARATION_SLOT,
            IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);
        break;
      case COMPLETED:
        _muscleState.set(_module.getFreeChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PREPARATION_SLOT,
            IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);
        break;
      case FAILED:
        _muscleState.set(_module.getErrorChunk(), IStatusBuffer.STATE_SLOT,
            IPerceptualBuffer.PREPARATION_SLOT,
            IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);
        break;
    }

  }

  @Override
  public String toString()
  {
    return _definingPattern.toString();
  }
}
