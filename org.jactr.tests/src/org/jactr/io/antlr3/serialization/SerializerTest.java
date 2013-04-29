/*
 * Created on Jun 2, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.serialization;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import junit.framework.TestCase;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.IOUtilities;

public class SerializerTest extends TestCase
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(SerializerTest.class);

  public void testSaveAndLoad() throws Exception
  {
    ArrayList<Exception> warnings = new ArrayList<Exception>();
    ArrayList<Exception> errors = new ArrayList<Exception>();

    CommonTree modelDesc = IOUtilities.loadModelFile(
        "org/jactr/core/runtime/semantic-model.jactr", warnings, errors);

    assertEquals(0, warnings.size());
    assertEquals(0, errors.size());

    assertTrue(IOUtilities.compileModelDescriptor(modelDesc, warnings, errors));

    // dump to file
    File outputFile = File.createTempFile("test", ".compiled");
    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(
        new GZIPOutputStream(new FileOutputStream(outputFile))));

    Serializer.write(modelDesc, dos);
    dos.close();

    DataInputStream dis = new DataInputStream(new GZIPInputStream(
        new BufferedInputStream(new FileInputStream(outputFile))));

    CommonTree newTree = Serializer.read(dis);

    compare(modelDesc, newTree);
  }

  static public void compare(CommonTree oldTree, CommonTree newTree)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Old token " + oldTree.getToken() + " new token:"
          + newTree.getToken());
    assertEquals("Type inequal", oldTree.getType(), newTree.getType());
    assertEquals("Text inequal", oldTree.getText(), newTree.getText());
    assertEquals("Line# inequal", oldTree.getLine(), newTree.getLine());
    assertEquals("CharPos inequal", oldTree.getCharPositionInLine(), newTree
        .getCharPositionInLine());
    assertEquals("StartIndex inequal", oldTree.getTokenStartIndex(), newTree.getTokenStartIndex());
    assertEquals("StopIndex inequal", oldTree.getTokenStopIndex(), newTree.getTokenStopIndex());
    assertEquals("Child# inequal", oldTree.getChildCount(), newTree
        .getChildCount());
    for (int i = 0; i < oldTree.getChildCount(); i++)
      compare((CommonTree) oldTree.getChild(i), (CommonTree) newTree
          .getChild(i));
  }
}
