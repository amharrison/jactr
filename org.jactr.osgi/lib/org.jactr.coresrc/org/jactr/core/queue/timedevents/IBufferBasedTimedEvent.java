/*
 * Created on Feb 21, 2007
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.queue.timedevents;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.queue.ITimedEvent;
/**
 * this is a timed event that operates upon a specific buffer. This is
 * primarily for tracking purposes so that we can inspect arbitrary timed
 * events and know what buffer they will be affecting.
 * @author developer
 *
 */
public interface IBufferBasedTimedEvent extends ITimedEvent
{

  /**
   * return the buffer that this timed event will be posting changes
   * to.
   * @return
   */
  public IActivationBuffer getBuffer();
  
  /**
   * return the chunk that is (or will be) in the buffer that will be
   * manipulated
   * @return
   */
  public IChunk getBoundChunk();
}


