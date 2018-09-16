package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;

public interface IChunkNamer
{
  public String generateName(IChunk chunk);
}
