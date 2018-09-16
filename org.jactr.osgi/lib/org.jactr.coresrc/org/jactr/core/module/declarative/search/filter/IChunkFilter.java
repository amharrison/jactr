package org.jactr.core.module.declarative.search.filter;

import org.jactr.core.chunk.IChunk;

/*
 * default logging
 */

/**
 * filter function for search results.
 * 
 * @author harrison
 */
@FunctionalInterface
public interface IChunkFilter
{

  /**
   * return true if this chunk should be included in the search results.
   * 
   * @param chunk
   * @return
   */
  public boolean accept(IChunk chunk);
}
