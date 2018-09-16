package org.jactr.core.module.declarative.six.learning;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.declarative.basic.DefaultAssociativeLinkageSystem;
import org.jactr.core.module.declarative.four.learning.DefaultDeclarativeLearningModule4;
import org.jactr.core.module.declarative.six.associative.DefaultAssociativeLinkageSystem6;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * uses the {@link DefaultAssociativeLinkageSystem6} which handles most of the
 * differences between this version and
 * {@link DefaultDeclarativeLearningModule4}.
 * 
 * @see {@linkplain http://jactr.org/node/129}
 * @author harrison
 */
public class DefaultDeclarativeLearningModule6 extends
    DefaultDeclarativeLearningModule4 implements IDeclarativeLearningModule6
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                 = LogFactory
                                                                .getLog(DefaultDeclarativeLearningModule6.class);



  private double                     _maximumStrength;

  public DefaultDeclarativeLearningModule6()
  {
  }

  @Override
  protected DefaultAssociativeLinkageSystem createAssociativeLinkageSystem()
  {
    return new DefaultAssociativeLinkageSystem6(this, getExecutor());
  }

  public double getMaximumStrength()
  {
    return _maximumStrength;
  }

  public void setMaximumStrength(double strength)
  {
    _maximumStrength = strength;
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Collection<String> rtn = super.getPossibleParameters();
    rtn.remove(ASSOCIATIVE_LEARNING_RATE);
    rtn.add(MAXIMUM_STRENGTH_PARAM);
    return rtn;
  }

  @Override
  public String getParameter(String key)
  {
    if (MAXIMUM_STRENGTH_PARAM.equalsIgnoreCase(key))
      return "" + getMaximumStrength();
    else
      return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (ASSOCIATIVE_LEARNING_RATE.equalsIgnoreCase(key))
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("%s is not used in 6.0",
            ASSOCIATIVE_LEARNING_RATE));

    if (MAXIMUM_STRENGTH_PARAM.equalsIgnoreCase(key))
      setMaximumStrength(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else
      super.setParameter(key, value);
  }
}
