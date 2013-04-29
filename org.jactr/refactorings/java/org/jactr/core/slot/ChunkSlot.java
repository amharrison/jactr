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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.event.ChunkEvent;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ChunkSlot extends DefaultMutableSlot implements ISlotOwner
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkSlot.class);

  protected IChunk                   _owner;

  public ChunkSlot(String name, Object value)
  {
    super(name, value);
    setOwner(this);
  }

  /**
   * @param slot
   */
  public ChunkSlot(ISlot slot)
  {
    super(slot);
    setOwner(this);
  }

  private ChunkSlot(ChunkSlot slot)
  {
    super(slot);
    setChunkOwner(slot.getOwner());
    setOwner(this);
  }

  public ChunkSlot(ISlot slot, IChunk c)
  {
    super(slot);
    setChunkOwner(c);
    setOwner(this);
  }

  public IChunk getOwner()
  {
    return _owner;
  }

  private void setChunkOwner(IChunk chunk)
  {
    _owner = chunk;
    setValue(getValue());
  }

  @Override
  public void setValue(Object value)
  {
    if (_owner != null && !_owner.isMutable() && _owner.isEncoded())
      throw new IllegalChunkStateException(
          "Cannot change the slot value of the immutable and encoded chunk "
              + _owner);
    super.setValue(value);
  }

  @Override
  public ChunkSlot clone()
  {
    return new ChunkSlot(this);
  }

  public void valueChanged(ISlot slot, Object oldValue, Object newValue)
  {
    if (_owner.hasListeners())
      _owner.dispatch(new ChunkEvent(_owner, this, oldValue));
  }
}
