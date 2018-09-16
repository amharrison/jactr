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
package org.jactr.io.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.antlr.runtime.tree.CommonTree;

/**
 * Output a modelDescriptor to a file. This often wraps ICodeGenerators
 * 
 * @author developer
 */
public interface IModelWriter
{
  public void write(CommonTree modelDescriptor, OutputStream outputStream)
      throws IOException;
}
