package org.jactr.modules.pm.visual.buffer.processor;

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
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer;
import org.jactr.modules.pm.visual.event.IVisualModuleListener;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;

/**
 * handles visual-location requests by checking and managing the buffer states
 * and routing to the visual module. This also uses a blocking timed event from
 * the super class to make sure that the model does not run past the firing time
 * until the visual module has completed its visual search
 * 
 * @author harrison
 */
public class VisualSearchRequestDelegate extends AbstractRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisualSearchRequestDelegate.class);

  private IVisualModule              _module;

  private boolean                    _isStuffRequest;

  private boolean                    _useDefaultForEmptySearches = false;

  @SuppressWarnings("serial")
  public VisualSearchRequestDelegate(IVisualModule module,
      boolean useDefaultForEmptySearches)
  {
    super(module.getVisualLocationChunkType());
    _useDefaultForEmptySearches = useDefaultForEmptySearches;
    setAsynchronous(true);
    setUseBlockingTimedEvents(true);
    setDelayStart(false);

    _module = module;
    _module.addListener(new IVisualModuleListener() {

      public void trackedObjectMoved(VisualModuleEvent event)
      {

      }

      public void trackingObjectStarted(VisualModuleEvent event)
      {

      }

      public void trackingObjectStopped(VisualModuleEvent event)
      {

      }

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
        // TODO Auto-generated method stub

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
    IModel model = _module.getModel();

    /*
     * check to see if we are busy or not..
     */
    if (isBusy(buffer) /* || isBusy(_module.getVisualActivationBuffer()) */)
    {
      String msg = "Visual system is currently busy, cannot perform visual search";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
      return false;
    }

    return true;
  }

  /**
   * we expand empty requests to be the default search pattern
   */
  @Override
  protected IRequest expandRequest(IRequest request)
  {
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    if (_useDefaultForEmptySearches && ctr.getSlots().size() == 0)
      ctr = _module.getVisualLocationBuffer().getDefaultSearch();
    return ctr;
  }

  @Override
  final protected Object startRequest(IRequest request,
      IActivationBuffer buffer, double requestTime)
  {
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    IVisualLocationBuffer locBuffer = _module.getVisualLocationBuffer();
    // IVisualActivationBuffer actBuffer = _module.getVisualActivationBuffer();
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
    IVisualLocationBuffer locBuffer = _module.getVisualLocationBuffer();
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
    IVisualLocationBuffer locBuffer = _module.getVisualLocationBuffer();
    IDeclarativeModule decM = model.getDeclarativeModule();
    IChunk error = decM.getErrorChunk();
    IChunk free = decM.getFreeChunk();
    IChunk visualLocation = error;
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

    if (/* searchResult != null && */searchResult.getLocation() != null)
      visualLocation = searchResult.getLocation();

    if (error.equals(visualLocation))
    {
      if (_isStuffRequest)
      {
        locBuffer.setStateChunk(free);
        locBuffer.setModalityChunk(free);
      }
      else
      {
        if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
        {
          String msg = String.format(
              "No valid visual location could be found matching request %s",
              request);
          LOGGER.debug(msg);
          Logger.log(model, Logger.Stream.VISUAL, msg);
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
            visualLocation, request, visualLocation
                .getMetaData(IPerceptualMemory.SEARCH_RESULT_IDENTIFIER_KEY));
        LOGGER.debug(msg);
        Logger.log(model, Logger.Stream.VISUAL, msg);
      }

      locBuffer.setStateChunk(free);
      locBuffer.setModalityChunk(free);

      locBuffer.addSourceChunk(visualLocation);

      /*
       * and handle the stuff flag
       */
      if (visualLocation != null) if (_isStuffRequest)
        locBuffer.setBufferChunk(decM.getUnrequestedChunk());
      else
        locBuffer.setBufferChunk(decM.getRequestedChunk());
    }

    locBuffer.setExecutionChunk(free);
    locBuffer.setProcessorChunk(free);
    locBuffer.setPreparationChunk(free);
  }

}
