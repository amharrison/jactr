package org.jactr.modules.pm.aural.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.asynch.delegate.AbstractAsynchronousModuleDelegate;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.audicon.IAudicon;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;

public class AuralEncodingDelegate extends
    AbstractAsynchronousModuleDelegate<AbstractAuralModule, IChunk>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralEncodingDelegate.class);

  public AuralEncodingDelegate(AbstractAuralModule module,
      double minimumProcessingTime, IChunk cantProcessResult)
  {
    super(module, minimumProcessingTime, cantProcessResult);
  }

  protected void enqueueTimedEvent(ITimedEvent timedEvent)
  {
    getModule().getAuralBuffer().enqueueTimedEvent(timedEvent);
  }

  @Override
  protected double computeHarvestTime(IRequest request, IChunk result,
      double startTime, Object... parameters)
  {
    AbstractAuralModule module = getModule();

    if (module.getErrorChunk().equals(result)) return startTime;

    return startTime
        + module.getEncodingTimeEquation().computeEncodingTime(result);
  }

  @Override
  protected void finalizeProcessing(IRequest request, IChunk result,
      Object... parameters)
  {
    AbstractAuralModule module = getModule();
    IModel model = module.getModel();
    IAuralActivationBuffer buffer = module.getAuralBuffer();

    IChunk error = module.getErrorChunk();
    IChunk free = module.getFreeChunk();

    IChunk audioLocation = (IChunk) parameters[0];
    IChunk state = free;
    String message = null;

    if (error.equals(result))
    {
      message = "Could not encode aural chunk at " + audioLocation;
      state = error;
    }
    else
    {
      message = "Encoded aural chunk " + result + " at " + audioLocation;
      buffer.addSourceChunk(result);
    }

    buffer.setPreparationChunk(free);
    buffer.setProcessorChunk(free);
    buffer.setExecutionChunk(free);
    buffer.setStateChunk(state);

    if (LOGGER.isDebugEnabled()) LOGGER.debug(message);

    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.AURAL, message);
  }

  @Override
  protected IChunk processInternal(IRequest request, Object... parameters)
  {
    AbstractAuralModule module = getModule();
    IModel model = module.getModel();
    IChunk error = model.getDeclarativeModule().getErrorChunk();
    IChunk soundChunk = error;
    IChunk audioLocation = (IChunk) parameters[0];
    IAudicon audicon = module.getAudicon();

    try
    {
      soundChunk = (IChunk) audioLocation
          .getMetaData(IAudicon.AUDIO_EVENT_SOUND_LINK);
      if (soundChunk == null)
        soundChunk = error;
      else
      {
        soundChunk.setMetaData(IAudicon.AUDIO_EVENT_SOUND_LINK, null);
        /*
         * ok, we've got a sound chunk, we need to flag the identifier as
         * attended..
         */
        IIdentifier identifier = (IIdentifier) soundChunk
            .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);

        audicon.getFINSTFeatureMap().flagAsAttended(identifier, soundChunk,
            getModule().getAuralDecayTime());
      }
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(
            "exception while getting sound chunk, may have been disposed ", e);
      soundChunk = error;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("returning sound:" + soundChunk + " from audioLocation:"
          + audioLocation);

    return soundChunk;
  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
    AbstractAuralModule module = getModule();
    IModel model = module.getModel();
    IAuralActivationBuffer buffer = module.getAuralBuffer();
    IChunk auralLocation = (IChunk) parameters[0];

    if (auralLocation == null
        || !auralLocation.isA(module.getAudioEventChunkType()))
    {
      if (LOGGER.isWarnEnabled() || Logger.hasLoggers(model))
      {
        String message = "aural location must be properly specified when attending to sound, ignoring.";
        LOGGER.warn(message);
        Logger.log(model, Logger.Stream.AURAL, message);
      }
      return false;
    }

    if (buffer.isStateBusy())
    {
      String message = "Aural buffer is currently busy, cannot encode, ignoring.";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, message);

      return false;
    }

    if (buffer.getSourceChunk() != null)
      buffer.removeSourceChunk(buffer.getSourceChunk());

    IChunk busy = module.getBusyChunk();
    buffer.setPreparationChunk(busy);
    buffer.setProcessorChunk(busy);
    buffer.setExecutionChunk(busy);
    buffer.setStateChunk(busy);

    return true;
  }

}
