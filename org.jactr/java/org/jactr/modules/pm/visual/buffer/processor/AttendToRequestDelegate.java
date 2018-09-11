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
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.DelayedBufferInsertionTimedEvent;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.event.IVisualModuleListener;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.modules.pm.visual.memory.VisualUtilities;

/**
 * attend-to and move-attention delegate that handles buffer states and routes
 * the request to the module
 * 
 * @author harrison
 */
public class AttendToRequestDelegate extends AbstractRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AttendToRequestDelegate.class);

  final private String               _locationSlotName;

  final private IVisualModule        _module;

  private ITimedEvent                _pendingHarvest;

  private PerceptualSearchResult     _searchResult;

  public AttendToRequestDelegate(IVisualModule module,
      IChunkType attendChunkType, String locationSlotName)
  {
    super(attendChunkType);
    _module = module;
    _chunkType = attendChunkType;
    _locationSlotName = locationSlotName;
    setAsynchronous(true);
    setUseBlockingTimedEvents(true);
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
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Encoded " + event.getChunk() + " releasing");
        // release block, if any
        release();
      }

      public void perceptIndexFound(IPerceptualMemoryModuleEvent event)
      {

      }

    }, ExecutorServices.INLINE_EXECUTOR);
  }

  /**
   * clear and possibly abort a pending encoding
   */
  @Override
  final public void clear()
  {
    super.clear();
    if (_pendingHarvest != null) _pendingHarvest.abort();
  }

  /**
   * returns a visual-location contained in the slot with the name matching the
   * constructor supplied name
   * 
   * @param request
   * @return
   */
  protected IChunk getVisualLocation(IRequest request)
  {
    /*
     * check to be sure the location slot is not null
     */
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    /*
     * figure out if this is a stuff request
     */
    List<ISlot> slots = FastListFactory.newInstance();
    try
    {
      ctr.getSlots(slots);

      for (ISlot slot : slots)
      {
        IConditionalSlot cSlot = (IConditionalSlot) slot;
        if (cSlot.getName().equalsIgnoreCase(_locationSlotName)
            && cSlot.getValue() instanceof IChunk
            && cSlot.getCondition() == IConditionalSlot.EQUALS)
          return (IChunk) cSlot.getValue();
      }
      return null;
    }
    finally
    {
      FastListFactory.recycle(slots);
    }
  }

  /**
   * make sure the requst is properly formatted and that the visual system is
   * free
   * 
   * @param request
   * @param buffer
   * @return
   * @throws IllegalArgumentException
   * @see org.jactr.core.buffer.delegate.AsynchronousRequestDelegate#isValid(org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer)
   */
  @Override
  final protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    boolean rtn = true;
    boolean setError = false;
    /*
     * check to see if we are busy or not..
     */
    IModel model = _module.getModel();
    if (isBusy(buffer))
    {
      String msg = "Visual system is currently busy, cannot shift attention";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
      rtn = false;
    }

    IChunk location = getVisualLocation(request);

    if (location == null)
    {
      String msg = _locationSlotName
          + " is null, no clue where to look. Ignoring request.";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
      rtn = false;
      setError = true;
    }

    if (!location.isA(_module.getVisualLocationChunkType()))
    {
      String msg = "Content of " + _locationSlotName + "(" + location
          + ") is not visual-location. Ignoring request.";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
      rtn = false;
      setError = true;
    }

    PerceptualSearchResult psr = VisualUtilities.getSearchResult(location,
        _module.getVisualMemory());
    if (psr == null)
    {
      String msg = String
          .format("No recent visual search associated with location %s",
          location);
      if (LOGGER.isDebugEnabled()) LOGGER.warn(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
      rtn=false;
      setError=true;
    }

    if (setError)
    {
      IChunk errorChunk = _module.getModel().getDeclarativeModule()
          .getErrorChunk();
      ((IVisualActivationBuffer) buffer).setStateChunk(errorChunk);
      ((IVisualActivationBuffer) buffer).setExecutionChunk(errorChunk);
    }

    return rtn;
  }

  // protected PerceptualSearchResult getMatchingResult(IChunk visualLocation)
  // {
  // PerceptualSearchResult searchResult = VisualUtilities.getSearchResult(
  // visualLocation, _module.getVisualMemory());
  //
  // IModel model = visualLocation.getModel();
  // if (searchResult == null)
  // {
  // String msg = String
  // .format("No recent visual search associated with location %s",
  // visualLocation);
  // if (LOGGER.isDebugEnabled()) LOGGER.warn(msg);
  // if (Logger.hasLoggers(model))
  // Logger.log(model, Logger.Stream.VISUAL, msg);
  // }
  //
  //
  // return searchResult;
  // }
  //
  /**
   * start the encoding request. first we remove the current contents, then
   * request the encoding. we return a Future<IChunk> for the finishRequest
   * method, allowing this to be processed asynchronously
   * 
   * @param request
   * @param buffer
   * @return
   * @see org.jactr.core.buffer.delegate.AsynchronousRequestDelegate#startRequest(org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer, double)
   */
  @Override
  final protected Object startRequest(IRequest request,
      IActivationBuffer buffer, double requestTime)
  {
    IChunk location = getVisualLocation(request);
    _searchResult = VisualUtilities.getSearchResult(location,
        _module.getVisualMemory());

    /*
     * clear
     */
    buffer.removeSourceChunk(buffer.getSourceChunk());

    /*
     * flag as busy
     */
    IVisualActivationBuffer actBuffer = _module.getVisualActivationBuffer();
    IChunk busy = _module.getModel().getDeclarativeModule().getBusyChunk();
    actBuffer.setStateChunk(busy);
    actBuffer.setModalityChunk(busy);
    actBuffer.setPreparationChunk(busy);
    actBuffer.setProcessorChunk(busy);
    actBuffer.setExecutionChunk(busy);

    return _module.attendTo(_searchResult, requestTime);
  }

  /**
   * abort the request and reset to free
   * 
   * @param request
   * @param buffer
   * @param startValue
   * @see org.jactr.core.buffer.delegate.AsynchronousRequestDelegate#abortRequest(org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer, java.lang.Object)
   */
  @Override
  final protected void abortRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    IVisualActivationBuffer actBuffer = _module.getVisualActivationBuffer();
    IChunk free = _module.getModel().getDeclarativeModule().getFreeChunk();
    actBuffer.setStateChunk(free);
    actBuffer.setModalityChunk(free);
    actBuffer.setPreparationChunk(free);
    actBuffer.setProcessorChunk(free);
    actBuffer.setExecutionChunk(free);
    _searchResult = null;
    super.abortRequest(request, buffer, startValue);
  }

  /**
   * handles the completion of the encoding, but we then need to post an
   * additional event to deal with making the encoded chunk available
   * 
   * @param request
   * @param buffer
   * @param startValue
   * @see org.jactr.core.buffer.delegate.AsynchronousRequestDelegate#finishRequest(org.jactr.core.production.request.IRequest,
   *      org.jactr.core.buffer.IActivationBuffer, java.lang.Object)
   */
  @SuppressWarnings("unchecked")
  @Override
  final protected void finishRequest(IRequest request,
      IActivationBuffer buffer, Object startValue)
  {
    IModel model = _module.getModel();
    final IChunk errorChunk = model.getDeclarativeModule().getErrorChunk();
    final IChunk freeChunk = model.getDeclarativeModule().getFreeChunk();
    IChunk visualChunk = errorChunk;

    try
    {
      visualChunk = ((Future<IChunk>) startValue).get();
    }
    catch (InterruptedException e)
    {
      return;
    }
    catch (Exception e)
    {
      LOGGER.error("Could not get future of encoding " + request, e);
      visualChunk = errorChunk;
    }

    /*
     * now we have the encoded chunk, but this is not the actual time to return
     * the result, we need to deal with the encoding time
     */
    final IChunk result = visualChunk;
    final ChunkTypeRequest ctRequest = (ChunkTypeRequest) request;
    double startTime = getCurrentTimedEvent().getStartTime();
    double encodingTime = _module.getEncodingTimeEquation()
        .computeEncodingTime(visualChunk, _module);

    _pendingHarvest = new DelayedBufferInsertionTimedEvent(buffer, visualChunk,
        startTime, startTime + encodingTime) {
      @Override
      public void fire(double currentTime)
      {
        _pendingHarvest = null;
        super.fire(currentTime);
        harvest(ctRequest, result, errorChunk, freeChunk);
      }

      @Override
      public void abort()
      {
        _pendingHarvest = null;
        super.abort();
        abortRequest(ctRequest, null, null);
      }
    };

    if (Logger.hasLoggers(model) || LOGGER.isDebugEnabled())
    {
      String msg = "Will encode " + result + " by "
          + (encodingTime + startTime);
      if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, msg);
    }

    _module.getModel().getTimedEventQueue().enqueue(_pendingHarvest);

  }

  /**
   * called when the actual harvest is to occur
   * 
   * @param request
   * @param visualChunk
   * @param errorChunk
   * @param freeChunk
   */
  private void harvest(ChunkTypeRequest request, IChunk visualChunk,
      IChunk errorChunk, IChunk freeChunk)
  {
    IModel model = _module.getModel();
    IVisualActivationBuffer buffer = _module.getVisualActivationBuffer();
    IChunk visualLocation = getVisualLocation(request);

    if (errorChunk.equals(visualChunk) || _searchResult == null)
    {
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        String msg = "No visual object could be encoded at " + visualLocation;
        LOGGER.debug(msg);
        Logger.log(model, Logger.Stream.VISUAL, msg);
      }

      buffer.setStateChunk(errorChunk);
      buffer.setModalityChunk(errorChunk);
      if (_searchResult != null)
        buffer.setErrorChunk(_searchResult.getErrorCode());
    }
    else
    {
      if (visualChunk.hasBeenDisposed() || !_searchResult.isValid())
      {
        /*
         * this can occur if the cached visual object is disposed of or
         * disappears before the encoding request finishes..
         */
        String msg = "Visual object has already been disposed, nothing left to encode ";

        if (!_searchResult.isValid())
          msg = "Visual object is no longer visible.";

        if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
        {
          LOGGER.debug(msg);
          Logger.log(model, Logger.Stream.VISUAL, msg);
        }

        buffer.setStateChunk(errorChunk);
        buffer.setExecutionChunk(errorChunk);
        if (_searchResult != null)
          buffer.setErrorChunk(_searchResult.getErrorCode());
        // but the difference is we add it anyway.
        buffer.addSourceChunk(visualChunk);
      }
      else
      {
        buffer.setStateChunk(freeChunk);
        buffer.setExecutionChunk(freeChunk);

        buffer.addSourceChunk(visualChunk);
      }

      // only no search result sets modality to error
      buffer.setModalityChunk(freeChunk);
    }

    buffer.setProcessorChunk(freeChunk);
    buffer.setPreparationChunk(freeChunk);

    if (_searchResult != null) _searchResult.invalidate();
    _searchResult = null;
  }

}
