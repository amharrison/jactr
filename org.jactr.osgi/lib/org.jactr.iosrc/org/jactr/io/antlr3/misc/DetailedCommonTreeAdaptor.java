/*
 * Created on Apr 16, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.parser.IModelParser;

/**
 * We override this to compensate for the fact that createToken(Token oldToken)
 * doesn't snag byte offsets. only copies
 * 
 * @author developer
 */
public class DetailedCommonTreeAdaptor extends CommonTreeAdaptor
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(DetailedCommonTreeAdaptor.class);

  IModelParser             _modelParser;

  public DetailedCommonTreeAdaptor()
  {
  }

  /**
   * 
   */
  public DetailedCommonTreeAdaptor(IModelParser parser)
  {
    _modelParser = parser;
  }

  @Override
  public Object create(Token payload)
  {
    if (_modelParser != null)
      return new DetailedCommonTree(payload, _modelParser.getBaseURL());
    return new DetailedCommonTree(payload, null);
  }

  @Override
  public Token createToken(Token fromToken)
  {
    CommonToken rtn = new CommonToken(fromToken);
    if (fromToken instanceof CommonToken)
    {
      CommonToken old = (CommonToken) fromToken;
      rtn.setStartIndex(old.getStartIndex());
      rtn.setStopIndex(old.getStopIndex());
    }
    return rtn;
  }

  /**
   * since we can't gain access to the post processing of trees in the parser in
   * order to call the tree trackers, we do it here. this is called at the end
   * of all tree assemblies
   * 
   * @see org.antlr.runtime.tree.CommonTreeAdaptor#setTokenBoundaries(java.lang.Object,
   *      org.antlr.runtime.Token, org.antlr.runtime.Token)
   */
  @Override
  public void setTokenBoundaries(Object tree, Token one, Token two)
  {
    super.setTokenBoundaries(tree, one, two);
    if (tree instanceof DetailedCommonTree && one instanceof CommonToken
        && two instanceof CommonToken)
    {
      DetailedCommonTree dct = (DetailedCommonTree) tree;
      CommonToken c1 = (CommonToken) one;
      CommonToken c2 = (CommonToken) two;
      dct.setStartOffset(c1.getStartIndex());
      //note: offset not length
      dct.setEndOffset(c2.getStopIndex());
    }
    if (tree instanceof CommonTree) _modelParser.delegate((CommonTree) tree);
  }
}
