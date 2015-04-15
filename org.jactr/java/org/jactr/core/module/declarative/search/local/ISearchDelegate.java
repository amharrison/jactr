package org.jactr.core.module.declarative.search.local;

import java.util.Comparator;
import java.util.SortedSet;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.production.request.ChunkTypeRequest;

/*
 * default logging
 */

/**
 * a local interface for the creation of delegate code to implement different
 * forms of search within the DefaultSearchSystem. This is not intended to be
 * implemented by others.
 * 
 * @author harrison
 */
public interface ISearchDelegate
{

  /**
   * search for something that matches the pattern, filtering out, and sorted
   * 
   * @param pattern
   * @param sortRule
   *          sort by this comparator
   * @param filter
   *          exclude chunks that don't pass this
   * @param searchSystem
   * @return
   */
  public SortedSet<IChunk> find(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter,
      DefaultSearchSystem searchSystem);
}
