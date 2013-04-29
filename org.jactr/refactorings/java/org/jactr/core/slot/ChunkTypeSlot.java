/*
 * Created on Sep 21, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.slot;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.event.ChunkTypeEvent;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ChunkTypeSlot extends DefaultMutableSlot implements ISlotOwner
{

  protected IChunkType _owner;

  public ChunkTypeSlot(String name, Object value)
  {
    super(name, value);
    setOwner(this);
  }

  /**
   * @param slot
   */
  public ChunkTypeSlot(ISlot slot)
  {
    super(slot);
    setOwner(this);
  }

  private ChunkTypeSlot(ChunkTypeSlot slot)
  {
    super(slot);
    setChunkTypeOwner(slot.getOwner());
    setOwner(this);
  }

  public ChunkTypeSlot(ISlot slot, IChunkType ct)
  {
    super(slot);
    setChunkTypeOwner(ct);
    setOwner(this);
  }

  public IChunkType getOwner()
  {
    return _owner;
  }

  private void setChunkTypeOwner(IChunkType chunkType)
  {
    _owner = chunkType;
    setValue(getValue());
  }

  @Override
  public ChunkTypeSlot clone()
  {
    return new ChunkTypeSlot(this);
  }

  public void valueChanged(ISlot slot, Object oldValue, Object newValue)
  {
    if (_owner.hasListeners())
      _owner.dispatch(new ChunkTypeEvent(_owner, this, oldValue));
  }
}
