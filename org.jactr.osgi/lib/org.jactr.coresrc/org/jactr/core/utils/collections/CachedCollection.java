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
package org.jactr.core.utils.collections;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * a collection that permits asynchronous changes to its contents without
 * affecting calls to getCachedCopy() Even though it is synchronized, care must
 * be taken to synchronize the collection while iterating over it
 * 
 * @author developer
 * @param <I>
 */
public class CachedCollection<I> implements Collection<I>
{
  /**
   * logger definition
   */
  static private final Log         LOGGER = LogFactory
                                              .getLog(CachedCollection.class);

  private Collection<I>            _backingCollection;

  private Reference<Collection<I>> _cachedCollectionReference;

  public CachedCollection(Collection<I> backing)
  {
    _backingCollection = backing;
    _cachedCollectionReference = null;
  }

  synchronized public boolean add(I arg0)
  {
    boolean rtn = _backingCollection.add(arg0);
    if (rtn) clearCachedValues();
    return rtn;
  }

  synchronized public boolean addAll(Collection< ? extends I> arg0)
  {
    boolean rtn = _backingCollection.addAll(arg0);
    if (rtn) clearCachedValues();
    return rtn;
  }

  synchronized public void clear()
  {
    _backingCollection.clear();
    clearCachedValues();
  }

  synchronized public boolean contains(Object arg0)
  {
    return _backingCollection.contains(arg0);
  }

  synchronized public boolean containsAll(Collection< ? > arg0)
  {
    return _backingCollection.containsAll(arg0);
  }

  synchronized public boolean isEmpty()
  {
    return _backingCollection.isEmpty();
  }

  synchronized public Iterator<I> iterator()
  {
    return getCachedValues().iterator();
  }

  synchronized public boolean remove(Object arg0)
  {
    boolean rtn = _backingCollection.remove(arg0);
    if (rtn) clearCachedValues();
    return rtn;
  }

  synchronized public boolean removeAll(Collection< ? > arg0)
  {
    boolean rtn = _backingCollection.removeAll(arg0);
    if (rtn) clearCachedValues();
    return rtn;
  }

  synchronized public boolean retainAll(Collection< ? > arg0)
  {
    boolean rtn = _backingCollection.retainAll(arg0);
    if (rtn) clearCachedValues();
    return rtn;
  }

  synchronized public int size()
  {
    return _backingCollection.size();
  }

  synchronized public Object[] toArray()
  {
    return _backingCollection.toArray();
  }

  synchronized public <T> T[] toArray(T[] arg0)
  {
    return _backingCollection.toArray(arg0);
  }

  public Collection<I> getBackingValues()
  {
    return _backingCollection;
  }

  synchronized public Collection<I> getCachedValues()
  {
    Collection<I> rtn = null;
    if (_cachedCollectionReference == null ||
        (rtn = _cachedCollectionReference.get()) == null)
    {
      rtn = Collections
          .unmodifiableCollection(new ArrayList<I>(
          _backingCollection));
      _cachedCollectionReference = new SoftReference<Collection<I>>(rtn);
    }
    return rtn;
  }

  synchronized protected void clearCachedValues()
  {
    if (_cachedCollectionReference != null) _cachedCollectionReference.clear();
    _cachedCollectionReference = null;
  }

  @Override
  synchronized public String toString()
  {
    return getCachedValues().toString();
  }
}
