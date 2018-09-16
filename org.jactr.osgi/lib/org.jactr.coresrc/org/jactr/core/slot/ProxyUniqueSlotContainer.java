package org.jactr.core.slot;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProxyUniqueSlotContainer implements IUniqueSlotContainer
{
  /**
   * Logger definition
   */
  static private final transient Log       LOGGER = LogFactory
                                                      .getLog(ProxyUniqueSlotContainer.class);

  private List<IUniqueSlotContainer> _containers;

  public ProxyUniqueSlotContainer(IUniqueSlotContainer container)
  {
    _containers = new ArrayList<IUniqueSlotContainer>(2);
    addSlotContainer(container);
  }

  @Override
  public String toString()
  {
    if (_containers.size() != 0) return _containers.get(0).toString();
    return super.toString();
  }

  public void addSlotContainer(IUniqueSlotContainer container)
  {
    _containers.add(container);
  }

  public ISlot getSlot(String slotName)
  {
    ISlot rtn = null;
    for (IUniqueSlotContainer container : _containers)
    {
      rtn = container.getSlot(slotName);
      if (rtn != null) break;
    }
    
    return rtn;
  }

  public boolean hasSlot(String slotName)
  {
    for (IUniqueSlotContainer container : _containers)
    {
      if(container.hasSlot(slotName)) return true;
    }
    
    return false;
  }
  
  public void addSlot(ISlot slot)
  {
    throw new UnsupportedOperationException("Cannot add to a proxy container");
  }

  public Collection<? extends ISlot> getSlots()
  {
    return getSlots(null);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if(slots==null)
      slots = new ArrayList<ISlot>();
    
    for(IUniqueSlotContainer container : _containers)
      slots = container.getSlots(slots);
    
    return slots;
  }

  public void removeSlot(ISlot slot)
  {
    throw new UnsupportedOperationException("Cannot remove from a proxy container");
  }

}
