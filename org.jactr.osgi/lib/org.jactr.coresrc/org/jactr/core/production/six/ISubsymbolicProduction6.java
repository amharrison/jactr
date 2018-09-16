package org.jactr.core.production.six;

/*
 * default logging
 */
import org.jactr.core.production.ISubsymbolicProduction;

public interface ISubsymbolicProduction6 extends ISubsymbolicProduction
{

  static public final String EXPECTED_UTILITY_PARAM = "ExpectedUtility";

  static public final String UTILITY_PARAM          = "Utility";

  static public final String REWARD_PARAM           = "Reward";

  /**
   * return the computed expected utility
   * @return
   */
  public double getExpectedUtility();
  
  public void setExpectedUtility(double utility);

  /**
   * return the predefined utility of the production
   * 
   * @return
   */
  public double getUtility();

  public void setUtility(double utility);

  /**
   * return the reward value associated with this production or Double.NaN if
   * there is no reward explicitly defined for this production
   * 
   * @return
   */
  public double getReward();

  public void setReward(double reward);
}
