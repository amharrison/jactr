/*
 * Created on Oct 13, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.chunk.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IACTREvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ISlot;

public class ChunkEvent extends AbstractACTREvent<IChunk, IChunkListener>
    implements IACTREvent<IChunk, IChunkListener>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ChunkEvent.class);

  static public enum Type {
    ENCODED, ACCESSED, MERGING_WITH, MERGING_INTO, SIMILARITY_CHANGED, SLOT_VALUE_CHANGED
  };

  /*
   * for parameter & slot changes
   */
  private String _itemName;

  private Object _oldValue;

  private Object _newValue;

  /*
   * for similarity & merge
   */
  private IChunk _otherChunk;

  private double _newSimilarity;

  private double _oldSimiliarity;

  private Type   _type;

  public ChunkEvent(IChunk source, Type type)
  {
    super(source, ACTRRuntime.getRuntime().getClock(source.getModel())
        .getTime());
    _type = type;
  }

  public ChunkEvent(IChunk source, ISlot slot, Object oldValue)
  {
    this(source, Type.SLOT_VALUE_CHANGED);
    _itemName = slot.getName();
    _oldValue = oldValue;
    _newValue = slot.getValue();
  }

  public ChunkEvent(IChunk source, IChunk similarChunk, double oldSim,
      double newSim)
  {
    this(source, Type.SIMILARITY_CHANGED);
    _otherChunk = similarChunk;
    _oldSimiliarity = oldSim;
    _newSimilarity = newSim;
  }

  public ChunkEvent(IChunk sourceAndOriginalChunk, Type type, IChunk mergie)
  {
    this(sourceAndOriginalChunk, type);
    _otherChunk = mergie;
  }

  public Type getType()
  {
    return _type;
  }

  @Override
  public void fire(IChunkListener listener)
  {
    switch (getType())
    {
      case ACCESSED:
        listener.chunkAccessed(this);
        break;
      case ENCODED:
        listener.chunkEncoded(this);
        break;
      case MERGING_WITH:
        listener.mergingWith(this);
        break;
      case MERGING_INTO:
        listener.mergingInto(this);
        break;
      case SLOT_VALUE_CHANGED:
        listener.slotChanged(this);
        break;
      case SIMILARITY_CHANGED:
        listener.similarityChanged(this);
        break;
      default:
        LOGGER.warn("No clue what to do with even type " + this.getType());
    }
  }

  public String getSlotName()
  {
    return _itemName;
  }

  public Object getNewSlotValue()
  {
    return _newValue;
  }

  public Object getOldSlotValue()
  {
    return _oldValue;
  }

  public IChunk getSimilarChunk()
  {
    return _otherChunk;
  }

  public double getOldSimilarity()
  {
    return _oldSimiliarity;
  }

  public double getNewSimilarity()
  {
    return _newSimilarity;
  }

  public IChunk getChunk()
  {
    return _otherChunk;
  }

}
