package org.jactr.core.module.procedural.six.learning;

import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;

/*
 * default logging
 */

public interface IExpectedUtilityEquation
{

  /**
   * compute the expected utility for the production given the discounted
   * reward. if the returned value is NaN or infinite, it will be ignored
   * 
   * @param production
   * @param model
   * @param reward
   * @return
   */
  public double computeExpectedUtility(IProduction production, IModel model, double reward);
}
