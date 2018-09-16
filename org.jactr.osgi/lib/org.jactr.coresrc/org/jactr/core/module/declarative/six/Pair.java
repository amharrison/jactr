/*
 * Created on Nov 21, 2006
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
package org.jactr.core.module.declarative.six;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class Pair
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(Pair.class);

  private Object _one;
  private Object _two;
  
  
  public Pair(Object one, Object two)
  {
    if(one==null || two==null)
      throw new NullPointerException("No value may be null");
   _one = one;
   _two = two;
  }
  
  public Object getOne()
  {
    return _one;
  }
  
  public Object getTwo()
  {
    return _two;
  }
  

  @Override
  public int hashCode()
  {
    final int PRIME = 31;
    int result = 1;
    result = PRIME * result + ((_one == null) ? 0 : _one.hashCode());
    result = PRIME * result + ((_two == null) ? 0 : _two.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    final Pair other = (Pair) obj;
    if (_one == null)
    {
      if (other._one != null) return false;
    }
    else if (!_one.equals(other._one)) return false;
    if (_two == null)
    {
      if (other._two != null) return false;
    }
    else if (!_two.equals(other._two)) return false;
    return true;
  }
  
  
  
}


