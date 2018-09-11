/*
 * Created on Oct 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.declarative.search.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.collection.CompositeCollection;
import org.apache.commons.collections.set.CompositeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.search.ISearchSystem;
import org.jactr.core.module.declarative.search.filter.AcceptAllFilter;
import org.jactr.core.module.declarative.search.filter.DelegatedFilter;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.module.declarative.search.filter.SlotFilter;
import org.jactr.core.module.declarative.search.map.BooleanTypeValueMap;
import org.jactr.core.module.declarative.search.map.ITypeValueMap;
import org.jactr.core.module.declarative.search.map.NullTypeValueMap;
import org.jactr.core.module.declarative.search.map.NumericTypeValueMap;
import org.jactr.core.module.declarative.search.map.StringTypeValueMap;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.ChunkNameComparator;
import org.jactr.core.utils.collections.CompositeCollectionFactory;
import org.jactr.core.utils.collections.CompositeSetFactory;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.collections.SkipListSetFactory;

/**
 * basic, but memory intensive inverted index of encoded chunks and their
 * values. At the top level we store a map, keyed on chunkType.slotName, that
 * contains a collection of ITypeValueMap<?, Chunk>'s. These are maps for each
 * unique <i>Type</i> of information that can be stored (currently: null,
 * string, boolean, number, chunk, chunktype, production, buffer). These
 * <i>typed</i> value maps contains, for each unique value encountered, a
 * collection of chunks that reference the value. Some value maps are sorted
 * (string, number, boolean) the rest are not. <br/>
 * The various search commands perform boolean set operations based on the
 * contents of a particular value map.<br/>
 * The chunkType.slotName key on indexing generates unique keys for all of a
 * chunk's parent types, including the special (*). On the query side, we just
 * generate the key for the request's unique chunktype (or * if null). <br/>
 * Current optimizations include:
 * <ul>
 * <li>individual slot queries are sorted before execution, allowing minimum
 * candidate set traversals</li>
 * <li>recycled collections, sets and maps to minimize garbage generation</li>
 * <li>log(n) collections, sets and maps.</li>
 * <li>sorting, filtering, and type verification are implemented as a single
 * candidate iteration</li>
 * </ul>
 * <br/>
 * Current performance characteristics:
 * <ul>
 * <li>merge check searches : near constant runtime for primary case (unique
 * chunk), otherwise it is a function, f(#slots, averageFan)</li>
 * <li>search time is <i>weakly</i> related to the number of search features
 * (slots in query)</li>
 * <li>search time is <i>weakly</i> related to the size of DM</li>
 * <li>search time is <i>fundamentally</i> related to the query values' fans
 * (i.e. # of chunks that reference that value)</li>
 * <li>search time is <i>weakly</i> associated with the number of chunks of the
 * queried type (log(N))</li>
 * </ul>
 * <br/>
 */
public class DefaultSearchSystem implements ISearchSystem
{
  /**
   * logger definition
   */
  static public final Log                                   LOGGER               = LogFactory
                                                                                     .getLog(DefaultSearchSystem.class);

  private ReentrantReadWriteLock                            _lock                = new ReentrantReadWriteLock();

  // private ACTREventDispatcher<IDeclarativeModule,ISearchListener>
  // _eventDispatcher;

  private Map<String, Collection<ITypeValueMap<?, IChunk>>> _slotMap;

  protected final IDeclarativeModule                        _module;

  protected ChunkNameComparator                             _chunkNameComparator = new ChunkNameComparator();

  protected IChunkFilter                                    _defaultFilter       = new AcceptAllFilter();

  /*
   * made default AMH 5/29/15
   */
  private boolean                                           _enableNotFilters    = !Boolean
                                                                                     .getBoolean("jactr.search.disableNotFilters");

  /**
   * will do all the filter processing, but not actually swap out the filter for
   * the search. this tests the overhead of building the filters.
   */
  private boolean                                           _testNotFilter       = Boolean
                                                                                     .getBoolean("jactr.search.testNotFilters");

  private ISearchDelegate                                   _exactSearch         = new ExactSingleThreadedSearchDelegate();

  private ISearchDelegate                                   _partialSearch       = new PartialSingleThreadedSearchDelegate();

  public DefaultSearchSystem(IDeclarativeModule module)
  {
    _slotMap = new TreeMap<String, Collection<ITypeValueMap<?, IChunk>>>();
    // _eventDispatcher = new ACTREventDispatcher<IDeclarativeModule,
    // ISearchListener>();
    _module = module;
  }

  public void setExactDelegate(ISearchDelegate exact)
  {
    _exactSearch = exact;
  }

  public void setPartialDelegate(ISearchDelegate partial)
  {
    _partialSearch = partial;
  }

  public ISearchDelegate getExactDelegate()
  {
    return _exactSearch;
  }

  public ISearchDelegate getPartialDelegate()
  {
    return _partialSearch;
  }

  public void clear()
  {
    try
    {
      _lock.writeLock().lock();
      for (Collection<ITypeValueMap<?, IChunk>> collection : _slotMap.values())
        if (collection != null)
        {
          for (ITypeValueMap<?, IChunk> tvm : collection)
            tvm.clear();
          collection.clear();
        }
      _slotMap.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  protected Collection<ITypeValueMap<?, IChunk>> instantiateTypeValueMapCollection()
  {
    return new ArrayList<ITypeValueMap<?, IChunk>>();
  }

  protected ITypeValueMap<?, IChunk> instantiateTypeValueMap(Object value)
  {
    if (value == null) return new NullTypeValueMap<IChunk>();
    if (value instanceof String) return new StringTypeValueMap<IChunk>();
    if (value instanceof Number) return new NumericTypeValueMap<IChunk>();
    if (value instanceof Boolean) return new BooleanTypeValueMap<IChunk>();
    if (value instanceof IChunk) return new ChunkTypeValueMap<IChunk>();
    if (value instanceof IChunkType)
      return new ChunkTypeTypeValueMap<IChunk>();
    if (value instanceof IProduction)
      return new ProductionTypeValueMap<IChunk>();
    if (value instanceof IActivationBuffer)
      return new ActivationBufferTypeValueMap<IChunk>();

    if (LOGGER.isWarnEnabled())
      LOGGER
          .warn("Could not determine what type of value map to provide given "
              + value + " of " + value.getClass());

    return null;
  }

  protected ReentrantReadWriteLock getLock()
  {
    return _lock;
  }

  /**
   * this implementation fails fast
   * 
   * @see org.jactr.core.module.declarative.search.ISearchSystem#findExact(ChunkTypeRequest,
   *      java.util.Comparator, IChunkFilter)
   */
  public SortedSet<IChunk> findExact(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter)
  {
    // SortedSet<IChunk> candidates = findExactSingleThreaded(pattern, sortRule,
    // filter);
    //
    // return candidates;
    return _exactSearch.find(pattern, sortRule, filter, this);
  }

  /**
   * old test code for threaded search, migrated to
   * {@link ExactParallelSearchDelegate}
   * 
   * @param pattern
   * @return
   */
  @Deprecated
  protected Collection<IChunk> findExactPooledThreads(ChunkTypeRequest pattern)
  {
    /*
     * will not work yet since this old version of fastset might not work
     * multithreaded
     */
    final Set<IChunk> candidates = new HashSet<IChunk>();
    final IChunkType chunkType = pattern.getChunkType();
    if (chunkType != null)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());

    ExecutorService pool = ExecutorServices.getExecutor(ExecutorServices.POOL);

    Collection<Future<Collection<IChunk>>> results = FastCollectionFactory
        .newInstance();

    for (ISlot slot : pattern.getConditionalAndLogicalSlots())
    {
      final ISlot fSlot = slot;
      /*
       * submit and snag the future for the results
       */
      results.add(pool.submit(new Callable<Collection<IChunk>>() {
        public Collection<IChunk> call() throws Exception
        {
          return find(chunkType, fSlot, candidates);
        }

      }));
    }

    /*
     * a search has been invoked for every slot pattern specified. Now we
     * iterate through and block on the results. Since order only matters if
     * this is the first result, we just block on the results and process them
     * in order
     */
    boolean first = chunkType == null;
    boolean zeroResults = false;
    for (Future<Collection<IChunk>> result : results)
      try
      {
        // if we've got nothing by now, cancel all the remaining searches
        if (zeroResults)
          result.cancel(true);
        else
        {
          Collection<IChunk> slotCandidates = result.get();
          if (first)
          {
            cleanAddAll(candidates, slotCandidates);
            first = false;
          }
          else
            cleanRetainAll(candidates, slotCandidates);
        }

        zeroResults = candidates.size() == 0;
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to process parallel search results :", e);
      }

    FastCollectionFactory.recycle(results);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  /**
   * Moved to
   * {@link ExactSingleThreadedSearchDelegate#sortPattern(IChunkType, Collection, List, DefaultSearchSystem)}
   * sort the slots by the guessed size of the result set. This is only used by
   * findExact. We also convert not's into filters instead whereever possible
   * 
   * @param chunkType
   * @param originalSlots
   * @return
   */
  @Deprecated
  protected IChunkFilter sortPattern(IChunkType chunkType,
      Collection<? extends ISlot> originalSlots, List<ISlot> container)
  {
    // ArrayList<ISlot> sorted = new ArrayList<ISlot>(originalSlots);
    container.addAll(originalSlots);

    Map<ISlot, Long> sizeMap = new HashMap<ISlot, Long>();
    for (ISlot slot : originalSlots)
      sizeMap.put(slot, guessSize(chunkType, slot));

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

    return notFilter == null ? new AcceptAllFilter() : notFilter;
  }

  /**
   * Moved to
   * {@link ExactSingleThreadedSearchDelegate#sortPatternOriginal(IChunkType, Collection, DefaultSearchSystem)}
   * sort the slots by the guessed size of the result set.
   * 
   * @param chunkType
   * @param slots
   * @return
   */
  @Deprecated
  protected List<ISlot> sortPatternOriginal(IChunkType chunkType,
      Collection<? extends ISlot> slots)
  {
    ArrayList<ISlot> sorted = new ArrayList<ISlot>(slots);

    Map<ISlot, Long> sizeMap = new HashMap<ISlot, Long>();
    for (ISlot slot : slots)
      sizeMap.put(slot, guessSize(chunkType, slot));

    Collections.sort(sorted, new PatternComparator(sizeMap));

    return sorted;
  }

  /**
   * old single threaded search. moved to
   * {@link ExactSingleThreadedSearchDelegate}
   * 
   * @param pattern
   * @param sortRule
   * @param filter
   * @return
   */
  @Deprecated
  protected SortedSet<IChunk> findExactSingleThreaded(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter)
  {
    /*
     * second pass, ditch all those that don't match our chunktype
     */
    SortedSet<IChunk> candidates = SkipListSetFactory
        .newInstance(_chunkNameComparator);
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
    IChunkFilter primaryFilter = _defaultFilter;
    if (_enableNotFilters || _testNotFilter)
    {
      sortedSlots = new ArrayList<ISlot>(originalSlots.size());
      primaryFilter = sortPattern(chunkType, originalSlots, sortedSlots);
    }
    else
      sortedSlots = sortPatternOriginal(chunkType, originalSlots);

    /*
     * first things first, find all the candidates based on the content of the
     * pattern. We sort the slots based on the estimated size of the returned
     * set, then execute them. This lets us keep our candidate size down, which
     * reduces the time cost of retainAll operations.
     */
    boolean first = candidates.size() == 0;
    for (ISlot slot : sortedSlots)
    {
      if (first)
      {
        // candidates.addAll(find(slot, candidates));
        cleanAddAll(candidates, find(chunkType, slot, candidates));
        first = false;
      }
      else
        cleanRetainAll(candidates, find(chunkType, slot, candidates));
      // candidates.retainAll(find(slot, candidates));

      if (candidates.size() == 0) break;
    }

    /**
     * if there are no slots, we need all the chunks of the type
     */
    if (sortedSlots.size() == 0)
      if (chunkType != null)
        candidates.addAll(chunkType.getSymbolicChunkType().getChunks());
      else
        try
        {
          candidates.addAll(_module.getChunks().get());
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
      Comparator<IChunk> comparator = _chunkNameComparator;
      if (sortRule != null) comparator = sortRule;

      IChunkFilter chunkFilter = filter == null ? _defaultFilter : filter;

      SortedSet<IChunk> returnCandidates = SkipListSetFactory
          .newInstance(comparator);

      for (IChunk candidate : candidates)
        if (chunkType == null || candidate.isA(chunkType))
          if (primaryFilter.accept(candidate))
            if (chunkFilter.accept(candidate)) returnCandidates.add(candidate);

      recycleCollection(candidates);
      candidates = returnCandidates;
    }
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  public SortedSet<IChunk> findFuzzy(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter)
  {
    return _partialSearch.find(pattern, sortRule, filter, this);
    // return findFuzzyInternal(pattern, sortRule, filter);
  }

  /**
   * moved to {@link PartialSingleThreadedSearchDelegate} default fuzzy search.
   * 
   * @param pattern
   * @param sortRule
   * @param filter
   * @return
   */
  @Deprecated
  protected SortedSet<IChunk> findFuzzyInternal(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule, IChunkFilter filter)
  {

    /*
     * second pass, ditch all those that don't match our chunktype
     */
    Collection<IChunk> candidates = null;
    SortedSet<IChunk> returnCandidates = null;
    IChunkType chunkType = pattern.getChunkType();

    if (chunkType != null)
      candidates = chunkType.getSymbolicChunkType().getChunks();
    else
      try
      {
        candidates = _module.getChunks().get();
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
      Comparator<IChunk> comparator = _chunkNameComparator;
      if (sortRule != null) comparator = sortRule;

      IChunkFilter chunkFilter = filter == null ? new AcceptAllFilter()
          : filter;

      returnCandidates = SkipListSetFactory.newInstance(comparator);

      for (IChunk candidate : candidates)
        if (chunkType == null || candidate.isA(chunkType))
          if (chunkFilter.accept(candidate)) returnCandidates.add(candidate);

      recycleCollection(candidates);
    }
    else
      returnCandidates = new TreeSet<IChunk>();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + returnCandidates);

    return returnCandidates;
  }

  protected long guessSize(IChunkType type, ISlot slot)
  {
    long size = 0;
    if (slot instanceof IConditionalSlot)
    {
      IConditionalSlot conditionalSlot = (IConditionalSlot) slot;
      switch (conditionalSlot.getCondition())
      {
        case IConditionalSlot.EQUALS:
          if (slot.getName().equals(ISlot.ISA))
            size += ((IChunkType) slot.getValue()).getSymbolicChunkType()
                .getNumberOfChunks();
          else
            size = guessEqualsSize(type, conditionalSlot);
          break;
        case IConditionalSlot.GREATER_THAN:
          size = guessGreaterThanSize(type, conditionalSlot);
          break;
        case IConditionalSlot.GREATER_THAN_EQUALS:
          size = guessGreaterThanSize(type, conditionalSlot);
          size += guessEqualsSize(type, conditionalSlot);
          break;
        case IConditionalSlot.LESS_THAN:
          size = guessLessThanSize(type, conditionalSlot);
          break;
        case IConditionalSlot.LESS_THAN_EQUALS:
          size = guessLessThanSize(type, conditionalSlot);
          size += guessEqualsSize(type, conditionalSlot);
          break;
        case IConditionalSlot.NOT_EQUALS:
          size = guessNotSize(type, conditionalSlot);
          break;
        case IConditionalSlot.WITHIN:
        default:
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("No clue what to do with this search condition "
                + conditionalSlot);
      }
    }
    else if (slot instanceof ILogicalSlot)
    {
      ILogicalSlot logicalSlot = (ILogicalSlot) slot;
      List<ISlot> children = FastListFactory.newInstance();
      logicalSlot.getSlots(children);

      switch (logicalSlot.getOperator())
      {
        case ILogicalSlot.AND:
        case ILogicalSlot.OR:
          size = guessSize(type, children.get(0));
          size += guessSize(type, children.get(children.size() - 1));
          break;
        case ILogicalSlot.NOT:
          size = guessSize(type, children.get(0));
      }

      FastListFactory.recycle(children);
    }
    else
      LOGGER.error("Ignoring slot " + slot
          + " because it's neither conditional nor logical");

    return size;
  }

  /**
   * current candidates is required in the case of NOT conditions
   * 
   * @param slot
   * @param candidates
   * @return
   */
  protected Collection<IChunk> find(IChunkType type, ISlot slot,
      Set<IChunk> candidates)
  {
    // Set<IChunk> rtn = SkipListSetFactory.newInstance(_chunkNameComparator);
    Collection<IChunk> rtn = CompositeCollectionFactory.newInstance();
    if (slot instanceof IConditionalSlot)
    {
      IConditionalSlot conditionalSlot = (IConditionalSlot) slot;
      switch (conditionalSlot.getCondition())
      {
        case IConditionalSlot.EQUALS:
          if (slot.getName().equals(ISlot.ISA))
            rtn.addAll(((IChunkType) slot.getValue()).getSymbolicChunkType()
                .getChunks());
          else
            cleanAddAll(rtn, equals(type, conditionalSlot));
          break;
        case IConditionalSlot.GREATER_THAN:
          cleanAddAll(rtn, greaterThan(type, conditionalSlot));
          break;
        case IConditionalSlot.GREATER_THAN_EQUALS:
          cleanAddAll(rtn, greaterThan(type, conditionalSlot));
          cleanAddAll(rtn, equals(type, conditionalSlot));
          break;
        case IConditionalSlot.LESS_THAN:
          cleanAddAll(rtn, lessThan(type, conditionalSlot));
          break;
        case IConditionalSlot.LESS_THAN_EQUALS:
          cleanAddAll(rtn, lessThan(type, conditionalSlot));
          cleanAddAll(rtn, equals(type, conditionalSlot));
          break;
        case IConditionalSlot.NOT_EQUALS:
          if (slot.getName().equals(ISlot.ISA))
          {
            // don't use this guy, it won't work. if we use retainAll/removeAll
            // we need the skip list set.
            CompositeCollectionFactory.recycle((CompositeCollection) rtn);

            rtn = SkipListSetFactory.newInstance(_chunkNameComparator);

            cleanAddAll(rtn, candidates);
            cleanRemoveAll(rtn, ((IChunkType) slot.getValue())
                .getSymbolicChunkType().getChunks());
          }
          else
            cleanAddAll(rtn, not(type, conditionalSlot));
          break;
        case IConditionalSlot.WITHIN:
        default:
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("No clue what to do with this search condition "
                + conditionalSlot);
      }
    }
    else if (slot instanceof ILogicalSlot)
    {
      ILogicalSlot logicalSlot = (ILogicalSlot) slot;
      List<ISlot> children = FastListFactory.newInstance();
      logicalSlot.getSlots(children);

      switch (logicalSlot.getOperator())
      {
        case ILogicalSlot.AND:
          // don't use this guy, it won't work. if we use retainAll/removeAll
          // we need the skip list set.
          CompositeCollectionFactory.recycle((CompositeCollection) rtn);

          rtn = SkipListSetFactory.newInstance(_chunkNameComparator);

          cleanAddAll(rtn, find(type, children.get(0), candidates));
          cleanRetainAll(rtn,
              find(type, children.get(children.size() - 1), candidates));
          break;
        case ILogicalSlot.OR:
          cleanAddAll(rtn, find(type, children.get(0), candidates));
          cleanAddAll(rtn,
              find(type, children.get(children.size() - 1), candidates));
          break;
        case ILogicalSlot.NOT:
          // don't use this guy, it won't work. if we use retainAll/removeAll
          // we need the skip list set.
          CompositeCollectionFactory.recycle((CompositeCollection) rtn);

          rtn = SkipListSetFactory.newInstance(_chunkNameComparator);

          cleanAddAll(rtn, candidates);
          cleanRemoveAll(rtn, find(type, children.get(0), candidates));
      }

      FastListFactory.recycle(children);
      LOGGER.debug("Logical.AND search for " + logicalSlot + " returning "
          + rtn);
    }
    else
      LOGGER.error("Ignoring slot " + slot
          + " because it's neither conditional nor logical");

    if (LOGGER.isDebugEnabled())
      LOGGER
          .debug("Search for " + slot + " yielded " + rtn.size() + " results");

    return rtn;
  }

  /**
   * wrappers for the set logic so that we can easily clean up of the temporary
   * collections. Specifically, recycling the candidates collection if possible.
   * 
   * @param rtnSet
   * @param candidates
   */
  protected void cleanAddAll(Collection<IChunk> rtnSet,
      Collection<IChunk> candidates)
  {
    if (rtnSet instanceof CompositeCollection)
      ((CompositeCollection) rtnSet).addComposited(candidates);
    else
    {
      rtnSet.addAll(candidates);
      recycleCollection(candidates);
    }
  }

  /**
   * retain all and recycle the candidates
   * 
   * @param rtnSet
   * @param candidates
   */
  protected void cleanRetainAll(Collection<IChunk> rtnSet,
      Collection<IChunk> candidates)
  {
    rtnSet.retainAll(candidates);
    recycleCollection(candidates);
  }

  /**
   * removeall from rtnSet and recycle candidates
   * 
   * @param rtnSet
   * @param candidates
   */
  protected void cleanRemoveAll(Collection<IChunk> rtnSet,
      Collection<IChunk> candidates)
  {
    rtnSet.removeAll(candidates);
    recycleCollection(candidates);
  }

  protected Collection<IChunk> equals(IChunkType type, ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        getKey(type, slot.getName()), slot.getValue(), false);
    if (typeValueMap != null) return typeValueMap.equalTo(slot.getValue());
    return Collections.EMPTY_LIST;
  }

  protected long guessEqualsSize(IChunkType type, ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        getKey(type, slot.getName()), slot.getValue(), false);
    if (typeValueMap != null) return typeValueMap.equalToSize(slot.getValue());
    return 0;
  }

  protected Collection<IChunk> lessThan(IChunkType type, ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        getKey(type, slot.getName()), slot.getValue(), false);
    if (typeValueMap != null)
      try
      {
        return typeValueMap.lessThan(slot.getValue());
      }
      catch (UnsupportedOperationException uoe)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(slot.getValue() + " does not have natural ordering");
      }
    return Collections.EMPTY_LIST;
  }

  protected long guessLessThanSize(IChunkType type, ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        getKey(type, slot.getName()), slot.getValue(), false);
    if (typeValueMap != null)
      try
      {
        return typeValueMap.lessThanSize(slot.getValue());
      }
      catch (UnsupportedOperationException uoe)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(slot.getValue() + " does not have natural ordering");
      }
    return 0;
  }

  protected Collection<IChunk> greaterThan(IChunkType type, ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        getKey(type, slot.getName()), slot.getValue(), false);
    if (typeValueMap != null)
      try
      {
        return typeValueMap.greaterThan(slot.getValue());
      }
      catch (UnsupportedOperationException uoe)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(slot.getValue() + " does not have natural ordering");
      }

    return Collections.EMPTY_LIST;
  }

  protected long guessGreaterThanSize(IChunkType type, ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        getKey(type, slot.getName()), slot.getValue(), false);
    if (typeValueMap != null)
      try
      {
        return typeValueMap.greaterThanSize(slot.getValue());
      }
      catch (UnsupportedOperationException uoe)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(slot.getValue() + " does not have natural ordering");
      }

    return 0;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void recycleCollection(Collection<?> collection)
  {
    if (collection instanceof CompositeCollection)
      CompositeCollectionFactory.recycle((CompositeCollection) collection);
    else if (collection instanceof CompositeSet)
      CompositeSetFactory.recycle((CompositeSet) collection);
    else if (collection instanceof ConcurrentSkipListSet)
      SkipListSetFactory.recycle((ConcurrentSkipListSet) collection);

  }

  @SuppressWarnings("unchecked")
  protected Collection<IChunk> not(IChunkType type, ISlot slot)
  {
    /*
     * return values are not only what the approriate typevalue map say they
     * are, but also all the other type value maps.all() we'll start with the
     * obvious part first
     */
    CompositeCollection rtn = CompositeCollectionFactory.newInstance();
    String key = getKey(type, slot.getName());
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(key,
        slot.getValue(), false);

    Collection<IChunk> container = Collections.EMPTY_LIST;

    if (typeValueMap != null) container = typeValueMap.not(slot.getValue());

    rtn.addComposited(container);

    // now let's snag all the rest. This could actually be null.
    Collection<ITypeValueMap<?, IChunk>> maps = _slotMap.get(key);
    // might actually be nothing else..
    if (maps == null) return rtn;

    // now let's snag all the rest
    try
    {
      getLock().readLock().lock();
      for (ITypeValueMap<?, IChunk> tvm : maps)
        if (tvm != typeValueMap && tvm != null)
        {
          container = tvm.all();
          rtn.addComposited(container);
          // rtn.addAll(container);
          // recycleCollection(container);
        }
      return rtn;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  protected long guessNotSize(IChunkType type, ISlot slot)
  {
    /*
     * return values are not only what the approriate typevalue map say they
     * are, but also all the other type value maps.all() we'll start with the
     * obvious part first
     */
    long rtn = 0;
    String key = getKey(type, slot.getName());
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(key,
        slot.getValue(), false);

    if (typeValueMap != null) rtn += typeValueMap.notSize(slot.getValue());

    // now let's snag all the rest. This could actually be null.
    Collection<ITypeValueMap<?, IChunk>> maps = _slotMap.get(key);
    // might actually be nothing else..
    if (maps == null) return rtn;

    try
    {
      getLock().readLock().lock();
      for (ITypeValueMap<?, IChunk> tvm : maps)
        if (tvm != typeValueMap && tvm != null) rtn += tvm.allSize();
      return rtn;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  protected String getKey(IChunkType chunkType, String slotName)
  {
    return String.format("%s.%s",
        chunkType == null ? "*" : chunkType.getSymbolicChunkType().getName(),
        slotName).toLowerCase();
  }

  protected Set<String> getKeys(IChunkType chunkType, String slotName)
  {
    TreeSet<String> rtn = new TreeSet<String>();
    rtn.add(getKey(chunkType, slotName)); // handles *. and chunkType.*

    /*
     * and only for parent types if they have the slot.
     */
    if (chunkType != null)
      for (IChunkType parent : chunkType.getSymbolicChunkType().getParents())
        if (parent.getSymbolicChunkType().getSlot(slotName) != null)
          rtn.addAll(getKeys(parent, slotName));

    return rtn;
  }

  public void index(IChunk chunk)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Indexing " + chunk);

    if (!chunk.isEncoded())
      throw new RuntimeException(chunk
          + " has not been encoded, will not index");

    for (ISlot slot : chunk.getSymbolicChunk().getSlots())
      addIndexing(chunk, slot.getName(), slot.getValue());
  }

  public void unindex(IChunk chunk)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Unindexing " + chunk);

    for (ISlot slot : chunk.getSymbolicChunk().getSlots())
      removeIndexing(chunk, slot.getName(), slot.getValue());
  }

  public void update(IChunk chunk, String slotName, Object oldValue,
      Object newValue)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Updating indexing for " + chunk + "." + slotName);
    if (oldValue == null) oldValue = NullTypeValueMap.NULL;
    if (newValue == null) newValue = NullTypeValueMap.NULL;
    removeIndexing(chunk, slotName, oldValue);
    addIndexing(chunk, slotName, newValue);
  }

  protected void removeIndexing(IChunk chunk, String slotName, Object value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Unindexing " + chunk + "." + slotName + "=" + value);

    for (String key : getKeys(chunk.getSymbolicChunk().getChunkType(), slotName))
    {
      ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(key,
          value, false);
      if (typeValueMap != null) typeValueMap.remove(value, chunk);
    }

    /*
     * now, what about all those maps that contain chunk as a value?
     */

  }

  protected void addIndexing(IChunk chunk, String slotName, Object value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Indexing " + chunk + "." + slotName + "=" + value);

    for (String key : getKeys(chunk.getSymbolicChunk().getChunkType(), slotName))
    {
      ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(key,
          value, true);
      // this is possible if we can't index the data type
      if (typeValueMap != null) typeValueMap.add(value, chunk);
    }
  }

  /**
   * return the ITypeValueMap for the slot name. if create is true, the write
   * lock will be acquired and if no map exists, it will be created based on the
   * value passed
   * 
   * @param typeSlotNameIndexKey
   * @param create
   * @return
   */
  protected ITypeValueMap<?, IChunk> getSlotNameTypeValueMap(
      String typeSlotNameIndexKey, Object value, boolean create)
  {
    typeSlotNameIndexKey = typeSlotNameIndexKey.toLowerCase();
    ReentrantReadWriteLock lock = getLock();
    Collection<ITypeValueMap<?, IChunk>> typeValueMaps = null;
    ITypeValueMap<?, IChunk> typeValueMap = null;
    if (create)
      try
      {
        lock.writeLock().lock();
        typeValueMaps = _slotMap.get(typeSlotNameIndexKey);
        if (typeValueMaps == null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("slot " + typeSlotNameIndexKey
                + " has no type value map collection, creating");
          // create
          typeValueMaps = instantiateTypeValueMapCollection();
          _slotMap.put(typeSlotNameIndexKey, typeValueMaps);

        }

        for (ITypeValueMap<?, IChunk> tvm : typeValueMaps)
          if (tvm.isValueRelevant(value))
          {
            typeValueMap = tvm;
            // continue; //good job :-p
            break;
          }

        /*
         * no typevaluemap was found, create
         */
        if (typeValueMap == null)
        {
          typeValueMap = instantiateTypeValueMap(value);
          if (typeValueMap != null)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("No type value map exists for current value "
                  + value + ", created " + typeValueMap);
            typeValueMaps.add(typeValueMap);
          }
        }
      }
      finally
      {
        lock.writeLock().unlock();
      }
    else
      try
      {
        lock.readLock().lock();
        typeValueMaps = _slotMap.get(typeSlotNameIndexKey);
        if (typeValueMaps != null)
        {
          for (ITypeValueMap<?, IChunk> tvm : typeValueMaps)
            if (tvm.isValueRelevant(value))
            {
              typeValueMap = tvm;
              // continue; //once again.. ?
              break;
            }
            else if (LOGGER.isDebugEnabled())
              LOGGER.debug(tvm + " is irrelevant to " + value);

          if (typeValueMap == null)
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("No type value map was found for "
                  + typeSlotNameIndexKey + ", returning");
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("slot " + typeSlotNameIndexKey
              + " has no type value map collection, returning");
      }
      finally
      {
        lock.readLock().unlock();
      }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning " + typeValueMap + " for " + typeSlotNameIndexKey
          + "=" + value);
    return typeValueMap;
  }

  // public void addListener(ISearchListener listener, Executor executor)
  // {
  // _eventDispatcher.addListener(listener, executor);
  // }
  //
  //
  // public void removeListener(ISearchListener listener)
  // {
  // _eventDispatcher.removeListener(listener);
  // }
  //
  // public boolean hasListeners()
  // {
  // return _eventDispatcher.hasListeners();
  // }
}
