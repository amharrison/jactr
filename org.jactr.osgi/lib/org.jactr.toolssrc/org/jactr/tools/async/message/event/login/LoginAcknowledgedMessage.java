/*
 * Created on Mar 7, 2007
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
package org.jactr.tools.async.message.event.login;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.async.message.BaseMessage;
/**
 * @author developer
 *
 */
public class LoginAcknowledgedMessage extends BaseMessage implements
    Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -530402329119582763L;

  /**
   logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                      .getLog(LoginAcknowledgedMessage.class);

  private boolean _accepted;
  private String _message;
  
  public LoginAcknowledgedMessage(boolean accepted, String message)
  {
    _accepted = accepted;
    _message = message;
  }
  
  public boolean wasAccepted()
  {
    return _accepted;
  }
  
  public String getMessage()
  {
    return _message;
  }

  @Override
  public String toString()
  {
    return String.format("[%s] accepted:%s  %s ", getClass().getSimpleName(),
        getMessage(), wasAccepted());
  }
}


