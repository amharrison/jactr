package org.jactr.core.slot.event;

/*
 * default logging
 */
import java.util.EventListener;

public interface ISlotContainerListener extends EventListener
{

  public void slotAdded(SlotEvent se);

  public void slotRemoved(SlotEvent se);

  public void slotChanged(SlotEvent se);
}
