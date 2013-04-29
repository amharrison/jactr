/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.declarative.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.basic.AbstractChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.ChunkListenerAdaptor;
import org.jactr.core.chunk.five.DefaultChunk5;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.five.DefaultChunkType5;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.basic.DefaultDeclarativeModule;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.five.IDeclarativeModule5;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ChunkSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.StringUtilities;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

public class DefaultDeclarativeModule6 extends DefaultDeclarativeModule implements
    IDeclarativeModule, IDeclarativeModule4, IDeclarativeModule5,
    IParameterized
{
  /**
   * logger definition
   */
  static final Log                                                              LOGGER                  = LogFactory
                                                                                                            .getLog(DefaultDeclarativeModule6.class);

  static private boolean              _warnedAboutMerging = false;
  
  protected double                                                              _activationNoise;

  protected double                                                              _permanentActivationNoise;

  protected boolean                                                             _partialMatchingEnabled = false;

  protected double                                                              _mismatchPenalty;

  protected double                                                              _baseLevelConstant;

  protected double                                                              _maximumSimilarity;

  protected double                                                              _maximumDifference;

  protected Map<Pair, Double>                                                   _similarities;

 

  public DefaultDeclarativeModule6()
  {
    super();
    _similarities = new HashMap<Pair, Double>();
  }

  @Override
  synchronized public void dispose()
  {
    try
    {
      _chunkLock.writeLock().lock();
      _similarities.clear();
      _similarities = null;
    }
    finally
    {
      _chunkLock.writeLock().unlock();
    }

    super.dispose();
  }

  

  /**
   * actually do the work.
   * 
   * @param originalChunk
   * @param newChunk
   */
  protected void mergeChunks(IChunk originalChunk, IChunk newChunk)
  {
    ISubsymbolicChunk oSC = originalChunk.getSubsymbolicChunk();

    
    if (oSC instanceof ISubsymbolicChunk5)
    {
      
      if (LOGGER.isWarnEnabled() && !_warnedAboutMerging)
      {
        LOGGER.warn("Merging of similarities temporarily not working");
        _warnedAboutMerging = true;
      }
      
    }

    super.mergeChunks(originalChunk, newChunk);
  }

  

  public double getActivationNoise()
  {
    return _activationNoise;
  }

  public double getPermanentActivationNoise()
  {
    return _permanentActivationNoise;
  }

  public boolean isPartialMatchingEnabled()
  {
    return _partialMatchingEnabled;
  }

  public void setActivationNoise(double noise)
  {
    double old = _activationNoise;
    _activationNoise = noise;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, ACTIVATION_NOISE,
          old, noise));
  }

  public void setPartialMatchingEnabled(boolean enable)
  {
    boolean old = _partialMatchingEnabled;
    _partialMatchingEnabled = enable;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, PARTIAL_MATCHING,
          old, enable));
  }

  public void setPermanentActivationNoise(double noise)
  {
    double old = _permanentActivationNoise;
    _permanentActivationNoise = noise;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this,
          PERMANENT_ACTIVATION_NOISE, old, noise));
  }

  public double getMismatchPenalty()
  {
    return _mismatchPenalty;
  }

  public void setMismatchPenalty(double mismatch)
  {
    double old = _mismatchPenalty;
    _mismatchPenalty = mismatch;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, MISMATCH_PENALTY,
          old, mismatch));
  }

  public double getMaximumDifference()
  {
    return _maximumDifference;
  }

  public double getMaximumSimilarity()
  {
    return _maximumSimilarity;
  }

  public void setMaximumDifference(double maxDiff)
  {
    double old = _maximumDifference;
    _maximumDifference = maxDiff;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this,
          MAXIMUM_DIFFERENCE, old, maxDiff));
  }

  public void setMaximumSimilarity(double maxSim)
  {
    double old = _maximumSimilarity;
    _maximumSimilarity = maxSim;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this,
          MAXIMUM_SIMILARITY, old, maxSim));
  }

  public double getBaseLevelConstant()
  {
    return _baseLevelConstant;
  }

  public void setBaseLevelConstant(double base)
  {
    double old = _baseLevelConstant;
    _baseLevelConstant = base;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this,
          BASE_LEVEL_CONSTANT, old, _baseLevelConstant));
  }

  public double getSimilarity(Object one, Object two)
  {
    Pair tmp = new Pair(one, two);
    if (_similarities.containsKey(tmp)) return _similarities.get(tmp);

    return _maximumDifference;
  }

  public void setSimilarity(Object one, Object two, double sim)
  {
    _similarities.put(new Pair(one, two), sim);
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (PARTIAL_MATCHING.equalsIgnoreCase(key))
      return "" + isPartialMatchingEnabled();
    if (BASE_LEVEL_CONSTANT.equalsIgnoreCase(key))
      return "" + getBaseLevelConstant();
    if (ACTIVATION_NOISE.equalsIgnoreCase(key))
      return "" + getActivationNoise();
    if (PERMANENT_ACTIVATION_NOISE.equalsIgnoreCase(key))
      return "" + getPermanentActivationNoise();
    if (MISMATCH_PENALTY.equalsIgnoreCase(key))
      return "" + getMismatchPenalty();
    if (MAXIMUM_DIFFERENCE.equalsIgnoreCase(key))
      return "" + getMaximumDifference();
    if (MAXIMUM_SIMILARITY.equalsIgnoreCase(key))
      return "" + getMaximumSimilarity();
    return super.getParameter(key);
  }


  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(PARTIAL_MATCHING);
    rtn.add(BASE_LEVEL_CONSTANT);
    rtn.add(ACTIVATION_NOISE);
    rtn.add(PERMANENT_ACTIVATION_NOISE);
    rtn.add(MISMATCH_PENALTY);
    rtn.add(MAXIMUM_DIFFERENCE);
    rtn.add(MAXIMUM_SIMILARITY);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (PARTIAL_MATCHING.equalsIgnoreCase(key))
      setPartialMatchingEnabled(ParameterHandler.booleanInstance()
          .coerce(value));
    else if (BASE_LEVEL_CONSTANT.equalsIgnoreCase(key))
      setBaseLevelConstant(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (ACTIVATION_NOISE.equalsIgnoreCase(key))
      setActivationNoise(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (PERMANENT_ACTIVATION_NOISE.equalsIgnoreCase(key))
      setPermanentActivationNoise(ParameterHandler.numberInstance().coerce(
          value).doubleValue());
    else if (MISMATCH_PENALTY.equalsIgnoreCase(key))
      setMismatchPenalty(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (MAXIMUM_DIFFERENCE.equalsIgnoreCase(key))
      setMaximumDifference(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (MAXIMUM_SIMILARITY.equalsIgnoreCase(key))
      setMaximumSimilarity(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else 
      super.setParameter(key, value);
  }

}
