/*
 * Created on Nov 29, 2006
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
package org.jactr.core.utils.parameter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author developer
 *
 */
public class ParameterException extends RuntimeException
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ParameterException.class);

  /**
   * 
   */
  public ParameterException()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   */
  public ParameterException(String arg0)
  {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   */
  public ParameterException(Throwable arg0)
  {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   * @param arg1
   */
  public ParameterException(String arg0, Throwable arg1)
  {
    super(arg0, arg1);
    // TODO Auto-generated constructor stub
  }

}


