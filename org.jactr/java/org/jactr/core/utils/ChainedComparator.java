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

package org.jactr.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class ChainedComparator<T> implements Comparator<T>
{

  List<Comparator<T>> _comparators;

  boolean             _permitEqualities = true;

  /**
   * Constructor for the ChainedComparator object
   */
  public ChainedComparator(boolean permitEqualities)
  {
    _comparators = new ArrayList<Comparator<T>>(3);
    _permitEqualities = permitEqualities;
  }

  /**
   * @param comp
   */
  public ChainedComparator(Comparator<T> comp, boolean permitEqualities)
  {
    this(permitEqualities);
    add(comp);
  }
  
  public ChainedComparator(Collection<Comparator<T>> comps, boolean permitEqualities)
  {
    this(permitEqualities);
    _comparators.addAll(comps);
  }

  /**
   * Adds a feature to the Comparator attribute of the ChainedComparator object
   * 
   * @param comp
   *            The feature to be added to the Comparator attribute
   */
  public void add(Comparator<T> comp)
  {
    _comparators.add(comp);
  }
  

  /**
   * Description of the Method
   * 
   * @param comp
   *            Description of the Parameter
   */
  public void remove(Comparator<T> comp)
  {
    _comparators.remove(comp);
  }

  @Deprecated
  public int getNumberOfComparators()
  {
    return _comparators.size();
  }
  
  public int size()
  {
    return _comparators.size();
  }

  /**
   * Gets the comparators attribute of the ChainedComparator object
   * 
   * @return The comparators value
   */
  public List<Comparator<T>> getComparators()
  {
    return new ArrayList<Comparator<T>>(_comparators);
  }

  /**
   * Description of the Method
   * 
   * @param one
   *            Description of the Parameter
   * @param two
   *            Description of the Parameter
   * @return Description of the Return Value
   */
  public int compare(T one, T two)
  {
    if(one==two) return 0;

    for (Comparator<T> comparator : _comparators)
    {
      int rtnValue = comparator.compare(one, two);
      if (rtnValue != 0) return rtnValue;
    }

    if(_permitEqualities)
      return 0;
    
    return (one.hashCode()>two.hashCode()?1:-1);
  }

}