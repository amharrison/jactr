package org.jactr.modules.pm.visual.timedevent;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;

public class ReencodingTimedEvent extends AbstractTimedEvent implements
    IBufferBasedTimedEvent
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ReencodingTimedEvent.class);

  IChunk                             _chunk;

  IVisualActivationBuffer            _buffer;

  public ReencodingTimedEvent(IVisualActivationBuffer buffer,
      IChunk visualChunk, double reencodingTime)
  {
    IModel model = buffer.getModel();
    double now = ACTRRuntime.getRuntime().getClock(model).getTime();
    setTimes(now, now + reencodingTime);
    _chunk = visualChunk;
    _buffer = buffer;

    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.VISUAL, _chunk
          + " has changed, reencoding will complete at " + getEndTime());

    /*
     * set execution state to busy..
     */
    IChunk busy = model.getDeclarativeModule().getBusyChunk();
    buffer.setExecutionChunk(busy);
  }

  public void fire(double currentTime)
  {
    super.fire(currentTime);
    IChunk free = _buffer.getModel().getDeclarativeModule().getFreeChunk();
    _buffer.setExecutionChunk(free);
  }

  public IChunk getBoundChunk()
  {
    return _chunk;
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

}
