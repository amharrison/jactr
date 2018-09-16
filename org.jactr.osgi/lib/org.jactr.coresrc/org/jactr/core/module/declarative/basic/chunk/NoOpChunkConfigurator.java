package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

public class NoOpChunkConfigurator implements IChunkConfigurator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NoOpChunkConfigurator.class);

  public void configure(IChunk chunk)
  {

  }

}
