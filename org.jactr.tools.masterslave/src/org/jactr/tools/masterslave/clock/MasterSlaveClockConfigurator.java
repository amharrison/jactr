package org.jactr.tools.masterslave.clock;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.WrappedClock;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.IClockConfigurator;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.masterslave.master.MasterExtension;
import org.jactr.tools.masterslave.slave.SlaveExtension;

public class MasterSlaveClockConfigurator implements IClockConfigurator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MasterSlaveClockConfigurator.class);

  private IClockConfigurator         _delegate;

  public MasterSlaveClockConfigurator(IClockConfigurator originalConfig)
  {
    _delegate = originalConfig;
  }

  /**
   * if the model contains the masterExtension, we will wrap its clock
   * 
   * @see org.jactr.core.reality.connector.IClockConfigurator#getClockFor(org.jactr.core.model.IModel,
   *      IAgent)
   */
  public IClock getClockFor(IModel model, IAgent agent)
  {
    IClock masterClock = getClockForMaster(model, agent.getClock());
    if (masterClock != null) return masterClock;

    // slave? this should never happen
    SlaveExtension sExt = SlaveExtension.getSlaveExtension(model);
    if (sExt != null)
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(String
                .format("Slave models do not generally have an agent provided. No clue how to handle this situation, returning agent clock"));

    return _delegate.getClockFor(model, agent);
  }

  /**
   * if the model contains the slave extension or the master extension, wrap its
   * clock
   */
  public IClock getClockFor(IModel model, IClock defaultClock)
  {
    IClock masterClock = getClockForMaster(model, defaultClock);
    if (masterClock != null) return masterClock;

    masterClock = getClockForSlave(model, defaultClock);
    if (masterClock != null) return masterClock;

    return _delegate.getClockFor(model, defaultClock);

  }

  protected IClock getClockForMaster(IModel model, IClock defaultClock)
  {
    MasterExtension mExt = MasterExtension.getMaster(model);
    if (mExt != null)
    {
      // wrap the agent's clock with the masterslave clock
      // and wrap the master slave

      MasterSlaveClock msc = new MasterSlaveClock(defaultClock,
          Thread.currentThread());
      WrappedClock wrapped = new WrappedClock(msc);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Created master clock for %s",
            model.getName()));

      return wrapped;
    }

    return null;
  }

  protected IClock getClockForSlave(IModel model, IClock defaultClock)
  {
    SlaveExtension sExt = SlaveExtension.getSlaveExtension(model);
    if (sExt != null)
    {
      // wrap the agent's clock with the masterslave clock
      // and wrap the master slave
      MasterExtension mExt = sExt.getMaster();

      WrappedClock wc = (WrappedClock) ACTRRuntime.getRuntime().getClock(
          mExt.getModel());

      // MasterSlaveClock master = (MasterSlaveClock) wc.getMasterClock();
      MasterSlaveClock master = (MasterSlaveClock) wc.getDelegate();

      // master.addOwner(Thread.currentThread());
      WrappedClock wrapped = new WrappedClock(master);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Created slave clock for %s",
            model.getName()));

      return wrapped;
    }

    return _delegate.getClockFor(model, defaultClock);
  }

  public void release(IModel model, IClock clock)
  {
    /*
     * if the clock wrapped around a master slave clokc?
     */
    boolean released = false;
    if (clock instanceof WrappedClock)
    {
      IClock master = ((WrappedClock) clock).getDelegate();
      if (master instanceof MasterSlaveClock)
      {
        // msc.removeOwner(Thread.currentThread());
        released = true;
      }
    }

    if (!released) _delegate.release(model, clock);

  }

}
