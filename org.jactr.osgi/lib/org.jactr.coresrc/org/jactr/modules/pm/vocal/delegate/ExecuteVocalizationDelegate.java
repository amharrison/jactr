package org.jactr.modules.pm.vocal.delegate;

/*
 * default logging
 */
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.net.message.command.object.IObjectCommand;
import org.commonreality.net.message.request.object.ObjectCommandRequest;
import org.commonreality.object.identifier.ISensoryIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.vocal.AbstractVocalModule;
import org.jactr.modules.pm.vocal.buffer.IVocalActivationBuffer;

@Deprecated
public class ExecuteVocalizationDelegate extends AbstractVocalDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ExecuteVocalizationDelegate.class);

  public ExecuteVocalizationDelegate(AbstractVocalModule module,
      double minimumProcessingTime, IChunk cantProcessResult)
  {
    super(module, minimumProcessingTime, cantProcessResult);

    setDriftStates(IEfferentCommand.ActualState.RUNNING);
    setEarlyTerminationStates(IEfferentCommand.ActualState.ABORTED,
        IEfferentCommand.ActualState.COMPLETED);
  }

  @Override
  protected void finalizeProcessingInternal(ChunkTypeRequest pattern,
      IChunk result, Object... parameters)
  {
    AbstractVocalModule module = getModule();
    IModel model = module.getModel();
    IVocalActivationBuffer buffer = getModule().getVocalBuffer();

    IChunk error = module.getErrorChunk();
    IChunk free = module.getFreeChunk();
    IChunk bufferState = error;
    Object failureMessage = null;
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);

    IEfferentCommand vocalizationCommand = agent.getEfferentCommandManager()
        .get(getCommandIdentifier());

    IEfferentCommand.ActualState state = IEfferentCommand.ActualState.ABORTED;

    if (vocalizationCommand != null)
    {
      state = vocalizationCommand.getActualState();
      failureMessage = vocalizationCommand.getResult();
    }

    String msg = null;

    if (state == IEfferentCommand.ActualState.COMPLETED)
    {
      msg = "Execution of vocalization completed.";
      bufferState = free;
    }
    else
    {
      msg = "Execution of vocalization failed [" + state + "] : "
          + failureMessage;
      bufferState = error;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Deleting command");

    /*
     * delete the command
     */
    agent.send(new ObjectCommandRequest(
        ((ISensoryIdentifier) getCommandIdentifier()).getSensor(), agent
            .getIdentifier(), IObjectCommand.Type.REMOVED, Collections
            .singleton(getCommandIdentifier())));

    if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.VOCAL, msg);

    buffer.setExecutionChunk(bufferState);
    buffer.setStateChunk(bufferState);
    buffer.setModalityChunk(bufferState);
  }

  @Override
  protected double computeHarvestTime(IRequest request, IChunk result,
      double startTime, Object... parameters)
  {
    IIdentifier commandIdentifier = (IIdentifier) parameters[0];
    AbstractVocalModule module = getModule();
    IModel model = module.getModel();

    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);
    VocalizationCommand command = (VocalizationCommand) agent
        .getEfferentCommandManager().get(commandIdentifier);

    double computedDuration = getModule().getExecutionTimeEquation().compute(
        command.getText(), getModule());

    double estimatedDuration = command.getEstimatedDuration();
    double actualDuration = computedDuration;

    if (Math.abs(estimatedDuration - computedDuration) >= 0.01)
    {
      switch (module.getExecutionTimeResolution())
      {
        case ACTR:
          actualDuration = computedDuration;
          break;
        case CR:
          actualDuration = estimatedDuration;
          break;
        case MINIMUM:
          actualDuration = Math.min(estimatedDuration, computedDuration);
          break;
        case MAXIMUM:
          actualDuration = Math.max(estimatedDuration, computedDuration);
          break;
      }

      String msg = "CR and jACT-R disagree as to the vocalization duration, "
          + estimatedDuration + " & " + computedDuration
          + " respectively. Using " + actualDuration;

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
    }

    return startTime + actualDuration;
  }

  @Override
  protected IChunk processInternal(IRequest request, Object... parameters)
  {
    /*
     * Noop
     */
    return null;
  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
    AbstractVocalModule module = getModule();
    IVocalActivationBuffer vBuffer = module.getVocalBuffer();
    IModel model = module.getModel();
    IChunk busy = module.getBusyChunk();
    IIdentifier commandIdentifier = (IIdentifier) parameters[0];
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);

    if (vBuffer.isExecutionBusy())
    {
      String msg = "Vocalizations cannot be executed when the vocal buffer is busy executing. Ignoring request";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);

      return false;
    }

    if (getModule().getVocalizationSource() == null)
    {
      String msg = "No vocalization source could be found. Ignoring request";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);

      return false;
    }

    if (commandIdentifier == null)
    {
      String msg = "No vocalization was prepared. Ignoring request.";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
      return false;
    }

    if (getCommandIdentifier() != null)
    {
      String msg = "Vocalization is already being executed but not passed on. Ignoring old vocalization.";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
      setCommandIdentifier(null);
    }

    VocalizationCommand command = (VocalizationCommand) agent
        .getEfferentCommandManager().get(commandIdentifier);

    if (command == null)
    {
      String msg = "No command was found for " + commandIdentifier
          + ". Something has gone horribly wrong";
      /**
       * Error : error
       */
      LOGGER.error(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      return false;
    }

    vBuffer.setExecutionChunk(busy);
    setCommandIdentifier(commandIdentifier);

    return true;
  }

}
