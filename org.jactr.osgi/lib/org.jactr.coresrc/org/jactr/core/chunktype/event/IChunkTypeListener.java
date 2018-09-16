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

package org.jactr.core.chunktype.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;

/*
 * interface for listening to changes in a specific chunk type
 */
/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IChunkTypeListener extends EventListener, IParameterListener
{

  /**
   * chunktype has been encoded by the model
   * 
   * @param cte
   */
  public void chunkTypeEncoded(ChunkTypeEvent cte);

  /**
   * a child chunk type has been added ... this chunktype may not have been
   * encoded yet though..
   * 
   * @param cte
   */
  public void childAdded(ChunkTypeEvent cte);

  /**
   * a chunk has been added. this chunk will necessarily have been encoded
   * 
   * @param cte
   */
  public void chunkAdded(ChunkTypeEvent cte);

  /**
   * a slot value has changed
   * 
   * @param cte
   */
  public void slotChanged(ChunkTypeEvent cte);

  /**
   * a slot was added
   * 
   * @param cte
   */
  public void slotAdded(ChunkTypeEvent cte);

  /**
   * slot has been removed
   * 
   * @param cte
   */
  public void slotRemoved(ChunkTypeEvent cte);

}
