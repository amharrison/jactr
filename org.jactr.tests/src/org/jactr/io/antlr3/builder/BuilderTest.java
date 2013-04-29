/*
 * Created on Jun 6, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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

import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.io.CommonIO;
import org.jactr.io.antlr3.misc.ASTSupport;

public class BuilderTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(BuilderTest.class);

  public void testConstruction() throws Exception
  {
    IModel model = CommonIO
        .loadModel("org/jactr/core/runtime/semantic-model.jactr");

    // check the chunktypes
    assertEquals("Incorrect number of chunktypes", 5, model
        .getDeclarativeModule().getChunkTypes().get().size());
    IChunkType chunk = model.getDeclarativeModule().getChunkType("chunk").get();
    assertNotNull("chunktype chunk is missing", chunk);
    IChunkType property = model.getDeclarativeModule().getChunkType("property")
        .get();
    assertNotNull(property);
    // p1-p20
    assertEquals("Incorrect number of property chunks", 20, property
        .getSymbolicChunkType().getChunks().size());

    IChunkType member = model.getDeclarativeModule().getChunkType("is-member")
        .get();
    assertNotNull(member);
    assertEquals("Incorrect number of is-member chunks", 3, member
        .getSymbolicChunkType().getChunks().size());

    // these won't be actually equal since the buffer will copy it on insertion
    assertEquals(model.getDeclarativeModule().getChunk("g3").get()
        .getSymbolicChunk().getName(), model.getActivationBuffer("goal")
        .getSourceChunk().getSymbolicChunk().getName());
  }

  public void test2() throws Exception
  {
    CommonTree modelDescriptor = CommonIO.parserTest(
        "org/jactr/core/runtime/semantic-model.jactr", true, true);
    Map<String, CommonTree> buffers = ASTSupport.getMapOfTrees(modelDescriptor,
        JACTRBuilder.BUFFER);
    assertEquals("not the right number of buffers", 2, buffers.size());
    // goal had better have a chunk in it
    Collection<CommonTree> source = ASTSupport.getAllDescendantsWithType(
        buffers.get("goal"), JACTRBuilder.IDENTIFIER);
    assertEquals("missing source chunk", 1, source.size());
  }

}
