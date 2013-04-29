package org.jactr.core.slot;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface ISlotOwner
{

  public void valueChanged(ISlot slot, Object oldValue, Object newValue);
}
