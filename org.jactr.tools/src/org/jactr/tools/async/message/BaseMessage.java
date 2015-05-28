/*
 * Created on Feb 22, 2007
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
package org.jactr.tools.async.message;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author developer
 *
 */
public class BaseMessage implements IMessage, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -5284931270187598326L;

  /**
   logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(BaseMessage.class);

  static private transient long      LAST_ID          = 0;
  
  private long _id;

  private final long        _timestamp       = System.currentTimeMillis();
  
  public BaseMessage()
  {
    synchronized(IMessage.class)
    {
      _id = ++LAST_ID;
    }
  }
  
  /**
   * @see org.jactr.tools.async.message.IMessage#getID()
   */
  public long getID()
  {
    return _id;
  }

  public long getTimestamp()
  {
    return _timestamp;
  }

}


