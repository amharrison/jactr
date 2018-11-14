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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class DefaultConditionalSlot extends DefaultMutableSlot implements
    IConditionalSlot
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(DefaultConditionalSlot.class);

  static public final double         EPSILON    = 0.0001;

  private int                        _condition = EQUALS;

  /**
   * @param name
   * @param value
   */
  public DefaultConditionalSlot(String name, Object value)
  {
    super(name, value);
  }

  public DefaultConditionalSlot(String name, int condition, Object value)
  {
    this(name, value);
    setCondition(condition);
  }

  public DefaultConditionalSlot(ISlot slot)
  {
    super(slot);
    if (slot instanceof IConditionalSlot)
    {
      IConditionalSlot cs = (IConditionalSlot) slot;
      setCondition(cs.getCondition());
    }
  }

  @Override
  public DefaultConditionalSlot clone()
  {
    return new DefaultConditionalSlot(this);
  }

  /**
   * @see org.jactr.core.slot.IConditionalSlot#getCondition()
   */
  public int getCondition()
  {
    return _condition;
  }

  /**
   * @see org.jactr.core.slot.IConditionalSlot#setCondition(int)
   */
  public void setCondition(int condition)
  {
    if (condition >= EQUALS && condition <= NOT_EQUALS)
    {
      _condition = condition;
      invalidateToString();
    }
  }

  public boolean matchesCondition(Object test)
  {
    if (test instanceof ISlot) test = ((ISlot) test).getValue();

    if (LOGGER.isDebugEnabled())
    {
      StringBuilder sb = new StringBuilder("Test value : ");
      sb.append(test);
      if (test != null)
      {
        sb.append(", ");
        sb.append(test.getClass().getName());
        sb.append(", ");
        sb.append(test.hashCode());
      }
      LOGGER.debug(sb.toString());
      sb.delete(0, sb.length());

      sb.append("Cond value : ");
      Object tmp = getValue();
      sb.append(tmp);
      if (tmp != null)
      {
        sb.append(", ");
        sb.append(tmp.getClass().getName());
        sb.append(", ");
        sb.append(tmp.hashCode());
      }
      LOGGER.debug(sb.toString());
    }

    boolean rtn = false;
    try
    {
      switch (_condition)
      {
        case EQUALS:
          rtn = equalValues(test);
          break;

        case LESS_THAN:
          rtn = lessThan(test);
          break;

        case LESS_THAN_EQUALS:
          rtn = equalValues(test) | lessThan(test);
          break;

        case GREATER_THAN:
          rtn = greaterThan(test);
          break;

        case GREATER_THAN_EQUALS:
          rtn = equalValues(test) | greaterThan(test);
          break;

        case NOT_EQUALS:
          rtn = !equalValues(test);
          break;

        case WITHIN:
          rtn = within(test);
          break;
      }
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Unknown failure comparing " + this + " with " + test, e);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("matchesCondition " + this + " " + test + " : " + rtn);

    return rtn;
  }

  @Override
  public boolean equalValues(Object value)
  {
    boolean rtn = super.equalValues(value);
    if (!rtn && value instanceof Number && getValue() instanceof Number)
    {
      double myValue = ((Number) getValue()).doubleValue();
      double hisValue = ((Number) value).doubleValue();
      return Math.abs(myValue - hisValue) <= EPSILON;
    }
    return rtn;
  }

  /**
   * @param value
   * @return
   */
  private boolean lessThan(Object value)
  {
    double selfValue = ((Number) getValue()).doubleValue();
    double otherValue = ((Number) value).doubleValue();
    return otherValue < selfValue;
  }

  private boolean greaterThan(Object value)
  {
    double selfValue = ((Number) getValue()).doubleValue();
    double otherValue = ((Number) value).doubleValue();
    return otherValue > selfValue;
  }

  @SuppressWarnings("unchecked")
  private boolean within(Object value)
  {
    // we check to see if getValue() is in the collection
    Collection collection = (Collection) value;
    return collection.contains(getValue());
  }

  @Override
  protected String createToString()
  {

    String condition = "=";
    switch (getCondition())
    {
      case NOT_EQUALS:
        condition = "!=";
        break;
      case LESS_THAN:
        condition = "<";
        break;
      case LESS_THAN_EQUALS:
        condition = "<=";
        break;
      case GREATER_THAN:
        condition = ">";
        break;
      case GREATER_THAN_EQUALS:
        condition = ">=";
        break;
      case WITHIN:
        condition = "w/in";
        break;
    }

    return String.format("%s %s %s", getName(), condition, getValue());
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + _condition;
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    DefaultConditionalSlot other = (DefaultConditionalSlot) obj;
    if (_condition != other._condition) return false;
    return true;
  }

}
