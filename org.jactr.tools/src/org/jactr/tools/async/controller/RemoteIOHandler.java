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
package org.jactr.tools.async.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.handler.demux.ExceptionHandler;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.IDebugController;
import org.jactr.tools.async.common.BaseIOHandler;
import org.jactr.tools.async.controller.handlers.BreakpointHandler;
import org.jactr.tools.async.controller.handlers.LoginHandler;
import org.jactr.tools.async.controller.handlers.LogoutHandler;
import org.jactr.tools.async.controller.handlers.ModelStateHandler;
import org.jactr.tools.async.controller.handlers.ProductionHandler;
import org.jactr.tools.async.controller.handlers.RuntimeStateHandler;
import org.jactr.tools.async.message.command.breakpoint.IBreakpointCommand;
import org.jactr.tools.async.message.command.breakpoint.IProductionCommand;
import org.jactr.tools.async.message.command.login.LoginCommand;
import org.jactr.tools.async.message.command.login.LogoutCommand;
import org.jactr.tools.async.message.command.state.IModelStateCommand;
import org.jactr.tools.async.message.command.state.IRuntimeStateCommand;
import org.jactr.tools.async.message.event.login.LoginAcknowledgedMessage;

/**
 * @author developer
 */
public class RemoteIOHandler extends BaseIOHandler
{
  /**
   * logger definition
   */
  static private final Log   LOGGER          = LogFactory
                                                 .getLog(RemoteIOHandler.class);

  static public final String CREDENTIALS     = "jactr.credentials";

  private IoSession          _ownerSession;

  private boolean            _allowListeners = false;

  private IController        _controller;

  /**
   * 
   */
  public RemoteIOHandler(IController controller)
  {
    _controller = controller;

    /*
     * handle start up credential
     */
    addReceivedMessageHandler(LoginCommand.class, new LoginHandler(this));
    /*
     * state control
     */
    addReceivedMessageHandler(IRuntimeStateCommand.class,
        new RuntimeStateHandler(this));

    addReceivedMessageHandler(IModelStateCommand.class, new ModelStateHandler(
        this));

    addReceivedMessageHandler(LogoutCommand.class, new LogoutHandler());

    /*
     * breakpoints
     */
    if (_controller instanceof IDebugController)
    {
      addReceivedMessageHandler(IBreakpointCommand.class,
          new BreakpointHandler(this));
      addReceivedMessageHandler(IProductionCommand.class,
          new ProductionHandler(this));
    }

    addExceptionHandler(Throwable.class, new ExceptionHandler<Throwable>() {

      public void exceptionCaught(IoSession session, Throwable exception)
          throws Exception
      {
        /**
         * Error : error
         */
        LOGGER.error(
            "Exception caught from session " + session + ", closing. ",
            exception);
        /*
         * waiting for all the pending writes would result in a serious backlog
         * of queued messages if we couldn't actually wait for the pending
         * writes.
         */
        // waitForPendingWrites();
        session.close();
      }

    });
  }

  /**
   * return the controller, we require the session so we can ensure no errant
   * handlers access the controller
   */
  public IController getController(IoSession session)
  {
    allowsCommands(session);
    return _controller;
  }

  final synchronized public boolean isOwner(IoSession session)
  {
    boolean couldBeOwner = getCredentials().equals(
        session.getAttribute(CREDENTIALS));
    if (couldBeOwner)
    {
      if (_ownerSession == null)
      {
        _ownerSession = session;
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Acknowledging login");
        session.write(new LoginAcknowledgedMessage(true,
            "You are the owner of this runtime"));
      }

      if (session != _ownerSession)
      {
        String msg = "Another session with the same credentials owns this runtime";
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Rejecting login : " + msg);
        session.write(new LoginAcknowledgedMessage(false, msg));
        throw new SecurityException(msg);
      }
    }
    else if (LOGGER.isDebugEnabled())
      LOGGER.debug(getCredentials() + " do not match those of session "
          + session.getAttribute(CREDENTIALS));

    return couldBeOwner;
  }

  final public void allowsCommands(IoSession session)
  {
    if (!isOwner(session))
    {
      String message = session
          + " is not allowed to send commands, disconnecting ";
      SecurityException e = new SecurityException(message);
      if (LOGGER.isWarnEnabled()) LOGGER.warn(message, e);
      throw e;
    }
  }

  final public void allowsListeners(IoSession session)
  {
    if (!isOwner(session) && !_allowListeners)
    {
      String message = "Listening is not permitted by anyone other than owner, closing "
          + session;
      session.write(new LoginAcknowledgedMessage(false, message));
      if (LOGGER.isWarnEnabled()) LOGGER.warn(message);
      throw new SecurityException(message);
    }
  }

  @Override
  public void sessionOpened(IoSession session) throws Exception
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Accepted connection from " + session);
    _ownerSession = session;
    super.sessionOpened(session);
  }

  public IoSession getOwner()
  {
    return _ownerSession;
  }

  @Override
  public void sessionClosed(IoSession session) throws Exception
  {
    super.sessionClosed(session);
    if (isOwner(session))
    {
      _ownerSession = null;
      if (_controller.isRunning())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("No master is connected, must permit uninterrupted run");
        /*
         * clear all the break points and resume
         */
        if (_controller instanceof IDebugController)
          ((IDebugController) _controller).clearBreakpoints();

        if (_controller.isSuspended()) _controller.resume();
      }
    }
  }
}
