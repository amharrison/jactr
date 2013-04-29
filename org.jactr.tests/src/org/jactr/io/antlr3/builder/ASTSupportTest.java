/*
 * Created on May 22, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.builder;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.compiler.JACTRCompiler;
import org.jactr.io.antlr3.misc.ASTSupport;

public class ASTSupportTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(ASTSupportTest.class);

  ASTSupport              _support;

  @Override
  protected void setUp() throws Exception
  {
    _support = new ASTSupport();
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  protected void compile(Tree modelTree) throws Exception
  {
    CommonTreeNodeStream nodes = new CommonTreeNodeStream(modelTree);
    JACTRCompiler compiler = new JACTRCompiler(nodes);
    compiler.model();
  }

  public void testModel()
  {
    CommonTree modelTree = _support.createModelTree("testModel");
    try
    {
      compile(modelTree);
    }
    catch (Exception e)
    {
      LOGGER.error("failed to compile", e);
      fail(e.getMessage());
    }
  }
}
