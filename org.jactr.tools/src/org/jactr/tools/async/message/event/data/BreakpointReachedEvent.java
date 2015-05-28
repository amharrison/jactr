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
package org.jactr.tools.async.message.event.data;

import java.io.Serializable;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.controller.debug.BreakpointType;
import org.jactr.tools.async.message.ast.BaseASTMessage;
import org.jactr.tools.async.message.event.IEvent;

/**
 * @author developer
 */
public class BreakpointReachedEvent extends BaseASTMessage implements IEvent, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 5706443530405417056L;

  /**
   * logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                      .getLog(BreakpointReachedEvent.class);

  BreakpointType _type;
  String _modelName;
  double _simulationTime;
  
  public BreakpointReachedEvent(String modelName, BreakpointType type, double simulationTime, CommonTree details)
  {
    super(details);
    // compressAST();
    _simulationTime = simulationTime;
    _modelName = modelName;
    _type = type;
  }
  
  public double getSimulationTime()
  {
    return _simulationTime;
  }
  
  public String getModelName()
  {
    return _modelName;
  }
  
  public BreakpointType getBreakpointType()
  {
    return _type;
  }
  

  
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("[");
    sb.append(getClass().getSimpleName()).append(":");
    sb.append(_type).append(",").append(_modelName).append(",").append(getAST()).append("]");
    return sb.toString();
  }
}
