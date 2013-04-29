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
package org.jactr.core.chunktype.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.IllegalChunkTypeStateException;
import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.ChunkTypeSlot;
import org.jactr.core.slot.ISlot;

/**
 * default impl. All slots and parenting must be resolved before the given
 * chunktype is encoded. parents must be encoded as well. chunks, however, can
 * be added after encoding
 * 
 * @author harrison
 * @created January 22, 2003
 */
public class BasicSymbolicChunkType implements ISymbolicChunkType
{

  final private static transient Log  LOGGER      = LogFactory
                                                      .getLog(BasicSymbolicChunkType.class
                                                          .getName());

  private static int                  TOTAL_COUNT = 0;

  protected Collection<IChunkType>    _children;

  protected Collection<IChunk>        _chunks;

  protected Collection<ChunkTypeSlot> _slots;

  protected String                    _name;

  /**
   * This refers to the IChunkType object that this is the symbolic portion of.
   * This is not to be confused with the supertype chunktype which is the
   * IChunkType that this _parentChunkType was derived from..
   * 
   * @since
   */
  protected AbstractChunkType         _parentChunkType;

  protected IChunkType                _supertypeParent;

  /**
   * Constructor for the DefaultSymbolicChunkType5 object
   */
  protected BasicSymbolicChunkType(AbstractChunkType parentChunkType)
  {
    _parentChunkType = parentChunkType;
    _children = new TreeSet<IChunkType>();
    _slots = new TreeSet<ChunkTypeSlot>();
    _chunks = new ArrayList<IChunk>();
    ++TOTAL_COUNT;
  }

  public BasicSymbolicChunkType(AbstractChunkType parentChunkType,
      IChunkType superParentType)
  {
    this(parentChunkType);
    setParent(superParentType);
  }

  /**
   * 
   */
  public void dispose()
  {
    IDeclarativeModule decM = _parentChunkType.getModel().getDeclarativeModule();
    for (IChunk chunk : _chunks)
      if (!chunk.hasBeenDisposed())
        decM.dispose(chunk);
    _chunks.clear();
    _chunks = null;
    

    for (IChunkType child : _children)
      child.dispose();

    _children.clear();
    _children = null;

    _slots.clear();
    _slots = null;


    _supertypeParent = null;
    _parentChunkType = null;
  }

  /**
   * @see org.jactr.core.chunktype.ISymbolicChunkType#getParent()
   */
  public IChunkType getParent()
  {
    return _supertypeParent;
  }

  /**
   * @see org.jactr.core.chunktype.ISymbolicChunkType#getName()
   */
  public String getName()
  {
    if (_name == null) _name = String.format("ChunkType-%d", TOTAL_COUNT);
    return _name;
  }

  /**
   * 
   */
  public void setName(String name)
  {
    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "cannot change the name of encoded chunktypes");

    _name = name;
  }

  /**
   * grab the slots of the parent and the children
   */
  protected void setParent(IChunkType ct)
  {
    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "parent cannot be set after encoding");

    if (ct == null) return;

    if (!ct.isEncoded())
      throw new IllegalChunkTypeStateException(
          "parent must be encoded before using it as a parent");

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding parent chunk type = " + ct);

    _supertypeParent = ct;

    /*
     * copy all his slots
     */
    for (ISlot slot : ct.getSymbolicChunkType().getSlots())
      addSlot(slot);
  }

  /**
   * return all the chunktypes that are directly decended from this one
   */
  public Collection<IChunkType> getChildren()
  {
    return Collections.unmodifiableCollection(_children);
  }

  public int getNumberOfChildren()
  {
    return _children.size();
  }

  /**
   * @see org.jactr.core.chunktype.ISymbolicChunkType#addChild(org.jactr.core.chunktype.IChunkType)
   */
  public void addChild(IChunkType ct)
  {
    /*
     * ct had better not have any chunks
     */
    if (ct.getSymbolicChunkType().getNumberOfChunks() != 0)
      throw new IllegalChunkTypeStateException(
          "Cannot add a child that already has chunks");

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding child chunktype = "
          + ct.getSymbolicChunkType().getName());

    /*
     * we don't notify the parent since children is only concerned with the
     * immediate children
     */
    _children.add(ct);

    if (_parentChunkType.hasListeners())
      _parentChunkType.dispatch(new ChunkTypeEvent(_parentChunkType, ct));
  }

  /**
   * return all chunks of this type
   */
  public Collection<IChunk> getChunks()
  {
    return Collections.unmodifiableCollection(_chunks);
  }

  /**
   * add this chunk to this chunktype and then up the parental hierachy
   * 
   * @see org.jactr.core.chunktype.ISymbolicChunkType#addChunk(org.jactr.core.chunk.IChunk)
   */
  public void addChunk(IChunk c)
  {
    if (LOGGER.isDebugEnabled())
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(this + ": Adding chunk = " + c);
    _chunks.add(c);

    if (_supertypeParent != null)
      _supertypeParent.getSymbolicChunkType().addChunk(c);

    if (_parentChunkType.hasListeners())
      _parentChunkType.dispatch(new ChunkTypeEvent(_parentChunkType, c));
  }

  /**
   */
  public int getNumberOfChunks()
  {
    return _chunks.size();
  }

  /**
   * return all slots
   */
  public Collection<? extends ISlot> getSlots()
  {
    return getSlots(null);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if (slots == null) slots = new ArrayList<ISlot>(_slots.size());
    slots.addAll(_slots);
    return slots;
  }

  /**
   */
  public boolean hasSlot(ISlot s)
  {
    return _slots.contains(s);
  }

  /**
   * Yes, you can modify the slots (add/remove) but off if not encoded
   */
  public boolean canModify()
  {
    return !_parentChunkType.isEncoded();
  }

  /**
   */
  public ISlot getSlot(String name)
  {
    for (ISlot slot : _slots)
      if (slot.getName().equals(name)) return slot;
    return null;
  }

  /**
   * Gets the authoritative attribute of the DefaultSymbolicChunkType5 object
   * 
   * @param slot
   *          Description of the Parameter
   * @return The authoritative value
   */
  public boolean isAuthoritative(ISlot slot)
  {
    return (_supertypeParent == null || _supertypeParent
        .getSymbolicChunkType().getSlot(slot.getName()) == null) && _slots
        .contains(slot);
  }

  /**
   * Adds a feature to the ISlot attribute of the DefaultSymbolicChunkType5
   * object
   * 
   * @param s
   *          The feature to be added to the ISlot attribute
   * @since
   */
  public void addSlot(ISlot s)
  {
    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "cannot add slots after encoding");

    if (LOGGER.isDebugEnabled()) LOGGER.debug(this + ":Adding slot " + s);
    ChunkTypeSlot cts = new ChunkTypeSlot(s, _parentChunkType);

    ChunkTypeSlot oldSlot = (ChunkTypeSlot) getSlot(s.getName());
    if (oldSlot != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Overriding existing slot " + oldSlot + " with new value "
            + s.getValue());
      oldSlot.setValue(s.getValue());
    }
    else
      _slots.add(cts);

    if (_parentChunkType.hasListeners())
      _parentChunkType.dispatch(new ChunkTypeEvent(_parentChunkType,
          ChunkTypeEvent.Type.SLOT_ADDED, s));
  }

  /**
   * Description of the Method
   * 
   * @param s
   *          Description of Parameter
   * @since
   */
  public void removeSlot(ISlot s)
  {
    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "cannot remove slots after encoding");

    if (LOGGER.isDebugEnabled()) LOGGER.debug(this + ":Removing slot " + s);

    _slots.remove(s);

    if (_parentChunkType.hasListeners())
      _parentChunkType.dispatch(new ChunkTypeEvent(_parentChunkType,
          ChunkTypeEvent.Type.SLOT_REMOVED, s));
  }

  /**
   * @return true if this chunktype is ct or derived from ct
   */
  public boolean isA(IChunkType ct)
  {
    if (ct == null) return false; // AMH 7/19/06 was true
    if (ct == _parentChunkType)
      return true;
    else if (_supertypeParent != null)
      return _supertypeParent.getSymbolicChunkType().isA(ct);
    return false;
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  @Override
  public String toString()
  {
    return "Sym:" + getName();
  }

  public void encode()
  {
    // noop
  }
}