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
import org.jactr.core.module.asynch.delegate.BasicAsynchronousModuleDelegate;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.event.AuralModuleEvent;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;

public class AuralAttendingDelegate extends
    BasicAsynchronousModuleDelegate<AbstractAuralModule, IChunk>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralAttendingDelegate.class);

  public AuralAttendingDelegate(AbstractAuralModule module)
  {
    super(module, module.getErrorChunk());
  }

  /**
   * returns the contents of the perceptual search
   * 
   * @param searchResult
   * @param auralModule
   * @param streamName
   * @return
   */
  public IChunk getSearchResult(PerceptualSearchResult searchResult,
      IAuralModule auralModule, String streamName)
  {
    IModel model = auralModule.getModel();
    IChunk errorChunk = model.getDeclarativeModule().getErrorChunk();
    IChunk audioEvent = searchResult.getLocation();
    IChunk auralPercept = searchResult.getPercept();

    if (auralPercept == null || auralPercept.hasBeenDisposed())
    {
      // object has moved too far..
      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        String msg = "Result of search found at " + audioEvent
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
        String msg = "Result of search found at " + audioEvent
            + " has no identifier associated with it, returning error";
        if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);
        if (Logger.hasLoggers(model) && streamName != null)
          Logger.log(model, streamName, msg);
      }
      return errorChunk;
    }



    return auralPercept;
  }

  @Override
  protected IChunk processInternal(IRequest request, double requestTime,
      Object... parameters)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Requesting attending " + request + " @ " + requestTime
          + " with " + getModule().getAuralMemory().getPendingUpdates()
          + " pending updates");

    PerceptualSearchResult searchResult = (PerceptualSearchResult) parameters[0];

    return getSearchResult(searchResult, getModule(), Logger.Stream.AURAL
        .toString());
  }

  @Override
  protected void processInternalCompleted(IRequest request, IChunk visualChunk,
      Object... parameters)
  {
    AbstractAuralModule module = getModule();

    if (module.hasListeners())
      module.dispatch(new AuralModuleEvent(module,
          AuralModuleEvent.Type.ENCODED, visualChunk));
  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
    AbstractAuralModule module = getModule();
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
        Logger.log(model, Logger.Stream.AURAL, message);

      return false;
    }

    /*
     * make sure vis loc is valid
     */
    if (!searchResult.getLocation().isA(module.getAudioEventChunkType()))
    {
      String message = "An invalid move-attention was used, event does not contain a audio-event chunk ("
          + searchResult.getLocation() + ")";
      if (LOGGER.isWarnEnabled()) LOGGER.warn(message);

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.AURAL, message);

      return false;
    }

    return true;
  }

}
