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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.slot.ChunkSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

/**
 * default symbolic chunk implementation
 * 
 * @author harrison
 * @created February 5, 2003
 */
public class BasicSymbolicChunk implements ISymbolicChunk
{

  private static transient Log     LOGGER      = LogFactory
                                                   .getLog(BasicSymbolicChunk.class
                                                       .getName());

  protected IChunkType             _chunkType;

  protected String                 _chunkName;

  final protected IChunk           _parentChunk;

  /**
   * slots are often accessed both individually and as a collection so this
   * cachedMap makes sense
   */
  protected Map<String, ChunkSlot> _slotMap;

  private static int               TOTAL_COUNT = 0;

  public BasicSymbolicChunk(IChunk parentChunk, IChunkType ct)
  {
    TOTAL_COUNT++;
    _slotMap = new TreeMap<String, ChunkSlot>();
    _parentChunk = parentChunk;
    setChunkType(ct);
  }

  /**
   * create a symbolicchunk that has the same values as reference this is used
   * when making copies of the chunk
   */
  protected BasicSymbolicChunk(IChunk realParent, ISymbolicChunk reference)
  {
    this(realParent, reference.getChunkType());

    /*
     * copy the slot values
     */
    for (IMutableSlot slot : _slotMap.values())
      slot.setValue(reference.getSlot(slot.getName()).getValue());

    setName(reference.getName() + "-" + TOTAL_COUNT);
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
   * clear resources
   */
  public void dispose()
  {
    try
    {
      writeLock().lock();
      _slotMap.clear();
    }
    finally
    {
      writeLock().unlock();
    }
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
    try
    {
      readLock().lock();
      return _chunkName;
    }
    finally
    {
      readLock().unlock();
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

    try
    {
      writeLock().lock();
      if (_chunkName != null && _chunkName.equals(name)) return;
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Setting chunk name " + name);
      _chunkName = name;
    }
    finally
    {
      writeLock().unlock();
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

    try
    {
      writeLock().lock();
      _chunkType = ct;

      for (ISlot slot : _chunkType.getSymbolicChunkType().getSlots())
        addSlotInternal(slot);
    }
    finally
    {
      writeLock().unlock();
    }
  }

  /**
   * return a collection of the ACTUAL slots
   * 
   * @see org.jactr.core.slot.ISlotContainer#getSlots()
   */
  public Collection<? extends ISlot> getSlots()
  {
    /*
     * no need to lock since the slots never add or remove
     */
    return Collections.unmodifiableCollection(_slotMap.values());
  }

  /**
   * non-locking since the number of slots never changes
   * 
   * @param slots
   * @return
   * @see org.jactr.core.slot.ISlotContainer#getSlots(java.util.Collection)
   */
  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if (slots == null) slots = new ArrayList<ISlot>(_slotMap.size());
    slots.addAll(_slotMap.values());
    return slots;
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

    ChunkSlot cs = new ChunkSlot(s, _parentChunk);
    _slotMap.put(cs.getName().toLowerCase(), cs);
  }

  /**
   * will set the value of a slot You cannot add slots to a chunk, only the
   * chunk-type - but this will set the value, assuming the slot exists
   */
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
   * return the actual chunk slot backing the map. Not locked since the contents
   * of the slot map never change
   * 
   * @param slotName
   * @return
   */
  private ChunkSlot getSlotInternal(String slotName)
  {
    return _slotMap.get(slotName.toLowerCase());
  }

  /**
   * return the actual slot
   */
  public ISlot getSlot(String slotName)
  {
    ChunkSlot s = getSlotInternal(slotName);
    if (s == null)
      throw new IllegalChunkStateException(getName() + " of type "
          + getChunkType() + " does not contain a slot named " + slotName
          + " possible " + _slotMap.keySet());
    return s;
  }

  @Override
  public String toString()
  {
    return getName();
  }
}