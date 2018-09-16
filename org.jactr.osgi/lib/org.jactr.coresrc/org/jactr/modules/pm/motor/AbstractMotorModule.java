package org.jactr.modules.pm.motor;

/*
 * default logging
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.object.IEfferentObject;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.concurrent.FutureManager;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.AbstractPerceptualModule;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.motor.buffer.IMotorActivationBuffer;
import org.jactr.modules.pm.motor.command.DefaultCommandTranslator;
import org.jactr.modules.pm.motor.command.ICommandTranslator;
import org.jactr.modules.pm.motor.command.ICommandTranslatorDelegate;
import org.jactr.modules.pm.motor.command.IMotorTimeEquation;
import org.jactr.modules.pm.motor.command.IMovement;
import org.jactr.modules.pm.motor.event.IMotorModuleListener;
import org.jactr.modules.pm.motor.event.MotorModuleEvent;
import org.jactr.modules.pm.motor.managers.MotorCommandManager;
import org.jactr.modules.pm.motor.managers.MuscleState;
import org.jactr.modules.pm.motor.managers.MuscleStateManager;

/**
 * abstract motor module that handles just about everything one needs. It uses a
 * combination of the default {@link MotorManager} (which handles all the logic
 * of communicating with common reality, as well as the movement lifecylce), the
 * {@link DefaultCommandTranslator} to route {@link ChunkPattern} movement
 * requests to specific {@link ICommandTranslatorDelegate}s, and uses
 * {@link IMotorTimeEquation} to determine how long preparation and processing
 * phases last. <br>
 * <br>
 * The {@link ICommandTranslatorDelegate}s map {@link ChunkPattern}s and Strings
 * to specific muscles, as defined by {@link IEfferentObject}s, as well as
 * translating the {@link ChunkPattern}s into precise {@link IEfferentCommand}s
 * which will be passed to common reality by the {@link MotorManager}. There
 * should be a unique {@link ICommandTranslatorDelegate} for each and every
 * movement chunktype defined. <br>
 * <br>
 * One will notice that only preparation and processing times are directly
 * controled by the motor module. execution times can only be indirectly
 * specified since execution time is actually up to common reality and the
 * sensor that receives the command. It can be influenced by having the
 * {@link ICommandTranslatorDelegate} set the
 * {@link MovementCommand#MOVEMENT_RATE} property of the
 * {@link IEfferentCommand}. <br>
 * <br>
 * If StrictSynchronizationEnabled is false, preparation and processing times
 * may not match ACT-R's predictions. If it is enabled, processing will block
 * until a response is received from CR. However, run times may still not be
 * consistent since the sensor may require more time than predicted by ACT-R.
 * 
 * @author harrison
 */
public abstract class AbstractMotorModule extends AbstractPerceptualModule
    implements IMotorModule
{
  /**
   * Logger definition
   */
  protected static final transient Log                            LOGGER                            = LogFactory
                                                                                                        .getLog(AbstractMotorModule.class);

  static public final String                                      ENABLE_PARALLEL_MUSCLES_PARAM     = "EnableMuscleLevelParallelism";

  static public final String                                      COMPOUND_MOTOR_COMMAND_CHUNK_TYPE = "compound-motor-command";

  private IChunkType                                              _movementChunkType;

  private IChunkType                                              _compoundCommandChunkType;

  private IChunkType                                              _abortChunkType;

  private IChunk                                                  _abortingChunk;

  private ICommandTranslator                                      _commandTranslator;

  private IMotorTimeEquation                                      _preparationTimeEquation;

  private IMotorTimeEquation                                      _processingTimeEquation;

  private boolean                                                 _enableMuscleParallelism          = false;

  private MuscleStateManager                                      _muscleManager;

  private MotorCommandManager                                     _motorCommandManager;

  private FutureManager<IMovement, IMovement>                     _futureManager                    = new FutureManager<IMovement, IMovement>();

  private ACTREventDispatcher<IMotorModule, IMotorModuleListener> _listener;

  public AbstractMotorModule(String name)
  {
    super(name);
    _listener = new ACTREventDispatcher<IMotorModule, IMotorModuleListener>();
    setCommandTranslator(new DefaultCommandTranslator());
    addListener(new IMotorModuleListener() {

      public void movementAborted(MotorModuleEvent event)
      {
        _futureManager.release(event.getMovement(), event.getMovement());
      }

      public void movementCompleted(MotorModuleEvent event)
      {
        _futureManager.release(event.getMovement(), event.getMovement());
      }

      public void movementPrepared(MotorModuleEvent event)
      {
        _futureManager.release(event.getMovement(), event.getMovement());
      }

      public void movementStarted(MotorModuleEvent event)
      {
        _futureManager.release(event.getMovement(), event.getMovement());
      }

      public void movementRejected(MotorModuleEvent event)
      {
        _futureManager.release(event.getMovement(), event.getMovement());
      }

      public void muscleAdded(MotorModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

      public void muscleRemoved(MotorModuleEvent event)
      {
        // TODO Auto-generated method stub

      }

    }, ExecutorServices.INLINE_EXECUTOR);
  }

  public void setCommandTranslator(ICommandTranslator translator)
  {
    _commandTranslator = translator;
  }

  public ICommandTranslator getCommandTranslator()
  {
    return _commandTranslator;
  }

  public void setProcessingTimeEquation(IMotorTimeEquation equation)
  {
    _processingTimeEquation = equation;
  }

  public IMotorTimeEquation getProcessingTimeEquation()
  {
    return _processingTimeEquation;
  }

  public void setPreparationTimeEquation(IMotorTimeEquation equation)
  {
    _preparationTimeEquation = equation;
  }

  public IMotorTimeEquation getPreparationTimeEquation()
  {
    return _preparationTimeEquation;
  }

  public IChunkType getMovementChunkType()
  {
    return _movementChunkType;
  }

  public IChunkType getAbortChunkType()
  {
    return _abortChunkType;
  }

  public IChunk getAbortingChunk()
  {
    return _abortingChunk;
  }

  public IChunkType getCompoundCommandChunkType()
  {
    return _compoundCommandChunkType;
  }

  @Override
  public void initialize()
  {
    super.initialize();
    _movementChunkType = getNamedChunkType(MOVEMENT_CHUNK_TYPE);
    _abortChunkType = getNamedChunkType(ABORT_CHUNK_TYPE);
    _abortingChunk = getNamedChunk(ABORTING_CHUNK);
    _compoundCommandChunkType = getNamedChunkType(COMPOUND_MOTOR_COMMAND_CHUNK_TYPE);
  }

  /**
   * @return
   */
  public boolean isMuscleParallelismEnabled()
  {
    return _enableMuscleParallelism;
  }

  public void setMuscleParallelismEnabled(boolean enable)
  {
    _enableMuscleParallelism = enable;
  }

  @Override
  protected void connectToCommonReality()
  {
    super.connectToCommonReality();

    _muscleManager = new MuscleStateManager(this);
    _motorCommandManager = new MotorCommandManager(this);

    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(getModel());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Attaching translator to common reality");

    _muscleManager.install(agent);
    _motorCommandManager.install(agent);
  }

  @Override
  protected void disconnectFromCommonReality()
  {
    super.disconnectFromCommonReality();

    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(getModel());

    _muscleManager.uninstall(agent);
    _motorCommandManager.uninstall(agent);
  }

  public boolean canPrepare(ChunkTypeRequest request)
  {
    boolean prepareable = _motorCommandManager.canPrepare(request);
    if (!isMuscleParallelismEnabled())
      prepareable &= !getBuffer().isPreparationBusy();
    return prepareable;
  }

  public Future<IMovement> prepare(ChunkTypeRequest pattern,
      double requestTime, boolean prepareOnly)
  {
    try
    {
      IMovement movement = _motorCommandManager.prepare(pattern, requestTime,
          prepareOnly);
      return _futureManager.acquireOrGet(movement);
    }
    catch (Exception e)
    {
      String message = String.format(
          "Could not prepare motor command %s because %s.", pattern, e
              .getMessage());

      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.MOTOR, message);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(message, e);

      FutureTask<IMovement> nullMovement = new FutureTask<IMovement>(
          new Callable<IMovement>() {

            public IMovement call() throws Exception
            {
              return null;
            }

          });
      nullMovement.run();
      return nullMovement;
    }
  }

  public boolean canExecute(ChunkTypeRequest request)
  {
    boolean executable = _motorCommandManager.canExecute(request);
    if (!isMuscleParallelismEnabled())
    {
      IMotorActivationBuffer buffer = getBuffer();
      executable &= !buffer.isExecutionBusy();
      executable &= !buffer.isProcessorBusy();
    }
    return executable;
  }

  public Future<IMovement> execute(IMovement movement, double requestTime)
  {
    try
    {
      _futureManager.release(movement, movement);
      movement = _motorCommandManager.execute(movement, requestTime);
      return _futureManager.acquireOrGet(movement);
    }
    catch (Exception e)
    {
      String message = String.format(
          "Could not execute motor command %s because %s.", movement, e
              .getMessage());

      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.MOTOR, message);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(message, e);

      FutureTask<IMovement> nullMovement = new FutureTask<IMovement>(
          new Callable<IMovement>() {

            public IMovement call() throws Exception
            {
              return null;
            }

          });
      nullMovement.run();
      return nullMovement;
    }
  }

  public boolean canAdjust(ChunkTypeRequest request)
  {
    boolean adjustable = _motorCommandManager.canAdjust(request);
    if (!isMuscleParallelismEnabled())
      adjustable &= getBuffer().isExecutionBusy();

    return adjustable;
  }

  public boolean canAdjust(IMovement movement)
  {
    boolean adjustable = _motorCommandManager.canAdjust(movement);
    if (!isMuscleParallelismEnabled())
      adjustable &= getBuffer().isExecutionBusy();

    return adjustable;
  }

  public Future<IMovement> adjust(IMovement movement, ChunkTypeRequest request,
      double requestTime)
  {
    try
    {
      _futureManager.release(movement, movement);
      movement = _motorCommandManager.adjust(movement, request, requestTime);
      return _futureManager.acquireOrGet(movement);
    }
    catch (Exception e)
    {
      String message = String.format(
          "Could not adjust motor command %s because %s.", movement, e
              .getMessage());

      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.MOTOR, message);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(message, e);

      FutureTask<IMovement> nullMovement = new FutureTask<IMovement>(
          new Callable<IMovement>() {

            public IMovement call() throws Exception
            {
              return null;
            }

          });
      nullMovement.run();
      return nullMovement;
    }
  }

  public boolean canAbort(ChunkTypeRequest request)
  {
    boolean abortable = _motorCommandManager.canAbort(request);
    if (!isMuscleParallelismEnabled())
    {
      IMotorActivationBuffer buffer = getBuffer();
      abortable &= buffer.isExecutionBusy() || buffer.isPreparationBusy()
          || buffer.isProcessorBusy();
    }
    return abortable;
  }

  public boolean canAbort(IMovement movement)
  {
    boolean abortable = _motorCommandManager.canAbort(movement);
    if (!isMuscleParallelismEnabled())
    {
      IMotorActivationBuffer buffer = getBuffer();
      abortable &= buffer.isExecutionBusy() || buffer.isPreparationBusy()
          || buffer.isProcessorBusy();
    }
    return abortable;
  }

  public Future<IMovement> abort(IMovement movement, double requestTime)
  {
    // _motorManager.abort(movement);
    try
    {
      _futureManager.release(movement, movement);

      movement = _motorCommandManager.abort(movement, requestTime);
      return _futureManager.acquireOrGet(movement);
    }
    catch (Exception e)
    {
      String message = String.format(
          "Could not abort motor command %s because %s.", movement, e
              .getMessage());

      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.MOTOR, message);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(message, e);

      FutureTask<IMovement> nullMovement = new FutureTask<IMovement>(
          new Callable<IMovement>() {

            public IMovement call() throws Exception
            {
              return null;
            }

          });
      nullMovement.run();
      return nullMovement;
    }
  }

  public void reset()
  {
    reset(true);
  }

  public void reset(boolean stopActiveMovements)
  {
    double now = ACTRRuntime.getRuntime().getClock(getModel()).getTime();

    final FutureTask<Object> clearIt = new FutureTask<Object>(new Runnable() {
      public void run()
      {
        _motorCommandManager.clear();
        getBuffer().clear();
      }
    }, null);

    ITimedEvent ite = new AbstractTimedEvent(now, now + 0.05) {
      @Override
      public void fire(double now)
      {
        super.fire(now);
        try
        {
          clearIt.get();
        }
        catch (Exception e)
        {
          /**
           * Error : Co
           */
          LOGGER.error("could not reset motor module! ", e);
        }
      }
    };

    getCommonRealityExecutor().execute(clearIt);
    getModel().getTimedEventQueue().enqueue(ite);
  }

  public void reset(final String muscleName)
  {
    double now = ACTRRuntime.getRuntime().getClock(getModel()).getTime();
    ITimedEvent ite = new AbstractTimedEvent(now, now + 0.05) {
      @Override
      public void fire(double now)
      {
        super.fire(now);
        MuscleState state = getMuscleManager().getMuscleState(muscleName);
        if (state == null && Logger.hasLoggers(getModel()))
          Logger.log(getModel(), Logger.Stream.MOTOR, String.format(
              "Could not find muscle %s to reset", muscleName));

        if (state != null)
        {
          getCommandManager().clear(state.getIdentifier());

          state.set(getFreeChunk(), IStatusBuffer.STATE_SLOT,
              IPerceptualBuffer.PREPARATION_SLOT,
              IPerceptualBuffer.PROCESSOR_SLOT,
              IPerceptualBuffer.EXECUTION_SLOT);

          if (Logger.hasLoggers(getModel()))
            Logger.log(getModel(), Logger.Stream.MOTOR, String.format(
                "Reset state for %s", muscleName));
        }
      }
    };
    getModel().getTimedEventQueue().enqueue(ite);
  }

  public IMovement getLastMovement(IIdentifier muscle)
  {
    // return _motorManager.getLastPreparedMovement(muscle);
    return _motorCommandManager.getPreparedMovement(muscle);
  }

  public MotorCommandManager getCommandManager()
  {
    return _motorCommandManager;
  }

  public MuscleStateManager getMuscleManager()
  {
    return _muscleManager;
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>(super.getSetableParameters());
    rtn.add(ENABLE_PARALLEL_MUSCLES_PARAM);
    /*
     * we also include all the class names of the delegates
     */
    for (ICommandTranslatorDelegate delegate : ((DefaultCommandTranslator) getCommandTranslator())
        .getDelegates())
      rtn.add(delegate.getClass().getName());

    return rtn;
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (ENABLE_PARALLEL_MUSCLES_PARAM.equalsIgnoreCase(key))
      setMuscleParallelismEnabled(Boolean.parseBoolean(value));
    else if (isClassName(key))
    {
      if (Boolean.parseBoolean(value))
        try
        {
          /*
           * try to add
           */
          ICommandTranslatorDelegate delegate = (ICommandTranslatorDelegate) getClass()
              .getClassLoader().loadClass(key).newInstance();
          ((DefaultCommandTranslator) getCommandTranslator()).add(delegate);
        }
        catch (Exception e)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("Could not install ICommandTranslatorDelegate : "
                + value + " : ", e);
        }
      else
        /*
         * try to remove
         */
        for (ICommandTranslatorDelegate delegate : ((DefaultCommandTranslator) getCommandTranslator())
            .getDelegates())
          if (delegate.getClass().getName().equals(key))
            ((DefaultCommandTranslator) getCommandTranslator())
                .remove(delegate);
    }
    else
      super.setParameter(key, value);
  }

  @Override
  public String getParameter(String key)
  {
    if (ENABLE_PARALLEL_MUSCLES_PARAM.equalsIgnoreCase(key))
      return "" + isMuscleParallelismEnabled();
    else if (isClassName(key))
    {
      for (ICommandTranslatorDelegate delegate : ((DefaultCommandTranslator) getCommandTranslator())
          .getDelegates())
        if (delegate.getClass().getName().equals(key)) return "true";
      return "false";
    }
    else
      return super.getParameter(key);
  }

  protected boolean isClassName(String key)
  {
    try
    {
      getClass().getClassLoader().loadClass(key);
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public void addListener(IMotorModuleListener listener, Executor executor)
  {
    _listener.addListener(listener, executor);
  }

  public boolean hasListeners()
  {
    return _listener.hasListeners();
  }

  public void removeListener(IMotorModuleListener listener)
  {
    _listener.removeListener(listener);
  }

  public void dispatch(MotorModuleEvent event)
  {
    if (_listener.hasListeners()) _listener.fire(event);
  }
}
