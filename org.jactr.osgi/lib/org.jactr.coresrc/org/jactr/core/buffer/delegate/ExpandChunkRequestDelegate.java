package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.ISlot;

/**
 * takes a chunk request and automatically expands it to the chunktype request
 * and resubmits it. This is a simple way to prevent modelers from trying to
 * directly add a chunk.
 * 
 * @author harrison
 */
public class ExpandChunkRequestDelegate implements IRequestDelegate
{

  private boolean _includeNull = false;

  public ExpandChunkRequestDelegate(boolean includeNullValues)
  {
    _includeNull = includeNullValues;
  }

  public boolean willAccept(IRequest request)
  {
    return request instanceof ChunkRequest;
  }

  public boolean request(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    if (request instanceof ChunkRequest)
    {
      ChunkRequest cRequest = (ChunkRequest) request;

      ChunkTypeRequest ctr = new ChunkTypeRequest(cRequest.getChunkType());
      
      for (ISlot slot : cRequest.getChunk().getSymbolicChunk().getSlots())
        if (slot.getValue() != null || _includeNull) ctr.addSlot(slot);

      for (ISlot slot : cRequest.getSlots())
        ctr.addSlot(slot);

      return ((IDelegatedRequestableBuffer) buffer).request(ctr, requestTime);
    }

    return false;
  }
  
  public void clear()
  {
    //noop;
  }

}
