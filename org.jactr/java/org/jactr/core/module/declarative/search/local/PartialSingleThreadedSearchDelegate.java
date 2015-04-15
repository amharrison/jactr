package org.jactr.core.module.declarative.search.local;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.search.filter.AcceptAllFilter;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.utils.collections.SkipListSetFactory;

/**
 * basic implementation of a hideously inefficient partial matching (but it is
 * by necessity)
 * 
 * @author harrison
 */
public class PartialSingleThreadedSearchDelegate implements ISearchDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PartialSingleThreadedSearchDelegate.class);

  public PartialSingleThreadedSearchDelegate()
  {

  }

  /**
   * partial matching
   */
  @Override
  public SortedSet<IChunk> find(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter,
      DefaultSearchSystem searchSystem)
  {

    Collection<IChunk> candidates = null;
    SortedSet<IChunk> returnCandidates = null;
    IChunkType chunkType = pattern.getChunkType();

    if (chunkType != null)
      candidates = chunkType.getSymbolicChunkType().getChunks();
    else
      try
      {
        candidates = searchSystem._module.getChunks().get();
      }
      catch (Exception e)
      {
        LOGGER
            .error("Failed to fetch all chunks for null chunktype search ", e);
      }

    if (candidates.size() != 0)
    {
      /*
       * we now need to deal with those that are actually the correct chunk
       * type. Iteration over the candidates doing an isA() test would be
       * O(candidates.size). candidates.retainAll(chunksOfType) is either
       * O(candidates.size)*O(log(chunksOfType.size) or
       * O(chunksOfType.size)*O(log(candidates.size)). Until the Fast
       * collections come out with their predicate iterators, we will just
       * iterate raw. And use the opportunity to filter and sort.
       */
      Comparator<IChunk> comparator = searchSystem._chunkNameComparator;
      if (sortRule != null) comparator = sortRule;

      IChunkFilter chunkFilter = filter == null ? new AcceptAllFilter()
          : filter;

      returnCandidates = SkipListSetFactory.newInstance(comparator);

      for (IChunk candidate : candidates)
        if (chunkType == null || candidate.isA(chunkType))
          if (chunkFilter.accept(candidate)) returnCandidates.add(candidate);

      searchSystem.recycleCollection(candidates);
    }
    else
      returnCandidates = new TreeSet<IChunk>();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + returnCandidates);

    return returnCandidates;

  }

}
