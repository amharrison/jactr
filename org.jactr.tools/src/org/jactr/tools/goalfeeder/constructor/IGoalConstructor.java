package org.jactr.tools.goalfeeder.constructor;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.tools.goalfeeder.GoalFeeder;
import org.jactr.tools.goalfeeder.responder.IGoalResponder;
import org.jactr.tools.goalfeeder.timedevents.GatingTimedEvent;

/**
 * an interface for a chunk of code that transforms some task of an
 * experiment into a chunk
 * into a goal
 * @author harrison
 *
 */
@Deprecated
public interface IGoalConstructor<T>
{
  /**
   * return true if we can transform a experimentTask into a goal
   * @param component
   * @return
   */
  public boolean handles(T experimentTask);
  
  /**
   * construct a goal chunk based on the experimentTask
   * @param component
   * @param model
   * @return
   */
  public IChunk construct(T experimentTask, IModel model, GoalFeeder<T> feeder);
  
  
  
  public GatingTimedEvent createGate(double currentTime, T experimentTask);
  
  /**
   * 
   * @return
   */
  public IGoalResponder getResponder();
}
