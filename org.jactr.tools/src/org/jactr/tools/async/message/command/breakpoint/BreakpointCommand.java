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
package org.jactr.tools.async.message.command.breakpoint;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.controller.debug.BreakpointType;
import org.jactr.tools.async.message.BaseMessage;

/**
 * @author developer
 */
public class BreakpointCommand extends BaseMessage implements IBreakpointCommand, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -2997600226500457567L;

  /**
   * logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(BreakpointCommand.class);

  Action                   _action;

  BreakpointType           _type;

  String                   _details;
  String _modelName;

  
  public BreakpointCommand(Action action, BreakpointType type)
  {
    this(action, type, null, null);
  }
  
  public BreakpointCommand(Action action, BreakpointType type, String modelName, String details)
  {
    _action = action;
    _type = type;
    _details = details;
    _modelName = modelName;

    /*
     * do some sanity checks..
     */
    if (_type == BreakpointType.ALL)
    {
      if (_details != null || _modelName ==null)
        throw new IllegalArgumentException(
            "Can't provide a breakpoint type of all and a breakpoint name");

      if (_action == Action.ADD)
        throw new IllegalArgumentException("Can't add breakpoint type of all");

      /*
       * remove and clear are redundant here
       */
    }
    else if (_type == BreakpointType.CONFLICT_SET)
    {
      
      /*
       * name is meaningless
       */
      if (_details != null)
        throw new IllegalArgumentException(
            "Names with conflict set breakpoints are meaningless");
    }
    else if (_type == BreakpointType.CYCLE || _type == BreakpointType.TIME)
    {
      /*
       * name must be a number.. or clear..
       */
      if (_action != Action.CLEAR)
        try
        {
          Double.parseDouble(_details);
        }
        catch (NumberFormatException nfe)
        {
          throw new IllegalArgumentException(
              "cycle break points must have clear action or a numeric name");
        }
    }
    else if (_type == BreakpointType.EXCEPTION)
      throw new IllegalArgumentException(
          "Exception breakpoints are always enabled");
    else if (_type == BreakpointType.PRODUCTION) /*
     * name must not be null.. uless clear
     */
    if (_action != Action.CLEAR && _details == null)
      throw new IllegalArgumentException(
          "Unless clearing, production breakpoints must have a name");
  }

  /**
   * @see org.jactr.tools.async.message.command.breakpoint.IBreakpointCommand#getAction()
   */
  public Action getAction()
  {
    return _action;
  }

  /**
   * @see org.jactr.tools.async.message.command.breakpoint.IBreakpointCommand#getType()
   */
  public BreakpointType getType()
  {
    return _type;
  }

  public String getDetails()
  {
    return _details;
  }

  public String getModelName()
  {
    return _modelName;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("[");
    sb.append(getClass().getSimpleName()).append(":");
    sb.append(_action).append(",").append(_type).append(",").append(_modelName);
    sb.append(".").append(_details).append("]");
    return sb.toString();
  }
}
