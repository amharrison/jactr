/*
 * Created on Oct 11, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * describes an event from a source (S) that can be caught by a listener (L)
 * 
 * @author harrison
 * @param <S>
 * @param <L>
 */
public abstract class AbstractACTREvent<S, L> implements IACTREvent<S, L>
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(AbstractACTREvent.class);

  private S               _source;

  private long            _systemTime;

  private double          _simulationTime;

  protected AbstractACTREvent()
  {
    _source = null;
    _simulationTime = Double.NaN;
    _systemTime = System.currentTimeMillis();
  }

  protected AbstractACTREvent(S source)
  {
    this();
    setSource(source);
  }

  protected AbstractACTREvent(S source, double simulationTime)
  {
    this(source);
    setSimulationTime(simulationTime);
  }

  protected void setSource(S source)
  {
    _source = source;
  }

  protected void setSimulationTime(double simulationTime)
  {
    _simulationTime = simulationTime;
  }

  public double getSimulationTime()
  {
    return _simulationTime;
  }

  public S getSource()
  {
    return _source;
  }

  public long getSystemTime()
  {
    return _systemTime;
  }

  abstract public void fire(final L listener);

}
