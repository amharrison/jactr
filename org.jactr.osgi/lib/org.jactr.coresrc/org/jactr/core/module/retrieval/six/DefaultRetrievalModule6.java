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
package org.jactr.core.module.retrieval.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.ChunkActivationComparator;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.search.filter.ActivationFilter;
import org.jactr.core.module.declarative.search.filter.ActivationPolicy;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.module.declarative.search.filter.ILoggedChunkFilter;
import org.jactr.core.module.declarative.search.filter.PartialMatchActivationFilter;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.module.retrieval.buffer.DefaultRetrievalBuffer6;
import org.jactr.core.module.retrieval.buffer.RetrievalRequestDelegate;
import org.jactr.core.module.retrieval.event.IRetrievalModuleListener;
import org.jactr.core.module.retrieval.event.RetrievalModuleEvent;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jactr.core.module.retrieval.time.DefaultRetrievalTimeEquation;
import org.jactr.core.module.retrieval.time.IRetrievalTimeEquation;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.collections.SkipListSetFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * default retrieval buffer
 * 
 * @see http://jactr.org/node/33
 * @author harrison
 */
public class DefaultRetrievalModule6 extends AbstractModule implements
    IRetrievalModule4, IParameterized
{
  /**
   * logger definition
   */
  static private final Log                                                LOGGER                           = LogFactory
                                                                                                               .getLog(DefaultRetrievalModule6.class);

  static public final String                                              INDEXED_RETRIEVALS_ENABLED_PARAM = "EnableIndexedRetrievals";

  static public final String                                              RECENTLY_RETRIEVED_SLOT          = ":recently-retrieved";

  static public final String                                              RETRIEVAL_THRESHOLD_SLOT         = ":retrievalThreshold";

  static public final String                                              PARTIAL_MATCH_SLOT               = ":partialMatch";

  static public final String                                              ACCESSIBILITY_SLOT               = ":accessibility";

  static public final String                                              RETRIEVAL_TIME_SLOT              = ":retrievalTime";

  static public final String                                              INDEXED_RETRIEVAL_SLOT           = ":indexedRetrieval";

  private double                                                          _retrievalThreshold              = Double.NEGATIVE_INFINITY;

  private double                                                          _latencyFactor                   = 1;

  private double                                                          _latencyExponent                 = 1;

  private boolean                                                         _indexedRetrievalsEnabled        = false;

  private ACTREventDispatcher<IRetrievalModule, IRetrievalModuleListener> _eventDispatcher;

  private DefaultRetrievalBuffer6                                         _retrievalBuffer;

  private IRetrievalTimeEquation                                          _retrievalTimeEquation;

  private DeclarativeFINSTManager                                         _finstManager;

  private IChunk                                                          _resetChunk;

  static final protected ChunkActivationComparator                        _activationSorter                = new ChunkActivationComparator();

  public DefaultRetrievalModule6()
  {
    this(IActivationBuffer.RETRIEVAL);
  }

  protected DefaultRetrievalModule6(String moduleName)
  {
    super(moduleName);
    _eventDispatcher = new ACTREventDispatcher<IRetrievalModule, IRetrievalModuleListener>();
    _retrievalTimeEquation = new DefaultRetrievalTimeEquation(this);
    setFINSTManager(new DeclarativeFINSTManager(this));
  }

  @Override
  public void dispose()
  {
    super.dispose();
    _eventDispatcher.clear();
    _retrievalBuffer.dispose();
    _retrievalTimeEquation = null;
  }

  protected @Override Collection<IActivationBuffer> createBuffers()
  {
    _retrievalBuffer = new DefaultRetrievalBuffer6(getName(), this);
    return Collections.singleton((IActivationBuffer) _retrievalBuffer);
  }

  public DeclarativeFINSTManager getFINSTManager()
  {
    return _finstManager;
  }

  public void setFINSTManager(DeclarativeFINSTManager manager)
  {
    if (manager == null)
      throw new IllegalArgumentException("FINST manager cannot be null");
    _finstManager = manager;
  }

  public boolean hasBeenRetrieved(IChunk chunk)
  {
    return _finstManager.hasBeenRetrieved(chunk);
  }

  public boolean isIndexedRetrievalEnabled()
  {
    return _indexedRetrievalsEnabled;
  }

  public void setIndexedRetrievalEnabled(boolean enabled)
  {
    _indexedRetrievalsEnabled = enabled;
  }

  public double getRetrievalThreshold()
  {
    return _retrievalThreshold;
  }

  private boolean isPartialMatchingEnabled(IDeclarativeModule dm,
      Collection<? extends ISlot> slots)
  {
    dm = dm.getAdapter(IDeclarativeModule4.class);
    if (dm instanceof IDeclarativeModule4)
      return RetrievalRequestDelegate.isPartialMatchEnabled(
          (IDeclarativeModule4) dm, slots);
    return false;
  }

  private double getThreshold(Collection<? extends ISlot> slots)
  {
    return RetrievalRequestDelegate.getThreshold(this, slots);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected IChunk retrieveChunkInternal(IDeclarativeModule dm,
      ChunkTypeRequest pattern) throws ExecutionException, InterruptedException
  {
    Future<Collection<IChunk>> fromDM = null;

    fireInitiated(pattern);

    List<ISlot> slots = FastListFactory.newInstance();
    pattern.getSlots(slots);

    // this must be vestigial code, but from when?
    // for (IConditionalSlot cSlot : pattern.getConditionalSlots())
    // if (cSlot.getName().equals(RECENTLY_RETRIEVED_SLOT)) break;

    ChunkTypeRequest cleanedPattern = cleanPattern(pattern);

    _activationSorter.setChunkTypeRequest(null);
    IChunkFilter filter = null;
    double threshold = getThreshold(slots);
    ActivationPolicy accessibility = RetrievalRequestDelegate
        .getActivationPolicy(ACCESSIBILITY_SLOT, slots);
    boolean wasIndexed = RetrievalRequestDelegate.isIndexRetrievalEnabled(
        dm.getAdapter(DefaultRetrievalModule6.class), slots);

    if (isPartialMatchingEnabled(dm, slots))
    {
      /**
       * set the reference pattern for partial matching discounting, which is
       * actually done in the comparator.
       */

      _activationSorter.setChunkTypeRequest(cleanedPattern);
      filter = new PartialMatchActivationFilter(accessibility, cleanedPattern,
          threshold, Logger.hasLoggers(getModel()));
      fromDM = dm.findPartialMatches(cleanedPattern, _activationSorter, filter);
    }
    else
    {
      filter = new ActivationFilter(accessibility, threshold,
          Logger.hasLoggers(getModel()));
      fromDM = dm.findExactMatches(cleanedPattern, _activationSorter, filter);
    }

    FastListFactory.recycle(slots);

    Collection<IChunk> results = fromDM.get();

    /*
     * snag the message from the filter.
     */
    if (filter instanceof ILoggedChunkFilter)
      if (Logger.hasLoggers(getModel()))
        Logger.log(getModel(), Logger.Stream.RETRIEVAL,
            ((ILoggedChunkFilter) filter).getMessageBuilder().toString());

    IChunk retrievalResult = selectRetrieval(results, dm.getErrorChunk(),
        pattern, cleanedPattern);

    // we should check to see if this is an indexed, as their time is immediate
    double retrievalTime = 0;
    if (!wasIndexed)
      retrievalTime = getRetrievalTimeEquation().computeRetrievalTime(
          retrievalResult, pattern);

    fireCompleted(pattern, retrievalResult, retrievalTime, results);

    // now we can recycle the collection
    if (results instanceof ConcurrentSkipListSet)
      SkipListSetFactory.recycle((ConcurrentSkipListSet) results);

    return retrievalResult;
  }

  /**
   * strip pattern of all the meta-slots
   * 
   * @param pattern
   * @return
   */
  private ChunkTypeRequest cleanPattern(ChunkTypeRequest pattern)
  {
    List<ISlot> slots = FastListFactory.newInstance();
    pattern.getSlots(slots);
    List<ISlot> cleanSlots = FastListFactory.newInstance();

    for (ISlot cSlot : slots)
      if (!cSlot.getName().startsWith(":")) cleanSlots.add(cSlot);

    if (cleanSlots.size() != slots.size())
      if (pattern instanceof ChunkTypeRequest)
        pattern = new ChunkTypeRequest(pattern.getChunkType(), cleanSlots);
      else if (pattern instanceof ChunkRequest)
        pattern = new ChunkRequest(((ChunkRequest) pattern).getChunk(),
            cleanSlots);

    FastListFactory.recycle(slots);
    FastListFactory.recycle(cleanSlots);

    return pattern;
  }

  /**
   * choose the best matching result from the colleciton
   * 
   * @param results
   * @param errorChunk
   * @param originalRequest
   * @return
   */
  protected IChunk selectRetrieval(Collection<IChunk> results,
      IChunk errorChunk, ChunkTypeRequest originalRequest,
      ChunkTypeRequest cleanedRequest)
  {
    if (results.size() == 0) return errorChunk;

    /*
     * now we need to check to see if the pattern included recently retrieved
     */
    boolean recentlySpecified = false;
    boolean ignoreRecent = false;
    for (IConditionalSlot cSlot : originalRequest.getConditionalSlots())
      if (cSlot.getName().equals(RECENTLY_RETRIEVED_SLOT))
      {
        recentlySpecified = true;
        /*
         * null, false, or != true results in ignoring recent
         */
        Object value = cSlot.getValue();

        /*
         * crappy compatibility support for clearing.. the prefered method is to
         * use +retrieval> isa clear full true
         */
        if (_resetChunk.equals(value)
            && cSlot.getCondition() == IConditionalSlot.EQUALS)
        {
          _finstManager.clearRecentRetrievals();
          recentlySpecified = false;
          if (Logger.hasLoggers(getModel()))
            Logger
                .log(
                    getModel(),
                    Logger.Stream.RETRIEVAL,
                    String
                        .format(
                            "%s reset is archaic, use +retrieval> isa clear full t instead",
                            RECENTLY_RETRIEVED_SLOT));
        }
        else
        {
          ignoreRecent = cSlot.getCondition() == IConditionalSlot.EQUALS
              && (value == null || Boolean.FALSE.equals(value))
              || cSlot.getCondition() == IConditionalSlot.NOT_EQUALS
              && Boolean.TRUE.equals(value);

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format("%s ignoring recently retrieved %s",
                cSlot, ignoreRecent));
        }
      }

    // just return the most active
    if (!recentlySpecified) return results.iterator().next();

    // now we have to iterate and filter
    for (IChunk chunk : results)
      if (ignoreRecent)
      {
        if (!_finstManager.hasBeenRetrieved(chunk))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "%s has not been recently retrieved, returning", chunk));

          if (Logger.hasLoggers(getModel()))
            Logger.log(getModel(), Logger.Stream.RETRIEVAL, String.format(
                "%s was not recently retrieved. Selecting.", chunk));
          return chunk;
        }
        else
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String.format(
                "%s has been recently retrieved, ignoring", chunk));

          if (Logger.hasLoggers(getModel()))
            Logger.log(getModel(), Logger.Stream.RETRIEVAL,
                String.format("%s was recently retrieved. Ignoring.", chunk));
        }
      }
      else if (_finstManager.hasBeenRetrieved(chunk))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "%s has been recently retrieved. Selecting.", chunk));

        if (Logger.hasLoggers(getModel()))
          Logger.log(getModel(), Logger.Stream.RETRIEVAL,
              String.format("%s was recently retrieved. Selecting.", chunk));

        return chunk;
      }
      else
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "%s has not been recently retrieved, ignoring", chunk));
        if (Logger.hasLoggers(getModel()))
          Logger.log(getModel(), Logger.Stream.RETRIEVAL,
              String.format("%s was not recently retrieved. Ignoring.", chunk));
      }

    if (LOGGER.isDebugEnabled())
      LOGGER
          .debug(String
              .format("No results matched the recently retrieved specification, returning error"));
    return errorChunk;
  }

  public CompletableFuture<IChunk> retrieveChunk(
      final ChunkTypeRequest chunkPattern)
  {
    return delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        return retrieveChunkInternal(getModel().getDeclarativeModule(),
            chunkPattern);
      }

    }, getExecutor());
  }

  public void setRetrievalThreshold(double threshold)
  {
    _retrievalThreshold = threshold;
  }

  protected void fireInitiated(ChunkTypeRequest pattern)
  {
    _eventDispatcher.fire(new RetrievalModuleEvent(this, pattern));
  }

  protected void fireCompleted(ChunkTypeRequest pattern, IChunk chunk,
      double retrievalTime, Collection<IChunk> allCandidates)
  {
    _eventDispatcher.fire(new RetrievalModuleEvent(this, pattern, chunk,
        retrievalTime, allCandidates));
  }

  public void addListener(IRetrievalModuleListener listener, Executor executor)
  {
    _eventDispatcher.addListener(listener, executor);
  }

  public void removeListener(IRetrievalModuleListener listener)
  {
    _eventDispatcher.removeListener(listener);
  }

  @Override
  public void initialize()
  {
    try
    {
      _resetChunk = getModel().getDeclarativeModule().getChunk("reset").get();
    }
    catch (Exception e)
    {
      throw new IllegalModuleStateException("Could not get reset chunk", e);
    }

    _finstManager.setErrorChunk(getModel().getDeclarativeModule()
        .getErrorChunk());
  }

  public IRetrievalTimeEquation getRetrievalTimeEquation()
  {
    return _retrievalTimeEquation;
  }

  public double getLatencyExponent()
  {
    return _latencyExponent;
  }

  public double getLatencyFactor()
  {
    return _latencyFactor;
  }

  public void setLatencyExponent(double exp)
  {
    _latencyExponent = exp;
  }

  public void setLatencyFactor(double fact)
  {
    _latencyFactor = fact;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (RETRIEVAL_THRESHOLD.equalsIgnoreCase(key))
      return "" + getRetrievalThreshold();
    if (LATENCY_EXPONENT.equalsIgnoreCase(key))
      return "" + getLatencyExponent();
    if (LATENCY_FACTOR.equalsIgnoreCase(key)) return "" + getLatencyFactor();
    if (INDEXED_RETRIEVALS_ENABLED_PARAM.equalsIgnoreCase(key))
      return "" + isIndexedRetrievalEnabled();
    if (DeclarativeFINSTManager.FINST_DURATION_PARAM.equalsIgnoreCase(key))
      return "" + _finstManager.getFINSTDuration();
    if (DeclarativeFINSTManager.NUMBER_OF_FINSTS_PARAM.equalsIgnoreCase(key))
      return "" + _finstManager.getNumberOfFINSTs();
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(RETRIEVAL_THRESHOLD);
    rtn.add(LATENCY_EXPONENT);
    rtn.add(LATENCY_FACTOR);
    rtn.add(INDEXED_RETRIEVALS_ENABLED_PARAM);
    rtn.add(DeclarativeFINSTManager.FINST_DURATION_PARAM);
    rtn.add(DeclarativeFINSTManager.NUMBER_OF_FINSTS_PARAM);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (RETRIEVAL_THRESHOLD.equalsIgnoreCase(key))
      setRetrievalThreshold(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (LATENCY_EXPONENT.equalsIgnoreCase(key))
      setLatencyExponent(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (LATENCY_FACTOR.equalsIgnoreCase(key))
      setLatencyFactor(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (INDEXED_RETRIEVALS_ENABLED_PARAM.equalsIgnoreCase(key))
      setIndexedRetrievalEnabled(ParameterHandler.booleanInstance()
          .coerce(value).booleanValue());
    else if (DeclarativeFINSTManager.FINST_DURATION_PARAM.equalsIgnoreCase(key))
      _finstManager.setFINSTDuration(ParameterHandler.numberInstance()
          .coerce(value).doubleValue());
    else if (DeclarativeFINSTManager.NUMBER_OF_FINSTS_PARAM
        .equalsIgnoreCase(key))
      _finstManager.setNumberOfFINSTs(ParameterHandler.numberInstance()
          .coerce(value).intValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format(
          "%s doesn't recognize %s. Available parameters : %s", getClass()
              .getSimpleName(), key, getSetableParameters()));

  }

  public void reset()
  {
    reset(false);
  }

  public void reset(boolean resetFinsts)
  {
    _retrievalBuffer.clear();
    if (resetFinsts) _finstManager.clearRecentRetrievals();
  }

}
