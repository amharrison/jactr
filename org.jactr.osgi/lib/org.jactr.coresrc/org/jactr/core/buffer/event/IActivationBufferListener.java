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

package org.jactr.core.buffer.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;

/**
 * listen to changes in a specific buffer. If you are tracking a chunk within a
 * buffer it is necessar to listen for the relevant
 * {@link #sourceChunkAdded(ActivationBufferEvent)}, but also the
 * {@link #sourceChunkRemoved(ActivationBufferEvent)} and
 * {@link #sourceChunksCleared(ActivationBufferEvent)}. clearing will not fire
 * off {@link #sourceChunkRemoved(ActivationBufferEvent)}
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IActivationBufferListener extends EventListener,
    IParameterListener
{

  /**
   * called when a source chunk is added to the buffer
   */
  public void sourceChunkAdded(ActivationBufferEvent abe);

  /**
   * called when a source chunk is removed from the buffer. If the listener
   * is not inline, the hash of the removed chunk may be different if
   * it is encoded and merged into an existing chunk
   */
  public void sourceChunkRemoved(ActivationBufferEvent abe);

  /**
   * called when all the chunks are removed from the buffer. If a clear is
   * called, this is the only event fired, not
   * {@link #sourceChunkRemoved(ActivationBufferEvent)}
   */
  public void sourceChunksCleared(ActivationBufferEvent abe);

  /**
   * Fired when a request is made to a buffer. That is, the request has been
   * accepted and possibly started. How far into the processing of the request
   * the notification occurs depends upon the implementation of the request
   * handler.
   * 
   * @param abe
   */
  public void requestAccepted(ActivationBufferEvent abe);

  /**
   * name says it all
   */
  public void statusSlotChanged(ActivationBufferEvent abe);

  /**
   * called when a chunk has been matched within the buffer by a some production
   * that has fired
   */
  public void chunkMatched(ActivationBufferEvent abe);
}
