/*
 * Created on Jul 23, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.jactr.io.resolver.ASTResolver;

public class GenerateFromScratchTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(GenerateFromScratchTest.class);

  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  public void testRaw() throws Exception
  {
    CommonTree ct = IOUtilities.createModelDescriptor("raw-test", true);

    ICodeGenerator cg = CodeGeneratorFactory.getCodeGenerator("jactr");
    LOGGER.debug("Raw");
    for (StringBuilder line : cg.generate(ct, true))
      LOGGER.debug(line.toString());

    // compile
    CommonIO.compilerTest(ct, true, true);

    IModel model = CommonIO.constructorTest(ct);

    ct = ASTResolver.toAST(model, true);

    LOGGER.debug("Resolved");
    for (StringBuilder line : cg.generate(ct, true))
      LOGGER.debug(line.toString());

    CommonIO.compilerTest(ct, true, true);
  }
}
