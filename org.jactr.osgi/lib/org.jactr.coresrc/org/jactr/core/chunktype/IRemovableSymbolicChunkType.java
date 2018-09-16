package org.jactr.core.chunktype;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.declarative.IRemovableDeclarativeModule;

/*
 * default logging
 */

/**
 * an optional symbolic chunk type that permits the modification of its encoded
 * state. This is primarily to support {@link IRemovableDeclarativeModule}
 * 
 * @author harrison
 */
public interface IRemovableSymbolicChunkType extends ISymbolicChunkType
{

  /**
   * @param chunk
   */
  public void removeChunk(IChunk chunk);

  public void removeChild(IChunkType chunkType);

  public void removeParent(IChunkType chunkType);
}
