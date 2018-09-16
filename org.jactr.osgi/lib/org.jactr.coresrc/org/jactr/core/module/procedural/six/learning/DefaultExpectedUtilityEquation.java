/**
 * Copyright (C) 1999-2007, Anthony Harrison anh23@pitt.edu This library is free
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
package org.jactr.core.module.procedural.six.learning;

/*
 * default logging
 */
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.six.ISubsymbolicProduction6;

public class DefaultExpectedUtilityEquation implements
    IExpectedUtilityEquation
{

  /**
   * 
   */
  private DefaultProceduralLearningModule6 _proceduralLearningModule6;

  /**
   * @param defaultProceduralLearningModule6
   */
  public DefaultExpectedUtilityEquation()
  {
  }

  private DefaultProceduralLearningModule6 getLearningModule(IModel model)
  {
    if (_proceduralLearningModule6 == null)
      _proceduralLearningModule6 = (DefaultProceduralLearningModule6) model
          .getModule(DefaultProceduralLearningModule6.class);
    return _proceduralLearningModule6;
  }

  public double computeExpectedUtility(IProduction production, IModel model,
      double reward)
  {
    DefaultProceduralLearningModule6 dplm = getLearningModule(model);

    ISubsymbolicProduction6 ssp = (ISubsymbolicProduction6) production
        .getSubsymbolicProduction();

    double previousUtility = ssp.getExpectedUtility();

    if (Double.isNaN(previousUtility)) previousUtility = ssp.getUtility();

    double partial = 0;

    if (dplm.isParameterLearningEnabled()
        && !(Double.isNaN(reward) || Double.isInfinite(reward)))
      partial = dplm.getParameterLearning() * (reward - previousUtility);

    double utility = previousUtility + partial;

    if (DefaultProceduralLearningModule6.LOGGER.isDebugEnabled())
      DefaultProceduralLearningModule6.LOGGER.debug(production + ".expectedUtility=" + utility + " previous="
          + previousUtility + " partial=" + partial + " reward=" + reward
 + " rate="
          + dplm.getParameterLearning());

    return utility;
  }
}