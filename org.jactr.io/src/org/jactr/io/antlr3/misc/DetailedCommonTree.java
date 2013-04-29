/*
 * Created on Apr 20, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.net.URL;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author developer
 */
public class DetailedCommonTree extends CommonTree
{
  /**
   * logger definition
   */
  static private final Log LOGGER       = LogFactory
                                            .getLog(DetailedCommonTree.class);

  private int              _endOffset   = -1;

  private int              _startOffset = -1;

  private URL              _source;

  /**
   * @param arg0
   */
  public DetailedCommonTree(CommonTree arg0, URL source)
  {
    super(arg0);
    _source = source;
  }
  
  public URL getSource()
  {
    return _source;
  }

  @Override
  public Tree dupNode()
  {
    return new DetailedCommonTree(this, _source);
  }

  /**
   * @param arg0
   */
  public DetailedCommonTree(Token arg0, URL source)
  {
    super(arg0);
    if (arg0 != null)
    {
      CommonToken token = (CommonToken) arg0;
      setEndOffset(token.getStopIndex());
      setStartOffset(token.getStartIndex());
    }
    _source = source;
  }

  public int getStopOffset()
  {
    return _endOffset;
  }

  public int getStartOffset()
  {
    return _startOffset;
  }

  public void setEndOffset(int end)
  {
    _endOffset = end;
  }

  public void setStartOffset(int start)
  {
    _startOffset = start;
  }
  
  public void setSource(URL url)
  {
    _source = url;
  }
}
