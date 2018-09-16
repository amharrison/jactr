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

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;

import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.ICommentable;
import org.jactr.core.utils.IMetaContainer;

/**
 * The basic chunk wrapper to contain the symbolic and subsymbolic portions. It
 * is also the point of access for all event notification.
 * 
 * @author harrison
 * @created December 6, 2002
 */

public interface IChunk extends Comparable<IChunk>, ICommentable,
    IMetaContainer, IAdaptable
{

  /**
   * add a chunk listener that will be notified with this executor
   * @param cl
   * @param executor
   */
  public void addListener(IChunkListener cl, Executor executor);

  /**
   * add a parameter listener that will be notified with this executor
   * @param pl
   * @param executor
   */
  public void addListener(IParameterListener pl, Executor executor);

  /**
   * remove said listener
   * @param cl
   */
  public void removeListener(IChunkListener cl);

  /**
   * remove said listener
   * @param pl
   */
  public void removeListener(IParameterListener pl);

  /**
   * are there listeners attached?
   * @return
   */
  public boolean hasListeners();

  /**
   * are there parameter listeners?
   * @return
   */
  public boolean hasParameterListeners();

  /**
   * dispatch an event. this is public so that ISymbolicChunk and
   * ISubsymbolicChunk can access it and is not intended to be
   * called by those outside the chunk domain
   * @param chunkEvent
   */
  public void dispatch(ChunkEvent chunkEvent);

  /**
   * dispatch parameter event
   * @param parameterEvent
   */
  public void dispatch(ParameterEvent parameterEvent);

  /**
   * return the subsymbolic portion of the chunk
   * @return
   */
  public ISubsymbolicChunk getSubsymbolicChunk();

  /**
   * return the symbolic component of the chunk
   * 
   * @return The ISymbolicChunk value
   * @since
   */
  public ISymbolicChunk getSymbolicChunk();

  /**
   * flag that this chunk has been encoded
   * @param when TODO
   */
  public void encode(double when);

  /**
   * has this chunk been encoded?
   * 
   * @return
   */
  public boolean isEncoded();

  /**
   * called when one is sure that this chunk will NEVER be used. This
   * should only be called by the {@link IDeclarativeModule}. To dispose
   * of a chunk, use {@link IDeclarativeModule#dispose(IChunk)} instead
   * 
   * @since
   */
  public void dispose();

  /**
   * @return true iff the chunk has been disposed
   */
  public boolean hasBeenDisposed();

  /**
   * is this chunk this type (checks all ancestors)
   * @param ct
   * @return
   */
  public boolean isA(IChunkType ct);

  /**
   * is this chunk's immediate type this
   */
  public boolean isAStrict(IChunkType ct);

  /**
   * will return true if this chunks slots can be changed after encoding (like
   * visual-location chunks). These chunks are not searchable by the declarative
   * memory system
   * 
   * @return
   */
  public boolean isMutable();

  /**
   * set that this chunk should be considered mutable
   * @param isMutable
   */
  public void setMutable(boolean isMutable);

  /**
   * get the model that is responsible for this chunk, note: the chunk may not
   * have been encoded yet
   * 
   * @return
   */
  public IModel getModel();

  /**
   * returns true if the symbolic contents of the two chunks are the same, i.e.
   * same chunktype and slot values
   * 
   * @param chunk
   *            cannot be null
   * @return true if they are the same symbolically
   */
  public boolean equalsSymbolic(IChunk chunk);
  
  /**
   * return the read lock for this chunk. this is an internal method made
   * public so that all sym/subsym chunks can access it. <br>
   * These locks are provided for fine-grained locking. Course use (spanning
   * complex methods) makes deadlock a possibility.
   * <br>
   * when using the read or write locks it is important to make sure that
   * {@link Lock#unlock()} is wrapped within the finally clause.<br>
   * <br>
   * Additionally, to prevent deadlock, only one chunk at a time should
   * be locked and the locks must be released before event notification.
   * <br>
   * You should also <b>not</b> save references to the lock, as merging
   * may change the lock, rendering saved references invalid.
   * 
   * @return
   */
  public Lock getReadLock();
  
  /**
   * return the read lock for this chunk. this is an internal method made
   * public so that all sym/subsym chunks can access it<br>
   * These locks are provided for fine-grained locking. Course use (spanning
   * complex methods) makes deadlock a possibility.
   * <br>
   * when using the read or write locks it is important to make sure that
   * {@link Lock#unlock()} is wrapped within the finally clause.<br>
   * <br>
   * Additionally, to prevent deadlock, only one chunk at a time should
   * be locked and the locks must be released before event notification.
   * <br>
   * You should also <b>not</b> save references to the lock, as merging
   * may change the lock, rendering saved references invalid.
   * @return
   */
  public Lock getWriteLock();
}
