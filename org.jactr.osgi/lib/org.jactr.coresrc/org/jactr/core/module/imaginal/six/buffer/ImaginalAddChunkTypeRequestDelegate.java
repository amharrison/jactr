package org.jactr.core.module.imaginal.six.buffer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.buffer.delegate.AddChunkTypeRequestDelegate;
import org.jactr.core.buffer.six.AbstractActivationBuffer6;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.imaginal.IImaginalModule;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * extends the {@link AddChunkRequestDelegate} to include to handling of
 * {@link IImaginalModule#getAddDelayTime()}
 * 
 * @author harrison
 */
public class ImaginalAddChunkTypeRequestDelegate extends
    AddChunkTypeRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ImaginalAddChunkTypeRequestDelegate.class);

  public ImaginalAddChunkTypeRequestDelegate()
  {
    super();
    setAsynchronous(true);
    setUseBlockingTimedEvents(false);
  }
  
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
  {
    if (isBusy(buffer))
    {
      IModel model = buffer.getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.IMAGINAL,
            "Ignoring request since currently busy :  " + request);
      return false;
    }
    
    return super.isValid(request, buffer);
  }

  protected double computeCompletionTime(double startTime, IRequest request,
      IActivationBuffer buffer)
  {
    IImaginalModule imaginal = (IImaginalModule) buffer.getModule();
    double rtn = super.computeCompletionTime(startTime, request, buffer);

    if (imaginal.getAddDelayTime() > 0 || imaginal.isRandomizeDelaysEnabled())
      rtn = ImaginalAddChunkRequestDelegate.computeTime(imaginal, startTime);


    IModel model = buffer.getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.IMAGINAL, "Will add "
          + ((ChunkTypeRequest) request).getChunkType() + " at " + rtn);

    return rtn;
  }

  protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    Object rtn = super.startRequest(request, buffer, requestTime);
    // clear the current
    buffer.removeSourceChunk(buffer.getSourceChunk());

    setBusy(buffer);
    
    return rtn;
  }

  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {

    super.finishRequest(request, buffer, startValue);
  }
}
