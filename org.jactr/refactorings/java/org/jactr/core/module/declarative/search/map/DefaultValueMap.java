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
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultValueMap<V, I> implements IValueMap<V, I>
{
  /**
   * logger definition
   */
  static public final Log        LOGGER = LogFactory
                                            .getLog(DefaultValueMap.class);

  private ReentrantReadWriteLock _lock;

  private Map<V, Collection<I>>  _map;

  public DefaultValueMap()
  {
    _lock = new ReentrantReadWriteLock();
    _map = instantiateCoreMap();
  }

  protected ReentrantReadWriteLock getLock()
  {
    return _lock;
  }

  protected Map<V, Collection<I>> getCoreMap()
  {
    return _map;
  }
  
  protected Collection<I> instantiateReturnCollection()
  {
    return new FastTable<I>();
  }
  
  protected Set<I> instantiateReturnSet()
  {
    return new FastSet<I>();
  }

  /**
   * override to provide a sorted map
   * 
   * @return
   */
  protected Map<V, Collection<I>> instantiateCoreMap()
  {
    FastMap<V, Collection<I>> rtn =  new FastMap<V, Collection<I>>();
    //rtn.setKeyComparator(FastComparator.DIRECT);
    return rtn;
    //return new HashMap<V, Collection<I>>();
  }

  /**
   * override to change the underlying collection currently HashSet
   */
  protected Collection<I> instantiateCoreCollection(V forValue)
  {
    /*
     * cant be a set since a chunk can point to this value multiple times
     */
    return new FastList<I>();
    //return new HashSet<I>();
  }

  public void add(V value, I indexable)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.writeLock().lock();

      Collection<I> indexables = getCoreMap().get(value);
      if (indexables == null)
      {
        indexables = instantiateCoreCollection(value);
        getCoreMap().put(value, indexables);
      }
      indexables.add(indexable);
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  public void clear(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.writeLock().lock();

      Collection<I> indexables = getCoreMap().remove(value);
      if (indexables != null)
      {
        indexables.clear();
      }
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  public void clear()
  {
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.writeLock().lock();

      getCoreMap().clear();
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  public Collection<I> get(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    ReentrantReadWriteLock lock = getLock();
    Collection<I> rtn = instantiateReturnCollection();

    try
    {
      lock.readLock().lock();

      Collection<I> indexables = getCoreMap().get(value);
      if (indexables != null) rtn.addAll(indexables);
      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  public Collection<I> greaterThan(V value)
      throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException(
        "Since natural ordering cannot be inferred, greaterThan is not implemented");
  }

  public Collection<I> lessThan(V value) throws UnsupportedOperationException
  {
    throw new UnsupportedOperationException(
        "Since natural ordering cannot be inferred, lessthan is not implemented");
  }

  public Collection<I> not(V value)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    Set<I> rtn = instantiateReturnSet();
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.readLock().lock();

      Map<V, Collection<I>> coreMap = getCoreMap();
      for (V tmpValue : coreMap.keySet())
      {
        if (!tmpValue.equals(value)) rtn.addAll(get(tmpValue));
      }
      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  public void remove(V value, I indexable)
  {
    if (value == null)
      throw new NullPointerException("null values are not permitted as keys");

    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.writeLock().lock();

      Collection<I> indexables = getCoreMap().get(value);
      if (indexables != null)
      {
        indexables.remove(indexable);
        if(indexables.size()==0)
          getCoreMap().remove(value);
      }
    }
    finally
    {
      lock.writeLock().unlock();
    }
  }

  
  public Collection<I> all()
  {
    Set<I> rtn = instantiateReturnSet();
    ReentrantReadWriteLock lock = getLock();
    try
    {
      lock.readLock().lock();
      
      for (Collection<I> values : getCoreMap().values())
        rtn.addAll(values);
      
      return rtn;
    }
    finally
    {
      lock.readLock().unlock();
    }
  }

  
  
}
