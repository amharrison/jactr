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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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

  private Type               _type;

  private ChunkTypeRequest   _request;

  private IChunk             _chunk;

  private double             _retrievalTime;

  private Collection<IChunk> _allCandidates;

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

  /**
   * pattern for a retrieval completion when there are no retrieval candidates
   * (i.e., error). If candidates are available, use
   * {@link #RetrievalModuleEvent(IRetrievalModule, ChunkTypeRequest, IChunk, double, Collection)}
   * 
   * @param source
   * @param pattern
   * @param chunk
   * @param retrievalTime
   */
  public RetrievalModuleEvent(IRetrievalModule source,
      ChunkTypeRequest pattern, IChunk chunk, double retrievalTime)
  {
    this(source, pattern, chunk, retrievalTime, null);
  }

  /**
   * @param source
   * @param pattern
   * @param chunk
   * @param retrievalTime
   * @param allCandidates
   *          all the retrieval candidates, including chunk
   */
  public RetrievalModuleEvent(IRetrievalModule source,
      ChunkTypeRequest pattern, IChunk chunk, double retrievalTime,
      Collection<IChunk> allCandidates)
  {
    this(source, ACTRRuntime.getRuntime().getClock(source.getModel()).getTime());
    _type = Type.COMPLETED;
    _request = pattern;
    _chunk = chunk;
    _retrievalTime = retrievalTime;
    if (allCandidates != null)
      _allCandidates = new ArrayList<IChunk>(allCandidates);
    else
      _allCandidates = Collections.EMPTY_LIST;
  }

  /**
   * duration of the retrieval, not the end time.
   * 
   * @return
   */
  public double getRetrievalTime()
  {
    return _retrievalTime;
  }

  public IChunk getChunk()
  {
    return _chunk;
  }

  public Collection<IChunk> getAllCandidates()
  {
    return _allCandidates;
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
