package org.jactr.core.module.declarative;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;

/*
 * default logging
 */

public interface IRemovableDeclarativeModule extends IDeclarativeModule
{

  /**
   * remove chunk from long term memory. This is an optional operation and given
   * the complexities of DM, it's contract is limited. After calling this, the
   * named chunk will no longer be accessible via {@link #getChunk(String)},
   * {@link #findExactMatches(org.jactr.core.production.request.ChunkTypeRequest, java.util.Comparator, IChunkFilter)}
   * , or {@link ISymbolicChunkType#getChunks()}. Any further references to the
   * chunk (e.g. by other chunks) may or may not be replaced by tombstones.
   * 
   * @param chunk
   */
  public void removeChunk(IChunk chunk);
}
