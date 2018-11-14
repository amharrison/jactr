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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.utils.WeaklyCached;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class BasicSlot implements ISlot, Comparable<ISlot>
{

  static private final transient Log LOGGER = LogFactory
                                                .getLog(BasicSlot.class);

  private Object                     _value;

  private String                     _name;

  /**
   * we cache the toString representation since it will be called so frequently,
   * however this creates a ton of strings that are actually relatively short
   * lived, so we use references to hold onto it for a short term
   */
  private WeaklyCached<String>       _toString = new WeaklyCached<>(
      this::createToString);

  public BasicSlot(String name)
  {
    this(name, null);
  }

  public BasicSlot(String name, Object value)
  {
    setNameInternal(name);
    setValueInternal(value);
  }

  public BasicSlot(ISlot slot)
  {
    this(slot.getName(), slot.getValue());
  }

  /**
   * @see org.jactr.core.slot.ISlot#getValue()
   */
  final public Object getValue()
  {
    return _value;
  }

  /**
   * @see org.jactr.core.slot.ISlot#getName()
   */
  final public String getName()
  {
    return _name;
  }

  /**
   * @see org.jactr.core.slot.ISlot#equalValues(java.lang.Object)
   */
  public boolean equalValues(Object test)
  {
    if (test instanceof ISlot) test = ((ISlot) test).getValue();

    /*
     * equalValues test==null returns true only if _value==null
     */
    if (_value == null) if (test == null)
      return true;
    else
      return false;

    if (isVariableValue() && test != null)
      return true;
    else if (_value instanceof String && test instanceof String)
      return ((String) _value).equalsIgnoreCase((String) test);
    else
      return _value.equals(test);
  }

  /**
   * equality is only defined as equal names
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */

  /**
   * @see org.jactr.core.utils.Duplicateable#duplicate()
   */
  @Override
  public BasicSlot clone()
  {
    return new BasicSlot(_name, _value);
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (_name == null ? 0 : _name.hashCode());
    result = prime * result + (_value == null ? 0 : _value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    BasicSlot other = (BasicSlot) obj;
    if (_name == null)
    {
      if (other._name != null) return false;
    }
    else if (!_name.equals(other._name)) return false;
    if (_value == null)
    {
      if (other._value != null) return false;
    }
    else if (!_value.equals(other._value)) return false;
    return true;
  }

  /**
   * checks to see if the slot value is a variable. Three things must be true:<br>
   * 1) the value must be a string <br>
   * 2) it must start with "=" <br>
   * 3) it must have no white spaces (so that resolvable strings such as "=chunk
   * has been retrieved" can be resolved dynamically) <br>
   * 
   * @see org.jactr.core.slot.IConditionalSlot#isVariable()
   */
  public boolean isVariableValue()
  {
    Object value = getValue();
    if (value instanceof String)
    {
      String str = (String) value;
      return str.startsWith("=") && str.indexOf(' ') == -1;
    }
    return false;
  }

  @Deprecated
  public boolean isVariable()
  {
    return isVariableValue();
  }

  /**
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(ISlot compSlot)
  {
    return getName().compareTo(compSlot.getName());
  }

  @Override
  public String toString()
  {
    return _toString.get();
  }

  protected void invalidateToString()
  {
    _toString.invalidate();
  }

  protected String createToString()
  {
    return String.format("%1$s(%2$s)", getName(), getValue());
  }

  /**
   * set the value of the slot, returning the prior value
   * 
   * @param value
   * @return
   */
  final protected Object setValueInternal(Object value)
  {
    if (value != null
        && !(value instanceof String || value instanceof Number
            || value instanceof Boolean || value instanceof StringBuilder /*
                                                                           * SB
                                                                           * is
                                                                           * used
                                                                           * internally
                                                                           * during
                                                                           * build
                                                                           * phase
                                                                           */
        || value instanceof IChunk || value instanceof IChunkType
        || value instanceof IProduction || value instanceof IActivationBuffer || value instanceof IModel))
      LOGGER
          .warn(String
              .format(
                  "Unexpected slot value found. Not critical, but could cause issues down the line. [%s : %s]",
                  value, value.getClass().getSimpleName()));

    Object old = _value;
    _value = value;
    invalidateToString();
    return old;
  }

  final protected Object setNameInternal(String name)
  {
    String old = _name;
    _name = name;
    invalidateToString();
    return old;
  }

}
