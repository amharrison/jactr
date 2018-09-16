/*
 * Created on Mar 19, 2007
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.tools.async.message.command.state;

import java.io.Serializable;

import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.message.BaseMessage;
/**
 * @author developer
 *
 */
public class ModelStateCommand extends BaseMessage implements
    IModelStateCommand, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -6052996914684968337L;
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ModelStateCommand.class);

  
  private State _state;
  private String _modelName;
  
  
  
  public ModelStateCommand(String modelName, State state)
  {
    _state = state;
    _modelName = modelName;
  }
  
  /** 
   * @see org.jactr.tools.async.message.command.state.IStateCommand#getState()
   */
  public State getState()
  {
    return _state;
  }

  public String getModelName()
  {
    return _modelName;
  }

  
}


