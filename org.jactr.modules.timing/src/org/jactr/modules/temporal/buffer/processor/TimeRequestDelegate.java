package org.jactr.modules.temporal.buffer.processor;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AsynchronousRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.temporal.ITemporalModule;

public class TimeRequestDelegate extends AsynchronousRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TimeRequestDelegate.class);

  private IChunkType                 _timeChunkType;

  public TimeRequestDelegate(IChunkType timeChunkType)
  {
    _timeChunkType = timeChunkType;
    // don't call finish until after 50 ms have passed
    setAsynchronous(true);
    // don't delay it..
    setDelayStart(false);
    setUseBlockingTimedEvents(false);
  }

  public boolean willAccept(IRequest request)
  {
    return request instanceof ChunkTypeRequest
        && ((ChunkTypeRequest) request).getChunkType().isA(_timeChunkType);
  }

  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    int initialTicks = (Integer) startValue;
    ((ITemporalModule) buffer.getModule()).startTimer(initialTicks);
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    return true;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    int initialTicks = 0;
    try
    {
      for (IConditionalSlot slot : ((ChunkTypeRequest) request).getConditionalSlots())
        if (slot.getName().equals("ticks")
            && slot.getCondition() == IConditionalSlot.EQUALS)
          initialTicks = ((Number) slot.getValue()).intValue();
    }
    catch (Exception e)
    {

    }

    return initialTicks;
  }
}
