/*
 * Created on Feb 21, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.message.event.state;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.async.message.BaseMessage;

/**
 * @author developer
 */
public class RuntimeStateEvent extends BaseMessage implements
    IRuntimeStateEvent, Serializable
{
  /**
   * 
   */
  private static final long  serialVersionUID = 5141289381492005562L;

  /**
   * logger definition
   */
  static private final Log   LOGGER           = LogFactory
                                                  .getLog(RuntimeStateEvent.class);

  private State              _state;

  private Collection<String> _modelNames;

  long                       _systemTime;

  double                     _simulationTime;

  private String             _exception;

  public RuntimeStateEvent(State state, double simulationTime)
  {
    _state = state;
    _simulationTime = simulationTime;
    _systemTime = System.currentTimeMillis();
  }

  public RuntimeStateEvent(Collection<IModel> models, double simulationTime)
  {
    this(State.STARTED, simulationTime);
    _modelNames = new ArrayList<String>();
    for (IModel model : models)
      _modelNames.add(model.getName());
  }

  public RuntimeStateEvent(Exception exception, double simulationTime)
  {
    this(State.STOPPED, simulationTime);
    StringWriter writer = new StringWriter();
    exception.printStackTrace(new PrintWriter(writer));
    writer.flush();
    _exception = writer.toString();
  }

  public State getState()
  {
    return _state;
  }

  public String getException()
  {
    return _exception;
  }

  public Collection<String> getModelNames()
  {
    return _modelNames;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("[");
    sb.append(getClass().getSimpleName()).append(":").append(_state)
        .append("]");
    return sb.toString();
  }

  /**
   * @see org.jactr.tools.async.message.command.state.IStateCommand#getSimulationTime()
   */
  public double getSimulationTime()
  {
    return _simulationTime;
  }

  /**
   * @see org.jactr.tools.async.message.command.state.IStateCommand#getSystemTime()
   */
  public long getSystemTime()
  {
    return _systemTime;
  }
}
