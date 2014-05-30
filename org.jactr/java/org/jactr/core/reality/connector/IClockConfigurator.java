package org.jactr.core.reality.connector;

/*
 * default logging
 */
import org.commonreality.time.IClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.ACTRAgent;

/**
 * interface accepted by IConnector to tweak how clocks are
 * configured/installed. one of the two getClockFor methods will be called
 * during connection of IConnector. This will occur in the model thread.
 * 
 * @author harrison
 */
public interface IClockConfigurator
{
  public IClock getClockFor(IModel model, ACTRAgent agent);

  public IClock getClockFor(IModel model, IClock defaultClock);

  public void release(IModel model, IClock clock);
}
