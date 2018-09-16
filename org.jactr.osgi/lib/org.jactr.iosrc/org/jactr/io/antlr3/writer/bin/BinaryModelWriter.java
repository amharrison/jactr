/*
 * Created on Apr 7, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.writer.bin;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.serialization.Serializer;
import org.jactr.io.writer.IModelWriter;

/**
 * @author developer
 */
public class BinaryModelWriter implements IModelWriter
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(BinaryModelWriter.class);

  /**
   * @see org.jactr.io.writer.IModelWriter#write(org.antlr.runtime.tree.CommonTree,
   *      java.io.OutputStream)
   */
  public void write(CommonTree modelDescriptor, OutputStream outputStream)
      throws IOException
  {
    Serializer.write(modelDescriptor, new DataOutputStream(outputStream));
  }

}
