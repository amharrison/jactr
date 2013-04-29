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
 * basic slot definition. slots are just key-value pairs that can be matched
 * against, or conditionalized.
 */
public interface ISlot extends Cloneable
{

  static public final String ISA = ":isa";

  /**
   * the name of the slot
   * @return
   */
  public String getName();

  /**
   * slots may contain variables, which are currently hardcoded
   * as strings starting with '='
   * @return
   * @see
   */
  @Deprecated
  public boolean isVariable();
  
  /**
   * does the value of the slot represent an unresolved variable?
   * @return
   */
  public boolean isVariableValue();
  
  /**
   * return the value of the slot. if the slot is a variable, this
   * returns the string name of the variable
   * @return may be null
   */
  public Object getValue();


  /**
   * 
   */
  public boolean equalValues(Object value);
  
  public ISlot clone();
  
}
