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

package org.jactr.core.chunktype;

import java.util.Collection;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.utils.IAdaptable;

/**
 * This is the basic interface for the symbolic component of chunktypes. Since
 * ChunkTypes can utilize an inheritance structure, implementations must be sure
 * that added slots are propagated to children chunktypes and chunk instances.
 * Likewise, the addition of new parents (not a common occurrence) should be
 * propogated appropriately.
 * 
 * @author harrison
 * @created January 22, 2003
 */
public interface ISymbolicChunkType extends
    org.jactr.core.slot.IUniqueSlotContainer, IAdaptable
{

  /**
   * Return the unique chunktype name. All chunktypes, like chunks, must have
   * unique names.
   * 
   * @return The chunkTypeName value
   * @since
   */
  public String getName();

  /**
   * Set the chunktype name. This should only be called once ? reseting the name
   * after the model has added the chunktype can have unpredictable results.
   * 
   * @param name
   *            The new chunkTypeName value
   * @since
   */
  public void setName(String name);

  /**
   * single inheritance parent access.
   * 
   * @throws IllegalStateException
   *           if there are more than one parent
   * @return
   */
  public IChunkType getParent();

  /**
   * Return the immediate parent of this chunktype. multiple parents are
   * possible to support multiple inheritance
   * 
   * @return The parent value
   * @since
   */
  public Collection<IChunkType> getParents();

  /**
   * return an array of all the children chunk types
   * 
   * @return The children value
   * @since
   */
  public Collection<IChunkType> getChildren();

  /**
   * Gets the numberOfChildren attribute of the ISymbolicChunkType object
   * 
   * @return The numberOfChildren value
   * @since
   */
  public int getNumberOfChildren();

  /**
   * add a parent of this chunktype
 * @return
   */
  public IChunkType addParent(IChunkType ct);
  
  /**
   * add a chunktype as a child,
   */
  public void addChild(IChunkType ct);

  /**
   * return all chunks of this type
   * 
   * @return The chunks value
   * @since
   */
  public Collection<IChunk> getChunks();

  /**
   * add an encoded chunk to this chunktypes collection
   */
  public void addChunk(IChunk c);

  /**
   * Gets the numberOfChunks attribute of the ISymbolicChunkType object
   * 
   * @return The numberOfChunks value
   * @since
   */
  public int getNumberOfChunks();

  /**
   *
   */
  public boolean isA(IChunkType ct);

  /**
   * Description of the Method
   * 
   * @since
   */
  public void dispose();

  public void encode();
}
