package org.jactr.tools.goalfeeder.action;


import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.IAction;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.goalfeeder.GoalFeeder;

/**
 * requires that GoalFeeder be attached to the runtime applicationData
 * 
 * @author harrison
 */
public class RespondAction implements IAction
{

  /*
   * we will resolve it during bind
   */
  private GoalFeeder< ? > _feeder;

  private IChunk          _goalChunk;

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    /*
     * do we have a feeder?
     */
    try
    {
      _feeder = (GoalFeeder< ? >) ACTRRuntime.getRuntime().getApplicationData();
      if (_feeder == null)
        throw new NullPointerException("GoalFeeder cannot be null");
    }
    catch (Exception e)
    {
      _feeder = null;
      _goalChunk = null;
      throw new CannotInstantiateException("No GoalFeeder was found ", e);
    }

    try
    {
      _goalChunk = (IChunk) variableBindings.get("=goal");
    }
    catch (Exception e)
    {
    }

    return this;
  }

  public void dispose()
  {
  }

  public double fire(final IInstantiation instantiation, double firingTime)
  {
    IModel model = instantiation.getModel();
    double pFire = instantiation.getSubsymbolicProduction().getFiringTime();

    ITimedEvent te = new AbstractTimedEvent(firingTime, firingTime + pFire) {
      @Override
      public void fire(double now)
      {
        super.fire(now);
        _feeder.respond(instantiation);
      }
    };
    model.getTimedEventQueue().enqueue(te);
    return 0;
  }

}
