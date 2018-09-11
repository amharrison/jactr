package org.jactr.modules.pm.aural.buffer.processor;

import java.util.List;
/*
 * default logging
 */
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.event.IAuralModuleListener;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;

/**
 * handles aural-location requests by checking and managing the buffer states
 * and routing to the aural module. This also uses a blocking timed event from
 * the super class to make sure that the model does not run past the firing time
 * until the visual module has completed its aural search
 * 
 * @author harrison
 */
public class AuralSearchRequestDelegate extends AbstractRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralSearchRequestDelegate.class);

  private IAuralModule               _module;

  private boolean                    _isStuffRequest;

  @SuppressWarnings("serial")
  public AuralSearchRequestDelegate(IAuralModule module)
  {
    super(module.getAudioEventChunkType());
    setAsynchronous(true);
    setUseBlockingTimedEvents(true);
    setDelayStart(false);

    _module = module;
    _module.addListener(new IAuralModuleListener() {

      @SuppressWarnings("unchecked")
      public void parameterChanged(IParameterEvent pe)
      {

      }

      public void moduleReset(IPerceptualMemoryModuleEvent event)
      {
        release();
      }

      public void perceptAttended(IPerceptualMemoryModuleEvent event)
      {

      }

      public void perceptIndexFound(IPerceptualMemoryModuleEvent event)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Found " + event.getChunk() + " releasing");
        // release the block, if any
        release();

      }

    }, ExecutorServices.INLINE_EXECUTOR);
  }

  final public boolean isBufferStuffPending()
  {
    ITimedEvent previousSearch = getCurrentTimedEvent();
    return previousSearch != null && !previousSearch.hasFired()
        && !previousSearch.hasAborted() && _isStuffRequest;
  }

  final public void cancelBufferStuff()
  {
    ITimedEvent previousSearch = getCurrentTimedEvent();
    if (previousSearch != null && !previousSearch.hasFired()
        && !previousSearch.hasAborted() && _isStuffRequest)
      previousSearch.abort();
  }

  @Override
  final protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    /*
     * check to see if we are busy or not..
     */
    IModel model = _module.getModel();
    if (isBusy(buffer) /* || isBusy(_module.getVisualActivationBuffer()) */)
    {
      String msg = "Aural system is currently busy, cannot perform aural search";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, msg);
      return false;
    }

    return true;
  }

  @Override
  final protected Object startRequest(IRequest request,
      IActivationBuffer buffer, double requestTime)
  {
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    IAuralLocationBuffer locBuffer = _module.getAuralLocationBuffer();
    IChunk busy = _module.getModel().getDeclarativeModule().getBusyChunk();

    /*
     * cancel previous stuff request
     */
    if (isBufferStuffPending()) cancelBufferStuff();

    /*
     * figure out if this is a stuff request
     */
    List<ISlot> slots = FastListFactory.newInstance();
    ctr.getSlots(slots);

    boolean isStuffRequest = false;
    for (ISlot slot : slots)
      if (IPerceptualBuffer.IS_BUFFER_STUFF_REQUEST.equals(slot.getName())
          && Boolean.TRUE.equals(slot.getValue()))
      {
        isStuffRequest = true;
        ctr.removeSlot(slot);
        break;
      }

    /*
     * clear it.
     */
    locBuffer.removeSourceChunk(locBuffer.getSourceChunk());

    /*
     * set the buffer states.
     */
    locBuffer.setStateChunk(busy);
    locBuffer.setModalityChunk(busy);
    locBuffer.setExecutionChunk(busy);
    locBuffer.setProcessorChunk(busy);
    locBuffer.setPreparationChunk(busy);

    _isStuffRequest = isStuffRequest;

    FastListFactory.recycle(slots);

    return _module.search(ctr, requestTime, isStuffRequest);
  }

  @Override
  final protected void abortRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    IAuralLocationBuffer locBuffer = _module.getAuralLocationBuffer();
    IChunk free = _module.getModel().getDeclarativeModule().getFreeChunk();
    /*
     * set the buffer states.
     */
    locBuffer.setStateChunk(free);
    locBuffer.setModalityChunk(free);
    locBuffer.setExecutionChunk(free);
    locBuffer.setProcessorChunk(free);
    locBuffer.setPreparationChunk(free);

    super.abortRequest(request, buffer, startValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  final protected void finishRequest(IRequest request,
      IActivationBuffer buffer, Object startValue)
  {
    IModel model = _module.getModel();
    IAuralLocationBuffer locBuffer = _module.getAuralLocationBuffer();
    IDeclarativeModule decM = model.getDeclarativeModule();
    IChunk error = decM.getErrorChunk();
    IChunk free = decM.getFreeChunk();
    IChunk auralLocation = error;
    PerceptualSearchResult searchResult = null;

    try
    {
      searchResult = ((Future<PerceptualSearchResult>) startValue).get();
    }
    catch (InterruptedException ie)
    {
      return;
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to get future value from search " + request, e);
    }

    if (searchResult.getLocation() != null)
      auralLocation = searchResult.getLocation();

    if (error.equals(auralLocation))
    {
      if (_isStuffRequest)
      {
        locBuffer.setStateChunk(free);
        locBuffer.setModalityChunk(free);
        locBuffer.setErrorChunk(null);
      }
      else
      {
        if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
        {
          String msg = String.format(
              "No valid audio-event could be found matching request %s",
              request);
          LOGGER.debug(msg);
          Logger.log(model, Logger.Stream.AURAL, msg);
        }

        locBuffer.setStateChunk(error);
        locBuffer.setModalityChunk(error);
        locBuffer.setErrorChunk(searchResult.getErrorCode());
      }
    }
    else
    {
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        String msg = String.format("Found %1$s matching %2$s because of %3$s.",
            auralLocation, request, auralLocation
                .getMetaData(IPerceptualMemory.SEARCH_RESULT_IDENTIFIER_KEY));
        LOGGER.debug(msg);
        Logger.log(model, Logger.Stream.AURAL, msg);
      }

      locBuffer.setStateChunk(free);
      locBuffer.setModalityChunk(free);
      locBuffer.setErrorChunk(null);
      locBuffer.addSourceChunk(auralLocation);

      /*
       * and handle the stuff flag
       */
      if (auralLocation != null) if (_isStuffRequest)
        locBuffer.setBufferChunk(decM.getUnrequestedChunk());
      else
        locBuffer.setBufferChunk(decM.getRequestedChunk());
    }

    locBuffer.setExecutionChunk(free);
    locBuffer.setProcessorChunk(free);
    locBuffer.setPreparationChunk(free);
  }

}
