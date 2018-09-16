package org.jactr.core.module.declarative.search.local;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.declarative.search.filter.ChunkTypeFilter;
import org.jactr.core.module.declarative.search.filter.DelegatedFilter;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.module.declarative.search.filter.SlotFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.core.utils.collections.SkipListSetFactory;

/**
 * still not correct. chunkFilter needs to be applied last. We can probably
 * stick the chunkType filter in to the first pass too.
 * 
 * @author harrison
 */
public class ExactParallelSearchDelegate implements ISearchDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
                                                           .getLog(ExactParallelSearchDelegate.class);

  protected final boolean            _enableNotFilters = Boolean
                                                           .getBoolean("jactr.search.enableNotFilters");

  /**
   * will do all the filter processing, but not actually swap out the filter for
   * the search. this tests the overhead of building the filters.
   */
  protected final boolean            _testNotFilter    = Boolean
                                                           .getBoolean("jactr.search.testNotFilters");

  public ExactParallelSearchDelegate()
  {

  }

  @Override
  public SortedSet<IChunk> find(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter,
      final DefaultSearchSystem searchSystem)
  {
    return findNew(pattern, sortRule, filter, searchSystem);
  }

  /**
   * @param pattern
   * @param sortRule
   * @param filter
   * @param searchSystem
   * @return
   */
  public SortedSet<IChunk> findNew(final ChunkTypeRequest pattern,
      final Comparator<IChunk> sortRule, final IChunkFilter filter,
      final DefaultSearchSystem searchSystem)
  {
    IChunkType chunkType = pattern.getChunkType();

    /*
     * we can optimze the not searches by converting all nots to a filter,
     * assuming there is at least one non-not conditional or logical slot. If
     * thereare only two nots, the first will populate, the second will filter.
     */
    DelegatedFilter primaryFilter = new DelegatedFilter();

    List<ISlot> prioritizedSlots = null;
    Collection<? extends ISlot> originalSlots = pattern
        .getConditionalAndLogicalSlots();
    if (_enableNotFilters || _testNotFilter)
    {
      prioritizedSlots = new ArrayList<ISlot>(originalSlots.size());
      IChunkFilter tmpFilter = selectSlotsToSearch(chunkType, originalSlots,
          prioritizedSlots, searchSystem);
      // filter out the nots
      primaryFilter.add(tmpFilter);
    }
    else
      prioritizedSlots = new ArrayList<ISlot>(originalSlots);

    // filter by type
    primaryFilter.add(new ChunkTypeFilter(chunkType, false));

    /*
     * for each slot, we execute a completable future
     */
    ExecutorService pool = ExecutorServices.getExecutor(ExecutorServices.POOL);
    final List<CompletableFuture<Collection<IChunk>>> submittedSlotSearches = new ArrayList<>();

    for (ISlot slot : prioritizedSlots)
    {
      final ISlot fSlot = slot;

      /**
       * executed on pool. The problem with this is that
       * DefaultSearchSystem.find requires the candidate set for operations
       * requiring not isa. but should otherwise function correctly.
       */
      CompletableFuture<Collection<IChunk>> slotSearchResult = CompletableFuture
          .supplyAsync(
              () -> searchSystem.find(chunkType, fSlot, new HashSet<IChunk>()),
              pool);

      /*
       * before finishing, filter. This will apply any of the converted nots.
       */
      slotSearchResult = slotSearchResult
          .thenApply((c) -> filterSlotSearchResults(c, fSlot, filter));

      submittedSlotSearches.add(slotSearchResult);
    }

    /*
     * no searches were submitted? crap. Grab all of type or all.
     */
    if (submittedSlotSearches.size() == 0)
    {
      CompletableFuture<Collection<IChunk>> rawSearch = CompletableFuture
          .supplyAsync(() -> getAllChunks(chunkType, searchSystem), pool);

      /*
       * before finishing, filter. This will apply any of the converted nots.
       */
      rawSearch = rawSearch.thenApply((c) -> filterSlotSearchResults(c, null,
          filter));

      submittedSlotSearches.add(rawSearch);
    }

    /*
     * We now wait for them all to finish
     */
    CompletableFuture<Collection<Collection<IChunk>>> searchDone = sequence(submittedSlotSearches);

    /*
     * combine the results, which will also sort.
     */
    CompletableFuture<SortedSet<IChunk>> combineResults = searchDone
        .thenApply((c) -> combineResults(c, filter, searchSystem));

    try
    {
      SortedSet<IChunk> results = combineResults.get();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("First pass candidates %s = %s", pattern,
            results));

      return results;
    }
    catch (InterruptedException e)
    {
      LOGGER.warn("Interrupted, expecting termination ", e);
      return new TreeSet<IChunk>();
    }
    catch (ExecutionException e)
    {
      LOGGER.error(String.format(
          "Failed to collect parallel search results for %s", pattern), e);
      return new TreeSet<IChunk>();
    }
  }

  /**
   * perform set logic to all the slot search results, recycling interim
   * collections
   * 
   * @param slotSearchResults
   * @param searchSystem
   * @return
   */
  protected SortedSet<IChunk> combineResults(
      Collection<Collection<IChunk>> slotSearchResults,
      IChunkFilter chunkFilter, DefaultSearchSystem searchSystem)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Collapsing %d result sets",
          slotSearchResults.size()));

    SortedSet<IChunk> candidates = SkipListSetFactory
        .newInstance(searchSystem._chunkNameComparator);

    boolean first = true;
    for (Collection<IChunk> slotSearchResult : slotSearchResults)
    {
      if (first)
      {
        searchSystem.cleanAddAll(candidates, slotSearchResult);
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String.format("Populating from %s %s", slotSearchResult));
      }
      else
      {
        searchSystem.cleanRetainAll(candidates, slotSearchResult);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Retaining %s", slotSearchResult));
      }

      first = false;

      if (candidates.size() == 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Early exit due to empty candidate set"));
        break;
      }
    }
    return finalFilter(candidates, chunkFilter);
  }

  protected SortedSet<IChunk> finalFilter(SortedSet<IChunk> candidates,
      IChunkFilter chunkFilter)
  {

    /*
     * ideally we would have done this filtering above, but doing it here ensure
     * we only run this test once.We only run this after combining because this
     * filter is often a logged one.
     */
    Iterator<IChunk> cItr = candidates.iterator();
    while (cItr.hasNext())
    {
      IChunk chunk = cItr.next();
      if (!chunkFilter.accept(chunk)) cItr.remove();
    }

    return candidates;
  }

  /**
   * creates a filtered copy of the candidates. We must create a new copy
   * because the candidate set is likely an optimized, unmodifiable collection.
   * 
   * @param candidates
   * @param chunkFilter
   * @return
   */
  protected Collection<IChunk> filterSlotSearchResults(
      Collection<IChunk> candidates, ISlot slot, IChunkFilter primaryFilter)
  {
    @SuppressWarnings("unchecked")
    Collection<IChunk> rtn = FastCollectionFactory.newInstance();

    /**
     * this collection comes directly from DeafultSearchSystems.find() which is
     * not a collection for removing from..
     */
    Iterator<IChunk> itr = candidates.iterator();
    while (itr.hasNext())
    {
      IChunk chunk = itr.next();
      if (primaryFilter.accept(chunk))
      // if(chunkFilter.accept(chunk)) //this filter needs to be last.
        rtn.add(chunk);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Result set for %s = %s. From %d candidates.",
          slot, rtn, candidates.size()));

    return rtn;
  }

  protected Collection<IChunk> getAllChunks(IChunkType chunkType,
      DefaultSearchSystem searchSystem)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Having to fetch all chunks of type %s",
          chunkType));

    if (chunkType != null)
      return chunkType.getSymbolicChunkType().getChunks();
    else
      try
      {
        LOGGER
            .warn("Searches without even a chunktype specified are extremely expensive!");
        return searchSystem._module.getChunks().get();
      }
      catch (Exception e)
      {
        return Collections.EMPTY_LIST;
      }
  }

  /**
   * We also convert not's into filters instead whereever possible. At most
   * there will only one not slot and the rest converted. If there is any
   * non-not slot, all the nots can be converted.
   * 
   * @param chunkType
   * @param originalSlots
   * @return
   */
  protected IChunkFilter selectSlotsToSearch(IChunkType chunkType,
      Collection<? extends ISlot> originalSlots, List<ISlot> container,
      DefaultSearchSystem searchSystem)
  {
    // ArrayList<ISlot> sorted = new ArrayList<ISlot>(originalSlots);
    container.addAll(originalSlots);

    // in single, we sort by the guessed size of the result set.

    // Map<ISlot, Long> sizeMap = new HashMap<ISlot, Long>();
    // for (ISlot slot : originalSlots)
    // sizeMap.put(slot, searchSystem.guessSize(chunkType, slot));
    //
    // Collections.sort(container, new PatternComparator(sizeMap));

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

    return notFilter == null ? null : notFilter;
  }

  private static <T> CompletableFuture<Collection<T>> sequence(
      Collection<CompletableFuture<T>> futures)
  {
    CompletableFuture<Void> allDoneFuture = CompletableFuture.allOf(futures
        .toArray(new CompletableFuture[futures.size()]));
    return allDoneFuture.thenApply(v -> futures.stream()
        .map(future -> future.join()).collect(Collectors.toList()));
  }
}
