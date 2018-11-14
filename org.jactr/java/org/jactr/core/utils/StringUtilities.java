/*
 * Created on Apr 11, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.utils;

import java.util.stream.Collectors;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;

/**
 * @author developer
 */
public class StringUtilities
{

  static public String toString(IChunk chunk)
  {
    if (chunk == null) return "null";
    ISymbolicChunk sc = chunk.getSymbolicChunk();
    StringBuilder sb = new StringBuilder(sc.getName()).append(" ");
    sb.append(sc.getSlots().stream().map(s -> {
      return s.getName() + "=" + s.getValue();
    }).collect(Collectors.joining(",")));
    return sb.toString();
  }

  static public String toString(IChunkType chunkType)
  {
    if (chunkType == null) return "null";
    ISymbolicChunkType sc = chunkType.getSymbolicChunkType();
    StringBuilder sb = new StringBuilder(sc.getName()).append(" ");
    sb.append(sc.getSlots().stream().map(s -> {
      return s.getName() + "=" + s.getValue();
    }).collect(Collectors.joining(",")));
    return sb.toString();
  }
}
