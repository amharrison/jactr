package org.jactr.modules.pm.motor.managers;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.motor.MotorUtilities;
import org.commonreality.modalities.motor.MovementCommand;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.DeltaTracker;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.common.efferent.EfferentCommandManager;
import org.jactr.modules.pm.motor.AbstractMotorModule;
import org.jactr.modules.pm.motor.buffer.IMotorActivationBuffer;
import org.jactr.modules.pm.motor.buffer.processor.MotorRequestDelegate;
import org.jactr.modules.pm.motor.command.IMovement;
import org.jactr.modules.pm.motor.command.IMovement.State;
import org.jactr.modules.pm.motor.event.MotorModuleEvent;

public class MotorCommandManager extends EfferentCommandManager<MovementCommand>
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER       = LogFactory
      .getLog(MotorCommandManager.class);

  static private final String               PREPARE_ONLY = ":prepare-only";

  static private final String               ADJUSTMENT   = MotorRequestDelegate.ADJUSTMENT;

  final private AbstractMotorModule         _module;

  /**
   * all the currently submitted movements, keyed on command id
   */
  final private Map<IIdentifier, IMovement> _commandMovementMap;

  /**
   * all the current and past submitted movements, keyed on muslce id
   */
  final private Map<IIdentifier, IMovement> _muscleMovementMap;

  final private Map<IIdentifier, IMovement> _preparedMovements;

  private IMovement                         _lastPreparedMovement;

  private IMovement                         _lastExecutedMovement;

  public MotorCommandManager(AbstractMotorModule module)
  {
    super(module.getCommonRealityExecutor());
    _module = module;
    _commandMovementMap = new HashMap<IIdentifier, IMovement>();
    _muscleMovementMap = new HashMap<IIdentifier, IMovement>();
    _preparedMovements = new HashMap<IIdentifier, IMovement>();

    // delete on complete, abort or reject, but only after the callback
    setAutoDeleteEnabled(true);
  }

  public IMovement getPreparedMovement(IIdentifier muscleId)
  {
    try
    {
      getLock().readLock().lock();
      if (muscleId == null) return _lastPreparedMovement;
      return _preparedMovements.get(muscleId);
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  /**
   * clear an individual muscle
   * 
   * @param muscleId
   */
  public void clear(IIdentifier muscleId)
  {
    try
    {
      getLock().writeLock().lock();

      IMovement movement = getMovementFromMuscle(muscleId);
      if (movement != null)
      {
        if (movement.getState() != State.COMPLETED
            && movement.getState() != State.FAILED
            && movement.getState() != State.ABORTING)
          abort(movement, 0);

        if (movement.getCommandIdentifier() != null)
        {
          MovementCommand command = getCommand(movement.getCommandIdentifier());
          if (command != null)
          {
            commandAborted(command, true);
            remove(command);
          }
        }
      }

      _preparedMovements.remove(muscleId);

    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  public void clear(boolean stopActiveMovements)
  {

  }

  @Override
  public void clear()
  {
    try
    {
      getLock().writeLock().lock();

      /*
       * after the clear, we should still recevie the callback notifications
       * which should allow us to handle all these folks.. but we still need to
       * clear out prep and last collections
       */
      _preparedMovements.clear();
      double now = ACTRRuntime.getRuntime().getClock(_module.getModel())
          .getTime();
      List<IMovement> movements = FastListFactory.newInstance();
      movements.addAll(_commandMovementMap.values());
      for (IMovement movement : movements)
        try
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Requesting abort of %s", movement));
          abort(movement, now);
        }
        catch (Exception e)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Failed to clear " + movement + ", forcing ", e);

          MovementCommand command = getCommand(movement.getCommandIdentifier());
          if (command != null)
          {
            commandAborted(command, true);
            remove(command);
          }
        }

      FastListFactory.recycle(movements);
    }
    finally
    {
      getLock().writeLock().unlock();
    }

    super.clear();
    updateBuffer();
  }

  public IMovement getMovementFromCommand(IIdentifier commandId)
  {
    return _commandMovementMap.get(commandId);
  }

  public IMovement getMovementFromMuscle(IIdentifier muscleId)
  {
    if (muscleId == null) return _lastExecutedMovement;
    return _muscleMovementMap.get(muscleId);
  }

  @Override
  protected boolean isInterestedIn(IEfferentCommand command)
  {
    return command instanceof MovementCommand && super.isInterestedIn(command);
  }

  private boolean isPrepareOnly(IMovement movement)
  {
    List<ISlot> container = FastListFactory.newInstance();
    movement.getChunkTypeRequest().getSlots(container);
    try
    {
      for (ISlot slot : container)
        if (slot.getName().equals(PREPARE_ONLY)) return true;
      return false;
    }
    finally
    {
      FastListFactory.recycle(container);
    }
  }

  private boolean isAdjustment(IMovement movement)
  {
    List<ISlot> container = FastListFactory.newInstance();
    movement.getChunkTypeRequest().getSlots(container);
    try
    {
      for (ISlot slot : container)
        if (slot.getName().equals(ADJUSTMENT)) return true;
      return false;
    }
    finally
    {
      FastListFactory.recycle(container);
    }
  }

  @Override
  protected void commandRemoved(MovementCommand command)
  {
    super.commandRemoved(command);

    IMovement movement = _commandMovementMap.get(command.getIdentifier());
    if (movement != null) try
    {
      IIdentifier cId = movement.getCommandIdentifier();

      getLock().writeLock().lock();
      // _muscleMovementMap.remove(movement.getMuscleIdentifier());
      _commandMovementMap.remove(cId);
      if (LOGGER.isDebugEnabled()) LOGGER
          .debug("Removed " + cId + ", leaving " + _commandMovementMap.size());
    }
    finally
    {
      getLock().writeLock().unlock();
    }

    updateBuffer();
  }

  /**
   * @param object
   * @param parameters
   *          {@link IMovement} movement command muscle
   * @return
   * @see org.jactr.modules.pm.common.efferent.EfferentCommandManager#createCommand(org.commonreality.object.IEfferentObject,
   *      java.lang.Object[])
   */
  @Override
  protected MovementCommand createCommand(IEfferentObject object,
      Object... parameters)
  {
    IModel model = _module.getModel();
    Movement movement = (Movement) parameters[0];
    IIdentifier muscleIdentifier = movement.getMuscleIdentifier();
    ChunkTypeRequest request = movement.getChunkTypeRequest();
    IEfferentObject muscle = getAgent().getEfferentObjectManager()
        .get(muscleIdentifier);

    MovementCommand command = (MovementCommand) _module.getCommandTranslator()
        .translate(request, muscle, model);

    movement.configure(
        _module.getMuscleManager().getMuscleState(muscleIdentifier),
        command.getIdentifier());

    try
    {
      getLock().writeLock().lock();
      _commandMovementMap.put(command.getIdentifier(), movement);
      _muscleMovementMap.put(muscle.getIdentifier(), movement);
    }
    finally
    {
      getLock().writeLock().unlock();
    }
    return command;
  }

  public boolean canAdjust(ChunkTypeRequest request)
  {
    try
    {
      testStatesForBusy(request,
          new String[] { IPerceptualBuffer.EXECUTION_SLOT });
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public boolean canAdjust(IMovement movement)
  {
    try
    {
      if (movement.getState() != IMovement.State.EXECUTING
          && movement.getState() != IMovement.State.PROCESSING)
        throw new IllegalStateException(String.format(
            "Movement %s is %s, cannot adjust", movement, movement.getState()));

      /*
       * we use a special test, something must be busy..
       */
      IEfferentObject muscle = getMuscle(movement.getMuscleIdentifier());
      testStatesForBusy(muscle, MotorUtilities.getName(muscle),
          new String[] { IPerceptualBuffer.EXECUTION_SLOT });

      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  /**
   * nudge an executing movement. There are no responses to these commands
   * currently..
   * 
   * @param request
   * @param requestTime
   * @return
   */
  public IMovement adjust(IMovement movement, ChunkTypeRequest request,
      double requestTime)
  {
    if (movement.getState() != IMovement.State.EXECUTING
        && movement.getState() != IMovement.State.PROCESSING)
      throw new IllegalStateException(String.format(
          "Movement %s is %s, cannot adjust", movement, movement.getState()));

    /*
     * we use a special test, something must be busy..
     */
    IEfferentObject muscle = getMuscle(movement.getMuscleIdentifier());
    testStatesForBusy(muscle, MotorUtilities.getName(muscle),
        new String[] { IPerceptualBuffer.EXECUTION_SLOT });

    IIdentifier commandIdentifier = movement.getCommandIdentifier();
    MovementCommand actualCommand = getCommand(commandIdentifier);

    if (!actualCommand.isAdjustable()) throw new IllegalArgumentException(
        String.format("%s is not adjustable", actualCommand));

    Movement actualMovement = (Movement) movement;

    DeltaTracker<MovementCommand> tracker = new DeltaTracker<MovementCommand>(
        actualCommand);

    IModel model = _module.getModel();

    /**
     * adjust the command..
     */
    try
    {
      _module.getCommandTranslator().adjust(request, tracker, actualCommand,
          _module.getModel());

      /*
       * send out the changes..
       */
      if (tracker.hasChanged())
      {
        send(tracker);

        if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR,
            String.format("Adjusting %s with %s", actualMovement, request));
      }
    }
    catch (OperationNotSupportedException onse)
    {
      throw new IllegalStateException(
          String.format("Cannot adjust %s", actualCommand));
    }

    return movement;
  }

  public boolean canPrepare(ChunkTypeRequest request)
  {
    try
    {
      testStatesForNotBusyOrAborting(request,
          IPerceptualBuffer.PREPARATION_SLOT);
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  /**
   * @param request
   * @param prepareOnly
   * @return
   */
  public IMovement prepare(ChunkTypeRequest request, double requestTime,
      boolean prepareOnly)
      throws IllegalArgumentException, IllegalStateException
  {
    /*
     * can we prepare?
     */
    testStatesForNotBusyOrAborting(request, IPerceptualBuffer.PREPARATION_SLOT);

    IModel model = _module.getModel();
    IEfferentObject muscle = _module.getCommandTranslator().getMuscle(request,
        model);

    if (muscle == null) throw new IllegalStateException(
        String.format("Coud not find appropriate muscle for %s", request));

    IIdentifier mId = muscle.getIdentifier();
    MovementCommand actualCommand = null;

    if (prepareOnly) request.addSlot(new BasicSlot(PREPARE_ONLY, true));
    Movement actualMovement = new Movement(request, mId, _module);

    actualMovement.setTimingInfo(new double[] { requestTime, _module
        .getPreparationTimeEquation().compute(actualMovement, _module) });

    if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR,
        String.format("Preparing %s.", actualMovement));

    try
    {
      getLock().writeLock().lock();

      _preparedMovements.remove(mId);

      /*
       * actually create and send the command, this is a temporary copy for
       * now..
       */
      actualCommand = newCommandInternal(muscle, actualMovement);
      actualCommand.getIdentifier();

      actualMovement.setState(IMovement.State.PREPARING, true);
    }
    catch (Exception e)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("Failed to prepare %s", request), e);
      actualMovement.setTimingInfo(new double[0]);
      actualMovement.setState(IMovement.State.FAILED, true);
      if (actualCommand != null) remove(actualCommand);
    }
    finally
    {
      getLock().writeLock().unlock();
      updateBuffer();
    }

    return actualMovement;
  }

  @Override
  protected void commandAccepted(final MovementCommand command)
  {
    IIdentifier cId = command.getIdentifier();
    final Movement movement = (Movement) getMovementFromCommand(cId);
    if (movement != null) try
    {
      if (LOGGER.isDebugEnabled()) LOGGER
          .debug(String.format("%s was accepted as %s.", movement, command));
      /*
       * pull up the timing info so we can reset the states correctly..
       */
      Runnable resetState = new Runnable() {
        public void run()
        {
          modelMovementPrepared(movement, command);
        }
      };

      getLock().writeLock().lock();

      double[] timingInfo = movement.getTimingInfo();
      movement.setTimingInfo(null);

      _preparedMovements.put(movement.getMuscleIdentifier(), movement);
      _lastPreparedMovement = movement;

      _module.getModel().getTimedEventQueue().enqueue(
          new RunnableTimedEvent(timingInfo[0] + timingInfo[1], resetState));
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  /**
   * called on model thread, this handles the states. removes the pending.
   * 
   * @param movement
   * @param command
   */
  protected void modelMovementPrepared(Movement movement,
      final MovementCommand command)
  {
    /*
     * clean up the pending states..
     */
    IIdentifier cId = command.getIdentifier();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s prepared.", movement));
    boolean shouldExecute = !isPrepareOnly(movement);
    IModel model = _module.getModel();

    /*
     * was an abort requested?
     */
    if (movement.getState() == IMovement.State.ABORTING)
    {

      if (LOGGER.isDebugEnabled()) LOGGER.debug(
          String.format("%s was aborted, ignoring prep completion", movement));

      getIndividualManager(cId);

      // if (im != null && !im.hasAborted())
      _module.getCommonRealityExecutor().execute(new Runnable() {
        public void run()
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Forcing abort of nonrunning command " + command);
          commandAborted(command, false);
          remove(command);

          updateBuffer();
        }
      });

      return;
    }

    movement.setState(IMovement.State.PREPARED, true);

    if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR,
        String.format("Prepared %s.", movement));

    updateBuffer();

    if (_module.hasListeners()) _module.dispatch(new MotorModuleEvent(_module,
        movement, MotorModuleEvent.Type.PREPARED));

    if (shouldExecute)
      execute(movement, ACTRRuntime.getRuntime().getClock(model).getTime());
  }

  @Override
  protected void commandRejected(final MovementCommand command)
  {
    IIdentifier cId = command.getIdentifier();
    final Movement movement = (Movement) getMovementFromCommand(cId);
    if (movement != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("%s (%s) has rejected.", movement, command));

      Runnable reset = new Runnable() {

        public void run()
        {
          modelMovementRejected(movement, command);
        }

      };

      // run asap
      _module.getModel().getTimedEventQueue()
          .enqueue(new RunnableTimedEvent(
              ACTRRuntime.getRuntime().getClock(_module.getModel()).getTime(),
              reset, reset));

      remove(command);
    }
  }

  protected void modelMovementRejected(Movement movement,
      MovementCommand command)
  {
    Movement actualMovement = movement;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s rejected.", movement));

    try
    {
      actualMovement.setState(IMovement.State.FAILED, true);
      actualMovement.getMuscleState().set(_module.getFreeChunk(),
          IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);
      IModel model = _module.getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.MOTOR, String
            .format("Rejected %s because %s.", movement, command.getResult()));
    }
    finally
    {
      updateBuffer();

      if (_module.hasListeners()) _module.dispatch(new MotorModuleEvent(_module,
          movement, MotorModuleEvent.Type.REJECTED));
    }
  }

  public boolean canExecute(ChunkTypeRequest request)
  {
    try
    {
      testStatesForNotBusyOrAborting(request, IPerceptualBuffer.PROCESSOR_SLOT,
          IPerceptualBuffer.EXECUTION_SLOT);
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public IMovement execute(IMovement movement, double requestTime)
      throws IllegalArgumentException, IllegalStateException
  {
    /*
     * can we execute?
     */
    testStatesForNotBusyOrAborting(getMuscle(movement.getMuscleIdentifier()),
        movement.getChunkTypeRequest().toString(),
        IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT);

    IIdentifier commandIdentifier = movement.getCommandIdentifier();
    MovementCommand actualCommand = getCommand(commandIdentifier);
    Movement actualMovement = (Movement) movement;

    DeltaTracker<MovementCommand> tracker = new DeltaTracker<MovementCommand>(
        actualCommand);
    tracker.setProperty(IEfferentCommand.REQUESTED_START_TIME, requestTime);

    try
    {
      getLock().writeLock().lock();

      if (movement.getState() == IMovement.State.ABORTING)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(commandIdentifier + " has been aborted. skipping");

        return movement;
      }

      /*
      * 
      */
      actualMovement.setTimingInfo(new double[] { requestTime, _module
          .getProcessingTimeEquation().compute(actualMovement, _module) });
      actualMovement.setState(IMovement.State.PROCESSING, true);

      execute(tracker);
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Failed to execute %s", movement), e);

      actualMovement.setState(IMovement.State.FAILED, true);
      if (actualCommand != null) remove(actualCommand);
    }
    finally
    {
      getLock().writeLock().unlock();
      updateBuffer();
    }

    return movement;
  }

  @Override
  protected void commandRunning(final MovementCommand command)
  {
    IIdentifier cId = command.getIdentifier();
    final Movement movement = (Movement) getMovementFromCommand(cId);
    if (movement != null) try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("%s (%s) has started.", movement, command));

      Runnable resetState = new Runnable() {
        public void run()
        {
          modelMovementProcessed(movement, command);
        }
      };

      getLock().writeLock().lock();
      double[] timing = movement.getTimingInfo();
      _lastExecutedMovement = movement;

      _module.getModel().getTimedEventQueue()
          .enqueue(new RunnableTimedEvent(timing[0] + timing[1], resetState));
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  protected void modelMovementProcessed(Movement movement,
      MovementCommand command)
  {
    command.getIdentifier();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s processed.", movement));

    IMovement.State mState = movement.getState();
    boolean fireEvent = false;

    try
    {
      if (mState != IMovement.State.PROCESSING)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug(
            String.format("%s state is not processing (%s)", movement, mState));

        if (mState == IMovement.State.COMPLETED)
        {
          if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format(
              "%s completed before processing was done. No need to do anything",
              movement));
        }
        else if (mState == IMovement.State.FAILED)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s failed", movement));
        }
        else if (mState == IMovement.State.ABORTING)
        {
          if (LOGGER.isDebugEnabled()) LOGGER.debug(String
              .format("%s was aborted, ignoring proc completion", movement));

          if (command
              .getRequestedState() != IEfferentCommand.RequestedState.ABORT)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Actual abort was not requested. doing so now.");

            /*
             * the abort request was made before CR acknowledged the start of
             * running, so it was never fully requested..
             */
            DeltaTracker<MovementCommand> tracker = new DeltaTracker<MovementCommand>(
                command);
            abort(tracker);
          }
          else if (LOGGER.isDebugEnabled())
            LOGGER.debug("Actual abort was requested.");
        }
      }
      else
      {
        movement.setState(IMovement.State.EXECUTING, true);

        fireEvent = true;

        IModel model = _module.getModel();
        if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR,
            String.format("Running %s.", movement));
      }
    }
    finally
    {
      updateBuffer();

      if (fireEvent && _module.hasListeners())
        _module.dispatch(new MotorModuleEvent(_module, movement,
            MotorModuleEvent.Type.STARTED));
    }
  }

  @Override
  protected void commandCompleted(final MovementCommand command)
  {
    IIdentifier cId = command.getIdentifier();
    final Movement movement = (Movement) getMovementFromCommand(cId);
    if (movement != null) try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Completed %s (%s).", movement, command));

      Runnable reset = new Runnable() {
        public void run()
        {
          modelMovementCompleted(movement, command);
        }
      };

      // run asap
      _module.getModel().getTimedEventQueue()
          .enqueue(new RunnableTimedEvent(
              ACTRRuntime.getRuntime().getClock(_module.getModel()).getTime(),
              reset));

      getLock().writeLock().lock();
      // _muscleMovementMap.remove(movement.getMuscleIdentifier());
      _commandMovementMap.remove(cId);
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  protected void modelMovementCompleted(Movement movement,
      MovementCommand command)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s completed.", movement));

    if (movement.getState() == IMovement.State.ABORTING)
      if (LOGGER.isDebugEnabled()) LOGGER.debug(
          "Movement had been aborted, but completed in time. Ignoring abort");

    try
    {
      movement.setState(IMovement.State.COMPLETED, true);

      IModel model = _module.getModel();

      if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.MOTOR,
          String.format("Completed %s.", movement));
    }
    finally
    {
      updateBuffer();

      if (_module.hasListeners()) _module.dispatch(new MotorModuleEvent(_module,
          movement, MotorModuleEvent.Type.COMPLETED));
    }
  }

  public boolean canAbort(ChunkTypeRequest request)
  {
    try
    {
      testStatesForBusy(request,
          new String[] { IPerceptualBuffer.PREPARATION_SLOT,
              IPerceptualBuffer.PROCESSOR_SLOT,
              IPerceptualBuffer.EXECUTION_SLOT });

      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public boolean canAbort(IMovement movement)
  {
    /*
     * we use a special test, something must be busy..
     */
    try
    {
      IEfferentObject muscle = getMuscle(movement.getMuscleIdentifier());
      testStatesForBusy(muscle, MotorUtilities.getName(muscle),
          new String[] { IPerceptualBuffer.PREPARATION_SLOT,
              IPerceptualBuffer.PROCESSOR_SLOT,
              IPerceptualBuffer.EXECUTION_SLOT });
      return true;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public IMovement abort(IMovement movement, double requestTime)
      throws IllegalArgumentException, IllegalStateException
  {
    Movement actualMovement = (Movement) movement;

    if (actualMovement.getState() == IMovement.State.ABORTING)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Already aborting " + movement);
      return movement;
    }

    /*
     * we use a special test, something must be busy..
     */
    IEfferentObject muscle = getMuscle(movement.getMuscleIdentifier());
    testStatesForBusy(muscle, MotorUtilities.getName(muscle),
        new String[] { IPerceptualBuffer.PREPARATION_SLOT,
            IPerceptualBuffer.PROCESSOR_SLOT,
            IPerceptualBuffer.EXECUTION_SLOT });

    /*
     * now we do our thing. set as aborting, and issue the request
     */

    IIdentifier commandIdentifier = movement.getCommandIdentifier();
    MovementCommand actualCommand = getCommand(commandIdentifier);

    try
    {
      getLock().writeLock().lock();

      /*
       * CR only accepts aborts if we are actually
       * running..(processing/executing) otherwise, we have to manage it
       * ourselves..
       */
      if (actualMovement.getState() == IMovement.State.PROCESSING
          || actualMovement.getState() == IMovement.State.EXECUTING)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug(
            String.format("%s is actually running, requesting abort of %s",
                actualMovement, actualCommand));

        if (actualCommand == null)
        {
          if (LOGGER.isWarnEnabled()) LOGGER.warn(String.format(
              "Somehow the movement is processing/executing and yet there is no actual command available for %s. Handling internally.",
              commandIdentifier));

        }
        else
        {
          if (LOGGER.isDebugEnabled()) LOGGER
              .debug(String.format("requesting abort of %s", actualCommand));

          DeltaTracker<MovementCommand> tracker = new DeltaTracker<MovementCommand>(
              actualCommand);
          abort(tracker);
        }
      }
      else /*
            * it is possible that abort was called immediately after prepare,
            * but before the service could actually accept the command. In this
            * case, modelMovementPrepared() will take care of the clean up
            */
      if (LOGGER.isDebugEnabled()) LOGGER.debug(
          String.format("%s is not actually running, handling abort internally",
              actualMovement));

      actualMovement.setState(IMovement.State.ABORTING, true);
    }
    catch (Exception e)
    {
      LOGGER.error(String.format("Failed to issue abort on %s", actualMovement),
          e);
      actualMovement.setState(IMovement.State.FAILED, true);
    }
    finally
    {
      getLock().writeLock().unlock();
      updateBuffer();
    }

    return actualMovement;
  }

  @Override
  protected void commandAborted(final MovementCommand command,
      final boolean wasRequested)
  {
    IIdentifier cId = command.getIdentifier();
    final Movement movement = (Movement) getMovementFromCommand(cId);
    if (movement != null) try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("%s (%s) was aborted because %s.", movement,
            command, wasRequested ? "model requested" : command.getResult()));

      Runnable reset = new Runnable() {
        public void run()
        {
          modelMovementAborted(movement, command, wasRequested);
        }
      };

      /*
       * run immediately unlike the rest of the events. this allows it to
       * supercede running and completed, which will both check to see if it has
       * been aborted first
       */
      _module.getModel().getTimedEventQueue()
          .enqueue(new RunnableTimedEvent(
              ACTRRuntime.getRuntime().getClock(_module.getModel()).getTime(),
              reset));

      getLock().writeLock().lock();
      // _muscleMovementMap.remove(movement.getMuscleIdentifier());
      _commandMovementMap.remove(cId);
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  protected void modelMovementAborted(Movement movement,
      MovementCommand command, boolean wasRequested)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s aborted.", movement));

    IModel model = _module.getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.MOTOR, String.format("Aborted %s %s.",
          movement,
          wasRequested ? "at user request" : "because " + command.getResult()));

    try
    {
      IMovement.State mState = movement.getState();
      if (wasRequested)
      {
        movement.setState(IMovement.State.COMPLETED, true);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("was requested, so clearing states");
      }
      else
      {
        movement.setState(IMovement.State.FAILED, true);
        if (mState == IMovement.State.PREPARING)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Was preparing, so freeing proc/exec");
          movement.getMuscleState().set(_module.getFreeChunk(),
              IPerceptualBuffer.PROCESSOR_SLOT,
              IPerceptualBuffer.EXECUTION_SLOT);
        }
        else if (mState == IMovement.State.PROCESSING
            || mState == IMovement.State.EXECUTING)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Was processing, so freeing prep");
          movement.getMuscleState().set(_module.getFreeChunk(),
              IPerceptualBuffer.PREPARATION_SLOT);
        }
        else if (LOGGER.isWarnEnabled())
          LOGGER.warn("Unknown state during abort : " + movement.getState());
      }
    }
    finally
    {
      updateBuffer();

      if (_module.hasListeners()) _module.dispatch(new MotorModuleEvent(_module,
          movement, MotorModuleEvent.Type.ABORTED));
    }

  }

  /**
   * update the state of the buffer based on the internal states..
   */
  private void updateBuffer()
  {
    IChunk free = _module.getFreeChunk();
    IChunk error = _module.getErrorChunk();
    IChunk aborting = _module.getAbortingChunk();
    IChunk busy = _module.getBusyChunk();

    Map<String, IChunk> status = new TreeMap<String, IChunk>();

    IChunk globalState = free;
    try
    {
      getLock().readLock().lock();
      /*
       * aborting supercedes error which supercedes busy, which supercedes free
       */
      String[] slotNames = new String[] { IPerceptualBuffer.PREPARATION_SLOT,
          IPerceptualBuffer.PROCESSOR_SLOT, IPerceptualBuffer.EXECUTION_SLOT };

      for (String slotName : slotNames)
        status.put(slotName, free);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("determining global buffer state from "
            + _muscleMovementMap.size() + " muscles");

      for (IMovement movement : _muscleMovementMap.values())
      {
        Movement actualMovement = (Movement) movement;
        MuscleState mState = actualMovement.getMuscleState();
        for (String slotName : slotNames)
        {
          IChunk value = (IChunk) mState.getSlot(slotName).getValue();
          IChunk prior = status.get(slotName);
          if (aborting.equals(value))
          {
            status.put(slotName, aborting);
            globalState = aborting;
          }
          else if (!aborting.equals(prior)) if (error.equals(value))
          {
            status.put(slotName, error);
            if (!aborting.equals(globalState)) globalState = error;
          }
          else if (!error.equals(prior)) if (busy.equals(value))
          {
            status.put(slotName, busy);
            if (!aborting.equals(globalState) && !error.equals(globalState))
              globalState = busy;
          }
          else
            status.put(slotName, free);
        }
      }
    }
    finally
    {
      getLock().readLock().unlock();
    }

    IMotorActivationBuffer buffer = _module.getBuffer();
    for (Map.Entry<String, IChunk> slot : status.entrySet())
      ((IMutableSlot) buffer.getSlot(slot.getKey())).setValue(slot.getValue());

    buffer.setStateChunk(globalState);
    buffer.setModalityChunk(globalState);

    if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format("%s %s=%s %s",
        buffer, IStatusBuffer.STATE_SLOT, globalState, status));
  }

  private void testStatesForNotBusyOrAborting(IEfferentObject muscle,
      String details, String... slotNamesToTest)
  {
    IChunk busyChunk = _module.getBusyChunk();
    IChunk abortingChunk = _module.getAbortingChunk();
    IUniqueSlotContainer container = _module.getBuffer();

    if (_module.isMuscleParallelismEnabled()) container = _module
        .getMuscleManager().getMuscleState(muscle.getIdentifier());

    for (String slotNameToTest : slotNamesToTest)
    {
      Object slotValue = container.getSlot(slotNameToTest).getValue();
      if (busyChunk.equals(slotValue)) throw new IllegalStateException(
          String.format("%s.%s is currently busy, cannot execute %s", container,
              slotNameToTest, details));

      if (abortingChunk.equals(slotValue)) throw new IllegalStateException(
          String.format("%s.%s is currently aborting, cannot execute %s",
              container, slotNameToTest, details));
    }
  }

  private void testStatesForNotBusyOrAborting(IRequest request,
      String... slotNamesToTest)
  {
    if (!(request instanceof ChunkTypeRequest))
      throw new IllegalArgumentException("request must be chunktype request");

    ChunkTypeRequest ctRequest = (ChunkTypeRequest) request;

    /*
     * what muscle are we talking about?
     */
    IModel model = _module.getModel();
    IEfferentObject muscle = _module.getCommandTranslator().getMuscle(ctRequest,
        model);

    if (muscle == null) throw new IllegalArgumentException(
        "Could not get muscle for request " + ctRequest);

    testStatesForNotBusyOrAborting(muscle, request.toString(), slotNamesToTest);
  }

  private void testStatesForBusy(IRequest request, String... slotNamesToTest)
  {
    if (!(request instanceof ChunkTypeRequest))
      throw new IllegalArgumentException("request must be chunktype request");

    ChunkTypeRequest ctRequest = (ChunkTypeRequest) request;

    /*
     * what muscle are we talking about?
     */
    IModel model = _module.getModel();
    IEfferentObject muscle = _module.getCommandTranslator().getMuscle(ctRequest,
        model);

    if (muscle == null) throw new IllegalArgumentException(
        "Could not get muscle for request " + ctRequest);

    testStatesForBusy(muscle, request.toString(), slotNamesToTest);
  }

  private void testStatesForBusy(IEfferentObject muscle, String details,
      String... slotNamesToTest)
  {
    IChunk busyChunk = _module.getBusyChunk();
    IUniqueSlotContainer container = _module.getBuffer();
    boolean isAnyBusy = false;

    if (_module.isMuscleParallelismEnabled()) container = _module
        .getMuscleManager().getMuscleState(muscle.getIdentifier());

    for (String slotNameToTest : slotNamesToTest)
      if (busyChunk.equals(container.getSlot(slotNameToTest).getValue()))
      {
        isAnyBusy = true;
        break;
      }

    if (!isAnyBusy) throw new IllegalStateException("No movements busy");
  }

}
