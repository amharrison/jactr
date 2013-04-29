/*
 * Created on Sep 22, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.production.condition;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.match.EmptyBufferMatchFailure;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;

/**
 * matches the contents of a buffer to a specific chunktype and slot values
 * 
 * @author harrison
 */
public class ChunkTypeCondition extends AbstractBufferCondition
{

  /**
   * Logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ChunkTypeCondition.class);

  private IChunkType       _chunkType;

  public ChunkTypeCondition(String bufferName, IChunkType chunkType)
  {
    this(bufferName, chunkType, Collections.EMPTY_LIST);
  }

  public ChunkTypeCondition(String bufferName, IChunkType chunkType,
      Collection<? extends ISlot> slots)
  {
    super(bufferName);
    _chunkType = chunkType;
    setRequest(new ChunkTypeRequest(chunkType, slots));
  }

  public ChunkTypeCondition clone(IModel model, VariableBindings bindings)
      throws CannotMatchException
  {
    /*
     * check the provisional chunk binding
     */
    if (!bindings.isBound("=" + getBufferName()))
      throw new CannotMatchException(new EmptyBufferMatchFailure(this));

    return new ChunkTypeCondition(getBufferName(), getChunkType(), getRequest()
        .getSlots());
  }

  public IChunkType getChunkType()
  {
    return _chunkType;
  }

  @Override
  public ChunkTypeRequest getRequest()
  {
    return (ChunkTypeRequest) super.getRequest();
  }

  @Override
  protected String createToString()
  {
    return String.format("ChunkTypeCondition(%s, %s, %s)", getBufferName(),
        getChunkType(), getSlots());
  }



  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    /*
     * check the provisional chunk binding
     */
    if (!variableBindings.isBound("=" + getBufferName()))
      throw new CannotMatchException(new EmptyBufferMatchFailure(this));

    IChunk testChunk = (IChunk) variableBindings.get("=" + getBufferName());


    try
    {
      return getRequest().bind(testChunk, model, variableBindings, isIterative);
    }
    catch (CannotMatchException cme)
    {
      cme.getMismatch().setCondition(this);
      throw cme;
    }
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (_chunkType == null ? 0 : _chunkType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ChunkTypeCondition other = (ChunkTypeCondition) obj;
    if (_chunkType == null)
    {
      if (other._chunkType != null) return false;
    }
    else if (!_chunkType.equals(other._chunkType)) return false;
    return true;
  }

}
