/*
 * Created on Feb 21, 2007
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
package org.jactr.tools.async.message.command.login;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.credentials.ICredentials;
import org.jactr.tools.async.message.BaseMessage;
import org.jactr.tools.async.message.command.ICommand;
/**
 * @author developer
 *
 */
public class LoginCommand extends BaseMessage implements ICommand, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -6888518069177191560L;

  /**
   logger definition
   */
  static transient private final Log LOGGER           = LogFactory
                                                          .getLog(LoginCommand.class);

  ICredentials _credentials;
  
  public LoginCommand(ICredentials credentials)
  {
    _credentials = credentials;
  }
  
  public ICredentials getCredentials()
  {
    return _credentials;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("[");
    sb.append(getClass().getSimpleName()).append(":").append(_credentials).append("]");
    return sb.toString();
  }
}


