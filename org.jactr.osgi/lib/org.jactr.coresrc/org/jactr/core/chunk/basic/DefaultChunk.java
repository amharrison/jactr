/*
 * Created on Aug 3, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.chunk.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.event.ParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.DefaultAdaptable;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * abstract chunk that handles most common logic for the developer.
 * 
 * @author harrison
 */
public class DefaultChunk extends DefaultAdaptable implements IChunk
{

  /**
   * chunk data used to store meta data event dispatchers and the like. this is
   * volatile because it may change after a call to {@link #mergeInto(IChunk)}
   * so we do not want the value to be cached.
   */
  private volatile ChunkData _chunkData;

  protected ISubsymbolicChunk _subsymbolicChunk;

  protected ISymbolicChunk    _symbolicChunk;

  public DefaultChunk(IModel model)
  {
    _chunkData = new ChunkData(model);
  }

  /**
   * @return
   * @see org.jactr.core.chunk.IChunk#getModel()
   */
  public IModel getModel()
  {
    return _chunkData.getModel();
  }

  /**
   * @param chunkEvent
   * @see org.jactr.core.chunk.IChunk#dispatch(org.jactr.core.chunk.event.ChunkEvent)
   */
  public void dispatch(ChunkEvent chunkEvent)
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");

    if (getSymbolicChunk() != null && getSubsymbolicChunk() != null)
      _chunkData.getChunkDispatcher().fire(chunkEvent);
  }

  /**
   * @param pEvent
   * @see org.jactr.core.chunk.IChunk#dispatch(org.jactr.core.event.ParameterEvent)
   */
  public void dispatch(ParameterEvent pEvent)
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");

    if (getSymbolicChunk() != null && getSubsymbolicChunk() != null)
    _chunkData.getParameterDispatcher().fire(pEvent);
  }

  /**
   * note : non-locking
   * 
   * @return
   * @see org.jactr.core.chunk.IChunk#hasParameterListeners()
   */
  public boolean hasParameterListeners()
  {
    return _chunkData.getParameterDispatcher().hasListeners();
  }

  /**
   * note : non-locking
   * 
   * @return
   * @see org.jactr.core.chunk.IChunk#hasListeners()
   */
  public boolean hasListeners()
  {
    return _chunkData.getChunkDispatcher().hasListeners();
  }

  /**
   * note : non-locking
   * 
   * @param cl
   * @param executor
   * @see org.jactr.core.chunk.IChunk#addListener(org.jactr.core.chunk.event.IChunkListener,
   *      java.util.concurrent.Executor)
   */
  public void addListener(IChunkListener cl, Executor executor)
  {
    _chunkData.getChunkDispatcher().addListener(cl, executor);
  }

  /**
   * note : non-locking
   * 
   * @param pl
   * @param executor
   * @see org.jactr.core.chunk.IChunk#addListener(org.jactr.core.event.IParameterListener,
   *      java.util.concurrent.Executor)
   */
  public void addListener(IParameterListener pl, Executor executor)
  {
    _chunkData.getParameterDispatcher().addListener(pl, executor);
  }

  /**
   * note : non-locking
   * 
   * @param cl
   * @see org.jactr.core.chunk.IChunk#removeListener(org.jactr.core.chunk.event.IChunkListener)
   */
  public void removeListener(IChunkListener cl)
  {
    _chunkData.getChunkDispatcher().removeListener(cl);
  }

  /**
   * note : non-locking
   * 
   * @param pl
   * @see org.jactr.core.chunk.IChunk#removeListener(org.jactr.core.event.IParameterListener)
   */
  public void removeListener(IParameterListener pl)
  {
    _chunkData.getParameterDispatcher().removeListener(pl);
  }

  public void bind(ISymbolicChunk symbolicChunk,
      ISubsymbolicChunk subsymbolicChunk)
  {
    _symbolicChunk = symbolicChunk;
    _subsymbolicChunk = subsymbolicChunk;
  }

  public ISubsymbolicChunk getSubsymbolicChunk()
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    return _subsymbolicChunk;
  }

  public ISymbolicChunk getSymbolicChunk()
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    return _symbolicChunk;
  }

  /**
   * merge the metadata, this does not affect the symbolic/subsymbolic contents.
   * That needs to be handled by the DM so that the original sub/symbolic
   * contents can be disposed of properly
   * 
   * @param masterChunk
   */
  public void mergeInto(IChunk masterChunk)
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");

    Lock originalWriteLock = getWriteLock();
    try
    {
      originalWriteLock.lock();

      if (masterChunk instanceof DefaultChunk)
      {
        DefaultChunk mc = (DefaultChunk) masterChunk;
        _chunkData.dispose();
        _chunkData = mc._chunkData;
        // adopt the adapter functionality
        adopt(mc);
      }
      else
      {
        _chunkData.setMutable(masterChunk.isMutable());
        _chunkData.setEncoded(masterChunk.isEncoded());
        _chunkData.setComment(masterChunk.getComment());
        /*
         * copy the meta data
         */
        for (String key : masterChunk.getMetaDataKeys())
          setMetaData(key, masterChunk.getMetaData(key));
      }

    }
    finally
    {
      /*
       * the lock may have changed if the master was abstract. if someone
       * snagged the lock to this chunk after this lock was acquired, but before
       * the merge, they would be stuck since originalWriteLock would not be
       * released if it were replaced.
       */
      originalWriteLock.unlock();
    }
  }

  /**
   * note : non-locking
   * 
   * @param comparison
   * @return
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(IChunk comparison)
  {
    return getSymbolicChunk().getName().compareTo(
        comparison.getSymbolicChunk().getName());
  }

  /**
   * @see org.jactr.core.chunk.IChunk#dispose()
   */
  public void dispose()
  {

    Lock lock = getWriteLock();
    try
    {
      lock.lock();

      if (hasBeenDisposed())
        throw new IllegalChunkStateException("Cant dispose of " + this
            + ", already been disposed!");

      _chunkData.dispose();
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * note : non-locking
   * 
   * @return
   * @see org.jactr.core.chunk.IChunk#hasBeenDisposed()
   */
  public boolean hasBeenDisposed()
  {
    return _chunkData.isDisposed();
  }

  /**
   * note : non-locking
   * 
   * @see org.jactr.core.chunk.IChunk#encode(double)
   */
  public void encode(double when)
  {
    Lock lock = getWriteLock();
    try
    {
      lock.lock();
      if (hasBeenDisposed())
        throw new IllegalChunkStateException(this + " has been disposed!");

      if (isEncoded()) return;

      /*
       * we cant actually lock here since the encoding process may trigger some
       * internal events, such as parameter events
       */
      getSymbolicChunk().encode(when);
      getSubsymbolicChunk().encode(when);
      _chunkData.setEncoded(true);
    }
    finally
    {
      lock.unlock();
    }

    /*
     * notify
     */
    if (hasListeners())
      dispatch(new ChunkEvent(this, ChunkEvent.Type.ENCODED));
  }

  /**
   * note : non-locking
   * 
   * @return
   * @see org.jactr.core.chunk.IChunk#isEncoded()
   */
  public boolean isEncoded()
  {
    return _chunkData.isEncoded();
  }

  /**
   * note : non-locking
   * 
   * @param ct
   * @return
   * @see org.jactr.core.chunk.IChunk#isA(org.jactr.core.chunktype.IChunkType)
   */
  public boolean isA(IChunkType ct)
  {
    return getSymbolicChunk().isA(ct);
  }

  /**
   * note : non-locking
   * 
   * @param ct
   * @return
   * @see org.jactr.core.chunk.IChunk#isAStrict(org.jactr.core.chunktype.IChunkType)
   */
  public boolean isAStrict(IChunkType ct)
  {
    return getSymbolicChunk().isAStrict(ct);
  }

  /**
   * note : non-locking
   * 
   * @param comment
   * @see org.jactr.core.utils.ICommentable#setComment(java.lang.String)
   */
  public void setComment(String comment)
  {
    _chunkData.setComment(comment);
  }

  /**
   * note : non-locking
   * 
   * @return
   * @see org.jactr.core.utils.ICommentable#getComment()
   */
  public String getComment()
  {
    return _chunkData.getComment();
  }

  /**
   * note : non-locking
   * 
   * @param key
   * @return
   * @see org.jactr.core.utils.IMetaContainer#getMetaData(java.lang.String)
   */
  public Object getMetaData(String key)
  {
    if (hasBeenDisposed() || _chunkData == null)
      throw new IllegalChunkStateException(this + " has been disposed!");
    return _chunkData.getMetaData().get(key);
  }

  /**
   * @param key
   * @param value
   * @see org.jactr.core.utils.IMetaContainer#setMetaData(java.lang.String,
   *      java.lang.Object)
   */
  public void setMetaData(String key, Object value)
  {
    Lock lock = getWriteLock();
    try
    {
      lock.lock();

      if (hasBeenDisposed())
        throw new IllegalChunkStateException(this + " has been disposed!");

      Map<String, Object> meta = _chunkData.getMetaData();
      meta.put(key, value);
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * @return
   * @see org.jactr.core.utils.IMetaContainer#getMetaDataKeys()
   */
  public Collection<String> getMetaDataKeys()
  {
    Lock lock = getReadLock();
    try
    {
      lock.lock();

      if (hasBeenDisposed())
        throw new IllegalChunkStateException(this + " has been disposed!");

      return new ArrayList<String>(_chunkData.getMetaData().keySet());
    }
    finally
    {
      lock.unlock();
    }
  }

  /**
   * note : non-locking
   * 
   * @return
   * @see org.jactr.core.chunk.IChunk#isMutable()
   */
  public boolean isMutable()
  {
    return _chunkData.isMutable();
  }

  /**
   * note : non-locking
   * 
   * @param isMutable
   * @see org.jactr.core.chunk.IChunk#setMutable(boolean)
   */
  public void setMutable(boolean isMutable)
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    _chunkData.setMutable(isMutable);
  }

  /**
   * @see org.jactr.core.chunk.IChunk#equalsSymbolic(org.jactr.core.chunk.IChunk)
   */
  public boolean equalsSymbolic(IChunk chunk)
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    if (equals(chunk)) return true;
    ISymbolicChunk mySC = getSymbolicChunk();
    ISymbolicChunk oSC = chunk.getSymbolicChunk();

    /*
     * same chunktypes?
     */
    if (!isAStrict(oSC.getChunkType())) return false;

    /*
     * same slots?
     */
    Collection<ISlot> slots = FastCollectionFactory.newInstance();
    try
    {
      for (ISlot slot : mySC.getSlots(slots))
        try
        {
          if (!oSC.getSlot(slot.getName()).equalValues(slot.getValue()))
            return false;
        }
        catch (Exception e)
        {
          return false;
        }
      return true;
    }
    finally
    {
      FastCollectionFactory.recycle(slots);
    }
  }

  /**
   * @return
   * @see org.jactr.core.chunk.IChunk#getReadLock()
   */
  final public Lock getReadLock()
  {
    return _chunkData.readLock();
  }

  /**
   * @return
   * @see org.jactr.core.chunk.IChunk#getWriteLock()
   */
  final public Lock getWriteLock()
  {
    return _chunkData.writeLock();
  }

  @Override
  public Object getAdapter(Class adapterClass)
  {
    Object superRtn = super.getAdapter(adapterClass);
    if (superRtn != null) return superRtn;

    if (ISymbolicChunk.class.equals(adapterClass))
      return getSymbolicChunk();
    else if (ISubsymbolicChunk.class.equals(adapterClass))
      return getSubsymbolicChunk();
    else if (ISymbolicChunk.class.isAssignableFrom(adapterClass))
      return getSymbolicChunk().getAdapter(adapterClass);
    else if (ISubsymbolicChunk.class.isAssignableFrom(adapterClass))
      return getSubsymbolicChunk().getAdapter(adapterClass);
    else
    {
      // test to see if our sub or sym implement
      Class clazz = getSymbolicChunk().getClass();
      if (adapterClass.isAssignableFrom(clazz)) return getSymbolicChunk();

      clazz = getSubsymbolicChunk().getClass();
      if (adapterClass.isAssignableFrom(clazz)) return getSubsymbolicChunk();
    }

    return null;
  }

  @Override
  public String toString()
  {
    if (_symbolicChunk == null) return super.toString();

    return _symbolicChunk.getName();
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + (_subsymbolicChunk == null ? 0 : _subsymbolicChunk.hashCode());
    result = prime * result
        + (_symbolicChunk == null ? 0 : _symbolicChunk.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof DefaultChunk)) return false;
    final DefaultChunk other = (DefaultChunk) obj;
    if (_subsymbolicChunk == null)
    {
      if (other._subsymbolicChunk != null) return false;
    }
    else if (!_subsymbolicChunk.equals(other._subsymbolicChunk)) return false;
    if (_symbolicChunk == null)
    {
      if (other._symbolicChunk != null) return false;
    }
    else if (!_symbolicChunk.equals(other._symbolicChunk)) return false;
    return true;
  }

}
