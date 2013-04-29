/*
 * Created on Jul 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.queue.timedevents;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IRequestableBuffer;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.condition.ChunkPattern;

/**
 * insert a chunk into a specific buffer after a certain time has elapsed
 * 
 * @author developer
 */
public class DelayedBufferInsertionTimedEvent extends AbstractTimedEvent
    implements IBufferBasedTimedEvent
{
  /**
   * logger definition
   */
  static public final Log     LOGGER = LogFactory
                                         .getLog(DelayedBufferInsertionTimedEvent.class);

  protected IChunk            _chunkToInsert;

  protected IChunk            _insertedChunk;

  protected IActivationBuffer _buffer;

  public DelayedBufferInsertionTimedEvent(IActivationBuffer buffer,
      IChunk chunkToInsert, double startTime, double endTime)
  {
    super(startTime, endTime);
    _buffer = buffer;
    _chunkToInsert = chunkToInsert;
  }

  @Override
  public void fire(double time)
  {
    super.fire(time);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Inserting " + _chunkToInsert + " into buffer "
          + _buffer.getName());
    _insertedChunk = _buffer.addSourceChunk(_chunkToInsert);

  }


  /**
   * the chunk that is to be inserted into the buffer
   * 
   * @return
   */
  public IChunk getChunkToInsert()
  {
    return _chunkToInsert;
  }

  /**
   * return the chunk that is actually in the buffer after firing, often this is
   * a copy of chunktoinsert
   * 
   * @return
   */
  public IChunk getInsertedChunk()
  {
    return _insertedChunk;
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

  public IChunk getBoundChunk()
  {
    if (_insertedChunk != null) return _insertedChunk;

    if (_chunkToInsert != null) return _chunkToInsert;
    return null;
  }
}
