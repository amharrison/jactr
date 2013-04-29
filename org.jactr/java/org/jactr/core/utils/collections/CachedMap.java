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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * cached values map for rapid access w/o worrying about iterating too
 * much..
 * @author developer
 *
 * @param <K>
 * @param <V>
 */
public class CachedMap<K, V> implements Map<K, V>
{
  /**
   * logger definition
   */
  static private final Log             LOGGER = LogFactory
                                                  .getLog(CachedMap.class);

  private Map<K, V>                    _backingMap;

  private SoftReference<Collection<V>> _cachedValuesReference;

  public CachedMap(Map<K,V> backing)
  {
    _backingMap = backing;
  }
  
  synchronized protected void clearCachedValues()
  {
    _cachedValuesReference = null;
  }

  synchronized protected Collection<V> getCachedValues()
  {
    Collection<V> rtn = null;
    if (_cachedValuesReference == null || (rtn = _cachedValuesReference.get()) == null)
    {
      rtn = Collections.unmodifiableCollection(new ArrayList<V>(_backingMap.values()));
      _cachedValuesReference = new SoftReference<Collection<V>>(rtn);
    }
    return rtn;
  }


  synchronized public void clear()
  {
    _backingMap.clear();
    clearCachedValues();
  }

  synchronized public boolean containsKey(Object arg0)
  {
    return _backingMap.containsKey(arg0);
  }

  synchronized public boolean containsValue(Object arg0)
  {
    return _backingMap.containsValue(arg0);
  }

  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    // TODO Auto-generated method stub
    if (LOGGER.isWarnEnabled())
      LOGGER.warn("CachedMap.entrySet is not implemented");
    return null;
  }

  synchronized public V get(Object arg0)
  {
    return _backingMap.get(arg0);
  }

  synchronized public boolean isEmpty()
  {
    return _backingMap.isEmpty();
  }

  synchronized public Set<K> keySet()
  {
    return _backingMap.keySet();
  }

  synchronized public V put(K arg0, V arg1)
  {
    V rtn = _backingMap.put(arg0, arg1);
    clearCachedValues();
    return rtn;
  }

  synchronized public void putAll(Map< ? extends K, ? extends V> arg0)
  {
    _backingMap.putAll(arg0);
    clearCachedValues();
  }

  synchronized public V remove(Object arg0)
  {
    V rtn = _backingMap.remove(arg0);
    clearCachedValues();
    return rtn;
  }

  synchronized public int size()
  {
    return _backingMap.size();
  }

  synchronized public Collection<V> values()
  {
    return getCachedValues();
  }

  synchronized public void getValues(Collection<V> container)
  {
    container.addAll(_backingMap.values());
  }
}
