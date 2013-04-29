package org.jactr.core.module.imaginal.six.buffer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AsynchronousRequestDelegate;
import org.jactr.core.buffer.six.AbstractActivationBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IllegalChunkTypeStateException;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.imaginal.IImaginalModule;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

public class ImaginalSlotRequestDelegate extends AsynchronousRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ImaginalSlotRequestDelegate.class);


  public ImaginalSlotRequestDelegate()
  {
    super();
    setAsynchronous(true);
    setUseBlockingTimedEvents(false);
  }
  
  public boolean willAccept(IRequest request)
  {
    return request instanceof SlotBasedRequest;
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    if (isBusy(buffer))
    {
      IModel model = buffer.getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.IMAGINAL,
            "Ignoring request since currently busy :  " + request);
      return false;
    }

    IChunk chunk = buffer.getSourceChunk();

    if (chunk == null)
      throw new IllegalArgumentException("No chunk in " + buffer.getName());

    ISymbolicChunk sc = chunk.getSymbolicChunk();
    for (ISlot slot : ((SlotBasedRequest) request).getSlots())
    {
      boolean valid = false;
      try
      {
        valid = null != sc.getSlot(slot.getName());
      }
      catch (IllegalChunkTypeStateException icse)
      {
        valid = false;
      }

      if (!valid)
        throw new IllegalArgumentException("No slot named " + slot.getName()
            + " available in " + chunk);
    }

    return true;
  }

  protected double computeCompletionTime(double startTime, IRequest request,
      IActivationBuffer buffer)
  {
    IImaginalModule imaginal = (IImaginalModule) buffer.getModule();
    double rtn = super.computeCompletionTime(startTime, request, buffer);

    if (imaginal.getAddDelayTime() > 0 || imaginal.isRandomizeDelaysEnabled())
      rtn = computeTime(imaginal, startTime);

    IModel model = buffer.getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.IMAGINAL, "Will modify "
          + buffer.getSourceChunk() + " at " + rtn);

    return rtn;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    setBusy(buffer);
    return null;
  }

  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    IChunk chunk = buffer.getSourceChunk();
    if (chunk == null) return;
    ISymbolicChunk sc = chunk.getSymbolicChunk();
    for (IConditionalSlot slot : ((SlotBasedRequest) request)
        .getConditionalSlots())
      if (slot.getCondition() == IConditionalSlot.EQUALS)
        ((IMutableSlot) sc.getSlot(slot.getName())).setValue(slot.getValue());

    flagAsFree(buffer);
  }

  private void flagAsFree(IActivationBuffer buffer)
  {
    AbstractActivationBuffer6 b = (AbstractActivationBuffer6) buffer;
    b.setStateChunk(b.getFreeChunk());
  }

  private double computeTime(IImaginalModule imaginal, double currentTime)
  {
    double time = imaginal.getModifyDelayTime() + currentTime;
    if (!imaginal.isRandomizeDelaysEnabled())
      return time;
    else
    {
      IRandomModule random = (IRandomModule) imaginal.getModel().getModule(
          IRandomModule.class);
      if (random == null)
        return time;
      else
        return random.randomizedTime(time);
    }
  }

}
