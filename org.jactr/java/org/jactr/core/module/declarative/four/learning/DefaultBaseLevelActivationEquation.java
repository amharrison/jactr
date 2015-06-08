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
import org.jactr.core.chunk.basic.AbstractSubsymbolicChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.Logger.Stream;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.four.IBaseLevelActivationEquation;
import org.jactr.core.module.declarative.four.IDeclarativeModule4;
import org.jactr.core.module.procedural.IProceduralModule;
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

  private static transient Log LOGGER       = LogFactory
                                                .getLog(DefaultBaseLevelActivationEquation.class
                                                    .getName());

  IDeclarativeLearningModule4  _declarativeLearningModule;

  IProceduralModule            _proceduralModule;

  IDeclarativeModule4          _declarativeModule;


  private IModel               _model;

  ThreadLocal<double[]>        _doubleArray = new ThreadLocal<double[]>();

  ThreadLocal<StringBuilder>   _stringBuilder = new ThreadLocal<StringBuilder>();

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
      _declarativeModule = (IDeclarativeModule4) _model.getDeclarativeModule();
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

  /**
   * return the thread local cached string builder so we don't keep
   * instantiating
   * 
   * @param initialize
   * @return
   */
  private StringBuilder getStringBuilder(String initialize)
  {
    StringBuilder sb = _stringBuilder.get();
    if (sb == null)
    {
      sb = new StringBuilder(initialize);
      _stringBuilder.set(sb);
    }
    else
    {
      sb.delete(0, sb.length());
      sb.append(initialize);
    }
    return sb;
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
    double now = model.getAge();
    IDeclarativeModule4 declarativeModule = getDeclarativeModule();
    IDeclarativeLearningModule4 declarativeLearningModule = getDeclarativeLearningModule();

    ISubsymbolicChunk ssc = c.getSubsymbolicChunk();
    double baseLevelActivation = 0;
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Computing activation for %s @ %.2f", c, now));

    if (declarativeLearningModule.isBaseLevelLearningEnabled())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("base level learning is enabled, calculating");

      /*
       * it is safer to use model.getAge() since it is less volatile if you are
       * using a realtime clock. getAge() only changes once per-cycle where the
       * actual time can change every call.
       */
      double minusD = -1.0 * declarativeLearningModule.getBaseLevelLearning();
      double defAct = getProceduralModule().getDefaultProductionFiringTime();
      double base = 0.0;

      IReferences referenceList = ssc.getReferences();
      long numberOfTotalTimeSamples = referenceList.getNumberOfReferences();
      int numberOfActualTimeSamples = referenceList
          .getNumberOfRecentReferences();

      StringBuilder logMsg = null;
      if (Logger.hasLoggers(model))
        logMsg = getStringBuilder(String.format(" refCount %d relativeRefs ",
            numberOfTotalTimeSamples));

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("References %s", referenceList));

      /*
       * we recycle the double[] for efficiency purposes
       */
      double[] times = referenceList.getRelativeTimes(now, _doubleArray.get());
      _doubleArray.set(times); // save it for later

      /*
       * exact portion. We cannot iterate blindly since the recycled container
       * may be larger than the actual number of samples. This is WRONG.
       */
      for (int i = 0; i < numberOfActualTimeSamples; i++)
      {
        double element = times[i];
        if (logMsg != null) logMsg.append(String.format("%.2f ", element));

        /*
         * a merge (which increments reference count), followed by the logging
         * of the merge (outputing activation) can result in a 0 relative time.
         * This produces a crazy artificial spike in activation. We skip it
         * entirely.
         */
//        if (Math.abs(element) <= 0.0001) continue;

        double tMinusD = Math.pow(Math.max(defAct, element), minusD);
        base += tMinusD;
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("tMinusD for " + element + " = " + tMinusD + " base = "
              + base);
      }

      // paranoid worst case scenario
      if (numberOfActualTimeSamples == 0) base = Math.pow(defAct, minusD);

      baseLevelActivation = base;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Exact-BaseLevelActivation " + baseLevelActivation
            + " with " + numberOfActualTimeSamples + " accesses");

      /*
       * now for the approximate portion
       */
      /**
       * <code>
       *            (1-d)    (1-d)
       * (n-k) * (tn^    - tk     )
       * --------------------------
       *  (1-d) * (tn-tk)
       * </code>
       */
      if (numberOfTotalTimeSamples > numberOfActualTimeSamples)
      {

        // there are more references than we have times for. approx portion
        double oneMinusD = 1 + minusD;
        // # of approx times
        double numerator = numberOfTotalTimeSamples - numberOfActualTimeSamples;
        double tn = now - ssc.getCreationTime();
        double tk = now - referenceList.getLastReferenceTime();
        numerator *= Math.pow(tn, oneMinusD) - Math.pow(tk, oneMinusD);
        double denom = oneMinusD * (tn - tk);

        base = numerator / denom;

        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String
                  .format(
                      "Aprox-BaseLevelAct %.4f, numerator=%.4f denom=%.4f, tn=%.2f tk=%.2f",
                      base, numerator, denom, tn, tk));

        // add the exact and the approximate portions
        if (!Double.isNaN(base) && !Double.isInfinite(base))
          baseLevelActivation += base;
      }

      baseLevelActivation = Math.log(baseLevelActivation);

      baseLevelActivation += declarativeModule.getBaseLevelConstant();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Adding constant "
            + _declarativeModule.getBaseLevelConstant()
            + " BaseLevelActivation " + baseLevelActivation);

      if (logMsg != null)
      {
        logMsg.insert(0,
            String.format("%s.base = %.2f", c, baseLevelActivation));
        Logger.log(model, Stream.ACTIVATION, logMsg.toString());
      }

    }
    else
    {
      baseLevelActivation = ssc.getBaseLevelActivation();

      if (Double.isNaN(baseLevelActivation))
        baseLevelActivation = declarativeModule.getBaseLevelConstant();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("forcing baselevel activation to " + baseLevelActivation);

      if (Logger.hasLoggers(model))
        Logger.log(model, Stream.ACTIVATION,
            String.format("%s.base = %.2f", c, baseLevelActivation));
    }


    return baseLevelActivation;
  }

  @Override
  public String getName()
  {
    return "base4";
  }

  @Override
  public double computeAndSetActivation(IChunk chunk, IModel model)
  {
    double base = computeBaseLevelActivation(model, chunk);
    ((AbstractSubsymbolicChunk) chunk.getSubsymbolicChunk())
        .setBaseLevelActivation(base);
    return base;
  }
}