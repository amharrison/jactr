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
import java.util.concurrent.ConcurrentSkipListSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.IRemovableSymbolicChunkType;
import org.jactr.core.chunktype.IllegalChunkTypeStateException;
import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.NotifyingSlotContainer;
import org.jactr.core.utils.collections.ChunkNameComparator;

/**
 * default impl. All slots and parenting must be resolved before the given
 * chunktype is encoded. parents must be encoded as well. chunks, however, can
 * be added after encoding
 * 
 * @author harrison
 * @created January 22, 2003
 */
public class BasicSymbolicChunkType extends NotifyingSlotContainer implements
    IRemovableSymbolicChunkType
{

  final private static transient Log LOGGER      = LogFactory
                                                     .getLog(BasicSymbolicChunkType.class
                                                         .getName());

  private static int                 TOTAL_COUNT = 0;

  protected Collection<IChunkType>   _children;

  protected Collection<IChunk>       _chunks;

  protected String                   _name;

  /**
   * This refers to the IChunkType object that this is the symbolic portion of.
   * This is not to be confused with the supertype chunktype which is the
   * IChunkType that this _parentChunkType was derived from..
   * 
   * @since
   */
  protected IChunkType               _parentChunkType;

  protected Collection<IChunkType>   _supertypeParents;

  /**
   * Constructor for the DefaultSymbolicChunkType5 object
   */
  public BasicSymbolicChunkType()
  {
    _children = new TreeSet<IChunkType>();
    // _chunks = new FastSet<IChunk>(); // was array list, behold perf
    // improvments
    _chunks = new ConcurrentSkipListSet<IChunk>(new ChunkNameComparator());
    _supertypeParents = new ArrayList<IChunkType>(2);
    ++TOTAL_COUNT;
  }

  // public BasicSymbolicChunkType(AbstractChunkType parentChunkType,
  // Collection<IChunkType> superParentTypes)
  // {
  // this(parentChunkType);
  // for (IChunkType parent : superParentTypes)
  // addParent(parent);
  // }

  public void bind(IChunkType wrapper, Collection<IChunkType> parents)
  {
    _parentChunkType = wrapper;
    _supertypeParents.clear();
    _chunks.clear();
    _children.clear();
    clear();

    for (IChunkType parent : parents)
      addParent(parent);
  }

  /**
   * 
   */
  @Override
  public void dispose()
  {
    IDeclarativeModule decM = _parentChunkType.getModel()
        .getDeclarativeModule();
    for (IChunk chunk : _chunks)
      if (!chunk.hasBeenDisposed()) decM.dispose(chunk);
    _chunks.clear();
    _chunks = null;

    for (IChunkType child : _children)
      child.dispose();

    _children.clear();
    _children = null;

    super.dispose();

    _supertypeParents.clear();

    _supertypeParents = null;
    _parentChunkType = null;
  }

  /**
   * @see org.jactr.core.chunktype.ISymbolicChunkType#getParent()
   */
  public Collection<IChunkType> getParents()
  {
    return Collections.unmodifiableCollection(_supertypeParents);
  }

  public IChunkType getParent()
  {
    int size = _supertypeParents.size();

    if (size > 1)
      throw new IllegalStateException(String.format(
          "%s has multiple parents, don't assume single parentage", getName()));

    if (size == 0) return null;

    return _supertypeParents.iterator().next();
  }

  public int getNumberOfParents()
  {
    return _supertypeParents.size();
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
  public IChunkType addParent(IChunkType ct)
  {
    if (_supertypeParents.contains(ct)) return _parentChunkType;

    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "cannot add parent to encoded chunktype");

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding parent chunk type = " + ct + " to type " + _name);

    _supertypeParents.add(ct);

    /*
     * copy all his slots
     */
    for (ISlot slot : ct.getSymbolicChunkType().getSlots())
      addSlot(slot);

    return _parentChunkType;
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
          + ct.getSymbolicChunkType().getName() + " to " + _name);

    /*
     * we don't notify the parent since children is only concerned with the
     * immediate children we also need to check to make sure no duplicates, b/c
     * of multiple inheritance this may get called more than once for each chunk
     * type.
     */
    if (!_children.contains(ct)) _children.add(ct);

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
        LOGGER.debug(this + ": Adding chunk = " + c + " to " + _name);
    _chunks.add(c);

    if (_supertypeParents.size() > 0)
      for (IChunkType parent : _supertypeParents)
        parent.getSymbolicChunkType().addChunk(c);

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
   */
  public boolean hasSlot(ISlot s)
  {
    return getSlot(s.getName()) != null;
  }

  /**
   * Yes, you can modify the slots (add/remove) but off if not encoded
   */
  public boolean canModify()
  {
    return !_parentChunkType.isEncoded();
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
    if (_supertypeParents.size() == 0) return true;
    if (hasSlot(slot))
    {
      for (IChunkType parent : _supertypeParents)
        if (parent.getSymbolicChunkType().getSlot(slot.getName()) != null)
          return false;
    }
    else
      return false;

    return true;
  }

  /**
   * Adds a feature to the ISlot attribute of the DefaultSymbolicChunkType5
   * object
   * 
   * @param s
   *          The feature to be added to the ISlot attribute
   * @since
   */
  @Override
  public void addSlot(ISlot s)
  {
    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "cannot add slots after encoding");

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + ":Adding slot " + s + " to " + _name);

    IMutableSlot oldSlot = (IMutableSlot) getSlot(s.getName());
    if (oldSlot != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Overriding existing slot " + oldSlot + " with new value "
            + s.getValue());
      oldSlot.setValue(s.getValue());
    }
    else
      super.addSlot(s);

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
  @Override
  public void removeSlot(ISlot s)
  {
    if (_parentChunkType.isEncoded())
      throw new IllegalChunkTypeStateException(
          "cannot remove slots after encoding");

    if (LOGGER.isDebugEnabled()) LOGGER.debug(this + ":Removing slot " + s);

    super.removeSlot(s);

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
    else
      for (IChunkType parent : _supertypeParents)
        if (parent.getSymbolicChunkType().isA(ct)) return true;
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

  public Object getAdapter(Class adapterClass)
  {
    if (adapterClass.isAssignableFrom(getClass())) return this;

    return null;
  }

  /**
   * remove this chunk from the chunktype's list of encoded chunks. No event is
   * fired.
   */
  public void removeChunk(IChunk chunk)
  {
    if (LOGGER.isDebugEnabled())
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(this + ": Removing chunk = " + chunk + " to " + _name);

    _chunks.remove(chunk);

    if (_supertypeParents.size() > 0)
      for (IChunkType parent : _supertypeParents)
        if (parent.getSymbolicChunkType() instanceof IRemovableSymbolicChunkType)
          ((IRemovableSymbolicChunkType) parent.getSymbolicChunkType())
              .removeChunk(chunk);
  }

  /**
   * noop for now
   */
  public void removeChild(IChunkType chunkType)
  {
    if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format("removeChild is a noop at this time"));

  }

  /**
   * Noop for now.
   */
  public void removeParent(IChunkType chunkType)
  {
    if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format("removeParent is a noop at this time"));

  }
}