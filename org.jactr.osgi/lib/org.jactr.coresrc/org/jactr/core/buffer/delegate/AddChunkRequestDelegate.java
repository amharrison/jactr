package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

/**
 * takes a chunk pattern and if the chunk is already encoded, copies it, before
 * inserting into the buffer via
 * {@link IActivationBuffer#addSourceChunk(org.jactr.core.chunk.IChunk)}. If the
 * chunk is encoded and there are slots to be modified, the chunk will
 * automatically be copied.<br/>
 * <br/>
 * This is used when you have a buffer that accepts new chunk insertions
 * directly and immediately (like goal). As opposed to a delayed request that
 * takes some amount of time (beyond the current cycle).
 * 
 * @author harrison
 */
public class AddChunkRequestDelegate extends AsynchronousRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER             = LogFactory
                                                            .getLog(AddChunkRequestDelegate.class);

  private boolean                    _copyEncodedChunks = true;

  private boolean                    _delayCopies       = true;

  /**
   * default is to copy encoded chunks
   */
  public AddChunkRequestDelegate()
  {
    this(true);
  }

  public AddChunkRequestDelegate(boolean copyEncodedChunks)
      throws IllegalArgumentException
  {
    _copyEncodedChunks = copyEncodedChunks;
  }

  public boolean willAccept(IRequest request)
  {
    return request instanceof ChunkRequest;
  }

  public void setDelayCopiesEnabled(boolean enable)
  {
    _delayCopies = enable;
  }

  public boolean shouldDelayCopies()
  {
    return _delayCopies;
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    if (!(request instanceof ChunkRequest))
      throw new IllegalArgumentException("Request must be ChunkRequest");

    ChunkRequest cRequest = (ChunkRequest) request;
    IChunk chunk = cRequest.getChunk();
    ISymbolicChunk sChunk = chunk.getSymbolicChunk();
    Collection<? extends ISlot> slots = cRequest.getSlots();
    for (ISlot slot : slots)
    {
      boolean valid = false;
      try
      {
        valid = null != sChunk.getSlot(slot.getName());
      }
      catch (IllegalChunkStateException icse)
      {
      }

      if (!valid)
        throw new IllegalArgumentException("No slot named " + slot.getName()
            + " available in " + chunk);
    }

    return true;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    ChunkRequest cRequest = (ChunkRequest) request;
    IChunk originalChunk = cRequest.getChunk();
    Future<IChunk> copiedChunk = null;

    if (shouldCopy(originalChunk))
    {
      if (shouldDelayCopies()) return null;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("copying %s", originalChunk));
      copiedChunk = buffer.getModel().getDeclarativeModule()
          .copyChunk(originalChunk);
    }
    else if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Using original %s", originalChunk));

    if (copiedChunk != null) return copiedChunk;

    return CompletableFuture.completedFuture(originalChunk);
  }

  protected boolean shouldCopy(IChunk chunk)
  {
    if (!_copyEncodedChunks) return false;

    if (chunk.isEncoded()) return true;

    Collection<IActivationBuffer> buffers = BufferUtilities
        .getContainingBuffers(chunk, true);

    // more than (possibly this) buffer contains this chunk, should copy
    if (buffers.size() > 1) return true;

    // the containing buffer isnt us
    if (buffers.size() == 1 && !buffers.contains(this)) return true;

    return false;
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    ChunkRequest cRequest = (ChunkRequest) request;
    CompletableFuture<IChunk> future = null;

    // startValue is either a completableFuture, or null.
    if (startValue == null)
    {
      IChunk original = cRequest.getChunk();
      // null value, we are doing a delayed copy
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("delayed copy %s", original));
      future = buffer.getModel().getDeclarativeModule().copyChunk(original);
    }
    else
      future = (CompletableFuture<IChunk>) startValue;

    future.thenAccept((c) -> {
      modifyChunk(c, cRequest);
      buffer.addSourceChunk(c);
    });
  }

  protected void modifyChunk(IChunk toAdd, ChunkRequest request)
  {
    Collection<? extends ISlot> slots = request.getSlots();
    try
    {
      toAdd.getWriteLock().lock();

      ISymbolicChunk sChunk = toAdd.getSymbolicChunk();
      for (ISlot slot : slots)
        ((IMutableSlot) sChunk.getSlot(slot.getName())).setValue(slot
            .getValue());
    }
    finally
    {
      toAdd.getWriteLock().unlock();
    }
  }

}
