package org.jactr.core.module.declarative.search.filter;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;

/**
 * allows all through
 * @author harrison
 *
 */
public class AcceptAllFilter implements IChunkFilter
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AcceptAllFilter.class);

  public boolean accept(IChunk chunk)
  {
    return true;
  }

}
