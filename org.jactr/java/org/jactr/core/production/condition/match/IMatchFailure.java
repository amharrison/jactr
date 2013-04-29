package org.jactr.core.production.condition.match;

import org.jactr.core.production.condition.ICondition;

/*
 * default logging
 */

/**
 * container for specific details as to why a match has failed
 * 
 * @author harrison
 */
public interface IMatchFailure
{


  /**
   * set condition is used by conditions that delegate to requests (which have
   * no knowledge of their containing condition)
   * 
   * @param condition
   */
  public void setCondition(ICondition condition);

  /**
   * the condition that is responsible for this failure, if any.
   * 
   * @return
   */
  public ICondition getCondition();
}
