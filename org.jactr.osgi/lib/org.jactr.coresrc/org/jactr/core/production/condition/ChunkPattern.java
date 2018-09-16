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
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;

/**
 * ChunkPatterns are a type of condition used only for searching within a model
 * they are not to be used as conditions in a production..
 */
public class ChunkPattern extends AbstractSlotCondition
{

  /**
   * Logger definition
   */
  static private transient Log LOGGER = LogFactory.getLog(ChunkPattern.class);

  private IChunkType           _chunkType;

  public ChunkPattern(IChunkType chunkType, Collection<? extends ISlot> slots)
  {
    setRequest(new ChunkTypeRequest(chunkType, slots));
  }

  public ChunkPattern(IChunkType chunkType)
  {
    this(chunkType, Collections.EMPTY_LIST);
  }

  public ChunkPattern()
  {
    super();
  }

  public ChunkPattern clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    return new ChunkPattern(_chunkType, getSlots());
  }

  public IChunkType getChunkType()
  {
    return _chunkType;
  }

  @Override
  protected String createToString()
  {
    return String.format("Pattern(%s,%s", getChunkType(), getSlots());
  }

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    try
    {
      return getRequest().bind(model, variableBindings, isIterative);
    }
    catch (CannotMatchException cme)
    {
      cme.getMismatch().setCondition(this);
      throw cme;
    }
  }

}
