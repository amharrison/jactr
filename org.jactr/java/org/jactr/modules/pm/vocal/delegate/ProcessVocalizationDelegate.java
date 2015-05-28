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
import org.commonreality.net.message.request.object.ObjectDataRequest;
import org.commonreality.object.delta.DeltaTracker;
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
public class ProcessVocalizationDelegate extends AbstractVocalDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProcessVocalizationDelegate.class);

  public ProcessVocalizationDelegate(AbstractVocalModule module,
      double minimumProcessingTime, IChunk cantProcessResult)
  {
    super(module, minimumProcessingTime, cantProcessResult);

    setDriftStates(IEfferentCommand.ActualState.ACCEPTED);
    setEarlyTerminationStates(IEfferentCommand.ActualState.ABORTED,
        IEfferentCommand.ActualState.RUNNING,
        IEfferentCommand.ActualState.COMPLETED,
        IEfferentCommand.ActualState.REJECTED);
  }

  @Override
  protected void finalizeProcessingInternal(ChunkTypeRequest pattern,
      IChunk result, Object... parameters)
  {
    AbstractVocalModule module = getModule();
    IModel model = module.getModel();
    IVocalActivationBuffer buffer = getModule().getVocalBuffer();
    Boolean isVocalization = (Boolean) parameters[1];
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

    if (state == IEfferentCommand.ActualState.RUNNING
        || state == IEfferentCommand.ActualState.COMPLETED
        || state == IEfferentCommand.ActualState.ACCEPTED)
    {
      msg = "Processing of vocalization completed.";
      bufferState = free;
    }
    else
    {
      msg = "Processing of vocalization failed [" + state + "] : "
          + failureMessage;
      bufferState = error;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    if (Logger.hasLoggers(model)) Logger.log(model, Logger.Stream.VOCAL, msg);

    buffer.setProcessorChunk(bufferState);

    if (!isVocalization)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Deleting command since this was a subvocalization");
      /*
       * delete and finish the states
       */
      agent.send(new ObjectCommandRequest(
          ((ISensoryIdentifier) getCommandIdentifier()).getSensor(), agent
              .getIdentifier(), IObjectCommand.Type.REMOVED, Collections
              .singleton(getCommandIdentifier())));

      buffer.setStateChunk(bufferState);
      buffer.setModalityChunk(bufferState);
    }
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
    return startTime
        + getModule().getProcessingTimeEquation().compute(command.getText(),
            getModule());
  }

  @Override
  protected IChunk processInternal(IRequest request, Object... parameters)
  {
    AbstractVocalModule module = getModule();
    IModel model = module.getModel();
    Boolean isVocalization = (Boolean) parameters[1];

    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);

    IIdentifier commandIdentifier = getCommandIdentifier();

    VocalizationCommand command = (VocalizationCommand) agent
        .getEfferentCommandManager().get(commandIdentifier);

    /*
     * start the vocal..
     */

    if (isVocalization)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Sending start request");

      DeltaTracker<VocalizationCommand> tracker = new DeltaTracker<VocalizationCommand>(
          command);
      tracker.setProperty(IEfferentCommand.REQUESTED_STATE,
          IEfferentCommand.RequestedState.START);
      tracker.setProperty(IEfferentCommand.REQUESTED_START_TIME, ACTRRuntime
          .getRuntime().getClock(model).getTime());

      /*
       * send the command
       */
      agent.send(new ObjectDataRequest(agent.getIdentifier(), command
          .getIdentifier().getSensor(), Collections.singleton(tracker
          .getDelta())));

      agent.send(new ObjectCommandRequest(agent.getIdentifier(), command
          .getIdentifier().getSensor(), IObjectCommand.Type.UPDATED,
          Collections.singleton(commandIdentifier)));
    }

    /*
     * return type is irrelevant at this point
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

    if (vBuffer.isProcessorBusy())
    {
      String msg = "Vocalizations cannot be started when the vocal buffer is busy processing. Ignoring request";
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
      String msg = "Vocalization is already being processed but not passed on. Ignoring old vocalization.";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
      setCommandIdentifier(null);
    }

    VocalizationCommand command = (VocalizationCommand) agent
        .getEfferentCommandManager().get(commandIdentifier);

    if (command == null)
    {
      String msg = "NO command was found for " + commandIdentifier
          + ". Something has gone horribly wrong";
      /**
       * Error : error
       */
      LOGGER.error(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      return false;
    }

    if (!IEfferentCommand.ActualState.ACCEPTED.equals(command.getActualState()))
    {
      String msg = "VocalizationCommand has not been accepted yet : "
          + command.getActualState() + ". Ignoring request.";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
      return false;
    }



    vBuffer.setProcessorChunk(busy);
    setCommandIdentifier(commandIdentifier);

    return true;
  }

}
