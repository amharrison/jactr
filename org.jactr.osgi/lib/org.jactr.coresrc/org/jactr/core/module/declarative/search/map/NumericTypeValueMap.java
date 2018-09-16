/*
 * Created on Oct 12, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.module.declarative.search.map;

import java.util.Collection;

import org.apache.commons.collections.set.CompositeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.collections.CompositeSetFactory;
public class NumericTypeValueMap<I> extends AbstractTypeValueMap<Double, I> implements ITypeValueMap<Double, I>
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(NumericTypeValueMap.class);

  private SortedValueMap<Double,I> _valueMap;
  
  public NumericTypeValueMap()
  {
    _valueMap = new SortedValueMap<Double,I>();
  }
  

  public IValueMap<Double,I> getValueMap()
  {
    return _valueMap;
  }

  public boolean isValueRelevant(Object value)
  {
    return value instanceof Number;
  }

 
  @Override
  public Double asKeyType(Object value)
  {
    if(value instanceof Number)
      return ((Number)value).doubleValue();
    
    return null;
  }
  
  // public Collection<I> get(Object value)
  // {
  // Collection<I> rtn = lessThan(value);
  // rtn.retainAll(greaterThan(value));
  // return rtn;
  // }
  
  /**
   * 
   */
  @Override
  public Collection<I> lessThan(Object value) throws UnsupportedOperationException
  {
    double val = asKeyType(value);
    return getValueMap().lessThan(val);
  }
  
  @Override
  public Collection<I> greaterThan(Object value) throws UnsupportedOperationException
  {
    double val = asKeyType(value);
    return getValueMap().greaterThan(val);
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public Collection<I> not(Object value)
  {
    double low = asKeyType(value);
    double hi = asKeyType(value);
    CompositeSet rtn = CompositeSetFactory.newInstance();

    rtn.addComposited(getValueMap().lessThan(low));
    rtn.addComposited(getValueMap().greaterThan(hi));

    return rtn;
  }

}


