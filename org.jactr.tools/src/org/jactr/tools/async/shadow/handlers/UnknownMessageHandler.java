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
package org.jactr.tools.async.shadow.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.handler.IMessageHandler;
import org.commonreality.net.session.ISessionInfo;
import org.jactr.tools.async.message.IMessage;

/**
 * @author developer
 */
public class UnknownMessageHandler implements IMessageHandler<IMessage>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(UnknownMessageHandler.class);

  // /**
  // * @see
  // org.apache.mina.handler.demux.MessageHandler#messageReceived(org.apache.mina.common.IoSession,
  // * java.lang.Object)
  // */
  // public void handleMessage(IoSession arg0, IMessage message) throws
  // Exception
  // {
  // }

  @Override
  public void accept(ISessionInfo session, IMessage message)
  {
    if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue what to do with debug event " + message + " class:"
          + message.getClass().getSimpleName());

  }

}
