/*
 * Created on May 14, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.misc;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonTreeException extends RuntimeException
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(CommonTreeException.class);

  CommonTree              _startNode;

  CommonTree              _endNode;

  public CommonTreeException(String message, CommonTree startNode)
  {
    super(message);
    _startNode = startNode;
  }

  public CommonTreeException(String message, CommonTree startNode,
      Throwable thrown)
  {
    super(message, thrown);
    _startNode = startNode;
  }

  public CommonTree getStartNode()
  {
    return _startNode;
  }

  public void setStartNode(CommonTree startNode)
  {
    _startNode = startNode;
  }

  public void setEndNode(CommonTree endNode)
  {
    _endNode = endNode;
  }

  public CommonTree getEndNode()
  {
    return _endNode;
  }

  /**
   * noop so we don't waste cycles with useless traces
   * 
   * @see java.lang.Throwable#fillInStackTrace()
   */
  @Override
  public Throwable fillInStackTrace()
  {
    return this;
  }
}
