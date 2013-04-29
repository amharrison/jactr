package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.manager.IAfferentObjectManager;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.ChainedComparator;
import org.jactr.modules.pm.IPerceptualModule;
import org.jactr.modules.pm.common.memory.IActivePerceptListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.common.memory.event.ActivePerceptEvent;
import org.jactr.modules.pm.common.memory.filter.IIndexFilter;
import org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;

public abstract class AbstractPerceptualMemory implements IPerceptualMemory
{
  /**
   * Logger definition
   */
  static private final transient Log                                           LOGGER      = LogFactory
                                                                                               .getLog(AbstractPerceptualMemory.class);

  private final Collection<IPerceptualEncoder>                                 _encoders;

  private final Collection<PerceptualEncoderBridge>                            _bridges;

  @SuppressWarnings("unchecked")
  private final Collection<IFeatureMap>                                        _featureMaps;

  private final Collection<IIndexFilter>                                       _filters;

  private IFINSTFeatureMap                                                     _finstFeatureMap;

  private final IPerceptualModule                                              _module;

  private final IIndexManager                                                  _indexManager;

  private final ACTREventDispatcher<IPerceptualMemory, IActivePerceptListener> _dispatcher = new ACTREventDispatcher<IPerceptualMemory, IActivePerceptListener>();

  private ACTRAgent                                                            _agent;

  private DelayableAfferentObjectListener                                      _agentListener;

  private List<PerceptualSearchResult>                                         _recentResults;

  @SuppressWarnings("unchecked")
  public AbstractPerceptualMemory(IPerceptualModule module,
      IIndexManager manager)
  {
    _encoders = new FastList<IPerceptualEncoder>();
    _bridges = new FastList<PerceptualEncoderBridge>();
    _featureMaps = new FastList<IFeatureMap>();
    _filters = new FastList<IIndexFilter>();
    _module = module;
    _indexManager = manager;
    _recentResults = Collections
        .synchronizedList(new FastList<PerceptualSearchResult>());
  }

  public void addListener(IActivePerceptListener listener, Executor executor)
  {
    _dispatcher.addListener(listener, executor);
  }

  public void removeListener(IActivePerceptListener listener)
  {
    _dispatcher.removeListener(listener);
  }

  protected boolean hasListeners()
  {
    return _dispatcher.hasListeners();
  }

  protected void dispatch(ActivePerceptEvent event)
  {
    _dispatcher.fire(event);
  }

  public double getLastChangeTime()
  {
    if (_agentListener != null) return _agentListener.getLastChangeTime();
    return -1;
  }

  public boolean isAttached()
  {
    return _agent != null;
  }

  /**
   * attach must be called after the model has been connected to commmon reality
   * 
   * @param agent
   */
  @SuppressWarnings("unchecked")
  public void attach(ACTRAgent agent)
  {
    if (_agent != null) throw new IllegalStateException("Already attached!");
    _agent = agent;
    _agentListener = new DelayableAfferentObjectListener(_module.getModel(),
        _agent, _agent.getExecutorService());

    for (PerceptualEncoderBridge bridge : _bridges)
    {
      _agentListener.add(bridge);
      IPerceptualEncoder encoder = bridge.getEncoder();
      if (encoder instanceof INeedsAgent)
        ((INeedsAgent) encoder).setAgent(agent);
    }

    for (IFeatureMap map : _featureMaps)
    {
      _agentListener.add(map);
      if (map instanceof INeedsAgent) ((INeedsAgent) map).setAgent(agent);
    }

    /*
     * the listener is processed as the messages come in, on the io thread, but
     * the actual work is done on the CR executor
     */
    _agent.getAfferentObjectManager().addListener(_agentListener,
        ExecutorServices.INLINE_EXECUTOR);

    // handle any potentially missed
    _agentListener.processExistingObjects();
  }

  @SuppressWarnings("unchecked")
  public void detach()
  {
    _agent.getAfferentObjectManager().removeListener(_agentListener);

    for (PerceptualEncoderBridge bridge : _bridges)
    {
      IPerceptualEncoder encoder = bridge.getEncoder();
      if (encoder instanceof INeedsAgent)
        ((INeedsAgent) encoder).setAgent(null);
    }

    for (IFeatureMap map : _featureMaps)
      if (map instanceof INeedsAgent) ((INeedsAgent) map).setAgent(null);

    _agent = null;
    _agentListener = null;
  }

  protected DelayableAfferentObjectListener getObjectListener()
  {
    return _agentListener;
  }

  public int getPendingUpdates()
  {
    DelayableAfferentObjectListener listener = getObjectListener();
    if (listener != null) return listener.getPendingUpdates();
    return 0;
  }

  public IPerceptualModule getModule()
  {
    return _module;
  }

  public void addEncoder(IPerceptualEncoder encoder)
  {
    if (_agent != null)
      throw new IllegalStateException("Cannot add encoders to a running model");

    _encoders.add(encoder);
    PerceptualEncoderBridge bridge = new PerceptualEncoderBridge(encoder, this);
    _bridges.add(bridge);
  }

  @SuppressWarnings("unchecked")
  public void addFeatureMap(IFeatureMap featureMap)
  {
    if (_agent != null)
      throw new IllegalStateException(
          "Cannot add featuremaps to a running model");

    _featureMaps.add(featureMap);
    featureMap.setPerceptualMemory(this);
    if (featureMap instanceof IFINSTFeatureMap)
    {
      if (_finstFeatureMap != null)
        LOGGER.warn("FINST feature map is being redefined with "
            + featureMap.getClass().getName());
      _finstFeatureMap = (IFINSTFeatureMap) featureMap;
    }

  }

  public void addFilter(IIndexFilter filter)
  {
    filter.setPerceptualMemory(this);
    _filters.add(filter);
  }

  public Collection<IPerceptualEncoder> getEncoders(
      Collection<IPerceptualEncoder> container)
  {
    if (container == null)
      container = new ArrayList<IPerceptualEncoder>(_encoders.size());
    container.addAll(_encoders);
    return container;
  }

  public IFINSTFeatureMap getFINSTFeatureMap()
  {
    return _finstFeatureMap;
  }

  @SuppressWarnings("unchecked")
  public Collection<IFeatureMap> getFeatureMaps(
      Collection<IFeatureMap> container)
  {
    if (container == null)
      container = new ArrayList<IFeatureMap>(_featureMaps.size());
    container.addAll(_featureMaps);
    return container;
  }

  public Collection<IIndexFilter> getFilters(Collection<IIndexFilter> container)
  {
    if (container == null)
      container = new ArrayList<IIndexFilter>(_filters.size());
    container.addAll(_filters);
    return container;
  }

  public void removeEncoder(IPerceptualEncoder encoder)
  {
    _encoders.remove(encoder);
    for (PerceptualEncoderBridge bridge : _bridges)
      if (bridge.getEncoder() == encoder)
      {
        _bridges.remove(bridge);
        break;
      }
  }

  @SuppressWarnings("unchecked")
  public void removeFeatureMap(IFeatureMap featureMap)
  {
    _featureMaps.remove(featureMap);
  }

  public void removeFilter(IIndexFilter filter)
  {
    _filters.remove(filter);
  }

  public Collection<IChunk> getEncodings(IIdentifier identifier,
      Collection<IChunk> container)
  {
    if (container == null) container = new ArrayList<IChunk>();
    for (PerceptualEncoderBridge bridge : _bridges)
    {
      IChunk chunk = bridge.get(identifier, true);
      if (chunk == null) continue;

      container.add(chunk);
    }

    return container;
  }

  protected IIndexManager getIndexManager()
  {
    return _indexManager;
  }

  /**
   * search, merely delegates to searchInternal on the common reality executor
   * 
   * @param request
   * @return
   * @see org.jactr.modules.pm.common.memory.IPerceptualMemory#search(org.jactr.core.production.request.ChunkTypeRequest)
   */
  public Future<PerceptualSearchResult> search(final ChunkTypeRequest request)
  {
    FutureTask<PerceptualSearchResult> future = new FutureTask<PerceptualSearchResult>(
        new Callable<PerceptualSearchResult>() {

          public PerceptualSearchResult call() throws Exception
          {
            return searchInternal(request);
          }
        });

    getModule().getCommonRealityExecutor().execute(future);

    return future;
  }

  private void buildSort(ChunkTypeRequest request,
      Collection<IIndexFilter> container)
  {
    FastList<IIndexFilter> filters = FastList.newInstance();
    TreeMap<Integer, IIndexFilter> sorted = new TreeMap<Integer, IIndexFilter>();

    getFilters(filters);
    for (IIndexFilter filter : filters)
    {
      // normalize
      filter.normalizeRequest(request);

      IIndexFilter actualFilter = filter.instantiate(request);

      if (actualFilter == null) continue;

      int weight = actualFilter.getWeight();
      while (sorted.containsKey(weight))
        weight++;

      sorted.put(weight, actualFilter);
    }

    FastList.recycle(filters);

    container.addAll(sorted.values());
  }

  @SuppressWarnings("unchecked")
  private void getCandidateIdentifiers(ChunkTypeRequest request,
      Collection<IFeatureMap> featureMaps, Collection<IIdentifier> container)
  {
    FastSet<IIdentifier> candidates = FastSet.newInstance();

    boolean firstRun = true;
    for (IFeatureMap featureMap : featureMaps)
      if (featureMap.isInterestedIn(request))
      {
        candidates.clear();
        featureMap.getCandidateRealObjects(request, candidates);

        if (firstRun)
          container.addAll(candidates);
        else
          container.retainAll(candidates);

        firstRun = false;
        if (container.size() == 0)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("No candidates were found after checking "
                + featureMap + ". aborting search.");
          break;
        }
      }

    FastSet.recycle(candidates);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning candidates : " + container);
  }

  /**
   * create a default comparator when none is specified from the search request
   * or null.
   * 
   * @return null
   */
  protected Comparator<ChunkTypeRequest> createDefaultComparator()
  {
    return null;
  }

  /**
   * provides a hook to set the slot values of the returned index chunk in case
   * they are recycled.. default impl does nothing
   * 
   * @param indexChunk
   * @param encodedChunk
   * @param originalRequest
   * @param expandedRequest
   */
  protected void fillIndexChunk(IChunk indexChunk, IChunk encodedChunk,
      ChunkTypeRequest originalRequest, ChunkTypeRequest expandedRequest)
  {

  }

  /**
   * hook to verify that an encoded chunk should be returned. this is the last
   * filter to be called on search results
   * 
   * @param encodedChunk
   * @param originalRequest
   * @return
   */
  protected boolean isAcceptable(IChunk encodedChunk,
      ChunkTypeRequest originalRequest)
  {
    return true;
  }

  @SuppressWarnings("unchecked")
  protected PerceptualSearchResult searchInternal(ChunkTypeRequest request)
  {
    /*
     * if the spec was empty, can we just take the first one that passes?
     */
    boolean earlyExit = request.getSlots().size() == 0;

    FastList<IIndexFilter> filters = FastList.newInstance();

    /*
     * build the temproary list of index filters
     */
    buildSort(request, filters);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Searching perceptual memory : " + request);

    /*
     * which then build the priority sort
     */
    ChainedComparator<ChunkTypeRequest> prioritySort = new ChainedComparator<ChunkTypeRequest>(
        false);
    for (IIndexFilter filter : filters)
    {
      Comparator<ChunkTypeRequest> comparator = filter.getComparator();
      if (comparator == null) continue;
      prioritySort.add(comparator);
    }

    /*
     * and if there is no comparator, use the default
     */
    if (prioritySort.size() == 0)
    {
      Comparator<ChunkTypeRequest> comparator = createDefaultComparator();
      if (comparator != null) prioritySort.add(comparator);
    }

    TreeMap<ChunkTypeRequest, PerceptualSearchResult> prioritizedResults = new TreeMap<ChunkTypeRequest, PerceptualSearchResult>(
        prioritySort);

    /*
     * now lets get the initial set based on feature maps
     */
    FastList<IFeatureMap> featureMaps = FastList.newInstance();
    getFeatureMaps(featureMaps);

    FastList<IIdentifier> candidateIdentifiers = FastList.newInstance();
    getCandidateIdentifiers(request, featureMaps, candidateIdentifiers);

    /*
     * now we need to check the actual chunks that have been encoded
     */
    IAfferentObjectManager objectManager = ACTRRuntime.getRuntime()
        .getConnector().getAgent(getModule().getModel())
        .getAfferentObjectManager();

    for (IIdentifier identifier : candidateIdentifiers)
    {
      // early exit
      if (earlyExit && prioritizedResults.size() > 1) break;

      // object no longer exists
      if (objectManager.get(identifier) == null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("No afferent object associated with " + identifier);
        continue;
      }

      for (PerceptualEncoderBridge bridge : _bridges)
      {
        IChunk encodedPercept = bridge.get(identifier, true);
        if (encodedPercept == null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(bridge.getEncoder() + " did not encode " + identifier);
          continue;
        }
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(bridge.getEncoder() + " encoded " + identifier);

        ChunkTypeRequest template = new ChunkTypeRequest(request.getChunkType());
        for (IFeatureMap featureMap : featureMaps)
          featureMap.fillSlotValues(template, identifier, encodedPercept,
              request);

        /*
         * now we need to make sure it is acceptable
         */
        boolean isAcceptable = true;
        for (IIndexFilter filter : filters)
          if (!filter.accept(template))
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(filter + " rejected " + template);
            isAcceptable = false;
            break;
          }

        if (!isAcceptable) continue;

        IChunk indexChunk = _indexManager.getIndexChunk(encodedPercept);
        if (indexChunk == null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("No index chunk availble for " + identifier);
          continue;
        }

        if (!isAcceptable(encodedPercept, request))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Perceptual memory rejected " + encodedPercept);
          continue;
        }

        /*
         * stash the result for sorting
         */
        PerceptualSearchResult result = new PerceptualSearchResult(
            encodedPercept, indexChunk, identifier, request, template);

        prioritizedResults.put(template, result);
      }
    }

    PerceptualSearchResult rtn = select(prioritizedResults);

    if (rtn != null)
    {
      fillIndexChunk(rtn.getLocation(), rtn.getPercept(), rtn.getRequest(), rtn
          .getLocationRequest());
      rtn.getLocation().setMetaData(SEARCH_RESULT_IDENTIFIER_KEY,
          rtn.getPerceptIdentifier());

      addRecentSearch(rtn);
    }

    FastList.recycle(candidateIdentifiers);
    FastList.recycle(featureMaps);
    FastList.recycle(filters);

    return rtn;
  }

  /**
   * select the best option. currently just uses the first key (if any)
   * 
   * @param results
   * @return
   */
  protected PerceptualSearchResult select(
      SortedMap<ChunkTypeRequest, PerceptualSearchResult> results)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("All results : " + results);
    if (results.size() == 0) return null;
    return results.get(results.firstKey());
  }

  protected void addRecentSearch(PerceptualSearchResult result)
  {
    int limit = 1;
    IFINSTFeatureMap f = getFINSTFeatureMap();
    if (f != null) limit = f.getMaximumFINSTs();

    synchronized (_recentResults)
    {
      limit--;

      /*
       * zip through the list looking for any other searches with the same
       * location and remove. any additional will be removed beyond limit
       */
      Iterator<PerceptualSearchResult> iterator = _recentResults.iterator();
      while (iterator.hasNext())
      {
        PerceptualSearchResult oldResult = iterator.next();

        if (limit <= 0 || oldResult.getLocation() == result.getLocation())
          iterator.remove();

        limit--;
      }

      _recentResults.add(0, result);
    }
  }

  public void getRecentSearchResults(List<PerceptualSearchResult> results)
  {
    synchronized (_recentResults)
    {
      results.addAll(_recentResults);
    }
  }

  public PerceptualSearchResult getLastSearchResult()
  {
    synchronized (_recentResults)
    {
      if (_recentResults.size() == 0) return null;
      return _recentResults.get(0);
    }
  }
}
