/*
 * Created on May 25, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3;

import java.util.Collection;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.Parser;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.compiler.JACTRCompiler;
import org.jactr.io.antlr3.misc.CommonTreeException;

public abstract class AbstractGeneralIOTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(AbstractGeneralIOTest.class);

  public void checkForExceptions(String fileName,
      Collection<Exception> exceptions, boolean failOnException)
  {
    for (Exception e : exceptions)
    {
      dumpException(fileName, e);
      if (failOnException) fail("Failed " + fileName + " " + e.getMessage());
    }
  }

  public void dumpException(String fileName, Exception e)
  {
    if (e instanceof CommonTreeException)
    {
      CommonTreeException te = (CommonTreeException) e;
      CommonTree node = te.getStartNode();
      if (node != null)
        LOGGER.error(fileName + "@ " + node.getLine() + ","
            + node.getCharPositionInLine() + ": \"" + node.getText() + "\" : "
            + node.getToken());
    }
    LOGGER.error("could not parse " + fileName, e);
  }

  public abstract Lexer instantiateLexer(CharStream input);

  public abstract Parser instantiateParser(CommonTokenStream tokenStream);

  public abstract CommonTree parse(Parser parser) throws Exception;

  public abstract Collection<Exception> getErrors(Parser parser);

  public abstract Collection<Exception> getWarnings(Parser parser);

  public CommonTree parseFile(String fileName)
  {
    try
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Beginning parse of " + fileName);
      CharStream in = new ANTLRInputStream(getClass().getClassLoader()
          .getResource(fileName).openStream());
      Lexer lexer = instantiateLexer(in);
      CommonTokenStream tokens = new CommonTokenStream(lexer);
      Parser parser = instantiateParser(tokens);
      CommonTree modelTree = parse(parser);

      checkForExceptions(fileName, getWarnings(parser), false);
      checkForExceptions(fileName, getErrors(parser), true);
      return modelTree;
    }
    catch (Exception e)
    {
      dumpException(fileName, e);
      fail("Failed " + fileName + " " + e.getMessage());
      return null;
    }
  }

  public CommonTree compile(String fileName, CommonTree modelTree)
  {
    try
    {
      CommonTreeNodeStream nodes = new CommonTreeNodeStream(modelTree);
      JACTRCompiler compiler = new JACTRCompiler(nodes);
      compiler.model();
      checkForExceptions(fileName, compiler.getWarnings(), false);
      checkForExceptions(fileName, compiler.getErrors(), true);
      return modelTree;
    }
    catch (Exception e)
    {
      dumpException(fileName, e);
      fail("Failed " + fileName + " " + e.getMessage());
      return null;
    }
  }

  public IModel build(String fileName, CommonTree modelTree)
  {
    try
    {
      CommonTreeNodeStream nodes = new CommonTreeNodeStream(modelTree);
      JACTRBuilder builder = new JACTRBuilder(nodes);
      IModel rtn = builder.model();
      checkForExceptions(fileName, builder.getWarnings(), false);
      checkForExceptions(fileName, builder.getErrors(), true);
      return rtn;
    }
    catch (Exception e)
    {
      dumpException(fileName, e);
      fail("Failed " + fileName + " " + e.getMessage());
      return null;
    }
  }

  public IModel fullTest(String fileName)
  {
    return build(fileName, compile(fileName, parseFile(fileName)));
  }
}
