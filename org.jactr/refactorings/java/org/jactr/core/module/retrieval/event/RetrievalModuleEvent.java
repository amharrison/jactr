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
package org.jactr.core.module.retrieval.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;

public class RetrievalModuleEvent extends
    AbstractACTREvent<IRetrievalModule, IRetrievalModuleListener>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(RetrievalModuleEvent.class);

  static public enum Type {
    INITIATED, COMPLETED
  };

  private Type         _type;

  private ChunkTypeRequest _request;

  private IChunk       _chunk;

  public RetrievalModuleEvent(IRetrievalModule source, double simulationTime)
  {
    super(source, simulationTime);
  }

  public RetrievalModuleEvent(IRetrievalModule source, ChunkTypeRequest pattern)
  {
    this(source, ACTRRuntime.getRuntime().getClock(source.getModel()).getTime());
    _type = Type.INITIATED;
    _request = pattern;
  }

  public RetrievalModuleEvent(IRetrievalModule source, ChunkTypeRequest pattern,
      IChunk chunk)
  {
    this(source, ACTRRuntime.getRuntime().getClock(source.getModel()).getTime());
    _type = Type.COMPLETED;
    _request = pattern;
    _chunk = chunk;
  }

  public IChunk getChunk()
  {
    return _chunk;
  }

  public ChunkTypeRequest getChunkTypeRequest()
  {
    return _request;
  }

  public Type getType()
  {
    return _type;
  }

  @Override
  public void fire(IRetrievalModuleListener listener)
  {
    switch (getType())
    {
      case INITIATED:
        listener.retrievalInitiated(this);
        break;
      case COMPLETED:
        listener.retrievalCompleted(this);
        break;
      default:
        LOGGER.warn("no clue what to do with this event " + this);
    }
  }
}
