package org.jactr.tools.goalfeeder.injector;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;
import org.jactr.tools.goalfeeder.GoalFeeder;
import org.jactr.tools.goalfeeder.responder.IGoalResponder;
import org.jactr.tools.goalfeeder.timedevents.GatingTimedEvent;

/**
 * code that is responsible for injecting a new goal into the running model.
 * The injector will only be called during a safe period when the model
 * is not running (as controlled by the gating timed events). Care should
 * be taken that inserting of goals is sensitivity to pending events. For
 * instance, the model may have fired a request to add a subgoal, but it
 * won't be added for 50ms, during which time, this injector could be called.
 * Before inserting the new goal, the {@link TimedEventQueue} should be checked
 * for any pending goal modifications executed by {@link IBufferBasedTimedEvent}.
 * <br>
 * Those that are using the threaded cognition modules will want to make sure
 * you are removing the correct goal manipulation event.
 * @author harrison
 *
 * @param <T>
 */
public interface IGoalInjector<T>
{

  /**
   * return true if we can transform a experimentTask into a goal
   * @param component
   * @return
   */
  public boolean handles(T experimentTask);
    
  
  /**
   * if {@link #handles(Object)} returns true, this will be called. returning
   * the inserted chunk. This is called on the model thread.
   * @param experimentTask
   * @param model
   * @param feeder
   * @return
   */
  public IChunk injectGoal(T experimentTask, IModel model, GoalFeeder<T> feeder);
  
  
  
  /**
   * GatingTimedEvents are necessary to control the flow of the model - i.e. 
   * prevent it from running ahead of the goal feeder and vice/versa. If the 
   * experiment task runs for a fixed amount of time, you will want to return
   * a gate that lasts that long.
   * @param currentTime
   * @param experimentTask
   * @return
   */
  public GatingTimedEvent createGate(double currentTime, T experimentTask);
  
  /**
   * return the goal responder necessary for handling responses
   * @return
   */
  public IGoalResponder getResponder();
}
