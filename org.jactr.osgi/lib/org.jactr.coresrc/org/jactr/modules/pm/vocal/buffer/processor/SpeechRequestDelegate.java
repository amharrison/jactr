package org.jactr.modules.pm.vocal.buffer.processor;

/*
 * default logging
 */
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand.ActualState;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.common.efferent.AbstractEfferentTimedEvent;
import org.jactr.modules.pm.vocal.IVocalModule;

public class SpeechRequestDelegate extends AbstractRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SpeechRequestDelegate.class);

  final private IVocalModule         _vocal;

  public SpeechRequestDelegate(IVocalModule module, IChunkType chunkType)
  {
    super(chunkType);
    _vocal = module;
    // we dont use blocking timed events, rather we drift
    setUseBlockingTimedEvents(false);
    setAsynchronous(true);
  }

  protected String getText(IRequest request)
  {
    for (IConditionalSlot cSlot : ((ChunkTypeRequest) request)
        .getConditionalSlots())
      if (cSlot.getName().equals(IVocalModule.STRING_SLOT)
          && cSlot.getCondition() == IConditionalSlot.EQUALS
          && cSlot.getValue() instanceof String) return (String) cSlot.getValue();

    return "";
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    // need to check prep..

    if (((IPerceptualBuffer) buffer).isPreparationBusy())
    {
      String msg = "Already preparing vocalization. Ignoring request";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(buffer.getModel()))
        Logger.log(buffer.getModel(), Logger.Stream.VOCAL, msg);
      return false;
    }

    return true;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    /*
     * flag as busy
     */
    IChunk busy = _vocal.getModel().getDeclarativeModule().getBusyChunk();
    ((IPerceptualBuffer) buffer).setPreparationChunk(busy);
    ((IPerceptualBuffer) buffer).setStateChunk(busy);

    double duration = _vocal.getExecutionTimeEquation().compute(
        getText(request), _vocal);

    return _vocal.prepare(request, duration);
  }

  @Override
  protected double computeCompletionTime(double startTime, IRequest request,
      IActivationBuffer buffer)
  {
    return startTime
        + _vocal.getPreparationTimeEquation().compute(getText(request), _vocal);
  }

  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    final IModel model = buffer.getModel();
    VocalizationCommand command = null;
    IChunk error = model.getDeclarativeModule().getErrorChunk();
    IChunk free = model.getDeclarativeModule().getFreeChunk();
    IChunk busy = model.getDeclarativeModule().getBusyChunk();
    IPerceptualBuffer pBuffer = (IPerceptualBuffer) buffer;
    String rejection = null;
    try
    {
      command = ((Future<VocalizationCommand>) startValue).get();
    }
    catch (InterruptedException e)
    {
      // bail
      return;
    }
    catch (ExecutionException e)
    {
      rejection = e.getCause().getMessage();
      LOGGER.error("Failed to get vocalization future ", e);
    }

    if (command != null && command.getActualState() == ActualState.REJECTED)
      rejection = "Vocalization was rejected : " + command.getResult();

    if (rejection != null)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug(rejection);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, rejection);

      pBuffer.setPreparationChunk(error);
      pBuffer.setStateChunk(error);
      return;
    }

    String msg = "Vocalization prepared.";

    if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.VOCAL, msg);

    pBuffer.setPreparationChunk(free);

    /*
     * now to actually execute, we use another drifting timed event.. two
     * actually..
     */
    Future<VocalizationCommand> executeFuture = _vocal.execute(command);

    double start = ACTRRuntime.getRuntime().getClock(model).getTime();
    double procEnd = model.getProceduralModule()
        .getDefaultProductionFiringTime()
        + start;
    double duration = _vocal.getExecutionTimeEquation().compute(
        getText(request), _vocal);
    double execEnd = duration + start;

    ProcessTimedEvent procEvent = new ProcessTimedEvent(start, procEnd,
        executeFuture, buffer);

    ExecuteTimedEvent execEvent = new ExecuteTimedEvent(start, execEnd,
        executeFuture, buffer);

    /*
     * set some flags
     */
    pBuffer.setProcessorChunk(busy);
    pBuffer.setExecutionChunk(busy);

    model.getTimedEventQueue().enqueue(procEvent);
    model.getTimedEventQueue().enqueue(execEvent);
  }

  /**
   * @param start
   * @param finish
   * @param request
   * @param buffer
   * @param startValue
   * @return
   * @see org.jactr.core.buffer.delegate.AsynchronousRequestDelegate#createFinishTimedEvent(double,
   *      double, org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer, java.lang.Object)
   */
  @Override
  @SuppressWarnings("unchecked")
  protected ITimedEvent createFinishTimedEvent(double start, double finish,
      final IRequest request, final IActivationBuffer buffer, Object startValue)
  {
    return new PrepareTimedEvent(start, finish, request, (Future<VocalizationCommand>)startValue, buffer);
  }
  
  
  /**
   * preparation phase timed event
   * @author harrison
   *
   */
  private class PrepareTimedEvent extends AbstractEfferentTimedEvent<VocalizationCommand>{

    private final IRequest _request;
    
    public PrepareTimedEvent(double start, double end, IRequest request,
        Future<VocalizationCommand> commandFuture, IActivationBuffer buffer)
    {
      super(start, end, commandFuture, buffer);
      _request = request;
      setDriftStates(ActualState.UNKNOWN);
    }

    @Override
    protected AbstractEfferentTimedEvent<VocalizationCommand> clone()
    {
      return new PrepareTimedEvent(getStartTime(), getEndTime(), _request, getFuture(), getBuffer());
    }
    
    @Override
    protected void fired(double currentTime,
        Future<VocalizationCommand> commandFuture)
    {
      finishRequest(_request, getBuffer(), commandFuture);
    }

    @Override
    protected void aborted(Future<VocalizationCommand> commandFuture)
    {
      abortRequest(_request, getBuffer(), commandFuture);
    }
    
  }

  /**
   * timed event to handle the process phase
   * 
   * @author harrison
   */
  private class ProcessTimedEvent extends
      AbstractEfferentTimedEvent<VocalizationCommand>
  {

    public ProcessTimedEvent(double start, double end,
        Future<VocalizationCommand> commandFuture, IActivationBuffer buffer)
    {
      super(start, end, commandFuture, buffer);
      setDriftStates(ActualState.ACCEPTED);
    }

    @Override
    protected AbstractEfferentTimedEvent<VocalizationCommand> clone()
    {
      return new ProcessTimedEvent(getStartTime(), getEndTime(), getFuture(),
          getBuffer());
    }

    @Override
    protected void fired(double currentTime,
        Future<VocalizationCommand> commandFuture)
    {
      IPerceptualBuffer pBuffer = (IPerceptualBuffer) getBuffer();
      IModel model = pBuffer.getModel();
      try
      {
        VocalizationCommand command = commandFuture.get();
        ActualState state = command.getActualState();
        if (state != ActualState.RUNNING && state != ActualState.COMPLETED)
          throw new IllegalStateException("Processing vocalization failed : "
              + command.getResult());

        String msg = "Processing vocalization completed";
        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.VOCAL, msg);

        pBuffer.setProcessorChunk(model.getDeclarativeModule().getFreeChunk());
      }
      catch (InterruptedException e)
      {
        return;
      }
      catch (Exception e)
      {
        String msg = e.getMessage();
        if (e instanceof ExecutionException)
          msg = ((ExecutionException) e).getCause().getMessage();

        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.VOCAL, msg);

        IChunk error = model.getDeclarativeModule().getErrorChunk();
        pBuffer.setProcessorChunk(error);
        pBuffer.setStateChunk(error);
      }
    }

  };

  /**
   * timed event to handle execution phase
   * 
   * @author harrison
   */
  private class ExecuteTimedEvent extends
      AbstractEfferentTimedEvent<VocalizationCommand>
  {

    public ExecuteTimedEvent(double start, double end,
        Future<VocalizationCommand> commandFuture, IActivationBuffer buffer)
    {
      super(start, end, commandFuture, buffer);
      setDriftStates(ActualState.ACCEPTED, ActualState.RUNNING);
    }

    @Override
    protected boolean shouldWarnOnTimeSlips()
    {
      return false;
    }

    @Override
    protected AbstractEfferentTimedEvent<VocalizationCommand> clone()
    {
      return new ExecuteTimedEvent(getStartTime(), getEndTime(), getFuture(),
          getBuffer());
    }

    @Override
    protected void fired(double currentTime,
        Future<VocalizationCommand> commandFuture)
    {
      IPerceptualBuffer pBuffer = (IPerceptualBuffer) getBuffer();
      IModel model = pBuffer.getModel();
      try
      {
        VocalizationCommand command = commandFuture.get();
        ActualState state = command.getActualState();
        if (state != ActualState.COMPLETED)
          throw new IllegalStateException("Execution of vocalization failed : "
              + command.getResult());

        String msg = "Execution of vocalization completed";
        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.VOCAL, msg);

        IChunk free = model.getDeclarativeModule().getFreeChunk();

        pBuffer.setExecutionChunk(free);
        pBuffer.setStateChunk(free);
      }
      catch (InterruptedException e)
      {
        return;
      }
      catch (Exception e)
      {
        String msg = e.getMessage();
        if (e instanceof ExecutionException)
          msg = ((ExecutionException) e).getCause().getMessage();

        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.VOCAL, msg);

        IChunk error = model.getDeclarativeModule().getErrorChunk();

        pBuffer.setExecutionChunk(error);
        pBuffer.setStateChunk(error);
      }
    }

  }
}
