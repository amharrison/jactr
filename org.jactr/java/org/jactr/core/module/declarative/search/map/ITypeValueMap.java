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

/**
 * @author developer
 */
public interface ITypeValueMap<V, I>
{

  public boolean isValueRelevant(Object value);

  public IValueMap<V, I> getValueMap();

  public void add(Object value, I indexable);

  public void remove(Object value, I indexable);

  public void clear(Object value);

  public void clear();

  public Collection<I> equalTo(Object value);

  public long equalToSize(Object value);

  /**
   * 
   */
  public Collection<I> lessThan(Object value)
      throws UnsupportedOperationException;

  public long lessThanSize(Object value) throws UnsupportedOperationException;

  public Collection<I> greaterThan(Object value)
      throws UnsupportedOperationException;

  public long greaterThanSize(Object value)
      throws UnsupportedOperationException;

  public Collection<I> not(Object value);

  public long notSize(Object value);

  public Collection<I> all();

  public long allSize();
}
