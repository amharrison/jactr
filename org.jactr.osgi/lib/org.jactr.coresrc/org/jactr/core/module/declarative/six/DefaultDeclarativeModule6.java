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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.four.ISubsymbolicChunk4;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.basic.DefaultDeclarativeModule;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.five.IDeclarativeModule5;
import org.jactr.core.module.declarative.four.DefaultBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.DefaultRandomActivationEquation;
import org.jactr.core.module.declarative.four.DefaultSpreadingActivationEquation;
import org.jactr.core.module.declarative.four.IBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.declarative.four.IRandomActivationEquation;
import org.jactr.core.module.declarative.four.ISpreadingActivationEquation;
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.core.utils.references.IOptimizedReferences;
import org.jactr.core.utils.references.IReferences;

/**
 * Default declarative module for ACT-R 6. <br/>
 * This module uses the {@link IDeclarativeConfigurator} to set the chunk's
 * various equations ({@link IBaseLevelActivationEquation},
 * {@link ISpreadingActivationEquation}, {@link IRandomActivationEquation}).
 * Clients extending this should be sure to delegate to the original
 * configurator as to keep this functionality ( {@link #getConfigurator()}). <h3>
 * Parameters</h3>
 * <ul>
 * <li><b>EnablePartialMatching</b> : Turn on partial matching in searches
 * (values:true/false. default: false)
 * <li><b>ActivationNoise</b> : Transient noise added to activations (value:
 * numeric. default: 0) (note: random module must be present)
 * <li><b>PermanentActivationNoise</b> : Permanent noise added to activations
 * (value: numeric. default:0) (note: random module must be present)
 * <li><b>BaseLevelConstant</b> : Constant added to base level activations
 * (value: numeric. default: 0)
 * <li><b>MismatchPenalty</b> : Penalty applied to all partial match scores on a
 * per mismatch basis (value: numeric. default: 0) (partial matching must be
 * enabled)
 * <li><b>MaximumSimilarity</b> : Similarity score between two perfectly matched
 * slot values (value: numeric. default:0)
 * <li><b>MaximumDifference</b> : Similarity score between two perfectly
 * mismatched slot values (value: numeric. default:0)
 * </ul>
 * 
 * @see http://jactr.org/node/87
 * @author harrison
 */
public class DefaultDeclarativeModule6 extends DefaultDeclarativeModule
    implements IDeclarativeModule, IDeclarativeModule4, IDeclarativeModule5,
    IParameterized
{
  /**
   * logger definition
   */
  static final Log                     LOGGER                  = LogFactory
                                                                   .getLog(DefaultDeclarativeModule6.class);

  static private boolean               _subsymbolicWarning     = false;

  protected double                     _activationNoise;

  protected double                     _permanentActivationNoise;

  protected boolean                    _partialMatchingEnabled = false;

  protected double                     _mismatchPenalty;

  protected double                     _baseLevelConstant;

  protected double                     _maximumSimilarity;

  protected double                     _maximumDifference;

  protected Map<Pair, Double>          _similarities;

  private IRandomActivationEquation    _randomActivationEquation;

  private IBaseLevelActivationEquation _baseLevelActivationEquation;

  private ISpreadingActivationEquation _spreadingActivationEquation;

  private int                          _optimizationLevel      = 0;

  public DefaultDeclarativeModule6()
  {
    super();
    _similarities = new HashMap<Pair, Double>();
  }

  /**
   * make sure that the appropriate activation equations are configured for the
   * chunks
   */
  @Override
  protected void configure(IChunk newChunk)
  {
    if (_baseLevelActivationEquation == null)
    {
      /*
       * now let's check for a learning module.. particularly one that has base
       * level
       */
      IDeclarativeLearningModule4 decLM = (IDeclarativeLearningModule4) getModel()
          .getModule(IDeclarativeLearningModule4.class);
      if (decLM != null)
      {
        _baseLevelActivationEquation = decLM.getBaseLevelActivationEquation();
        _optimizationLevel = decLM.getOptimizationLevel();
      }
      else
        _baseLevelActivationEquation = new DefaultBaseLevelActivationEquation(
            DefaultDeclarativeModule6.this);
    }

    if (_spreadingActivationEquation == null)
      _spreadingActivationEquation = new DefaultSpreadingActivationEquation();

    if (_randomActivationEquation == null)
    {
      IRandomModule random = (IRandomModule) getModel().getModule(
          IRandomModule.class);
      _randomActivationEquation = new DefaultRandomActivationEquation(random,
          DefaultDeclarativeModule6.this);
    }

    /*
     * set the equations to be used
     */
    ISubsymbolicChunk4 ssc = (ISubsymbolicChunk4) newChunk
        .getSubsymbolicChunk().getAdapter(ISubsymbolicChunk4.class);

    if (ssc != null)
    {
      ssc.setBaseLevelActivationEquation(_baseLevelActivationEquation);
      ssc.setSpreadingActivationEquation(_spreadingActivationEquation);
      ssc.setRandomActivationEquation(_randomActivationEquation);

      IReferences references = ssc.getReferences();
      if (references instanceof IOptimizedReferences)
        ((IOptimizedReferences) references)
            .setOptimizationLevel(_optimizationLevel);
    }
    else if (LOGGER.isWarnEnabled() && !_subsymbolicWarning)
    {
      LOGGER
          .warn(String
              .format(
                  "%s is designed for chunks with subsymbolics derived from ISubsymbolicChunk4",
                  getClass().getSimpleName()));
      _subsymbolicWarning = true;
    }


    super.configure(newChunk);
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
      dispatch(new DeclarativeModuleEvent(this, ACTIVATION_NOISE, old, noise));
  }

  public void setPartialMatchingEnabled(boolean enable)
  {
    boolean old = _partialMatchingEnabled;
    _partialMatchingEnabled = enable;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, PARTIAL_MATCHING, old, enable));
  }

  public void setPermanentActivationNoise(double noise)
  {
    double old = _permanentActivationNoise;
    _permanentActivationNoise = noise;

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, PERMANENT_ACTIVATION_NOISE,
          old, noise));
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
      dispatch(new DeclarativeModuleEvent(this, MISMATCH_PENALTY, old, mismatch));
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

    if (_maximumDifference > 0)
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("MaximumDifference should be <= 0"));

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, MAXIMUM_DIFFERENCE, old,
          maxDiff));
  }

  public void setMaximumSimilarity(double maxSim)
  {
    double old = _maximumSimilarity;
    _maximumSimilarity = maxSim;

    if (_maximumSimilarity < 0)
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("MaximumSimilarity should be >=0"));

    if (hasListeners())
      dispatch(new DeclarativeModuleEvent(this, MAXIMUM_SIMILARITY, old, maxSim));
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
      dispatch(new DeclarativeModuleEvent(this, BASE_LEVEL_CONSTANT, old,
          _baseLevelConstant));
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
  @Override
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
  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = super.getSetableParameters();
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
  @Override
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
      setPermanentActivationNoise(ParameterHandler.numberInstance()
          .coerce(value).doubleValue());
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
