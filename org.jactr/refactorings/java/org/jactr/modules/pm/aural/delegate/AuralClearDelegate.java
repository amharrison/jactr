package org.jactr.modules.pm.aural.delegate;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.module.asynch.delegate.AbstractAsynchronousModuleDelegate;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.aural.AbstractAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.visual.AbstractVisualModule;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;

public class AuralClearDelegate extends
    AbstractAsynchronousModuleDelegate<AbstractAuralModule, IChunk>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AuralClearDelegate.class);

  public AuralClearDelegate(AbstractAuralModule module,
      double minimumProcessingTime, IChunk cantProcessResult)
  {
    super(module, minimumProcessingTime, cantProcessResult);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected double computeHarvestTime(IRequest request, IChunk result,
      double startTime, Object... parameters)
  {
    return 0.05;
  }

  @Override
  protected void finalizeProcessing(IRequest request, IChunk result,
      Object... parameters)
  {
    /*
     * actually clear the buffer
     */
    AbstractAuralModule module = getModule();
    IAuralActivationBuffer vBuffer = module.getAuralBuffer();
    IAuralLocationBuffer lBuffer = module.getAuralLocationBuffer();

    vBuffer.clear();
    lBuffer.clear();

  }

  @Override
  protected IChunk processInternal(IRequest request, Object... parameters)
  {
    AbstractAuralModule module = getModule();

    module.getAudicon().clear();

    return null;
  }

  @Override
  protected boolean shouldProcess(IRequest request, Object... parameters)
  {
//    AbstractAuralModule module = getModule();
//    IAuralActivationBuffer vBuffer = module.getAuralBuffer();
//    IAuralLocationBuffer lBuffer = module.getAuralLocationBuffer();
//
//    if (lBuffer.isStateBusy())
//    {
//      String message = "Cannot clear aural module while aural-location state is busy";
//      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
//      Logger.log(module.getModel(), Logger.Stream.AURAL, message);
//      return false;
//    }
//
//    if (vBuffer.isStateBusy())
//    {
//      String message = "Cannot clear aural module while aural state is busy";
//      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
//      Logger.log(module.getModel(), Logger.Stream.AURAL, message);
//      return false;
//    }

    return true;
  }

}
