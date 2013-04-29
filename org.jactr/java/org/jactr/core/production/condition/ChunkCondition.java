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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.match.EmptyBufferMatchFailure;
import org.jactr.core.slot.ISlot;

/**
 * match the specifics of a chunk in a named buffer
 * 
 * @author harrison
 */
public class ChunkCondition extends ChunkTypeCondition
{


  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkCondition.class);

  private IChunk                     _chunk;

  public ChunkCondition(String bufferName, IChunk chunk)
  {
    this(bufferName, chunk, chunk.getSymbolicChunk().getSlots());
  }

  /**
   * @param bufferName
   */
  public ChunkCondition(String bufferName, IChunk chunk,
      Collection<? extends ISlot> slots)
  {
    super(bufferName, chunk.getSymbolicChunk().getChunkType(), slots);
    _chunk = chunk;
  }

  /**
   * @see org.jactr.core.utils.Duplicateable#duplicate()
   */
  public IChunk getChunk()
  {
    return _chunk;
  }

  @Override
  public ChunkCondition clone(IModel model, VariableBindings bindings)
      throws CannotMatchException
  {
    /*
     * check the provisional chunk binding
     */
    if (!bindings.isBound("=" + getBufferName()))
      throw new CannotMatchException(new EmptyBufferMatchFailure(this));

    return new ChunkCondition(getBufferName(), getChunk(), getRequest()
        .getSlots());
  }


  @Override
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
    result = prime * result + (_chunk == null ? 0 : _chunk.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    ChunkCondition other = (ChunkCondition) obj;
    if (_chunk == null)
    {
      if (other._chunk != null) return false;
    }
    else if (!_chunk.equals(other._chunk)) return false;
    return true;
  }

}
