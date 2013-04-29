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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.ChunkActivationComparator;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.module.retrieval.buffer.DefaultRetrievalBuffer6;
import org.jactr.core.module.retrieval.event.IRetrievalModuleListener;
import org.jactr.core.module.retrieval.event.RetrievalModuleEvent;
import org.jactr.core.module.retrieval.four.IRetrievalModule4;
import org.jactr.core.module.retrieval.time.DefaultRetrievalTimeEquation;
import org.jactr.core.module.retrieval.time.IRetrievalTimeEquation;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

public class DefaultRetrievalModule6 extends AbstractModule implements
    IRetrievalModule4, IParameterized
{
  /**
   * logger definition
   */
  static private final Log                                                LOGGER                           = LogFactory
                                                                                                               .getLog(DefaultRetrievalModule6.class);

  static public final String                                              INDEXED_RETRIEVALS_ENABLED_PARAM = "EnableIndexedRetrievals";

  private double                                                          _retrievalThreshold              = Double.NEGATIVE_INFINITY;

  private double                                                          _latencyFactor                   = 1;

  private double                                                          _latencyExponent                 = 1;

  private boolean                                                         _indexedRetrievalsEnabled        = false;

  private ACTREventDispatcher<IRetrievalModule, IRetrievalModuleListener> _eventDispatcher;

  private DefaultRetrievalBuffer6                                         _retrievalBuffer;


  private IRetrievalTimeEquation                                          _retrievalTimeEquation;

  static final protected ChunkActivationComparator                        _activationSorter                = new ChunkActivationComparator();

  public DefaultRetrievalModule6()
  {
    super("retrieval");
    _eventDispatcher = new ACTREventDispatcher<IRetrievalModule, IRetrievalModuleListener>();
    _retrievalTimeEquation = new DefaultRetrievalTimeEquation(this);
  }

  @Override
  public void dispose()
  {
    super.dispose();
    _eventDispatcher.clear();
    _retrievalBuffer.dispose();
    _retrievalTimeEquation = null;
  }

  protected @Override
  Collection<IActivationBuffer> createBuffers()
  {
    _retrievalBuffer = new DefaultRetrievalBuffer6(IActivationBuffer.RETRIEVAL,
        this);
    return Collections.singleton((IActivationBuffer) _retrievalBuffer);
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


  
  private boolean isPartialMatchingEnabled(IDeclarativeModule dm)
  {
    if(dm instanceof IDeclarativeModule4)
      return ((IDeclarativeModule4)dm).isPartialMatchingEnabled();
    return false;
  }

  protected IChunk retrieveChunkInternal(IDeclarativeModule dm,
      ChunkTypeRequest pattern) throws ExecutionException, InterruptedException
  {
    Future<Collection<IChunk>> fromDM = null;

    fireInitiated(pattern);
    
    if (isPartialMatchingEnabled(dm))
      fromDM = dm.findPartialMatches(pattern, _activationSorter,
          getRetrievalThreshold(), true);
    else
      fromDM = dm.findExactMatches(pattern, _activationSorter,
          getRetrievalThreshold(), true);

    Collection<IChunk> results = fromDM.get();
    IChunk retrievalResult = dm.getErrorChunk();

    if (results.size() != 0) retrievalResult = results.iterator().next();

    fireCompleted(pattern, retrievalResult);

    return retrievalResult;
  }

  public Future<IChunk> retrieveChunk(final ChunkTypeRequest chunkPattern)
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

  protected void fireCompleted(ChunkTypeRequest pattern, IChunk chunk)
  {
    _eventDispatcher.fire(new RetrievalModuleEvent(this, pattern, chunk));
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
      setIndexedRetrievalEnabled(ParameterHandler.booleanInstance().coerce(
          value).booleanValue());
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to set " + key + " to " + value);

  }
  
  public void reset()
  {
    _retrievalBuffer.clear();
  }

}
