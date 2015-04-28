package org.jactr.tools.goalfeeder.responder;

import org.jactr.core.production.IInstantiation;

/**
 * takes a goal, most likely created by an IGoalConstructor and produces an
 * action within an experiment
 * 
 * @author harrison
 */
@Deprecated
public interface IGoalResponder
{

  /**
   * can this IGoalResponder generate an experiment response based on the chunk
   * 
   * @param chunk
   * @return
   */
  public boolean handles(IInstantiation instantiation);

  /**
   * generate a response and return the maximum elapsed time to allow the model
   * to run after responding.
   * 
   * @param chunk
   */
  public void respond(IInstantiation instantiation);
}
