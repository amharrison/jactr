package org.jactr.modules.pm.vocal.delegate;

/*
 * default logging
 */
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.efferent.IEfferentCommandTemplate;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.vocal.VocalizationCommand;
import org.commonreality.modalities.vocal.VocalizationCommandTemplate;
import org.commonreality.net.message.command.object.IObjectCommand;
import org.commonreality.net.message.request.object.ObjectCommandRequest;
import org.commonreality.net.message.request.object.ObjectDataRequest;
import org.commonreality.object.IEfferentObject;
import org.commonreality.object.delta.FullObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.vocal.AbstractVocalModule;
import org.jactr.modules.pm.vocal.buffer.IVocalActivationBuffer;

@Deprecated
public class PrepareVocalizationDelegate extends AbstractVocalDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PrepareVocalizationDelegate.class);

  public PrepareVocalizationDelegate(AbstractVocalModule module,
      double minimumProcessingTime, IChunk cantProcessResult)
  {
    super(module, minimumProcessingTime, cantProcessResult);

    setDriftStates(IEfferentCommand.ActualState.UNKNOWN);
    setEarlyTerminationStates(IEfferentCommand.ActualState.ABORTED,
        IEfferentCommand.ActualState.ACCEPTED,
        IEfferentCommand.ActualState.REJECTED);
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

    IEfferentCommand.ActualState state = IEfferentCommand.ActualState.REJECTED;

    if (vocalizationCommand != null)
    {
      state = vocalizationCommand.getActualState();
      failureMessage = vocalizationCommand.getResult();
    }

    String msg = null;

    if (state == IEfferentCommand.ActualState.ACCEPTED)
    {
      msg = "Preparation of vocalization completed.";
      bufferState = free;
    }
    else
    {
      msg = "Preparation of vocalization failed ["+state+"] : " + failureMessage;
      bufferState = error;
    }
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
    if(Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.VOCAL, msg);

    buffer.setPreparationChunk(bufferState);

    /*
     * pass it back to the module for execution
     */
//    if (free == bufferState) module.execute(getCommandIdentifier(), isVocalization);
  }

  @Override
  protected double computeHarvestTime(IRequest request, IChunk result,
      double startTime, Object... parameters)
  {
    String text = (String) parameters[0];
    return startTime
        + getModule().getPreparationTimeEquation().compute(text, getModule());
  }

  @Override
  protected IChunk processInternal(IRequest request, Object... parameters)
  {
    AbstractVocalModule module = getModule();
    IModel model = module.getModel();
    String text = (String) parameters[0];

    IEfferentObject vocalizationSource = module.getVocalizationSource();
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);

    for (IEfferentCommandTemplate<?> template : vocalizationSource
        .getCommandTemplates())
      if (template instanceof VocalizationCommandTemplate)
        try
        {
          /*
           * create the command and send it out
           */
          VocalizationCommand command = ((VocalizationCommandTemplate) template)
              .instantiate(agent, vocalizationSource);
          command.setText(text);

          agent.send(new ObjectDataRequest(agent.getIdentifier(),
              vocalizationSource.getIdentifier().getSensor(), Collections
                  .singleton(new FullObjectDelta(command))));

          agent.send(new ObjectCommandRequest(agent.getIdentifier(),
              vocalizationSource.getIdentifier().getSensor(),
              IObjectCommand.Type.ADDED, Collections
                  .singleton((IIdentifier) command.getIdentifier())));

          /*
           * we are just going to store this command id and not the actual
           * object. Storing a reference to a ISimulationObject immediately
           * after sending the add is not a good idea because this reference
           * will not be the actual live reference. use
           * IObjectManager.get(IIdentifier) instead.
           */
          setCommandIdentifier(command.getIdentifier());

          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Sent vocalization command");

          continue;
        }
        catch (Exception e)
        {
          /**
           * Error : couldn't instantiate
           */
          LOGGER.error("Could not instantiate VocalizationCommand ", e);
          throw new RuntimeException(
              "Could not instantiate VocalizationCommand", e);
        }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug("Ignoring command template that is not vocalization "
            + template);

    if (getCommandIdentifier() == null)
    {
      String msg = "No vocalization command could be created. No vocalizing sensor found?";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);

      return null;
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
    IChunk free = module.getFreeChunk();

    if (vBuffer.isPreparationBusy())
    {
      String msg = "Vocalizations cannot be started when the vocal buffer is busy preparing. Ignoring request";
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

    if (getCommandIdentifier() != null)
    {
      String msg = "Vocalization has already been prepared but not passed on. Ignoring old vocalization.";
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VOCAL, msg);

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
      setCommandIdentifier(null);
    }

    vBuffer.setStateChunk(busy);
    vBuffer.setModalityChunk(busy);
    vBuffer.setPreparationChunk(busy);
    vBuffer.setProcessorChunk(free);
    vBuffer.setExecutionChunk(free);

    return true;
  }

}
