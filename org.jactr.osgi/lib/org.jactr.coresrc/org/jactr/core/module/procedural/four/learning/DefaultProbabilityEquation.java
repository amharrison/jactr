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
public class DefaultProbabilityEquation implements IProbabilityEquation
{

  static private transient Log       LOGGER   = LogFactory
                                                  .getLog(IProbabilityEquation.class);


  IProceduralModule4 _pm4;
  IProceduralLearningModule4 _plm4;
  
  public DefaultProbabilityEquation(IProceduralModule4 pm, IProceduralLearningModule4 plm4)
  {
    _pm4 = pm;
    _plm4 = plm4;
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
  public double computeProbability(IModel model, IProduction prod)
  {
    boolean parameterLearningEnabled = _plm4.isParameterLearningEnabled();
    double minusD = -_plm4.getParameterLearning();
    double defaultActionTime = _pm4.getDefaultProductionFiringTime();
     
    
    double prob = 0.0;
    ISubsymbolicProduction4 ssp = (ISubsymbolicProduction4) prod
        .getSubsymbolicProduction().getAdapter(ISubsymbolicProduction4.class);
    IReferences successes = ssp.getSuccesses();
    IReferences failures = ssp.getFailures();

    if (parameterLearningEnabled)
    {
      // double now = ACTRRuntime.getRuntime().getClock(model).getTime();
      double now = model.getAge();
      double s = ssp.getPriorSuccesses();
      double f = ssp.getPriorFailures();

      if (LOGGER.isDebugEnabled())
          LOGGER.debug("Time : " + now + " minusD : " + minusD + " defAct : "
              + defaultActionTime);

      double[] times = successes.getRelativeTimes(now);
      for (double time : times)
        s += Math.pow(Math.max(defaultActionTime, time), minusD);

      times = failures.getRelativeTimes(now);
      for (double time : times)
        f += Math.pow(Math.max(defaultActionTime, time), minusD);

      prob += s / (s + f);

      if (LOGGER.isDebugEnabled())
          LOGGER.debug("p : " + prob + "s : " + s + " f:" + f);
    }
    else
      prob += (ssp.getPriorSuccesses() + successes.getNumberOfReferences())
          / (double) (successes.getNumberOfReferences() + failures.getNumberOfReferences()
              + ssp.getPriorSuccesses() + ssp.getPriorFailures());
    return prob;
  }
}