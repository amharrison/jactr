package org.jactr.core.production.request;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.match.ChunkTypeMatchFailure;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;

public class ChunkTypeRequest extends SlotBasedRequest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkTypeRequest.class);

  private IChunkType                 _chunkType;

  public ChunkTypeRequest(IChunkType chunkType)
  {
    this(chunkType, Collections.EMPTY_LIST);
  }

  public ChunkTypeRequest(IChunkType chunkType,
      Collection<? extends ISlot> slots)
  {
    super(slots);
    _chunkType = chunkType;
  }

  @Override
  public ChunkTypeRequest clone()
  {
    return new ChunkTypeRequest(_chunkType, _slots);
  }

  public IChunkType getChunkType()
  {
    return _chunkType;
  }

  /**
   * same idea as {@link IRequest#bind(IModel, VariableBindings, boolean)}
   * 
   * @param testChunk
   * @param model
   * @param bindings
   * @return
   * @throws CannotMatchException
   */
  public int bind(IChunk testChunk, IModel model, VariableBindings bindings,
      boolean iterativeCall) throws CannotMatchException
  {
    IChunkType chunkType = getChunkType();

    if (chunkType != null && !testChunk.isA(chunkType))
      throw new CannotMatchException(new ChunkTypeMatchFailure(chunkType,
          testChunk));

    ISymbolicChunk sChunk = testChunk.getSymbolicChunk();

    return super.bind(model, sChunk.getName(), sChunk, bindings, iterativeCall);
  }

  /**
   * NOTE: this is hideous inefficient
   * 
   * @return
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    // TODO cache..
    StringBuilder sb = new StringBuilder("[");
    sb.append(_chunkType).append(":");
    for (ISlot slot : getConditionalAndLogicalSlots())
      sb.append(slot).append(" ");
    sb.append("]");
    return sb.toString();
  }
}
