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

package org.jactr.core.module.procedural.four.learning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.four.IProceduralModule4;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.four.ISubsymbolicProduction4;
import org.jactr.core.utils.references.IReferences;

/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class DefaultCostEquation implements ICostEquation
{

  static private transient Log LOGGER   = LogFactory
                                            .getLog(ICostEquation.class);

  private IProceduralModule4 _pm4;
  private IProceduralLearningModule4 _plm4;
  
  public DefaultCostEquation(IProceduralModule4 pm4, IProceduralLearningModule4 plm4)
  {
    _pm4 = pm4;
    _plm4 = plm4;
  }
  
  /**
   * Description of the Method
   * 
   * @param times
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  protected double sum(double[] times)
  {
    double sum = 0;
    for (double time : times)
      sum += time;

    return sum;
  }

  /**
   * Description of the Method
   * 
   * @param model
   *          Description of the Parameter
   * @param prod
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public double computeCost(IModel model, IProduction prod)
  {
    boolean parameterLearningEnabled = _plm4.isParameterLearningEnabled();
    double minusD = - _plm4.getParameterLearning();
    double defaultActionTime = _pm4.getDefaultProductionFiringTime();


    double cost = 0.0;
    ISubsymbolicProduction4 ssp = (ISubsymbolicProduction4) prod
        .getSubsymbolicProduction().getAdapter(ISubsymbolicProduction4.class);
    IReferences effortTimes = ssp.getEfforts();
    IReferences successTimes = ssp.getSuccesses();
    IReferences failureTimes = ssp.getFailures();
    double[] efforts = effortTimes.getTimes();

    if (parameterLearningEnabled)
    {
      // double time = ACTRRuntime.getRuntime().getClock(model).getTime();
      double time = model.getAge();
      double[] successes = successTimes.getRelativeTimes(time);
      double[] failures = failureTimes.getRelativeTimes(time);
      double effort = ssp.getPriorEfforts();
      double success = ssp.getPriorSuccesses();
      double failure = ssp.getPriorFailures();
      int eI = 0;
      int sI = 0;
      int fI = 0;
      double decay = 0.0;

      while (!(eI == efforts.length || sI == successes.length && fI == failures.length))
      {
        if (sI < successes.length
            && (fI == failures.length || successes[sI] > failures[fI]))
        {
          // are done with failures or s > f
          decay = Math
              .pow(Math.max(defaultActionTime, successes[sI++]), minusD);
          success += decay;

          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Success[" + (sI - 1) + "] : " + successes[(sI - 1)]
                + " => " + decay);
        }
        else
        {
          decay = Math.pow(Math.max(defaultActionTime, failures[fI++]), minusD);
          failure += decay;

          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Failure[" + (fI - 1) + "] : " + failures[(fI - 1)]
                + " => " + decay);
        }
        effort += efforts[eI++] * decay;

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Efforts[" + (eI - 1) + "] : " + efforts[(eI - 1)]
              + " =>" + efforts[eI - 1] * decay);
      }

      cost += effort / (success + failure);
    }
    else
    {
      double effTime = ssp.getPriorEfforts();
      for (double effort : efforts)
        effTime += effort;

      cost += effTime
          / (successTimes.getNumberOfReferences()
              + failureTimes.getNumberOfReferences() + ssp.getPriorSuccesses() + ssp
              .getPriorFailures());
    }
    return cost;
  }
}