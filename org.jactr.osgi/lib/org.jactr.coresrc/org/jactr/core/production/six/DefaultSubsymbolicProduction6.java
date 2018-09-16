package org.jactr.core.production.six;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.six.learning.DefaultProceduralLearningModule6;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.basic.BasicSubsymbolicProduction;
import org.jactr.core.utils.parameter.ParameterHandler;

public class DefaultSubsymbolicProduction6 extends BasicSubsymbolicProduction
    implements ISubsymbolicProduction6
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(DefaultSubsymbolicProduction6.class);

  private double                     _reward          = Double.NaN;

  private double                     _utility         = 0;

  private double                     _expectedUtility = Double.NaN;

  public DefaultSubsymbolicProduction6(IProduction parent, IModel model)
  {
    super(parent, model);
  }

  @Override
  protected void setDefaultParameters()
  {
    super.setDefaultParameters();

    setReward(Double.NaN);
    setUtility(0);
    setExpectedUtility(Double.NaN);
  }

  public double getExpectedUtility()
  {
    return _expectedUtility;
  }

  public void setExpectedUtility(double utility)
  {
    _expectedUtility = utility;
  }

  public double getReward()
  {
    return _reward;
  }

  public double getUtility()
  {
    return _utility;
  }

  public void setReward(double reward)
  {
    _reward = reward;

    /*
     * TODO propogate the event!
     */
  }

  public void setUtility(double utility)
  {
    _utility = utility;
    /*
     * TODO propogate the event
     */
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = new ArrayList<String>(Arrays.asList(new String[] {
        EXPECTED_UTILITY_PARAM, UTILITY_PARAM, REWARD_PARAM }));
    rtn.addAll(super.getSetableParameters());
    return rtn;
  }

  @Override
  public String getParameter(String key)
  {
    if (UTILITY_PARAM.equalsIgnoreCase(key))
      return "" + getUtility();
    else if (EXPECTED_UTILITY_PARAM.equalsIgnoreCase(key))
      return "" + getExpectedUtility();
    else if (REWARD_PARAM.equalsIgnoreCase(key)) return "" + getReward();
    return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (UTILITY_PARAM.equalsIgnoreCase(key))
      setUtility(ParameterHandler.numberInstance().coerce(value).doubleValue());
    else if (EXPECTED_UTILITY_PARAM.equalsIgnoreCase(key))
      setExpectedUtility(ParameterHandler.numberInstance().coerce(value)
          .doubleValue());
    else if (REWARD_PARAM.equalsIgnoreCase(key))
    {
      if ("default".equalsIgnoreCase(value))
        setReward(DefaultProceduralLearningModule6.PARTICIPATE);
      else if ("skip".equalsIgnoreCase(value))
        setReward(DefaultProceduralLearningModule6.SKIP_REWARD);
      else if ("stop".equalsIgnoreCase(value))
        setReward(DefaultProceduralLearningModule6.STOP_REWARD);
      else
        setReward(ParameterHandler.numberInstance().coerce(value).doubleValue());
    }
    else
      super.setParameter(key, value);
  }
}
