/*
 * Created on Jul 18, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm.motor;

import java.util.Collection;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.io.CommonIO;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.generator.CodeGeneratorFactory;

public class MotorLoadTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(MotorLoadTest.class);

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

  public void testLoad() throws Exception
  {
    CommonTree modelDescriptor = CommonIO.getModelDescriptor(CommonIO
        .parseModel("org/jactr/modules/pm/motor/motor-test.jactr"));
    Collection<CommonTree> knownBuffers = ASTSupport.getTrees(modelDescriptor,
        JACTRBuilder.BUFFER);

    LOGGER.debug("Descriptor Raw : " + modelDescriptor.toStringTree());
    for (StringBuilder line : CodeGeneratorFactory.getCodeGenerator("jactr")
        .generate(modelDescriptor, true))
      LOGGER.debug(line.toString());

    assertEquals("Not the right number of buffers " + knownBuffers, 4,
        knownBuffers.size());

    // we need to accept the warning for the bingind of =next
    CommonIO.compilerTest(modelDescriptor, false, true);
  }

  public void testConstruction() throws Exception
  {
    CommonTree desc = CommonIO.getModelDescriptor(CommonIO
        .parseModel("org/jactr/modules/pm/motor/motor-test.jactr"));
    CommonIO.compilerTest(desc, false, true);

    IModel model = CommonIO.constructorTest(desc);
    assertNotNull(model);

    IDeclarativeModule decM = model.getDeclarativeModule();
    assertNotNull(decM);

    IChunkType motorCommand = decM
        .getChunkType(IMotorModule.MOVEMENT_CHUNK_TYPE).get();
    assertNotNull(motorCommand);

  }
}
