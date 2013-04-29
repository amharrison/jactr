/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.module.declarative.four.learning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.module.random.six.DefaultRandomModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.references.IOptimizedReferences;
import org.jactr.core.utils.references.IReferences;

/**
 * default impl of the base level activation equation as defined in atomic
 * components of thought
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class DefaultBaseLevelActivationEquation implements
    IBaseLevelActivationEquation
{

  private static transient Log LOGGER = LogFactory
                                          .getLog(DefaultBaseLevelActivationEquation.class
                                              .getName());

  IDeclarativeLearningModule4  _declarativeLearningModule;

  IProceduralModule            _proceduralModule;

  IDeclarativeModule4          _declarativeModule;

  IRandomModule                _randomModule;

  private IModel               _model;

  public DefaultBaseLevelActivationEquation(IModel model)
  {
    _model = model;
  }

  private IProceduralModule getProceduralModule()
  {
    if (_proceduralModule == null)
    {
      _proceduralModule = _model.getProceduralModule();

      if (_proceduralModule == null)
        throw new RuntimeException(this.getClass().getSimpleName()
            + " depends upon IProceduralModule");
    }

    return _proceduralModule;
  }

  private IDeclarativeModule4 getDeclarativeModule()
  {
    if (_declarativeModule == null)
    {
      _declarativeModule = (IDeclarativeModule4) _model
          .getModule(IDeclarativeModule4.class);
      if (_declarativeModule == null)
        throw new RuntimeException(this.getClass().getSimpleName()
            + " depends upon IDeclarativeModule4");
    }
    return _declarativeModule;
  }

  private IDeclarativeLearningModule4 getDeclarativeLearningModule()
  {
    if (_declarativeLearningModule == null)
    {
      _declarativeLearningModule = (IDeclarativeLearningModule4) _model
          .getModule(IDeclarativeLearningModule4.class);
      if (_declarativeLearningModule == null)
        throw new RuntimeException(this.getClass().getSimpleName()
            + " depends upon IDeclarativeLearningModule4");
    }
    return _declarativeLearningModule;
  }

  private IRandomModule getRandomModule()
  {
    if (_randomModule == null)
    {
      _randomModule = (IRandomModule) _model.getModule(IRandomModule.class);
      if (_randomModule == null)
        _randomModule = DefaultRandomModule.getInstance();
    }

    return _randomModule;
  }

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   * @param c
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeBaseLevelActivation(IModel model, IChunk c)
  {
    IDeclarativeModule4 declarativeModule = getDeclarativeModule();
    IDeclarativeLearningModule4 declarativeLearningModule = getDeclarativeLearningModule();

    ISubsymbolicChunk ssc = c.getSubsymbolicChunk();
    double baseLevelActivation = 0;
    double noiseActivation = 0;

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Computing activation for " + c);
    if (declarativeLearningModule.isBaseLevelLearningEnabled())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("base level learning is enabled, calculating");
      double now = ACTRRuntime.getRuntime().getClock(model).getTime();
      // compute base level activation
      double minusD = -1.0 * declarativeLearningModule.getBaseLevelLearning();
      double defAct = getProceduralModule().getDefaultProductionFiringTime();
      double base = 0.0;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("defAct=" + defAct + " minusD=" + minusD);

      IReferences referenceList = ssc.getReferences();
      int optimization = 0;

      if (referenceList instanceof IOptimizedReferences)
        optimization = ((IOptimizedReferences) referenceList)
            .getOptimizationLevel();

      // exact portion
      double[] times = referenceList.getRelativeTimes(now);

      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug("Snagged " + times.length + " delta access times for " + c);

      if (optimization != 0)
      {
        for (double element : times)
        {
          double tMinusD = Math.pow(Math.max(defAct, element), minusD);
          base += tMinusD;
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("tMinusD for " + element + " = " + tMinusD
                + " base = " + base);
        }

        if (times.length == 0) base = Math.pow(defAct, minusD);
      }

      baseLevelActivation = base;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("NoOpt-BaseLevelActivation " + baseLevelActivation
            + " with " + times.length + " accesses");

      /*
       * optimized is : 1-d 1-d (n-m)(t0 - tn-m ) ----------------------
       * (1-d)(t0 - tn-m) where m is the optimization level (# of reference
       * times stored) n is the number of references total t0 is the creation
       * time tn-m is the largest time differential in the references stored
       */

      long nMinusM = referenceList.getNumberOfReferences() - times.length;
      double defActT0 = Math.max(defAct, (now - ssc.getCreationTime()));
      double defActTnm = Math.max(defAct, (times.length == 0 ? 0
          : times[times.length - 1]));
      // t0 - tn-m to the 1-d
      double numerator = 0;
      double denom = 0;

      // it's zero..
      if (optimization == 0)
      {
        numerator = Math.pow(defActT0, minusD);
        denom = 1;
      }
      else
      {
        numerator = Math.pow(defActT0, 1 + minusD)
            - Math.pow(defActTnm, 1 + minusD);
        denom = Math.max(defAct, defActT0 - defActTnm);
      }

      base = nMinusM * numerator / ((1 + minusD) * denom);

      if (LOGGER.isDebugEnabled())
      {
        LOGGER.debug(nMinusM + "," + numerator + "," + denom + ","
            + times.length);
        LOGGER.debug("defActT0 = " + defActT0 + " defActTnm = " + defActTnm);
        LOGGER.debug("referenceCount = "
            + referenceList.getNumberOfReferences() + " Optimization = "
            + optimization + " times=" + times.length);
        LOGGER.debug("Opt-BaseLevelActivation " + base);
      }

      if (!Double.isNaN(base) && !Double.isInfinite(base))
        baseLevelActivation += base;

      baseLevelActivation = Math.log(baseLevelActivation);

      baseLevelActivation += declarativeModule.getBaseLevelConstant();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Adding constant "
            + _declarativeModule.getBaseLevelConstant()
            + " BaseLevelActivation " + baseLevelActivation);
    }
    else
    {
      baseLevelActivation = ssc.getBaseLevelActivation();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("forcing baselevel activation to " + baseLevelActivation);
    }

    IRandomModule randomModule = getRandomModule();

    if (randomModule != null)
      noiseActivation = randomModule.logisticNoise(declarativeModule
          .getActivationNoise());
    // System.err.println("IModel time is now "+now);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(c + ".BaseLevelActivation = " + baseLevelActivation);
    return baseLevelActivation + noiseActivation;
  }
}