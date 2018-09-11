package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.manager.IAfferentObjectManager;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.ChainedComparator;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.core.utils.parameter.NumericParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.pm.IPerceptualModule;
import org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener;
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
  static private final transient Log                                           LOGGER             = LogFactory
                                                                                                      .getLog(AbstractPerceptualMemory.class);

  static public final String                                                   PRECODE_ONSET_TIME = ":precode-onset";

  private final Collection<IPerceptualEncoder>                                 _encoders;

  private final Collection<PerceptualEncoderBridge>                            _bridges;

  @SuppressWarnings("unchecked")
  private final Collection<IFeatureMap>                                        _featureMaps;

  private final Collection<IIndexFilter>                                       _filters;

  private IFINSTFeatureMap                                                     _finstFeatureMap;

  private int                                                                  _finstLimit        = 4;

  private double                                                               _finstDuration     = 3;

  private double                                                               _onsetDuration     = 0.5;

  private final IPerceptualModule                                              _module;

  private final IIndexManager                                                  _indexManager;

  private final ACTREventDispatcher<IPerceptualMemory, IActivePerceptListener> _dispatcher        = new ACTREventDispatcher<IPerceptualMemory, IActivePerceptListener>();

  private IAgent                                                               _agent;

  private DelayableAfferentObjectListener                                      _agentListener;

  private List<PerceptualSearchResult>                                         _recentResults;

  private Map<String, IChunk>                                                  _namedChunkCache   = new TreeMap<String, IChunk>();

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

  public int getFINSTLimit()
  {
    return _finstLimit;
  }

  public double getFINSTSpan()
  {
    return _finstDuration;
  }

  public double getNewFINSTOnsetDuration()
  {
    return _onsetDuration;
  }

  public void setFINSTLimit(int max)
  {
    _finstLimit = max;
    if (_finstFeatureMap != null) _finstFeatureMap.setMaximumFINSTs(max);
  }

  public void setFINSTSpan(double duration)
  {
    _finstDuration = duration;
  }

  public void setNewFINSTOnsetDuration(double duration)
  {
    _onsetDuration = duration;
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
  public void attach(IAgent agent)
  {
    if (_agent != null) throw new IllegalStateException("Already attached!");
    _agent = agent;
    _agentListener = new DelayableAfferentObjectListener(_module.getModel(),
        _agent, getModule().getCommonRealityExecutor());

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

  protected DefaultAfferentObjectListener getAfferentObjectListener()
  {
    return _agentListener;
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

  /**
   * this is the chunk that is used as an error code to invalidate
   * PerceptualSearchResults.
   * 
   * @return
   */
  abstract protected IChunk getRemovedErrorCodeChunk();

  public void addEncoder(IPerceptualEncoder encoder)
  {
    if (_agent != null)
      throw new IllegalStateException("Cannot add encoders to a running model");

    _encoders.add(encoder);
    PerceptualEncoderBridge bridge = new PerceptualEncoderBridge(encoder, this,
        getRemovedErrorCodeChunk());
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

      _finstFeatureMap.setMaximumFINSTs(getFINSTLimit());
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

  @SuppressWarnings({ "rawtypes" })
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

  public IIndexManager getIndexManager()
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

  public PerceptualSearchResult searchNow(ChunkTypeRequest request)
  {
    return searchInternal(request);
  }

  private void buildSort(ChunkTypeRequest request,
      Collection<IIndexFilter> container)
  {
    List<IIndexFilter> filters = FastListFactory.newInstance();
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

    FastListFactory.recycle(filters);

    container.addAll(sorted.values());
  }

  @SuppressWarnings("unchecked")
  private void getCandidateIdentifiers(ChunkTypeRequest request,
      Collection<IFeatureMap> featureMaps, Collection<IIdentifier> container)
  {
    Set<IIdentifier> candidates = FastSetFactory.newInstance();

    boolean firstRun = true;
    for (IFeatureMap featureMap : featureMaps)
      try
      {
        if (featureMap.isInterestedIn(request))
        {
          featureMap.normalizeRequest(request);

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
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to extract candidates from %s ",
            featureMap.getClass().getSimpleName()), e);
      }

    FastSetFactory.recycle(candidates);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning candidates : " + container);
  }

  /**
   * create a default comparator when none is specified from the search request.
   * by default this returns null. A good alternative is
   * {@link #createLatestOnsetComparator()}
   * 
   * @return null
   */
  protected Comparator<ChunkTypeRequest> createDefaultComparator()
  {

    return null;
  }

  protected Comparator<ChunkTypeRequest> createLatestOnsetComparator()
  {
    return new Comparator<ChunkTypeRequest>() {

      private double getPrecodeTime(ChunkTypeRequest request)
      {
        Collection<ISlot> container = FastCollectionFactory.newInstance();
        try
        {
          for (ISlot slot : request.getSlots(container))
            if (slot.getName().equals(PRECODE_ONSET_TIME))
              return ((Number) slot.getValue()).doubleValue();

          return Double.NEGATIVE_INFINITY;
        }
        catch (Exception e)
        {
          return Double.NEGATIVE_INFINITY;
        }
        finally
        {
          FastCollectionFactory.recycle(container);
        }
      }

      @Override
      public int compare(ChunkTypeRequest o1, ChunkTypeRequest o2)
      {
        double one = getPrecodeTime(o1);
        double two = getPrecodeTime(o2);
        return -Double.compare(one, two);
      }

    };
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

    List<IIndexFilter> filters = FastListFactory.newInstance();

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
        true);
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

    TreeMap<ChunkTypeRequest, Collection<PerceptualSearchResult>> prioritizedResults = new TreeMap<ChunkTypeRequest, Collection<PerceptualSearchResult>>(
        prioritySort);

    /*
     * now lets get the initial set based on feature maps
     */
    List<IFeatureMap> featureMaps = FastListFactory.newInstance();
    getFeatureMaps(featureMaps);

    List<IIdentifier> candidateIdentifiers = FastListFactory.newInstance();
    getCandidateIdentifiers(request, featureMaps, candidateIdentifiers);

    IAgent agent = ACTRRuntime.getRuntime().getConnector()
        .getAgent(getModule().getModel());

    if (agent == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(String
                .format("No IAgent found, model is in the midst of a shutdown. Ignoring request"));

      PerceptualSearchResult psr = new PerceptualSearchResult(null, null, null,
          request, request);
      psr.setErrorCode(getNamedChunk(IStatusBuffer.ERROR_UNKNOWN_CHUNK));
      return psr;
    }

    /*
     * now we need to check the actual chunks that have been encoded
     */
    IAfferentObjectManager objectManager = agent.getAfferentObjectManager();

    /**
     * nothing to match.
     */
    if (objectManager.getIdentifiers().size() == 0)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Nothing to match"));
      PerceptualSearchResult psr = new PerceptualSearchResult(null, null, null,
          request, request);
      psr.setErrorCode(getNamedChunk(IStatusBuffer.ERROR_NOTHING_AVAILABLE_CHUNK));
      return psr;
    }

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

        /*
         * explicitly add precode onset. except that this could screw up if
         * onsets actually span two cycles. This is not a problem with the lisp
         * since all updates are in the same cycle. But by defaulting to
         * prioritizing the most recent, we could get a literal recency effect.
         */
        template.addSlot(new BasicSlot(PRECODE_ONSET_TIME, encodedPercept
            .getMetaData(IPerceptualEncoder.COMMONREALITY_ONSET_TIME_KEY)));

        /*
         * we build up the templates based on the features of the object, the
         * encoding, and even the request (should need be) - but that can still
         * produce a stable ordering by the comparator, so we add some extra
         * entropy here to break things up.
         */
        // long random = (long) (Math.random() * System.nanoTime());
        // template.addSlot(new BasicSlot(String.format(":%d", random),
        // random));

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

        // performance optimization to prevent recalc
        template.lockHash();

        /**
         * if this has a value, the template was ranked as equivalent.
         */
        Collection<PerceptualSearchResult> equivalentResults = prioritizedResults
            .get(template);
        if (equivalentResults == null)
        {
          equivalentResults = FastListFactory.newInstance();
          prioritizedResults.put(template, equivalentResults);
        }
        equivalentResults.add(result);

      }
    }

    /**
     * no match
     */
    if (prioritizedResults.size() == 0)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Nothing matches %s", request));
      PerceptualSearchResult psr = new PerceptualSearchResult(null, null, null,
          request, request);
      psr.setErrorCode(getNamedChunk(IStatusBuffer.ERROR_NOTHING_MATCHES_CHUNK));
      return psr;
    }

    PerceptualSearchResult rtn = select(prioritizedResults.firstEntry()
        .getValue());

    if (rtn != null)
      try
      {
        fillIndexChunk(rtn.getLocation(), rtn.getPercept(), rtn.getRequest(),
            rtn.getLocationRequest());
        rtn.getLocation().setMetaData(SEARCH_RESULT_IDENTIFIER_KEY,
            rtn.getPerceptIdentifier());

        addRecentSearch(rtn);
      }
      catch (Exception e)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER
              .warn(
                  String
                      .format(
                          "Processing of search result (%s) @ %s of %s failed, returning null",
                          rtn.getRequest(), rtn.getLocation(),
                          rtn.getPerceptIdentifier()), e);
        rtn = null;
      }

    if (rtn == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Nothing matches %s", request));
      PerceptualSearchResult psr = new PerceptualSearchResult(null, null, null,
          request, request);
      psr.setErrorCode(getNamedChunk(IStatusBuffer.ERROR_NOTHING_MATCHES_CHUNK));
      return psr;
    }

    FastListFactory.recycle(candidateIdentifiers);
    FastListFactory.recycle(featureMaps);
    FastListFactory.recycle(filters);

    return rtn;
  }

  /**
   * select the best option. currently just uses the first key (if any)
   * 
   * @param results
   * @return
   */
  protected PerceptualSearchResult select(
      Collection<PerceptualSearchResult> results)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("All results : " + results);
    if (results.size() == 0) return null;
    return results.iterator().next();
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

  protected IChunk getNamedChunk(String name)
  {
    IChunk rtn = _namedChunkCache.get(name);
    if (rtn == null)
      try
      {
        rtn = _module.getModel().getDeclarativeModule().getChunk(name).get();
        _namedChunkCache.put(name, rtn);
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to get chunk %s from model", name),
            e);
      }
    return rtn;
  }

  public String getParameter(String key)
  {
    if (NEW_FINST_ONSET_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getNewFINSTOnsetDuration());
    else if (NUMBER_OF_FINSTS_PARAM.equalsIgnoreCase(key))
      return String.format("%d", getFINSTLimit());
    else if (FINST_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      return String.format("%f", getFINSTSpan());

    return null;
  }

  public void setParameter(String key, String value)
  {
    NumericParameterHandler nph = ParameterHandler.numberInstance();
    if (NEW_FINST_ONSET_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      setNewFINSTOnsetDuration(nph.coerce(value).doubleValue());
    else if (NUMBER_OF_FINSTS_PARAM.equalsIgnoreCase(key))
      setFINSTLimit(nph.coerce(value).intValue());
    else if (FINST_DURATION_TIME_PARAM.equalsIgnoreCase(key))
      setFINSTSpan(nph.coerce(value).doubleValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how set " + key + " = " + value);
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(NEW_FINST_ONSET_DURATION_TIME_PARAM,
        NUMBER_OF_FINSTS_PARAM, FINST_DURATION_TIME_PARAM);
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }
}
