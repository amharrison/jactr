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
package org.jactr.tools.async.shadow.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.tools.async.message.event.state.IRuntimeStateEvent;
import org.jactr.tools.async.shadow.ShadowController;
import org.jactr.tools.async.shadow.ShadowIOHandler;
/**
 * @author developer
 *
 */
public class RuntimeStateMessageHandler implements MessageHandler<IRuntimeStateEvent>
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(RuntimeStateMessageHandler.class);

  /**
   * @see org.apache.mina.handler.demux.MessageHandler#messageReceived(org.apache.mina.common.IoSession, java.lang.Object)
   */
  public void handleMessage(IoSession session, IRuntimeStateEvent message) throws Exception
  {
    ShadowController controller = (ShadowController) session.getAttribute(ShadowIOHandler.CONTROLLER_ATTR);
    
    controller.setCurrentSimulationTime(message.getSimulationTime());

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got "+message);
    
    switch(message.getState())
    {
      case STARTED :
        controller.started(message.getModelNames());
        break;
//      case STOPPED : controller.stopped(); break;
//      case SUSPENDED : controller.suspended(); break;
//      case RESUMED : controller.resumed(); break;
    }
  }

}


