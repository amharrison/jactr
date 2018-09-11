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

public abstract class AbstractTypeValueMap<V, I> implements ITypeValueMap<V, I>
{
  abstract public V asKeyType(Object value);

  public void add(Object value, I indexable)
  {
    getValueMap().add(asKeyType(value), indexable);
  }

  public void remove(Object value, I indexable)
  {
    getValueMap().remove(asKeyType(value), indexable);
  }

  public void clear(Object value)
  {
    getValueMap().clear(asKeyType(value));
  }

  public void clear()
  {
    getValueMap().clear();
  }

  public Collection<I> equalTo(Object value)
  {
    return getValueMap().equalTo(asKeyType(value));
  }

  public long equalToSize(Object value)
  {
    return getValueMap().equalToSize(asKeyType(value));
  }

  /**
   * 
   */
  public Collection<I> lessThan(Object value)
      throws UnsupportedOperationException
  {
    return getValueMap().lessThan(asKeyType(value));
  }

  public long lessThanSize(Object value) throws UnsupportedOperationException
  {
    return getValueMap().lessThanSize(asKeyType(value));
  }

  public Collection<I> greaterThan(Object value)
      throws UnsupportedOperationException
  {
    return getValueMap().greaterThan(asKeyType(value));
  }

  public long greaterThanSize(Object value)
      throws UnsupportedOperationException
  {
    return getValueMap().greaterThanSize(asKeyType(value));
  }

  public Collection<I> not(Object value)
  {
    return getValueMap().not(asKeyType(value));
  }
  
  public long notSize(Object value)
  {
    return getValueMap().notSize(asKeyType(value));
  }

  public Collection<I> all()
  {
    return getValueMap().all();
  }

  public long allSize()
  {
    return getValueMap().allSize();
  }
}
