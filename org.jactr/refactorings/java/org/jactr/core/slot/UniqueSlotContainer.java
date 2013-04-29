package org.jactr.core.slot;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UniqueSlotContainer implements IUniqueSlotContainer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(UniqueSlotContainer.class);
  
  private Map<String, ISlot> _slotMap;
  
  public UniqueSlotContainer()
  {
    _slotMap = new TreeMap<String, ISlot>();
  }
  
  public UniqueSlotContainer(IUniqueSlotContainer container)
  {
    this();
    for(ISlot slot : container.getSlots())
      addSlot(slot);
  }

  public ISlot getSlot(String slotName)
  {
    return _slotMap.get(slotName.toLowerCase());
  }
  
  protected ISlot createSlot(ISlot slot)
  {
    return new BasicSlot(slot.getName(), slot.getValue());
  }

  public void addSlot(ISlot slot)
  {
    _slotMap.put(slot.getName().toLowerCase(), createSlot(slot));
  }

  public Collection<? extends ISlot> getSlots()
  {
    return getSlots(null);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if(slots==null)
      slots = new ArrayList<ISlot>(_slotMap.size());
    slots.addAll(_slotMap.values());
    return slots;
  }

  public void removeSlot(ISlot slot)
  {
    _slotMap.remove(slot.getName().toLowerCase());
  }

}
