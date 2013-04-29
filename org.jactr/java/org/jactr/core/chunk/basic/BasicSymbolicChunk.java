/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.chunk.basic;


import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.NotifyingSlotContainer;
import org.jactr.core.utils.IAdaptable;

/**
 * default symbolic chunk implementation
 * 
 * @author harrison
 * @created February 5, 2003
 */
public class BasicSymbolicChunk extends NotifyingSlotContainer implements
    ISymbolicChunk, IAdaptable
{

  private static transient Log     LOGGER      = LogFactory
                                                   .getLog(BasicSymbolicChunk.class
                                                       .getName());

  protected IChunkType             _chunkType;
  
  protected String                 _chunkName;

  protected IChunk             _parentChunk;

  private ISlot                _chunkTypeSlot;                 // for
                                                                // efficiency

  private static int               TOTAL_COUNT = 0;

  public BasicSymbolicChunk()
  {
    TOTAL_COUNT++;
  }

  public void bind(IChunk wrapper, IChunkType type)
  {
    clear();
    _parentChunk = wrapper;
    setChunkType(type);
  }


  protected Lock readLock()
  {
    return _parentChunk.getReadLock();
  }

  protected Lock writeLock()
  {
    return _parentChunk.getWriteLock();
  }

  /**
   * cannot add/remove slots from a chunk
   */
  public boolean canModify()
  {
    return false;
  }



  /**
   * return the IChunk wrapper
   */
  public IChunk getParentChunk()
  {
    return _parentChunk;
  }

  /**
   * no op
   */
  public void encode(double when)
  {
  }

  /**
   * @see org.jactr.core.chunk.ISymbolicChunk#getName()
   */
  public String getName()
  {
    if (_chunkName == null || _chunkName == "")
      setName(_chunkType.getSymbolicChunkType().getName() + "-" + TOTAL_COUNT);
    Lock l = readLock();
    try
    {
      l.lock();
      return _chunkName;
    }
    finally
    {
      l.unlock();
    }
  }

  /**
   * set the chunk name, once encoded this will fail
   */
  public void setName(String name)
  {
    if (_parentChunk.isEncoded())
      throw new IllegalChunkStateException(
          "Cannot change chunks name once encoded");

    Lock l = writeLock();
    try
    {
      l.lock();
      if (_chunkName != null && _chunkName.equals(name)) return;
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Setting chunk name " + name);
      _chunkName = name;
    }
    finally
    {
      l.unlock();
    }
  }

  /**
   * @see org.jactr.core.chunk.ISymbolicChunk#getChunkType()
   */
  public IChunkType getChunkType()
  {
    return _chunkType;
  }

  /**
   * @param ct
   */
  private void setChunkType(IChunkType ct)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Setting chunk type " + ct.getSymbolicChunkType().getName());

    if (_chunkType != null)
      throw new IllegalChunkStateException(
          "Cannot overwrite parent IChunkType in chunk " + getName());

    Lock l = writeLock();
    try
    {
      l.lock();
      _chunkType = ct;

      for (ISlot slot : _chunkType.getSymbolicChunkType().getSlots())
        addSlotInternal(slot);
    }
    finally
    {
      l.unlock();
    }
  }



  /**
   * add a slot by copying it first. Not locked since this is only called from
   * the constructor
   * 
   * @param s
   */
  private void addSlotInternal(ISlot s)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("adding slot " + s);

    super.addSlot(s);
  }

  /**
   * will set the value of a slot You cannot add slots to a chunk, only the
   * chunk-type - but this will set the value, assuming the slot exists
   */
  @Override
  public void addSlot(ISlot s)
  {
    /**
     * not locked, ChunkSlot will do this for us.
     */
    ((IMutableSlot) getSlot(s.getName())).setValue(s.getValue());
  }

  /**
   * noop
   * 
   * @see org.jactr.core.slot.ISlotContainer#removeSlot(org.jactr.core.slot.ISlot)
   */
  @Override
  public void removeSlot(ISlot s)
  {
    // NoOp chunks cant remove slots
  }

  /**
   * @see org.jactr.core.chunk.ISymbolicChunk#isA(org.jactr.core.chunktype.IChunkType)
   */
  public boolean isA(IChunkType ct)
  {
    if (_chunkType != null) return _chunkType.isA(ct);
    return false;
  }

  /**
   * @see org.jactr.core.chunk.ISymbolicChunk#isAStrict(org.jactr.core.chunktype.IChunkType)
   */
  public boolean isAStrict(IChunkType ct)
  {
    return ct == _chunkType;
  }

  /**
   * notify both the slot container listeners and the chunk listeners
   */
  @Override
  public void valueChanged(ISlot slot, Object oldValue, Object newValue)
  {
    super.valueChanged(slot, oldValue, newValue);

    if (_parentChunk.hasListeners())
      _parentChunk.dispatch(new ChunkEvent(_parentChunk, slot, oldValue));
  }

  /**
   * return the actual slot
   */

  @Override
  public ISlot getSlot(String slotName)
  {
	if(slotName.equalsIgnoreCase("isa")) {
		if(_chunkTypeSlot == null)
			_chunkTypeSlot = new BasicSlot("isa",_chunkType);
		return _chunkTypeSlot;
	}
    ISlot s = super.getSlot(slotName);
    if (s == null)
      throw new IllegalChunkStateException(getName() + " of type "
          + getChunkType() + " does not contain a slot named " + slotName
          + " possible " + getSlots());
    return s;
  }

  @Override
  public String toString()
  {
    return getName();
  }

  public Object getAdapter(Class adapterClass)
  {
    if (adapterClass.isAssignableFrom(getClass())) return this;
    
    return null;
  }
}