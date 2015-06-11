/*
 * Created on Nov 30, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.reality.connector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.time.IClock;
import org.commonreality.time.impl.BasicClock;
import org.commonreality.time.impl.OwnedClock;
import org.commonreality.time.impl.OwnedClock.OwnedAuthoritativeClock;
import org.commonreality.time.impl.WrappedClock;
import org.jactr.core.model.IModel;

/**
 * local connector responsible for providing the clocks to the models, and any
 * and all attachment handling for the non-existant perceptual interfaces.
 * 
 * @author developer
 */
public class LocalConnector implements IConnector
{
  /**
   * logger definition
   */
  static private final Log     LOGGER                        = LogFactory
                                                                 .getLog(LocalConnector.class);

  static private final boolean _enableIndependentClocks      = Boolean
                                                                 .getBoolean("connector.independentClocks");

  static private boolean       _warnedAboutIndependentClocks = false;

  private final OwnedClock     _defaultClock;

  private Map<IModel, IClock>  _clocks;

  private IClockConfigurator   _configurator;

  private boolean              _useIndependentClocks         = false;

  /**
   * default will use the system property connector.independentClocks
   * effectively new
   * LocalConnector(Boolean.getBoolean("connector.independentClocks"))
   */
  public LocalConnector()
  {
    this(_enableIndependentClocks);
  }

  public LocalConnector(boolean useIndependentClocks)
  {
    if (!_warnedAboutIndependentClocks && useIndependentClocks)
    {
      LOGGER
          .warn("Independent clocks can cause strange behavior when models interact or in systems that assume synchronized time.");

      _warnedAboutIndependentClocks = true;
    }
    _useIndependentClocks = useIndependentClocks;

    _defaultClock = new OwnedClock(0.05);
    _clocks = new ConcurrentHashMap<IModel, IClock>();
    setClockConfigurator(new IClockConfigurator() {

      public void release(IModel model, IClock clock)
      {

      }

      public IClock getClockFor(IModel model, IClock defaultClock)
      {
        if (!_useIndependentClocks)
          return new WrappedClock(defaultClock);
        else
          return new BasicClock(true, model.getProceduralModule()
              .getDefaultProductionFiringTime());
      }

      public IClock getClockFor(IModel model, IAgent agent)
      {// this is not going to be called.
        return agent.getClock();
      }
    });
    // _defaultClock.setTime(-1);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#connect(org.jactr.core.model.IModel)
   */
  public void connect(IModel model)
  {
    if (!_useIndependentClocks)
    {
      OwnedAuthoritativeClock auth = (OwnedAuthoritativeClock) _defaultClock
          .getAuthority().get();
      auth.addOwner(model);
    }

    IClock clock = getClockConfigurator().getClockFor(model, _defaultClock);

    synchronized (this)
    {
      _clocks.put(model, clock);
    }
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  public void disconnect(IModel model)
  {
    if (!_useIndependentClocks)
    {
      OwnedAuthoritativeClock auth = (OwnedAuthoritativeClock) _defaultClock
          .getAuthority().get();

      auth.removeOwner(model);
    }

    // _defaultClock.removeOwner(Thread.currentThread());
    IClock defined = null;
    synchronized (this)
    {
      defined = _clocks.remove(model);
    }

    getClockConfigurator().release(model, defined);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#getAgentInterface(org.jactr.core.model.IModel)
   */
  public IAgent getAgent(IModel model)
  {
    return null;
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#isRunning()
   */
  public boolean isRunning()
  {
    return false;
  }

  public IClock getClock(IModel model)
  {
    // concurrent hash cant deal w/ null
    if (model == null) return _defaultClock;
    IClock rtn = _clocks.get(model);
    if (rtn == null) rtn = _defaultClock;
    return rtn;
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#start()
   */
  public void start()
  {

  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#stop()
   */
  public void stop()
  {

  }

  public IClockConfigurator getClockConfigurator()
  {
    return _configurator;
  }

  public void setClockConfigurator(IClockConfigurator clockConfig)
  {
    _configurator = clockConfig;
  }

}
