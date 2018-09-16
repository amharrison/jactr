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
package org.jactr.tools.async.controller.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.handler.IMessageHandler;
import org.commonreality.net.session.ISessionInfo;
import org.jactr.tools.async.controller.RemoteInterface;
import org.jactr.tools.async.credentials.ICredentials;
import org.jactr.tools.async.message.command.login.LoginCommand;

/**
 * Login handler, this is largely historic, but can be augmented later to add
 * multiple listeners
 * 
 * @author developer
 */
public class LoginHandler implements IMessageHandler<LoginCommand>
{
  /**
   * logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LoginHandler.class);

  public LoginHandler()
  {

  }

  @Override
  public void accept(ISessionInfo session, LoginCommand message)
  {
    ICredentials credentials = message.getCredentials();
    session.setAttribute(RemoteInterface.CREDENTIALS_KEY, credentials);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got " + credentials);
  }

}
