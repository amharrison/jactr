/*
 * Created on Jun 13, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.tools.async.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author developer
 */
public class BulkMessage extends BaseMessage implements Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = 1897510208777173838L;

  /**
   * logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(BulkMessage.class);

  Collection<IMessage>     _messages;

  public BulkMessage(Collection<IMessage> messages)
  {
    _messages = new ArrayList<IMessage>(messages);
  }

  public Collection<IMessage> getMessages()
  {
    return Collections.unmodifiableCollection(_messages);
  }
  
  public int getSize()
  {
    return _messages.size();
  }
}
