/*
 * Created on Oct 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.declarative.search.map;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.collection.CompositeCollection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * extends default value map to provide sortable values
 * 
 * @author harrison
 * @param <V>
 * @param <I>
 */
public class SortedValueMap<V extends Comparable<V>, I> extends
    DefaultValueMap<V, I>
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(SortedValueMap.class);

  private SortedSet<V>    _sortedValues;

  public SortedValueMap()
  {
    super();
  }

  /**
   * overridden to create a sortable set to track the sorted keys, but we still
   * use the same core map
   * 
   * @see org.jactr.core.module.declarative.search.map.DefaultValueMap#instantiateCoreMap()
   */
  @Override
  protected Map<V, Collection<I>> instantiateCoreMap()
  {
    _sortedValues = new TreeSet<V>();
    // return new TreeMap<V, Collection<I>>();
    return super.instantiateCoreMap();
//    FastMap<V, Collection<I>> rtn = new FastMap<V, Collection<I>>();
//    // since the values will be sorted, we can assume that the hashes are not
//    // evenly distributed
//    rtn.setKeyComparator(FastComparator.REHASH);
//    return rtn;
  }

  @Override
  protected Collection<I> instantiateCoreCollection(V forValue)
  {
    _sortedValues.add(forValue);
    return super.instantiateCoreCollection(forValue);
  }

  @Override
  public void clear()
  {
    super.clear();
    try
    {
      getLock().writeLock().lock();
      _sortedValues.clear();
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<I> greaterThan(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    CompositeCollection rtn = instantiateReturnSet();
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.readLock().lock();

      for (V tmpValue : _sortedValues.tailSet(value))
        if (!tmpValue.equals(value))
        {
          Collection<I> get = equalTo(tmpValue);
          rtn.addComposited(get);

          // recycleCollection(get);
        }

      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public long greaterThanSize(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    long rtn = 0;
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.readLock().lock();

      for (V tmpValue : _sortedValues.tailSet(value))
      {
        Collection<I> container = getCoreMap().get(tmpValue);
        if (container != null) rtn += container.size();
      }
      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<I> lessThan(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    CompositeCollection rtn = instantiateReturnSet();
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.readLock().lock();

      for (V tmpValue : _sortedValues.headSet(value))
      {
        Collection<I> get = equalTo(tmpValue);
        rtn.addComposited(get);
        // rtn.addAll(get);
        // recycleCollection(get);
      }

      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  @Override
  public long lessThanSize(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    long rtn = 0;
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.readLock().lock();

      for (V tmpValue : _sortedValues.headSet(value))
      {
        Collection<I> container = getCoreMap().get(tmpValue);
        if (container != null) rtn += container.size();
      }
      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }
}
