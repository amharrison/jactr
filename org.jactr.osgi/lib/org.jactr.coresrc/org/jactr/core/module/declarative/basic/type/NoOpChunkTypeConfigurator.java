package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;

public class NoOpChunkTypeConfigurator implements IChunkTypeConfigurator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NoOpChunkTypeConfigurator.class);

  public void configure(IChunkType chunk)
  {

  }

}
