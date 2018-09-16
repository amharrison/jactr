/*
 * Created on Oct 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.IRequestableBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.condition.ChunkPattern;

/**
 * will insert a chunk into the buffer after a specified amount of time has
 * elapsed. This uses the Future interface so that asynchronous operations can
 * be active up until the time that the chunk is actually required
 * 
 * @author developer
 */
@Deprecated
public class FutureChunkPatternInsertionTimedEvent extends AbstractTimedEvent
    implements IBufferBasedTimedEvent
{
  /**
   * logger definition
   */
  static public final Log        LOGGER = LogFactory
                                            .getLog(FutureChunkPatternInsertionTimedEvent.class);

  protected Future<ChunkPattern> _patternToInsert;

  protected IRequestableBuffer _buffer;

  public FutureChunkPatternInsertionTimedEvent(
      Future<ChunkPattern> patternToInsert, IRequestableBuffer buffer,
      double startTime, double endTime)
  {
    super(startTime, endTime);
    _patternToInsert = patternToInsert;
    _buffer = buffer;
  }

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

  public Future<ChunkPattern> getFutureChunkPattern()
  {
    return _patternToInsert;
  }

  @Override
  public void fire(double currentTime)
  {
    super.fire(currentTime);
    // try
    // {
    // ChunkPattern pattern = _patternToInsert.get();
//      _buffer.request(pattern);
    // }
    // catch (Exception e)
    // {
    // LOGGER.error("Could not get future chunk for insertion", e);
    // throw new RuntimeException("Could not get future chunk for insertion",
    // e);
    // }
  }

  public IChunk getBoundChunk()
  {
    return null;
  }
}
