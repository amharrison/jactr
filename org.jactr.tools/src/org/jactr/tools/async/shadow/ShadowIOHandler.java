/*
 * Created on Feb 20, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.shadow;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.ExceptionHandler;
import org.apache.mina.handler.demux.MessageHandler;
import org.jactr.tools.async.common.BaseIOHandler;
import org.jactr.tools.async.message.IMessage;
import org.jactr.tools.async.message.command.login.LoginCommand;
import org.jactr.tools.async.message.command.login.LogoutCommand;
import org.jactr.tools.async.message.event.data.BreakpointReachedEvent;
import org.jactr.tools.async.message.event.data.ModelDataEvent;
import org.jactr.tools.async.message.event.login.LoginAcknowledgedMessage;
import org.jactr.tools.async.message.event.state.IModelStateEvent;
import org.jactr.tools.async.message.event.state.IRuntimeStateEvent;
import org.jactr.tools.async.shadow.handlers.BreakpointMessageHandler;
import org.jactr.tools.async.shadow.handlers.LoginMessageHandler;
import org.jactr.tools.async.shadow.handlers.LogoutMessageHandler;
import org.jactr.tools.async.shadow.handlers.ModelDataMessageHandler;
import org.jactr.tools.async.shadow.handlers.ModelStateHandler;
import org.jactr.tools.async.shadow.handlers.RuntimeStateMessageHandler;
import org.jactr.tools.async.shadow.handlers.UnknownMessageHandler;

/**
 * @author developer
 */
public class ShadowIOHandler extends BaseIOHandler
{
  /**
   * logger definition
   */
  static private final Log              LOGGER                 = LogFactory
                                                                   .getLog(ShadowIOHandler.class);

  static public final String            CONTROLLER_ATTR        = "jactr.controller";

  final private ShadowController        _controller;

  private Lock                          _lock                  = new ReentrantLock();

  public ShadowIOHandler(ShadowController controller)
  {
    _controller = controller;

    addReceivedMessageHandler(LoginAcknowledgedMessage.class, new LoginMessageHandler(
        this));
    addReceivedMessageHandler(LogoutCommand.class, new LogoutMessageHandler(this));
    // handle start,stop,suspend,resume notifications
    addReceivedMessageHandler(IRuntimeStateEvent.class,
        new RuntimeStateMessageHandler());
    addReceivedMessageHandler(IModelStateEvent.class, new ModelStateHandler());
    // handle any full model descriptors
    addReceivedMessageHandler(ModelDataEvent.class, new ModelDataMessageHandler());
    // handle breakpoint reached notification
    addReceivedMessageHandler(BreakpointReachedEvent.class,
        new BreakpointMessageHandler());



    // everything else
    addReceivedMessageHandler(IMessage.class, new UnknownMessageHandler());
    
    addSentMessageHandler(IMessage.class, new MessageHandler<IMessage>() {

      public void handleMessage(IoSession arg0, IMessage arg1) throws Exception
      {

      }

    });

    addExceptionHandler(Throwable.class, new ExceptionHandler<Throwable>() {

      public void exceptionCaught(IoSession session, Throwable exception)
          throws Exception
      {
        /**
         * Error : error
         */
        LOGGER.error(
"Exception caught from session " + session
            + ". Will stumble forward for now",
            exception);
        // waitForPendingWrites();
        // session.close();
      }

    });
  }

  

  @Override
  public void sessionOpened(IoSession session) throws Exception
  {
    session.setAttribute(CONTROLLER_ATTR, _controller);
    /*
     * first thing's first, send out our credentials
     */
    session.write(new LoginCommand(getCredentials()));
    super.sessionOpened(session);
  }

  

  @Override
  public void sessionClosed(IoSession session) throws Exception
  {
    super.sessionClosed(session);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Session has closed, stopping controller");

    /*
     * explicitly stop
     */
    if (_controller.isRunning())
      for (String modelName : _controller.getRunningModels())
        _controller.stopped(modelName);
  }

  

  

}
