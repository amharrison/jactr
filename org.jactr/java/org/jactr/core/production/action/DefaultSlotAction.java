package org.jactr.core.production.action;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.UniqueSlotContainer;

public abstract class DefaultSlotAction extends DefaultAction implements
    IUniqueSlotContainer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultSlotAction.class);

  private UniqueSlotContainer        _slotContainer;

  public DefaultSlotAction()
  {
    _slotContainer = new UniqueSlotContainer(true);
  }

  /**
   * called from the bind method. This will duplicate the tempalte's slots and
   * then bind the variables
   * 
   * @param variableBindings
   * @param slots
   */
  protected DefaultSlotAction(VariableBindings variableBindings,
      Collection<? extends ISlot> slots) throws CannotInstantiateException
  {
    this();
    for (ISlot slot : slots)
      addSlot(slot);
    bindSlotValues(variableBindings, getSlotContainer().getMutableSlots());
  }

  protected UniqueSlotContainer getSlotContainer()
  {
    return _slotContainer;
  }

  public ISlot getSlot(String slotName)
  {
    return _slotContainer.getSlot(slotName);
  }
  
  public boolean hasSlot(String slotName)
  {
	  return _slotContainer.hasSlot(slotName);
  }

  public void addSlot(ISlot slot)
  {
    _slotContainer.addSlot(slot);
  }

  public Collection<? extends ISlot> getSlots()
  {
    return _slotContainer.getSlots();
  }

  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {
    return _slotContainer.getSlots(container);
  }

  public void removeSlot(ISlot slot)
  {
    _slotContainer.removeSlot(slot);
  }

  /**
   * makes sure the named slots are available and not null.
   * 
   * @param slotNames
   * @throws CannotInstantiateException
   */
  protected void checkForRequiredSlots(String... slotNames)
      throws CannotInstantiateException
  {
    for (String slotName : slotNames)
    {
      ISlot slot = getSlot(slotName);
      if (slot == null || slot.getValue() == null)
        throw new CannotInstantiateException(String.format(
            "The slot %s is required for this action", slotName));
    }
  }
}
