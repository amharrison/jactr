package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener;

/**
 * object listener that can delay the removal of percepts..
 * 
 * @author harrison
 */
public class DelayableAfferentObjectListener extends
    DefaultAfferentObjectListener
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(DelayableAfferentObjectListener.class);

  private double                     _perceptualDelay = 0;

  private final IModel               _model;

  public DelayableAfferentObjectListener(IModel model, IAgent agent,
      Executor executor)
  {
    super(agent, executor);
    _model = model;
  }
  
  protected IModel getModel()
  {
    return _model;
  }

  public double getPerceptualDelay()
  {
    return _perceptualDelay;
  }

  public void setPerceptualDelay(double delay)
  {
    _perceptualDelay = delay;
  }

  /**
   * if no delay, process immediately. If delay, post a timed event that will
   * trigger processing later..
   * 
   * @param toBeRemoved
   * @see org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener#objectsRemoved(java.util.Collection)
   */
  @Override
  protected void objectsRemoved(Collection<IAfferentObject> toBeRemoved)
  {
    if (_perceptualDelay <= 0)
      super.objectsRemoved(toBeRemoved);
    else
      delayRemoval(toBeRemoved);
  }

  protected void delayRemoval(Collection<IAfferentObject> toBeRemoved)
  {
    // copy of the collection as it will be recycled
    final List<IAfferentObject> internalToBeRemoved = FastListFactory
        .newInstance();
    internalToBeRemoved.addAll(toBeRemoved);

    double currentTime = getAgent().getClock().getTime();

    /*
     * run on the CR executor, this does the actual work..
     */
    final Runnable removalProcessor = new Runnable() {

      public void run()
      {
        for (IAfferentObject object : internalToBeRemoved)
          objectRemoved(object);
        FastListFactory.recycle(internalToBeRemoved);
      }

    };

    /*
     * the timedevent queues up the actual processing so that it is still
     * performed on the CR executor.
     */
    RunnableTimedEvent trigger = new RunnableTimedEvent(currentTime
        + getPerceptualDelay(), new Runnable() {

      public void run()
      {
        /*
         * now we can queue up the processing to actually do the work
         */
        getExecutor().execute(removalProcessor);
      }

    });

    getModel().getTimedEventQueue().enqueue(trigger);
  }
}
