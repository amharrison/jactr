package org.jactr.core.queue.timedevents;

import org.jactr.core.model.ModelTerminatedException;

public class TerminationTimedEvent extends AbstractTimedEvent
{

  public TerminationTimedEvent(double startTime, double endTime)
  {
    super(startTime, endTime);
  }
  
  public void fire(double now)
  {
    throw new ModelTerminatedException();
  }
}
