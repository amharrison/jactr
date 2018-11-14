package org.jactr.core.production.request;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.ISlot;

public class ChunkRequest extends ChunkTypeRequest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkRequest.class);
  
  private IChunk _chunk;
  
  public ChunkRequest(IChunk chunk)
  {
    this(chunk, Collections.EMPTY_LIST);
    _chunk = chunk;
  }

  public ChunkRequest(IChunk chunk, Collection<? extends ISlot> slots)
  {
    super(chunk.getSymbolicChunk().getChunkType(), slots);
    _chunk = chunk;
  }
  
  public IChunk getChunk()
  {
    return _chunk;
  }

  @Override
  public boolean matches(IChunk reference)
  {
    return _chunk.equalsSymbolic(reference) && super.matches(reference);
  }
}
