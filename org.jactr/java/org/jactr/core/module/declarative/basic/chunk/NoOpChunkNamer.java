package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

public class NoOpChunkNamer implements IChunkNamer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NoOpChunkNamer.class);

  public String generateName(IChunk chunk)
  {
    return chunk.getSymbolicChunk().getName();
  }

}
