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
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastList;
import javolution.util.FastSet;
import javolution.util.FastTable;

import org.apache.commons.collections.set.CompositeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.search.ISearchSystem;
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
import org.jactr.core.utils.collections.CompositeSetFactory;
import org.jactr.core.utils.collections.SkipListSetFactory;

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

  private IDeclarativeModule                                _module;

  private ChunkNameComparator                               _chunkNameComparator = new ChunkNameComparator();

  public DefaultSearchSystem(IDeclarativeModule module)
  {
    _slotMap = new TreeMap<String, Collection<ITypeValueMap<?, IChunk>>>();
    // _eventDispatcher = new ACTREventDispatcher<IDeclarativeModule,
    // ISearchListener>();
    _module = module;
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
   *      java.util.Comparator)
   */
  public Collection<IChunk> findExact(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule)
  {
    Collection<IChunk> candidates = findExactSingleThreaded(pattern);

    if (sortRule != null)
    {
      /*
       * finally, we sort them
       */
      TreeSet<IChunk> sortedResults = new TreeSet<IChunk>(sortRule);
      sortedResults.addAll(candidates);

      return sortedResults;
    }

    return candidates;
  }

  protected Collection<IChunk> findExactPooledThreads(ChunkTypeRequest pattern)
  {
    /*
     * will not work yet since this old version of fastset might not work
     * multithreaded
     */
    final FastSet<IChunk> candidates = new FastSet<IChunk>();
    IChunkType chunkType = pattern.getChunkType();
    if (chunkType != null)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());

    ExecutorService pool = ExecutorServices.getExecutor(ExecutorServices.POOL);

    FastList<Future<Collection<IChunk>>> results = FastList.newInstance();

    for (ISlot slot : pattern.getConditionalAndLogicalSlots())
    {
      final ISlot fSlot = slot;
      /*
       * submit and snag the future for the results
       */
      results.add(pool.submit(new Callable<Collection<IChunk>>() {
        public Collection<IChunk> call() throws Exception
        {
          return find(fSlot, candidates);
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

    FastList.recycle(results);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  protected Collection<ISlot> sortPattern(Collection<? extends ISlot> slots)
  {
    ArrayList<ISlot> sorted = new ArrayList<ISlot>(slots);

    Map<ISlot, Long> sizeMap = new HashMap<ISlot, Long>();
    for (ISlot slot : slots)
      sizeMap.put(slot, guessSize(slot));

    Collections.sort(sorted, new PatternComparator(sizeMap));

    return sorted;
  }

  protected Collection<IChunk> findExactSingleThreaded(ChunkTypeRequest pattern)
  {
    /*
     * second pass, ditch all those that don't match our chunktype
     */
    Set<IChunk> candidates = SkipListSetFactory
        .newInstance(_chunkNameComparator);
    IChunkType chunkType = pattern.getChunkType();

    Collection<ISlot> sortedSlots = sortPattern(pattern
        .getConditionalAndLogicalSlots());
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
        cleanAddAll(candidates, find(slot, candidates));
        first = false;
      }
      else
        cleanRetainAll(candidates, find(slot, candidates));
      // candidates.retainAll(find(slot, candidates));

      if (candidates.size() == 0) break;
    }

    /**
     * now, we can either grab all the chunks of type and do a set retainAll,
     * or.. I can iterate testing for the appropriate chunktype. for the most
     * accurate comparison in performance I should probably use set logic first.<br/>
     * <br/>
     * Set logic resulted in a 20x speed up for during adding. <br/>
     * Using an iterator, we could save a ton on memory allocations, except that
     * default IChunkType.getChunks() returns an unmodifiable wrapper, not copy.
     * Might as well leave this as set logic until getChunks is a copy operator
     */
    if (sortedSlots.size() == 0)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());
    else if (candidates.size() != 0 && chunkType != null)
      candidates.retainAll(chunkType.getSymbolicChunkType().getChunks());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  public Collection<IChunk> findFuzzy(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule)
  {

    /*
     * second pass, ditch all those that don't match our chunktype
     */
    FastSet<IChunk> candidates = FastSet.newInstance();
    IChunkType chunkType = pattern.getChunkType();

    /*
     * old code used slot values if any were provided. If not, it would grab
     * every chunk of the given type. This would reduce the set of candidates if
     * there was only one slot specified (the candidate set would include just
     * those that equaled the specified slot). In reality, we should just grab
     * EVERY chunk of the type, regardless of the pattern
     */
    // boolean noSlots = true;
    // for (IConditionalSlot slot : pattern.getConditionalSlots())
    // {
    // noSlots = false;
    // Collection<IChunk> containers = find(slot);
    // if (chunkType == null)
    // candidates.addAll(containers);
    // else
    // for (IChunk candidate : containers)
    // if (candidate.isA(chunkType)) candidates.add(candidate);
    // }
    //
    // if (noSlots && chunkType != null)
    // candidates.addAll(chunkType.getSymbolicChunkType().getChunks());
    if (chunkType != null)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    if (sortRule != null)
    {
      /*
       * finally, we sort them
       */
      TreeSet<IChunk> sortedResults = new TreeSet<IChunk>(sortRule);
      sortedResults.addAll(candidates);

      return sortedResults;
    }
    return candidates;
  }

  protected long guessSize(ISlot slot)
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
            size = guessEqualsSize(conditionalSlot);
          break;
        case IConditionalSlot.GREATER_THAN:
          size = guessGreaterThanSize(conditionalSlot);
          break;
        case IConditionalSlot.GREATER_THAN_EQUALS:
          size = guessGreaterThanSize(conditionalSlot);
          size += guessEqualsSize(conditionalSlot);
          break;
        case IConditionalSlot.LESS_THAN:
          size = guessLessThanSize(conditionalSlot);
          break;
        case IConditionalSlot.LESS_THAN_EQUALS:
          size = guessLessThanSize(conditionalSlot);
          size += guessEqualsSize(conditionalSlot);
          break;
        case IConditionalSlot.NOT_EQUALS:
          size = guessNotSize(conditionalSlot);
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
      FastList<ISlot> children = FastList.newInstance();
      logicalSlot.getSlots(children);

      switch (logicalSlot.getOperator())
      {
        case ILogicalSlot.AND:
        case ILogicalSlot.OR:
          size = guessSize(children.getFirst());
          size += guessSize(children.getLast());
          break;
        case ILogicalSlot.NOT:
          size = guessSize(children.getFirst());
      }

      FastList.recycle(children);
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
  protected Collection<IChunk> find(ISlot slot, Set<IChunk> candidates)
  {
    Set<IChunk> rtn = SkipListSetFactory.newInstance(_chunkNameComparator);
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
            cleanAddAll(rtn, equals(conditionalSlot));
          break;
        case IConditionalSlot.GREATER_THAN:
          cleanAddAll(rtn, greaterThan(conditionalSlot));
          break;
        case IConditionalSlot.GREATER_THAN_EQUALS:
          cleanAddAll(rtn, greaterThan(conditionalSlot));
          cleanAddAll(rtn, equals(conditionalSlot));
          break;
        case IConditionalSlot.LESS_THAN:
          cleanAddAll(rtn, lessThan(conditionalSlot));
          break;
        case IConditionalSlot.LESS_THAN_EQUALS:
          cleanAddAll(rtn, lessThan(conditionalSlot));
          cleanAddAll(rtn, equals(conditionalSlot));
          break;
        case IConditionalSlot.NOT_EQUALS:
          if (slot.getName().equals(ISlot.ISA))
          {
            cleanAddAll(rtn, candidates);
            cleanRemoveAll(rtn, ((IChunkType) slot.getValue())
                .getSymbolicChunkType().getChunks());
          }
          else
            cleanAddAll(rtn, not(conditionalSlot));
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
      FastList<ISlot> children = FastList.newInstance();
      logicalSlot.getSlots(children);

      switch (logicalSlot.getOperator())
      {
        case ILogicalSlot.AND:
          cleanAddAll(rtn, find(children.getFirst(), candidates));
          cleanRetainAll(rtn, find(children.getLast(), candidates));
          break;
        case ILogicalSlot.OR:
          cleanAddAll(rtn, find(children.getFirst(), candidates));
          cleanAddAll(rtn, find(children.getLast(), candidates));
          break;
        case ILogicalSlot.NOT:
          cleanAddAll(rtn, candidates);
          cleanRemoveAll(rtn, find(children.getFirst(), candidates));
      }

      FastList.recycle(children);
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
   * collections
   * 
   * @param rtnSet
   * @param candidates
   */
  protected void cleanAddAll(Set<IChunk> rtnSet, Collection<IChunk> candidates)
  {
    rtnSet.addAll(candidates);
    recycleCollection(candidates);
  }

  protected void cleanRetainAll(Set<IChunk> rtnSet,
      Collection<IChunk> candidates)
  {
    rtnSet.retainAll(candidates);
    recycleCollection(candidates);
  }

  protected void cleanRemoveAll(Set<IChunk> rtnSet,
      Collection<IChunk> candidates)
  {
    rtnSet.removeAll(candidates);
    recycleCollection(candidates);
  }

  protected Collection<IChunk> equals(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
    if (typeValueMap != null) return typeValueMap.equalTo(slot.getValue());
    return Collections.EMPTY_LIST;
  }

  protected long guessEqualsSize(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
    if (typeValueMap != null) return typeValueMap.equalToSize(slot.getValue());
    return 0;
  }

  protected Collection<IChunk> lessThan(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
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

  protected long guessLessThanSize(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
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

  protected Collection<IChunk> greaterThan(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
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

  protected long guessGreaterThanSize(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
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
    if (collection instanceof CompositeSet)
      CompositeSetFactory.recycle((CompositeSet) collection);
    else if (collection instanceof ConcurrentSkipListSet)
      SkipListSetFactory.recycle((ConcurrentSkipListSet) collection);
    else
    if (collection instanceof FastList)
      FastList.recycle((FastList) collection);
    else if (collection instanceof FastSet)
      FastSet.recycle((FastSet) collection);
    else if (collection instanceof FastTable)
      FastTable.recycle((FastTable) collection);
  }

  @SuppressWarnings("unchecked")
  protected Collection<IChunk> not(ISlot slot)
  {
    /*
     * return values are not only what the approriate typevalue map say they
     * are, but also all the other type value maps.all() we'll start with the
     * obvious part first
     */
    CompositeSet rtn = CompositeSetFactory.newInstance();
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);

    Collection<IChunk> container = Collections.EMPTY_LIST;

    if (typeValueMap != null) container = typeValueMap.not(slot.getValue());
    rtn.addComposited(container);

    recycleCollection(container);

    // now let's snag all the rest
    try
    {
      getLock().readLock().lock();
      for (ITypeValueMap<?, IChunk> tvm : _slotMap.get(slot.getName()
          .toLowerCase()))
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

  protected long guessNotSize(ISlot slot)
  {
    /*
     * return values are not only what the approriate typevalue map say they
     * are, but also all the other type value maps.all() we'll start with the
     * obvious part first
     */
    long rtn = 0;

    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);

    if (typeValueMap != null) rtn += typeValueMap.notSize(slot.getValue());

    // now let's snag all the rest
    try
    {
      getLock().readLock().lock();
      for (ITypeValueMap<?, IChunk> tvm : _slotMap.get(slot.getName()
          .toLowerCase()))
        if (tvm != typeValueMap && tvm != null) rtn += tvm.allSize();
      return rtn;
    }
    finally
    {
      getLock().readLock().unlock();
    }
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
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(slotName,
        value, false);
    if (typeValueMap != null) typeValueMap.remove(value, chunk);
  }

  protected void addIndexing(IChunk chunk, String slotName, Object value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Indexing " + chunk + "." + slotName + "=" + value);

    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(slotName,
        value, true);

    // this is possible if we can't index the data type
    if (typeValueMap != null) typeValueMap.add(value, chunk);
  }

  /**
   * return the ITypeValueMap for the slot name. if create is true, the write
   * lock will be acquired and if no map exists, it will be created based on the
   * value passed
   * 
   * @param slotName
   * @param create
   * @return
   */
  protected ITypeValueMap<?, IChunk> getSlotNameTypeValueMap(String slotName,
      Object value, boolean create)
  {
    slotName = slotName.toLowerCase();
    ReentrantReadWriteLock lock = getLock();
    Collection<ITypeValueMap<?, IChunk>> typeValueMaps = null;
    ITypeValueMap<?, IChunk> typeValueMap = null;
    if (create)
      try
      {
        lock.writeLock().lock();
        typeValueMaps = _slotMap.get(slotName);
        if (typeValueMaps == null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("slot " + slotName
                + " has no type value map collection, creating");
          // create
          typeValueMaps = instantiateTypeValueMapCollection();
          _slotMap.put(slotName, typeValueMaps);

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
        typeValueMaps = _slotMap.get(slotName);
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
              LOGGER.debug("No type value map was found for " + slotName
                  + ", returning");
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("slot " + slotName
              + " has no type value map collection, returning");
      }
      finally
      {
        lock.readLock().unlock();
      }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning " + typeValueMap + " for " + slotName + "="
          + value);
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
