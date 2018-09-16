package org.jactr.core.slot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.CannotInstantiateException;

public class DefaultLogicalSlot extends BasicSlot implements ILogicalSlot,
    IMutableSlot
{

  /**
   * Logger definition
   */
  static private final Log  LOGGER    = LogFactory
                                          .getLog(DefaultLogicalSlot.class);

  // protected ISlot _slot1;
  // private ISlot _value2 = null;

  // protected ISlot _slot2;
  private int               _operator;

  private Collection<ISlot> _children = new ArrayList<ISlot>(2);



  // public DefaultLogicalSlot(int operator, Object value)
  // throws CannotInstantiateException
  // {
  // super(":logic", value);
  //
  // _operator = operator;
  //
  // if (!(value instanceof ISlot))
  // throw new CannotInstantiateException(String.format(value
  // + " is not a slot."));
  //
  // }

  public DefaultLogicalSlot(int operator, ISlot value1, ISlot value2)
      throws CannotInstantiateException
  {
    this(operator, Arrays.asList(value1, value2));
  }

  public DefaultLogicalSlot(int operator, Collection<? extends ISlot> slots)
  {
    super(":logic");
    _operator = operator;
    for (ISlot slot : slots)
      if (slot != null) addSlot(slot);
  }

  @Override
  public boolean isVariable()
  {
    return false;
  }

  @Override
  public boolean isVariableValue()
  {
    return false;
  }

  public int getOperator()
  {
    return _operator;
  }

  public void setOperator(int operator)
  {
    _operator = operator;
  }

  @Override
  public DefaultLogicalSlot clone()
  {
    return new DefaultLogicalSlot(_operator, _children);
  }

  @Override
  protected String createToString()
  {

    String operation = "=";
    switch (getOperator())
    {
      case OR:
        operation = "or";
        break;
      case AND:
        operation = "and";
        break;
      case NOT:
        operation = "not";
        break;
    }

    return String.format("%s %s", operation, _children);
  }

  public void setValue(Object value)
  {
    LOGGER.error("Should never change value of logical slot!");
    new Throwable().printStackTrace();
  }

  public Collection<? extends ISlot> getSlots()
  {
    return getSlots(null);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {
    if (container == null) container = new ArrayList<ISlot>();
    container.addAll(_children);
    return container;
  }

  public void addSlot(ISlot slot)
  {
    _children.add(slot.clone());
  }

  public void removeSlot(ISlot slot)
  {
    _children.remove(slot);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (_children == null ? 0 : _children.hashCode());
    result = prime * result + _operator;
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (!(obj instanceof DefaultLogicalSlot)) return false;
    DefaultLogicalSlot other = (DefaultLogicalSlot) obj;
    if (_children == null)
    {
      if (other._children != null) return false;
    }
    else if (!_children.equals(other._children)) return false;
    if (_operator != other._operator) return false;
    return true;
  }

}
