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

package org.jactr.core.chunk.event;

import java.util.EventListener;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IChunkListener extends EventListener
{

  /**
   * when the chunk is accessed
   * 
   * @param event
   */
  public void chunkAccessed(ChunkEvent event);

  public void chunkEncoded(ChunkEvent event);

  public void slotChanged(ChunkEvent event);

  public void similarityChanged(ChunkEvent event);

  /**
   * {@link ChunkEvent#getChunk()} is about to be merged into
   * {@link ChunkEvent#getSource()}
   * 
   * @param event
   */
  public void mergingWith(ChunkEvent event);

  /**
   * {@link ChunkEvent#getSource()} is about to be merged into
   * {@link ChunkEvent#getChunk()}
   * 
   * @param event
   */
  public void mergingInto(ChunkEvent event);
}
