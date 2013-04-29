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
package org.jactr.core.chunktype.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ISlot;

public class ChunkTypeEvent extends
    AbstractACTREvent<IChunkType, IChunkTypeListener> implements
    IParameterEvent<IChunkType, IChunkTypeListener>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ChunkTypeEvent.class);

  static public enum Type {
    ENCODED, SLOT_ADDED, SLOT_REMOVED, SLOT_CHANGED, PARAMETER_CHANGED, CHUNK_ADDED, CHILD_ADDED
  };

  /*
   * for both slots and parameters
   */
  private String     _itemName;

  private Object     _oldValue;

  private Object     _newValue;

  private IChunk     _addedChunk;

  private IChunkType _addedChild;

  final private Type       _type;

  public ChunkTypeEvent(IChunkType source, Type type)
  {
    super(source, ACTRRuntime.getRuntime().getClock(source.getModel())
        .getTime());
    _type = type;
  }

  public ChunkTypeEvent(IChunkType source, IChunkType child)
  {
    this(source, Type.CHILD_ADDED);
    _addedChild = child;
  }

  public ChunkTypeEvent(IChunkType source, IChunk chunk)
  {
    this(source, Type.CHUNK_ADDED);
    _addedChunk = chunk;
  }

  public ChunkTypeEvent(IChunkType source, ISlot slot, Object oldValue)
  {
    this(source, Type.SLOT_CHANGED);
    _itemName = slot.getName();
    _oldValue = oldValue;
    _newValue = slot.getValue();
  }

  public ChunkTypeEvent(IChunkType source, Type type, ISlot slot)
  {
    this(source, type);
    _itemName = slot.getName();
    _oldValue = slot.getValue();
    _newValue = slot.getValue();
  }

  public ChunkTypeEvent(IChunkType source, String parameterName,
      Object newValue, Object oldValue)
  {
    this(source, Type.PARAMETER_CHANGED);
    _itemName = parameterName;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  public IChunkType getChild()
  {
    return _addedChild;
  }

  public IChunk getChunk()
  {
    return _addedChunk;
  }

  public Type getType()
  {
    return _type;
  }

  @Override
  public void fire(final IChunkTypeListener listener)
  {
    switch (getType())
    {
      case CHILD_ADDED:
        listener.childAdded(this);
        break;
      case CHUNK_ADDED:
        listener.chunkAdded(this);
        break;
      case SLOT_ADDED:
        listener.slotAdded(this);
        break;
      case SLOT_REMOVED:
        listener.slotRemoved(this);
        break;
      case SLOT_CHANGED:
        listener.slotChanged(this);
        break;
      case ENCODED:
        listener.chunkTypeEncoded(this);
        break;
      default:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("No clue what to do with " + this.getType());
    }
  }

  public String getSlotName()
  {
    return _itemName;
  }

  public Object getOldValue()
  {
    return _oldValue;
  }

  public Object getNewValue()
  {
    return _newValue;
  }

  public Object getNewParameterValue()
  {
    return _newValue;
  }

  public Object getOldParameterValue()
  {
    return _oldValue;
  }

  public String getParameterName()
  {
    return _itemName;
  }

}
