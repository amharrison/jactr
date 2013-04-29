package org.jactr.modules.pm.visual.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.asynch.delegate.BasicAsynchronousModuleDelegate;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.visual.AbstractVisualModule;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.modules.pm.visual.memory.impl.DefaultVisualMemory;

public class VisualSearchDelegate
    extends
    BasicAsynchronousModuleDelegate<AbstractVisualModule, PerceptualSearchResult>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisualSearchDelegate.class);

  public VisualSearchDelegate(AbstractVisualModule module)
  {
    super(module, null);
  }

  @Override
  protected PerceptualSearchResult processInternal(IRequest request,
      double requestTime, Object... parameters)
  {
    /*
     * merely delegate to visual memory
     */
    DefaultVisualMemory memory = (DefaultVisualMemory) getModule()
        .getVisualMemory();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Requesting visual search " + request + " @ " + requestTime
          + " with " + memory.getPendingUpdates() + " pending updates");

    return memory.searchNow((ChunkTypeRequest) request);
  }

  @Override
  protected void processInternalCompleted(IRequest searchPattern,
      PerceptualSearchResult result, Object... parameters)
  {
    AbstractVisualModule module = getModule();
    boolean wasStuffRequest = (Boolean) parameters[0];
    IChunk location = module.getErrorChunk();

    if (result == null)
    {
      if (LOGGER.isDebugEnabled())
      {
        String msg = wasStuffRequest ? "Stuff failed to find anything"
            : String
                .format(
                    "No valid visual location could be found matching requested %s",
                    searchPattern);
        LOGGER.debug(msg);
      }
    }
    else
    {
      location = result.getLocation();
      if (LOGGER.isDebugEnabled())
      {
        String msg = "Found " + location + " matching " + searchPattern
            + ". Because of object " + result.getPerceptIdentifier();
        LOGGER.debug(msg);
      }
    }

    if (module.hasListeners())
      module.dispatch(new VisualModuleEvent(module, location, wasStuffRequest));
  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
    return request instanceof ChunkTypeRequest;
  }

}
