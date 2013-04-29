package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.IllegalChunkTypeStateException;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

/**
 * takes a chunk type request creates a chunk, before inserting into the buffer
 * via {@link IActivationBuffer#addSourceChunk(org.jactr.core.chunk.IChunk)}.
 * 
 * @author harrison
 */
public class AddChunkTypeRequestDelegate extends AsynchronousRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AddChunkTypeRequestDelegate.class);

  
  
  
  public boolean willAccept(IRequest request)
  {
    return request instanceof ChunkTypeRequest;
  }
  
  
  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer) throws IllegalArgumentException
  {
    if(!(request instanceof ChunkTypeRequest))
      throw new IllegalArgumentException("request must be ChunkTypeRequest");
    
    ChunkTypeRequest ctRequest = (ChunkTypeRequest) request;
    IChunkType chunkType = ctRequest.getChunkType();
    ISymbolicChunkType sChunkType = chunkType.getSymbolicChunkType();
    Collection<? extends ISlot> slots = ctRequest.getSlots();
    for (ISlot slot : slots)
    {
      boolean valid = false;
      try
      {
        valid = null != sChunkType.getSlot(slot.getName());
      }
      catch (IllegalChunkTypeStateException icse)
      {
      }

      if (!valid)
        throw new IllegalArgumentException("No slot named " + slot.getName()
            + " available in " + chunkType);
    }
    
    return true;
  }

  
  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    IChunkType chunkType = ((ChunkTypeRequest)request).getChunkType();
    return buffer.getModel().getDeclarativeModule().createChunk(chunkType,
          null);
  }
  
  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    IChunk newChunk = null;
    try
    {
      newChunk = ((Future<IChunk>)startValue).get();
    }
    catch(Exception e)
    {
      LOGGER.error("Failed to get chunk from future reference. Request:"
          + request + " Buffer:" + buffer + " Start:" + startValue, e);
      
      //bail out, shop probably do some model logging
      return;
    }
    
    /*
     * set the values...
     */
    ISymbolicChunk sChunk = newChunk.getSymbolicChunk();
    for (ISlot slot : ((ChunkTypeRequest)request).getSlots())
      ((IMutableSlot) sChunk.getSlot(slot.getName())).setValue(slot.getValue());

    /*
     * and add to the buffer..
     */
    buffer.addSourceChunk(newChunk);
  }


 
}
