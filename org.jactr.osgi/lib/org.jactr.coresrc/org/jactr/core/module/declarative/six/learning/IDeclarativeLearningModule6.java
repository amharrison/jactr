package org.jactr.core.module.declarative.six.learning;

/*
 * default logging
 */
import org.jactr.core.module.declarative.four.learning.IDeclarativeLearningModule4;

public interface IDeclarativeLearningModule6 extends
    IDeclarativeLearningModule4
{
  static public final String MAXIMUM_STRENGTH_PARAM = "MaximumAssociativeStrength";
  
  public double getMaximumStrength();
}
