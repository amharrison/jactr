package org.jactr.core.production.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jactr.core.model.IModel;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IMutableVariableNameSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.IVariableNameSlot;

/*
 * default logging
 */

/**
 * basic slot based request
 */
public class SlotBasedRequest implements IRequest, ISlotContainer
{

  protected Collection<IConditionalSlot> _slots;

  protected Set<IConditionalSlot>        _unresolved;

  private boolean                        _locked = false;

  @SuppressWarnings("unchecked")
  public SlotBasedRequest()
  {
    this(Collections.EMPTY_LIST);
  }

  public SlotBasedRequest(Collection<? extends ISlot> slots)
  {
    _slots = new ArrayList<IConditionalSlot>(Math.max(slots.size(), 5));
    for (ISlot slot : slots)
      _slots.add(new DefaultConditionalSlot(slot));
  }

  /**
   * bind the slot values in this request against those slots contained in the
   * container. This allows us to generally bind against anything that contains
   * a slot (chunk, chunktype, or buffer for queries)
   * 
   * @param model
   * @param container
   * @param bindings
   * @param iterativeCall
   * @return
   * @throws CannotMatchException
   */
  public int bind(IModel model, String containerName,
      IUniqueSlotContainer container, Map<String, Object> bindings,
      boolean iterativeCall) throws CannotMatchException
  {
    if (_unresolved == null)
      _unresolved = new HashSet<IConditionalSlot>(_slots);

    Iterator<IConditionalSlot> slotItr = _unresolved.iterator();
    while (slotItr.hasNext())
    {
      IConditionalSlot cSlot = slotItr.next();

      /*
       * the name is a variable..
       */
      if (cSlot instanceof IMutableVariableNameSlot
          && ((IMutableVariableNameSlot) cSlot).isVariableName())
      {
        String variableName = cSlot.getName().toLowerCase();
        /*
         * if we can resolve, do so, otherwise skip until we can.
         */
        if (bindings.containsKey(variableName))
          ((IMutableVariableNameSlot) cSlot).setName(bindings.get(variableName)
              .toString());
        else
          continue;
      }

      ISlot valueSlot = null;
      try
      {
        valueSlot = container.getSlot(cSlot.getName());
      }
      catch (Exception e)
      {
        throw new CannotMatchException(String.format("%s.%s does not exist",
            containerName, cSlot.getName()));
      }

      if (!cSlot.isVariableValue())
      {
        if (valueSlot == null)
          throw new CannotMatchException(String.format("%s.%s does not exist",
              containerName, cSlot.getName()));

        if (!cSlot.matchesCondition(valueSlot.getValue()))
          throw new CannotMatchException(String.format(
              "%s.%s=%s does not match condition %s.", containerName, valueSlot
                  .getName(), valueSlot.getValue(), cSlot));

        else
          slotItr.remove(); // did match, no longer unresolved
      }
      else
      {
        String variableName = ((String) cSlot.getValue()).toLowerCase();

        /*
         * check to see if we can resolve
         */
        if (bindings.containsKey(variableName))
        {
          Object value = bindings.get(variableName);
          cSlot.setValue(value);

          // test
          if (!cSlot.matchesCondition(valueSlot.getValue()))
            throw new CannotMatchException(String.format(
                "%s.%s=%s does not match condition %s.", containerName,
                valueSlot.getName(), valueSlot.getValue(), cSlot));
        }
        else
        /*
         * do we bind this variable?
         */
        if (cSlot.getCondition() == IConditionalSlot.EQUALS
            && cSlot.matchesCondition(valueSlot.getValue()))
        {
          bindings.put(variableName, valueSlot.getValue());
          slotItr.remove();
        }
      }
    }

    if (_unresolved.size() > 0)
      if (!iterativeCall)
        throw new CannotMatchException("Unresolved variables for slots : "
            + _unresolved + ". available:" + bindings.keySet());

    return _unresolved.size();
  }

  @SuppressWarnings("unchecked")
  public int bind(IModel model, Map<String, Object> bindings,
      boolean iterativeCall) throws CannotMatchException
  {
    /*
     * this shouldn't be locked, nor should there be any other threads futzing
     * with it
     */
    if (_unresolved == null)
      _unresolved = new HashSet<IConditionalSlot>(_slots);

    Iterator<IConditionalSlot> slotItr = _unresolved.iterator();
    while (slotItr.hasNext())
    {
      IConditionalSlot slot = slotItr.next();
      /*
       * the name is a variable..
       */
      if (slot instanceof IMutableVariableNameSlot
          && ((IMutableVariableNameSlot) slot).isVariableName())
      {
        /*
         * if we can resolve, do so, otherwise skip until we can.
         */
        String variableName = slot.getName().toLowerCase();
        if (bindings.containsKey(variableName))
          ((IMutableVariableNameSlot) slot).setName(bindings.get(variableName)
              .toString());
        else
          continue;
      }

      /*
       * if we can resolve, do so.
       */
      if (slot.isVariableValue())
      {
        String variableName = ((String) slot.getValue()).toLowerCase();
        if (bindings.containsKey(variableName))
        {
          slot.setValue(bindings.get(variableName));
          slotItr.remove();
        }
      }
      else
        slotItr.remove();
    }

    if (_unresolved.size() > 0)
      if (!iterativeCall)
        throw new CannotMatchException("Unresolved variables for slots : "
            + _unresolved + ". available:" + bindings.keySet());

    return _unresolved.size();
  }

  @Override
  public SlotBasedRequest clone()
  {
    return new SlotBasedRequest(_slots);
  }

  protected void setLocked(boolean locked)
  {
    if (_locked != locked)
    {
      _locked = locked;
      if (_locked)
        _slots = Collections.unmodifiableCollection(_slots);
      else
        _slots = new ArrayList<IConditionalSlot>(_slots);
    }
  }

  public void addSlot(ISlot slot)
  {
    if (_locked)
      throw new RuntimeException("Cannot modify a locked slot container");

    if (slot instanceof IVariableNameSlot)
      _slots.add(new DefaultVariableConditionalSlot(slot));
    else
      _slots.add(new DefaultConditionalSlot(slot));
  }

  public Collection<? extends IConditionalSlot> getConditionalSlots()
  {
    return Collections.unmodifiableCollection(_slots);
  }

  public Collection<? extends ISlot> getSlots()
  {
    if (!_locked) return _slots;

    return Collections.unmodifiableCollection(_slots);
  }

  public void removeSlot(ISlot slot)
  {
    if (_locked)
      throw new RuntimeException("Cannot modify a locked slot container");

    _slots.remove(slot);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {
    if (container == null) if (_slots != null)
      container = new ArrayList<ISlot>(_slots.size() + 1);
    else
      container = new ArrayList<ISlot>();

    container.addAll(_slots);
    return container;
  }

}
