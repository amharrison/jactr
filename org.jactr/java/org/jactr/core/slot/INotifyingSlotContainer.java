package org.jactr.core.slot;

import java.util.concurrent.Executor;

import org.jactr.core.slot.event.ISlotContainerListener;

/*
 * default logging
 */

public interface INotifyingSlotContainer extends IUniqueSlotContainer
{

  public void addListener(ISlotContainerListener listener, Executor executor);

  public void removeListener(ISlotContainerListener listener);

  /**
   * method used by the owned slot to communicate its change of value to the
   * container
   * 
   * @param slot
   * @param oldValue
   * @param newValue
   */
  public void valueChanged(ISlot slot, Object oldValue, Object newValue);
}
