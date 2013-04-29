package org.jactr.modules.pm.aural.delegate;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.asynch.delegate.AbstractAsynchronousModuleDelegate;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.slot.ChunkSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.audicon.IAudicon;
import org.jactr.modules.pm.aural.audicon.encoder.IAuralChunkEncoder;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

public class AuralScanDelegate extends
    AbstractAsynchronousModuleDelegate<AbstractAuralModule, IChunk>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralScanDelegate.class);

  public AuralScanDelegate(AbstractAuralModule module,
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
    return startTime
        + getModule().getModel().getProceduralModule()
            .getDefaultProductionFiringTime();
  }

  @Override
  protected void finalizeProcessing(IRequest request, IChunk result,
      Object... parameters)
  {
    AbstractAuralModule module = getModule();
    IModel model = module.getModel();
    IAuralLocationBuffer buffer = module.getAuralLocationBuffer();
    boolean wasStuffRequest = (Boolean) parameters[0];

    IChunk error = module.getErrorChunk();
    IChunk free = module.getFreeChunk();

    IChunk state = free;
    String message = null;

    if (error.equals(result) && !wasStuffRequest) 
    {
      message = "Could not find aural location matching " + request;
      state = error;
    }
    else
    {
      message = "Found aural location matching " + request;
      buffer.addSourceChunk(result);
      
      if (wasStuffRequest)
        buffer.setBufferChunk(module.getUnrequestedChunk());
      else
        buffer.setBufferChunk(module.getRequestedChunk());
    }

    buffer.setPreparationChunk(free);
    buffer.setProcessorChunk(free);
    buffer.setExecutionChunk(free);
    buffer.setStateChunk(state);

    if (LOGGER.isDebugEnabled()) LOGGER.debug(message);

    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.AURAL, message);
  }

  /**
   * find the audio event that matches the provided pattern. this method works
   * like the visual system's multipass search. It seems heavey handed for the
   * audio system, but does permit further expansion of features through the
   * addition of additional {@link IFeatureMap} and
   * {@link IAfferentObjectEncoder} instantiations.<br>
   * <br>
   * we start by iterating through all the feature maps. If the featureMap.{@link IFeatureMap#isInterestedIn(ChunkTypeRequest)}
   * returns true, the set
   * {@link IFeatureMap#getCandidateRealObjects(ChunkTypeRequest, Set)} is considered. If
   * it is empty, the search immediate aborts. Why? all searches in ACT-R are
   * <b>and</b> only. if the feature map is interested in the pattern and it
   * returns 0, there are no identifiers that can match. Otherwise the set is
   * intersected with the totalset. if the totalset is ever empty, the search
   * aborts (the intersection of the features was empty).<br>
   * <br>
   * This set is then passed through all the encoders. the resulting chunks are
   * all the chunks that could possibly be
   * 
   * @see org.jactr.modules.pm.aural.audicon.IAudicon#getAudioEvent(org.jactr.core.production.condition.ChunkPattern)
   */
  @Override
  protected IChunk processInternal(IRequest request, Object... parameters)
  {
    AbstractAuralModule module = getModule();
    IModel model = module.getModel();
    IChunk error = model.getDeclarativeModule().getErrorChunk();
    IAudicon audicon = module.getAudicon();

    Collection<IFeatureMap> featureMaps = audicon.getFeatureMaps();
    
    ChunkTypeRequest cRequest = (ChunkTypeRequest) request;

    /*
     * first things first, we need to look through the conditional slots looking
     * for an :attended test, since :attended == null should match to new (ugh,
     * silly canonical semantics)
     */
    for (IConditionalSlot slot : cRequest.getConditionalSlots())
    {
      String slotName = slot.getName();
      Object slotValue = slot.getValue();
      if (slotName.equals(IAuralModule.ATTENDED_STATUS_SLOT)
          && slot.getCondition() == IConditionalSlot.EQUALS
          && slotValue == null)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Attended slot : " + slot);
        /*
         * :attended nil should match :attended new as well, so we change this
         * to :attended != true
         */
        slot.setCondition(IConditionalSlot.NOT_EQUALS);
        slot.setValue(Boolean.TRUE);
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Changed to " + slot);
      }
    }

    /*
     * iterate through the feature maps, selecting the relevant identifiers
     */
    FastSet<IIdentifier> candidates = FastSet.newInstance();
    Set<IIdentifier> identifiers = new HashSet<IIdentifier>();
    boolean firstIteration = true;
    for (IFeatureMap featureMap : featureMaps)
      if (featureMap.isInterestedIn(cRequest))
      {
        featureMap
            .getCandidateRealObjects(cRequest, candidates);

        if (candidates.size() == 0)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("No candidates are available for " + featureMap
                + ", returning error");
          return error;
        }

        if (firstIteration)
        {
          identifiers.addAll(candidates);
          firstIteration = false;
        }
        else
          identifiers.retainAll(candidates);

        if (identifiers.size() == 0)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER
                .debug("No candidates available after retention, returning error");
          return error;
        }
      }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Candidate identifiers " + identifiers);

    /*
     * now we'll collect what the audioEvent chunk should look like based on the
     * featuremaps and possible identifiers (to bind the actual slot values)
     */
    Map<IIdentifier, ChunkTypeRequest> candidatePatterns = new HashMap<IIdentifier, ChunkTypeRequest>();
    for (IIdentifier identifier : identifiers)
    {
      ChunkTypeRequest tmpPattern = new ChunkTypeRequest(cRequest.getChunkType());
      for (IFeatureMap featureMap : featureMaps)
        featureMap.fillSlotValues(tmpPattern, identifier, null, cRequest);
      candidatePatterns.put(identifier, tmpPattern);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("candidatePatterns " + candidatePatterns);

    /*
     * now lets find the appropriate chunks
     */
    Collection<IAuralChunkEncoder> encoders = audicon.getEncoders();
    boolean fastSearch = false;
    Map<IIdentifier, IChunk> soundChunks = new HashMap<IIdentifier, IChunk>(
        identifiers.size());

    for (Map.Entry<IIdentifier, ChunkTypeRequest> ifm : candidatePatterns
        .entrySet())
      for (IAuralChunkEncoder encoder : encoders)
      {
        if (fastSearch && soundChunks.size() == 1) break;

        IIdentifier id = ifm.getKey();
//        IChunk chunk = encoder.getEncodedChunkFor(id);
//        if (chunk != null) soundChunks.put(id, chunk);
      }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Candidate chunks : " + soundChunks);

    /*
     * select one. on the visual side, this is a pain in the ass.
     */
    if (soundChunks.size() == 0)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No hearable sounds available, returning error");
      return error;
    }

    IIdentifier soundIdentifier = null;
    IChunk soundChunk = null;

    if (soundChunks.size() == 1)
    {
      Map.Entry<IIdentifier, IChunk> entry = soundChunks.entrySet().iterator()
          .next();
      soundChunk = entry.getValue();
      soundIdentifier = entry.getKey();
    }
    else
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("More than one sound matches(" + soundChunks.size()
            + "), selecting randomly");
      /*
       * otherwise we shuffle
       */
      List<IIdentifier> ids = new ArrayList<IIdentifier>(soundChunks.keySet());
      Collections.shuffle(ids);
      soundIdentifier = ids.get(0);
      soundChunk = soundChunks.get(soundIdentifier);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER
          .debug("Selected " + soundChunk + " refering to " + soundIdentifier);

    /*
     * alright let's get the audioEvent from the appropriate candidatePattern
     */
    IChunk audioEventChunk = null;
    try
    {
      audioEventChunk = audicon.getAudioEventFor(soundIdentifier);
      
      if (audioEventChunk==null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(soundIdentifier
              + " does not refer to an active audio event, recreating");
        
        audioEventChunk = model.getDeclarativeModule().createChunk(
            getModule().getAudioEventChunkType(),
            "audioEvent").get();
      }

      ISymbolicChunk sc = audioEventChunk.getSymbolicChunk();
      /*
       * now we set the slot values of the audio-event chunk to match those of
       * the search pattern, excluding :attended
       */
      for (IConditionalSlot slot : candidatePatterns.get(soundIdentifier)
          .getConditionalSlots())
        if (slot.getCondition() == IConditionalSlot.EQUALS)
          if (!slot.getName().equalsIgnoreCase(
              IAuralModule.ATTENDED_STATUS_SLOT)
              && !slot.getName().equalsIgnoreCase("offset"))
            ((ChunkSlot) sc.getSlot(slot.getName())).setValue(slot.getValue());

      audioEventChunk.setMetaData(IAudicon.AUDIO_EVENT_SOUND_LINK, soundChunk);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not get audioEvent future result", e);
    }

    /*
     * and soundChunk must have both event and kind set..
     */
    ((ChunkSlot) soundChunk.getSymbolicChunk().getSlot(IAuralModule.EVENT_SLOT))
        .setValue(audioEventChunk);

    ((ChunkSlot) soundChunk.getSymbolicChunk().getSlot(IAuralModule.KIND_SLOT))
        .setValue(audioEventChunk.getSymbolicChunk().getSlot(
            IAuralModule.KIND_SLOT).getValue());

    soundChunk.setMetaData(IAudicon.AUDIO_EVENT_SOUND_LINK, audioEventChunk);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("returning audioEvent : " + audioEventChunk + " linked to "
          + soundChunk);

    return audioEventChunk;

  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
    AbstractAuralModule module = getModule();
    IModel model = module.getModel();
    IAuralLocationBuffer lBuffer = module.getAuralLocationBuffer();
    IAuralActivationBuffer aBuffer = module.getAuralBuffer();
    boolean wasStuffRequest = (Boolean) parameters[0];

    if(aBuffer.isStateBusy())
    {
      String message = "Aural buffer is currently busy, cannot scan, ignoring.";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, message);

      return false;
    }

    if (lBuffer.isStateBusy())
    {
      String message = "Aural-location buffer is currently busy, cannot scan, ignoring.";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, message);

      return false;
    }

    if (lBuffer.getSourceChunk() != null)
      lBuffer.removeSourceChunk(lBuffer.getSourceChunk());

    IChunk busy = module.getBusyChunk();
    lBuffer.setPreparationChunk(busy);
    lBuffer.setProcessorChunk(busy);
    lBuffer.setExecutionChunk(busy);
    lBuffer.setStateChunk(busy);

    return true;
  }

}
