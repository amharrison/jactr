/*
 * Created on May 31, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.parser.xml;

import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.misc.DetailedCommonTreeAdaptor;
import org.jactr.io.antlr3.parser.AbstractModelParser;
import org.jactr.io.parser.IModelParser;

public class JACTRModelParser extends AbstractModelParser implements
    IModelParser
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(JACTRModelParser.class);

  /**
   * @see org.jactr.io.antlr3.parser.AbstractModelParser#createLexer()
   */
  @Override
  protected Lexer createLexer()
  {
    JACTRLexer lexer = new JACTRLexer();
    lexer.setModelParser(this);
    return lexer;
  }

  /**
   * @see org.jactr.io.antlr3.parser.AbstractModelParser#createParser()
   */
  @Override
  protected Parser createParser()
  {
    JACTRParser parser = new JACTRParser(NULL_TOKEN_STREAM);
    parser.setModelParser(this, new DetailedCommonTreeAdaptor(this));
    return parser;
  }

  /**
   * @see org.jactr.io.antlr3.parser.AbstractModelParser#parseInternal(org.antlr.runtime.Parser)
   */
  @Override
  protected CommonTree parseInternal(Parser parser) throws RecognitionException
  {
    return ((JACTRParser) parser).model().tree;
  }

}
