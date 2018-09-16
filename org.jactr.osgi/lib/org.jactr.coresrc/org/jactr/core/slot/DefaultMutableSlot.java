/*
 * Created on Sep 21, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.slot;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultMutableSlot extends BasicSlot implements IMutableSlot
{

  /**
   * @param name
   * @param value
   */
  public DefaultMutableSlot(String name, Object value)
  {
    super(name, value);
  }

  public DefaultMutableSlot(String name)
  {
    super(name, null);
  }

  public DefaultMutableSlot(ISlot slot)
  {
    super(slot.getName(), slot.getValue());
  }

  @Override
  public DefaultMutableSlot clone()
  {
    return new DefaultMutableSlot(this);
  }

  /**
   * @see org.jactr.core.slot.IMutableSlot#setValue(java.lang.Object)
   */
  public void setValue(Object value)
  {
    setValueInternal(value);
  }

  /*
   * The below of not including the variable value in the hashcode was insane.
   * Hopefully we won't discover any code that required this mistake and a half.
   * Fortunately, most raw DefaultMutables are used in unique slot map
   * containers
   */
  // // /**
  // // * value is intentionally excluded since we dont want the hash to
  // change..
  // // */
  // // @Override
  // // public int hashCode()
  // // {
  // // final int prime = 31;
  // // int result = 1;
  // // result = prime * result + (getName() == null ? 0 :
  // getName().hashCode());
  // // return result;
  // // }
  //
  // /**
  // * however, we still want equals to include the value, so we delegate to
  // * super.
  // */
  // @Override
  // public boolean equals(Object obj)
  // {
  // return super.equals(obj);
  // }

}
