package org.jactr.modules.pm.motor;

/*
 * default logging
 */
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.modules.pm.IPerceptualModule;
import org.jactr.modules.pm.motor.buffer.IMotorActivationBuffer;
import org.jactr.modules.pm.motor.command.ICommandTranslator;
import org.jactr.modules.pm.motor.command.IMotorTimeEquation;
import org.jactr.modules.pm.motor.command.IMovement;
import org.jactr.modules.pm.motor.event.IMotorModuleListener;
import org.jactr.modules.pm.motor.event.MotorModuleEvent;
import org.jactr.modules.pm.motor.managers.MotorCommandManager;
import org.jactr.modules.pm.motor.managers.MuscleStateManager;

public interface IMotorModule extends IPerceptualModule, IParameterized
{
  static public final String MOVEMENT_CHUNK_TYPE = "motor-command";
  
  static public final String ABORT_CHUNK_TYPE = "abort";
  
  static public final String MUSCLE_SLOT = "muscle";
  
  static public final String ABORTING_CHUNK      = "aborting";

  /**
   * the root movement type chunk
   * @return
   */
  public IChunkType getMovementChunkType();
  
  public IChunkType getAbortChunkType();
  
  /**
   * the {@link IMotorTimeEquation} that determines how much time is spent
   * processing the movement. Actual equation times are determined by
   * individual {@link IEfferentCommandTranslator}s.
   * @return
   */
  public IMotorTimeEquation getProcessingTimeEquation();
  
  /**
   * the {@link IMotorTimeEquation} that determines how long the preparation
   * of a motor movement lasts
   * @return
   */
  public IMotorTimeEquation getPreparationTimeEquation();
  
  /**
   * returns the most recently prepared movement for the given muscle
   * @param muscle if null, return the last movement regardless of the muscle
   * @return
   */
  public IMovement getLastMovement(IIdentifier muscle);
  
  /**
   * responsible code snippet that translates {@link ChunkPattern}s into
   * {@link IEfferentCommand}s
   * @return
   */
  public ICommandTranslator getCommandTranslator();

  /**
   * tracker and manager of muscle info
   * 
   * @return
   */
  public MuscleStateManager getMuscleManager();

  public MotorCommandManager getCommandManager();

  /**
   * return true if we could prepare this motor command
   * 
   * @param pattern
   * @return
   */
  public boolean canPrepare(ChunkTypeRequest pattern);

  /**
   * prepare a motor movement. This involves creating the movement, setting up
   * the buffer (and queueing timed events), and negotiating the newly created
   * {@link IEfferentCommand} with common reality
   * 
   * @param pattern
   */
  public Future<IMovement> prepare(ChunkTypeRequest pattern,
      double requestTime, boolean prepareOnly);
  
 
  public boolean canExecute(ChunkTypeRequest pattern);
  
  /**
   * execute a motor movement. This involves communicating the movement
   * start with common reality and queueing whatever timed events are necessary
   * 
   * @param movement
   * @return
   */
  public Future<IMovement> execute(IMovement movement, double requestTime);
  
  
  public boolean canAbort(ChunkTypeRequest request);

  public boolean canAbort(IMovement movement);

  public Future<IMovement> abort(IMovement movement, double requestTime);
  
  public boolean canAdjust(ChunkTypeRequest request);

  public boolean canAdjust(IMovement movement);

  public Future<IMovement> adjust(IMovement movement, ChunkTypeRequest request,
      double requestTime);
  
  public IMotorActivationBuffer getBuffer();

  /**
   * reset the motor system. this merely calls reset(true)
   */
  public void reset();

  /**
   * reset all the muscles that are in error and optionally stop all active
   * movements too.
   * 
   * @param stopActiveMovements
   */
  public void reset(boolean stopActiveMovements);

  /**
   * reset the state of a single muscle
   * 
   * @param muscle
   */
  public void reset(String muscle);

  public void addListener(IMotorModuleListener listener, Executor executor);
  
  public void removeListener(IMotorModuleListener listener);
  
  public boolean hasListeners();
  
  public void dispatch(MotorModuleEvent event);
}
