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
import org.jactr.core.slot.ISlot;

/**
 * request based on a chunktype and slot pattern. Predominantly used in
 * specifying retrievals, but also chunktype based request from modules.
 * 
 * @author harrison
 */
public class ChunkTypeRequest extends SlotBasedRequest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
      .getLog(ChunkTypeRequest.class);

  private IChunkType                 _chunkType;

  private int                        _hashCode = -1;

  @SuppressWarnings("unchecked")
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

  /**
   * create a request that directly mirrors this chunk. All of its slots are
   * turned directly into equality conditions.
   * 
   * @param chunk
   */
  public ChunkTypeRequest(IChunk chunk)
  {
    this(chunk.getSymbolicChunk().getChunkType(),
        chunk.getSymbolicChunk().getSlots());
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
      throw new CannotMatchException(
          new ChunkTypeMatchFailure(chunkType, testChunk));

    ISymbolicChunk sChunk = testChunk.getSymbolicChunk();

    return super.bind(model, sChunk.getName(), sChunk, bindings, iterativeCall);
  }

  @Override
  public boolean matches(IChunk reference)
  {
    return reference.isA(getChunkType()) && super.matches(reference);
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

  @Override
  public int hashCode()
  {
    if (_hashCode == -1)
      return computeHashCode();
    else
      return _hashCode;
  }

  public void lockHash()
  {
    _hashCode = computeHashCode();
  }

  public int computeHashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (_chunkType == null ? 0 : _chunkType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ChunkTypeRequest other = (ChunkTypeRequest) obj;
    if (_chunkType == null)
    {
      if (other._chunkType != null) return false;
    }
    else if (!_chunkType.equals(other._chunkType)) return false;
    return true;
  }

}
