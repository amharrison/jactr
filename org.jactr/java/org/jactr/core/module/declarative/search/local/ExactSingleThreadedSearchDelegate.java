package org.jactr.core.module.declarative.search.local;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.search.filter.DelegatedFilter;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.module.declarative.search.filter.SlotFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.SkipListSetFactory;

/**
 * default single threaded search algorithm.
 * 
 * @author harrison
 */
public class ExactSingleThreadedSearchDelegate implements ISearchDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
                                                           .getLog(ExactSingleThreadedSearchDelegate.class);

  protected final boolean            _enableNotFilters = Boolean
                                                           .getBoolean("jactr.search.enableNotFilters");

  /**
   * will do all the filter processing, but not actually swap out the filter for
   * the search. this tests the overhead of building the filters.
   */
  protected final boolean            _testNotFilter    = Boolean
                                                           .getBoolean("jactr.search.testNotFilters");

  public ExactSingleThreadedSearchDelegate()
  {

  }

  @Override
  public SortedSet<IChunk> find(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter,
      DefaultSearchSystem searchSystem)
  {
    /*
     * second pass, ditch all those that don't match our chunktype
     */
    SortedSet<IChunk> candidates = SkipListSetFactory
        .newInstance(searchSystem._chunkNameComparator);
    IChunkType chunkType = pattern.getChunkType();

    /*
     * we optimize the following slot based searches by first sorting the slots
     * by an estimate of the result set size. This allows us to process the
     * smallest first, allowing us to bail early without processing everything.
     * We also support the conversion of not's (when possible) to filters, which
     * is often cheaper since not's are expensive in terms of large set
     * operations.
     */
    List<ISlot> sortedSlots = null;
    Collection<? extends ISlot> originalSlots = pattern
        .getConditionalAndLogicalSlots();
    IChunkFilter primaryFilter = searchSystem._defaultFilter;
    if (_enableNotFilters || _testNotFilter)
    {
      sortedSlots = new ArrayList<ISlot>(originalSlots.size());
      primaryFilter = sortPattern(chunkType, originalSlots, sortedSlots,
          searchSystem);
    }
    else
      sortedSlots = sortPatternOriginal(chunkType, originalSlots, searchSystem);

    /*
     * first things first, find all the candidates based on the content of the
     * pattern. We sort the slots based on the estimated size of the returned
     * set, then execute them. This lets us keep our candidate size down, which
     * reduces the time cost of retainAll operations.
     */
    boolean first = candidates.size() == 0;
    for (ISlot slot : sortedSlots)
    {
      Collection<IChunk> localResults = searchSystem.find(chunkType, slot,
          candidates);

      if (first)
      {
        searchSystem.cleanAddAll(candidates, localResults);
        first = false;
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Populating results from %s = %s", slot,
              localResults));
      }
      else
      {
        searchSystem.cleanRetainAll(candidates, localResults);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Retained results from %s = %s", slot,
              localResults));
      }

      if (candidates.size() == 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Early eject due to empty candidate set"));
        break;
      }
    }

    /*
     * if there were no slots specified, we need to grab all chunks, preferrably
     * with the type constrained. We could do this before the slot search and
     * just add all the chunks as the candidate set - but the kills performance.
     * it's quicker to search and start with a small set.
     */
    if (sortedSlots.size() == 0)
      if (chunkType != null)
        candidates.addAll(chunkType.getSymbolicChunkType().getChunks());
      else
        try
        {
          // this is such a patholical case.
          candidates.addAll(searchSystem._module.getChunks().get());
          LOGGER
              .error(String
                  .format(
                      "Warning: empty search specifications (%s) require full DM traversal. Please revise",
                      pattern));
        }
        catch (Exception e)
        {
          LOGGER.error("Failed to fetch all chunks for null chunktype search ",
              e);
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

      IChunkFilter chunkFilter = filter == null ? searchSystem._defaultFilter
          : filter;

      SortedSet<IChunk> returnCandidates = SkipListSetFactory
          .newInstance(comparator);

      for (IChunk candidate : candidates)
        if (chunkType == null || candidate.isA(chunkType))
          if (primaryFilter.accept(candidate))
            if (chunkFilter.accept(candidate))
            // shouldn't we actually test this against the pattern, jsut to be
            // sure?
              returnCandidates.add(candidate);

      searchSystem.recycleCollection(candidates);
      candidates = returnCandidates;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  /**
   * sort the slots by the guessed size of the result set. This is only used by
   * findExact. We also convert not's into filters instead whereever possible
   * 
   * @param chunkType
   * @param originalSlots
   * @return
   */
  protected IChunkFilter sortPattern(IChunkType chunkType,
      Collection<? extends ISlot> originalSlots, List<ISlot> container,
      DefaultSearchSystem searchSystem)
  {
    // ArrayList<ISlot> sorted = new ArrayList<ISlot>(originalSlots);
    container.addAll(originalSlots);

    Map<ISlot, Long> sizeMap = new HashMap<ISlot, Long>();
    for (ISlot slot : originalSlots)
      sizeMap.put(slot, searchSystem.guessSize(chunkType, slot));

    // Collections.sort(sorted, new PatternComparator(sizeMap));
    Collections.sort(container, new PatternComparator(sizeMap));

    /*
     * after they are sorted, we could iterate over this set and if the first
     * slot isn't a not, we can turn all subsequent not's (conditional, not
     * logical) into filters instead.
     */
    boolean safeToFilter = false;
    ListIterator<ISlot> sItr = container.listIterator();
    DelegatedFilter notFilter = null;
    while (sItr.hasNext())
    {
      ISlot slot = sItr.next();
      if (slot instanceof IConditionalSlot)
      {
        IConditionalSlot cSlot = (IConditionalSlot) slot;
        if (cSlot.getCondition() == IConditionalSlot.NOT_EQUALS)
          if (safeToFilter)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(String.format("Converting %s to a filter", cSlot));

            if (!_testNotFilter)
            {
              if (notFilter == null) notFilter = new DelegatedFilter();

              notFilter.add(new SlotFilter(cSlot));
              sItr.remove();
            }
          }
          else if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("Cannot convert %s to filter", cSlot));
      }
      safeToFilter = true;
    }

    return notFilter == null ? searchSystem._defaultFilter : notFilter;
  }

  /**
   * sort the slots by the guessed size of the result set.
   * 
   * @param chunkType
   * @param slots
   * @return
   */
  protected List<ISlot> sortPatternOriginal(IChunkType chunkType,
      Collection<? extends ISlot> slots, DefaultSearchSystem searchSystem)
  {
    ArrayList<ISlot> sorted = new ArrayList<ISlot>(slots);

    Map<ISlot, Long> sizeMap = new HashMap<ISlot, Long>();
    for (ISlot slot : slots)
      sizeMap.put(slot, searchSystem.guessSize(chunkType, slot));

    Collections.sort(sorted, new PatternComparator(sizeMap));

    return sorted;
  }

}
