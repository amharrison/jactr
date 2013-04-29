/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.chunk;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.utils.IAdaptable;

/**
 * Contains all the methods for manipulating slot/value pairs within a chunk.
 * Also manages the name and chunktype
 * 
 * @author harrison
 * @created January 22, 2003
 */
public interface ISymbolicChunk extends
    org.jactr.core.slot.IUniqueSlotContainer, IAdaptable
{

  /**
   * do any symbolic encoding
   * @param when TODO
   */
  public void encode(double when);

  /**
   * The parent chunk is the org.jactr.chunk.Chunk wrapper that contains this
   * symbolic chunk. IChunk.getSymbolicChunk().getParentChunk() == IChunk.
   * 
   * @return The parentChunk value
   * @since
   */
  public IChunk getParentChunk();

  /**
   * Return the name of the chunk. Each chunk within a model must have a unique
   * name. You may specify a preferred name during chunk creation (via.
   * IModel.createChunk(IChunkType, String)) ? however, if there is a name
   * collision, a unique name will be munged.
   * 
   * @return The chunkName value
   * @since
   */
  public String getName();

  /**
   * Set the chunk name. This is usually only called by the
   * IModel.addChunk(IChunk) method. If the chunk has already been encoded and
   * the name is changed, the model?s behavior is undefined. If you really must
   * change a chunk?s name, remove it from the model first
   * (IModel.removeChunk()), change the name to the new value, and then add it
   * again (IModel.addChunk(IChunk)).
   * 
   * @param name
   *            The new chunkName value
   * @since
   */
  public void setName(String name);

  /**
   * Returns the IChunkType of this chunk.
   * 
   * @return The chunkType value
   * @since
   */
  public IChunkType getChunkType();

  /**
   * Returns true if the chunk has any parent chunktype of ct.
   * 
   * @param ct
   *            Description of Parameter
   * @return The a value
   * @since
   */
  public boolean isA(IChunkType ct);

  /**
   * Returns true if and only if the immediate chunktype parent is
   * ct.SymbolicChunk.getChunkType()==ct.
   * 
   * @param ct
   *            Description of Parameter
   * @return The aStrict value
   * @since
   * @returns true iff the immediate chunktype is ct
   */
  public boolean isAStrict(IChunkType ct);

  /**
   * Description of the Method
   * 
   * @since
   */
  public void dispose();
}
