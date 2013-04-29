package org.jactr.modules.temporal.buffer.processor;

/*
 * default logging
 */
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.IRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.temporal.ITemporalModule;

public class ClearRequestDelegate implements IRequestDelegate
{

  private IChunkType                 _clearChunkType;

  public ClearRequestDelegate(IChunkType clearChunkType)
  {
    _clearChunkType = clearChunkType;
  }

  public boolean willAccept(IRequest request)
  {
    return (request instanceof ChunkTypeRequest) && 
      ((ChunkTypeRequest)request).getChunkType().isA(_clearChunkType);
  }

  public boolean request(IRequest request,
      IActivationBuffer buffer, double requestTime)
  {
    ((ITemporalModule) buffer.getModule()).reset();
    return true;
  }

  public void clear()
  {
    //noop
  }
}
