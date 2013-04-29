
package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;

public class NoOpChunkTypeNamer implements IChunkTypeNamer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NoOpChunkTypeNamer.class);

  public String generateName(IChunkType chunk)
  {
    return chunk.getSymbolicChunkType().getName();
  }

}
