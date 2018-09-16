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

import java.util.concurrent.Executor;

import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.chunktype.event.IChunkTypeListener;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.ICommentable;
import org.jactr.core.utils.IMetaContainer;

/**
 * The class the represents the top-level wrapper for common ChunkTypes.
 * Contains the wrapper for Symbolic, SubsymbolicChunkTypes and the event
 * listeners
 * 
 * @author harrison
 * @created February 5, 2003
 */
public interface IChunkType extends Comparable<IChunkType>, ICommentable,
    IAdaptable, IMetaContainer
{

  /**
   * return the symbolic compoentn.
   */
  public ISymbolicChunkType getSymbolicChunkType();

  /**
   * return the subsymbolic portion
   */
  public ISubsymbolicChunkType getSubsymbolicChunkType();

  /**
   * add chunk listener
   */
  public void addListener(IChunkTypeListener ctl, Executor executor);

  /**
   * remove chunk listener
   */
  public void removeListener(IChunkTypeListener ctl);

  public boolean hasListeners();

  public void dispatch(ChunkTypeEvent event);

  /**
   * Called when one is sure the IChunkType shall not be used again. This may
   * call the dispose() method of all the derived Chunks.
   * 
   * @since
   */
  public void dispose();

  /**
   * Gets the a attribute of the IChunkType object
   * 
   * @param ct
   *            Description of Parameter
   * @return The a value
   * @since
   */
  public boolean isA(IChunkType ct);

  public void encode();

  /**
   * has this chunktype been encoded
   * 
   * @return
   */
  public boolean isEncoded();

  /**
   * get the model that contains this chunktype
   * 
   * @return
   */
  public IModel getModel();

}
