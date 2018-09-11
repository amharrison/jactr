package org.jactr.core.module.declarative.basic.chunk;

import java.util.ArrayList;
/*
 * default logging
 */
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

/**
 * utility class for chaining multiple configurators together.
 * 
 * @author harrison
 */
public class ChainedChunkConfigurator implements IChunkConfigurator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChainedChunkConfigurator.class);

  private List<IChunkConfigurator>   _configurators = new ArrayList<IChunkConfigurator>();

  public void configure(IChunk chunk)
  {
    for (IChunkConfigurator config : _configurators)
      config.configure(chunk);
  }

  public void add(IChunkConfigurator config)
  {
    _configurators.add(config);
  }

  public void remove(IChunkConfigurator config)
  {
    _configurators.remove(config);
  }

}
