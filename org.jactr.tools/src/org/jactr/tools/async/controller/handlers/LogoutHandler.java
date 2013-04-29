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
package org.jactr.tools.async.controller.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.tools.async.controller.RemoteInterface;
import org.jactr.tools.async.message.command.login.LogoutCommand;

/**
 * @author developer
 */
public class LogoutHandler implements MessageHandler<LogoutCommand>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(LogoutHandler.class);

  /**
   * @see org.apache.mina.handler.demux.MessageHandler#messageReceived(org.apache.mina.common.IoSession,
   *      java.lang.Object)
   */
  public void handleMessage(IoSession session, LogoutCommand arg1) throws Exception
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got a disconnect command");
    RemoteInterface ri = RemoteInterface.getActiveRemoteInterface();
    if (ri.getHandler().isOwner(session))
    {
      IController controller = ACTRRuntime.getRuntime().getController();
      if (controller == null || !controller.isRunning())
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("disconnecting");
        ri.disconnectSafe(false);
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug("could not disconnect - not running");
    }
  }

}
