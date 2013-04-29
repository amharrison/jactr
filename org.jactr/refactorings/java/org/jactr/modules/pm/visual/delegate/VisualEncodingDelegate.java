package org.jactr.modules.pm.visual.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.asynch.delegate.BasicAsynchronousModuleDelegate;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.visual.AbstractVisualModule;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.modules.pm.visual.memory.impl.encoder.AbstractVisualEncoder;

public class VisualEncodingDelegate extends
    BasicAsynchronousModuleDelegate<AbstractVisualModule, IChunk>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisualEncodingDelegate.class);

  public VisualEncodingDelegate(AbstractVisualModule module)
  {
    super(module, module.getErrorChunk());
  }

  /**
   * returns the contents of the perceptual search
   * 
   * @param searchResult
   * @param visualModule
   * @param streamName
   * @return
   */
  public IChunk getSearchResult(PerceptualSearchResult searchResult,
      IVisualModule visualModule, String streamName)
  {
    IModel model = visualModule.getModel();
    IChunk errorChunk = model.getDeclarativeModule().getErrorChunk();
    IChunk visualLocation = searchResult.getLocation();
    IChunk visualObject = searchResult.getPercept();

    if (visualObject == null || visualObject.hasBeenDisposed())
    {
      // object has moved too far..
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        String msg = "Result of search found at " + visualLocation
            + " has changed too much since search occured, returning error";

        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
        if (Logger.hasLoggers(model) && streamName != null)
          Logger.log(model, streamName, msg);
      }
      return errorChunk;
    }

    /*
     * this shouldnt occur. the id should be set as long as the visual object
     * has not been disposed or encoded (removed from the visual buffer)
     */
    IIdentifier afferentId = searchResult.getPerceptIdentifier();
    if (afferentId == null)
    {
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        String msg = "Result of search found at " + visualLocation
            + " has no identifier associated with it, returning error";
        if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
        if (Logger.hasLoggers(model) && streamName != null)
          Logger.log(model, streamName, msg);
      }
      return errorChunk;
    }

    /*
     * if stick and the last search is the same as this result, we don't check
     * movement tolerance
     */
    IVisualMemory visualMemory = visualModule.getVisualMemory();
    if (!(visualMemory.isStickyAttentionEnabled() && visualMemory
        .getLastSearchResult() == searchResult))
    {
      /*
       * has it exceeded movement tolerance?
       */
      IChunk currentLocation = AbstractVisualEncoder.getVisualLocation(
          visualObject, visualModule.getVisualMemory());
      if (currentLocation == null
          || AbstractVisualEncoder.exceedsMovementTolerance(visualLocation,
              currentLocation, visualModule.getVisualMemory()))
      {
        if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
        {
          String msg = visualObject + " found at " + visualLocation
              + " is now at " + currentLocation
              + ", which exceeds movement tolerances, returning error";
          if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
          if (Logger.hasLoggers(model) && streamName != null)
            Logger.log(model, streamName, msg);
        }
        return errorChunk;
      }
    }

    return visualObject;
  }

  @Override
  protected IChunk processInternal(IRequest request, double requestTime,
      Object... parameters)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Requesting attending " + request + " @ " + requestTime
          + " with " + getModule().getVisualMemory().getPendingUpdates()
          + " pending updates");

    PerceptualSearchResult searchResult = (PerceptualSearchResult) parameters[0];

    return getSearchResult(searchResult, getModule(), Logger.Stream.VISUAL
        .toString());
  }

  @Override
  protected void processInternalCompleted(IRequest request, IChunk visualChunk,
      Object... parameters)
  {
    AbstractVisualModule module = getModule();

    if (module.hasListeners())
      module.dispatch(new VisualModuleEvent(module,
          VisualModuleEvent.Type.ENCODED, visualChunk));
  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
    AbstractVisualModule module = getModule();
    IModel model = module.getModel();
    PerceptualSearchResult searchResult = (PerceptualSearchResult) parameters[0];

    /*
     * make sure vis loc is valid
     */
    if (searchResult == null)
    {
      String message = "Cannot encode without first having performed a search";
      if (LOGGER.isWarnEnabled()) LOGGER.warn(message);

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, message);

      return false;
    }

    /*
     * make sure vis loc is valid
     */
    if (!searchResult.getLocation().isA(module.getVisualLocationChunkType()))
    {
      String message = "An invalid move-attention was used, screen-position does not contain a visual-location chunk ("
          + searchResult.getLocation() + ")";
      if (LOGGER.isWarnEnabled()) LOGGER.warn(message);

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, message);

      return false;
    }

    return true;
  }

}
