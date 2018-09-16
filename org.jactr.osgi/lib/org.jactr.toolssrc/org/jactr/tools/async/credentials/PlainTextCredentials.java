/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.credentials;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author developer
 */
public class PlainTextCredentials implements ICredentials, Serializable
{
  /**
   * 
   */
  private static final long serialVersionUID = -3627138776039369987L;

  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(PlainTextCredentials.class);

  private String           _user;

  private String           _password;

  public PlainTextCredentials(String ... args)
  {
    if(args.length<2)
      throw new IllegalArgumentException("Must provide two arguments: user password");
    _user = args[0];
    _password = args[1];
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder(_user);
    sb.append(":").append(_password);
    return sb.toString();
  }

  @Override
  public int hashCode()
  {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + (_password == null ? 0 : _password.hashCode());
    result = PRIME * result + (_user == null ? 0 : _user.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final PlainTextCredentials other = (PlainTextCredentials) obj;
    if (_password == null)
    {
      if (other._password != null) return false;
    }
    else if (!_password.equals(other._password)) return false;
    if (_user == null)
    {
      if (other._user != null) return false;
    }
    else if (!_user.equals(other._user)) return false;
    return true;
  }

}
