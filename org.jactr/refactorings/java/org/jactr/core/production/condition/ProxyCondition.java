/*
 * Created on Feb 2, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.production.condition;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;

/**
 * proxy condition that wraps a class that implements {@link ICondition}. This
 * allows one to use arbitrary conditions in a model without futzing with the
 * parsers. If the delegate {@link ICondition} is also an {@link ISlotContainer}
 * , the slots added to this proxy will be added to the delegate instead and the
 * delegate will be responsible for the variable resolution. If the delegate is
 * not an {@link ISlotContainer}, it will have to query the variable bindings
 * for the slot values.
 */
public class ProxyCondition extends AbstractSlotCondition
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProxyCondition.class);

  String                             _className;

  ICondition                         _delegateCondition;

  public ProxyCondition(String className) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException
  {
    this(className, Collections.EMPTY_LIST);
  }

  public ProxyCondition(String className, Collection<? extends ISlot> slots)
      throws ClassNotFoundException, InstantiationException,
      IllegalAccessException
  {
    super();
    setDelegateClassName(className);
    setRequest(new SlotBasedRequest(slots));
  }

  public ProxyCondition(Class<? extends ICondition> proxyClass,
      Collection<? extends ISlot> slots) throws InstantiationException,
      IllegalAccessException
  {
    super();
    setDelegateClass(proxyClass);
    setRequest(new SlotBasedRequest(slots));
  }

  protected ProxyCondition(ICondition delegate,
      Collection<? extends ISlot> slots)
  {
    super();
    _delegateCondition = delegate;
    _className = delegate.getClass().getName();
    setRequest(new SlotBasedRequest(slots));
  }

  public String getDelegateClassName()
  {
    return _className;
  }

  @SuppressWarnings("unchecked")
  public void setDelegateClassName(String name) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException
  {
    Class<? extends ICondition> tmpClass = (Class<? extends ICondition>) getClass()
        .getClassLoader().loadClass(name);
    setDelegateClass(tmpClass);
  }

  public void setDelegateClass(Class<? extends ICondition> proxyClass)
      throws InstantiationException, IllegalAccessException
  {
    _delegateCondition = proxyClass.newInstance();
    _className = proxyClass.getName();
  }

  /**
   * route to delegate if it implements {@link ISlotContainer}
   * 
   * @param slot
   * @see org.jactr.core.production.condition.AbstractSlotCondition#addSlot(org.jactr.core.slot.ISlot)
   */
  @Override
  public void addSlot(ISlot slot)
  {
    if (_delegateCondition instanceof ISlotContainer)
      ((ISlotContainer) _delegateCondition).addSlot(slot);
    else
      super.addSlot(slot);
  }

  @Override
  public void removeSlot(ISlot slot)
  {
    if (_delegateCondition instanceof ISlotContainer)
      ((ISlotContainer) _delegateCondition).removeSlot(slot);
    else
      super.removeSlot(slot);
  }

  public ProxyCondition clone(IModel model, Map<String, Object> variableBindings)
      throws CannotMatchException
  {
    try
    {
      return new ProxyCondition(_delegateCondition.clone(model,
          variableBindings), getRequest().getSlots());
    }
    catch (Exception e)
    {
      throw new CannotMatchException("Cannot clone proxy condition "
          + e.getMessage(), e);
    }
  }

  public int bind(IModel model, Map<String, Object> variableBindings,
      boolean iterativeCall) throws CannotMatchException
  {
    int unresolved = 0;

    unresolved = getRequest().bind(model, variableBindings, iterativeCall);
    
    TreeMap<String, Object> expandedBindings = new TreeMap<String, Object>(
        variableBindings);
    
    for (ISlot slot : getSlots())
    {
      expandedBindings.put(slot.getName(), slot.getValue());
      expandedBindings.put("=" + slot.getName(), slot.getValue());
    }

    unresolved = Math.max(unresolved, _delegateCondition.bind(model,
        expandedBindings, iterativeCall));

    return unresolved;
  }
}