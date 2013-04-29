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
import org.commonreality.time.impl.SharedClock;
import org.commonreality.time.impl.WrappedClock;
import org.jactr.core.model.IModel;

/**
 * @author developer
 */
public class LocalConnector implements IConnector
{
  /**
   * logger definition
   */
  static private final Log  LOGGER = LogFactory.getLog(LocalConnector.class);

  private final SharedClock _defaultClock;
  private Map<IModel, WrappedClock> _clocks;

  public LocalConnector()
  {
    _defaultClock = new SharedClock();
    _clocks = new ConcurrentHashMap<IModel, WrappedClock>();
//    _defaultClock.setTime(-1);
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#connect(org.jactr.core.model.IModel)
   */
  public void connect(IModel model)
  {
    _defaultClock.addOwner(Thread.currentThread());
    _clocks.put(model, new WrappedClock(_defaultClock));
  }

  /**
   * @see org.jactr.core.reality.connector.IConnector#disconnect(org.jactr.core.model.IModel)
   */
  public void disconnect(IModel model)
  {
    _defaultClock.removeOwner(Thread.currentThread());
    _clocks.remove(model);

    if (_defaultClock.getOwners().size() == 0)
    {
      _defaultClock.setIgnoreDiscrepencies(true);
      _defaultClock.setTime(0);
      _defaultClock.setIgnoreDiscrepencies(false);
    }
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
    //concurrent hash cant deal w/ null
    if(model==null) return _defaultClock;
    IClock rtn = _clocks.get(model);
    if(rtn==null) rtn = _defaultClock;
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

}
