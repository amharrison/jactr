/*
 * Created on May 30, 2007 Copyright (C) 2001-2007, Anthony Harrison
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
package org.jactr.tools.async.shadow.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.tools.async.common.BaseIOHandler;
import org.jactr.tools.async.message.command.login.LogoutCommand;

/**
 * logout sequence is really idiotic, the remote interface after all is done
 * sends the logout command to the shadow controller who just echoes it back.
 * when the remote interface receives the logout reply it actually disconnects
 * 
 * @author developer
 */
public class LogoutMessageHandler implements MessageHandler<LogoutCommand>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(LogoutMessageHandler.class);

  private BaseIOHandler    _handler;

  public LogoutMessageHandler(BaseIOHandler handler)
  {
    _handler = handler;
  }

  /**
   * @see org.apache.mina.handler.demux.MessageHandler#messageReceived(org.apache.mina.common.IoSession,
   *      java.lang.Object)
   */
  public void handleMessage(IoSession session, LogoutCommand arg1) throws Exception
  {
    /*
     * we merely echo this back
     */
    _handler.write(new LogoutCommand());
  }

}
