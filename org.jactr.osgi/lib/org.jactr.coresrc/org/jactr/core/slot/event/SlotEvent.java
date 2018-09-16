package org.jactr.core.slot.event;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IACTREvent;
import org.jactr.core.slot.INotifyingSlotContainer;
import org.jactr.core.slot.ISlot;

public class SlotEvent extends
    AbstractACTREvent<INotifyingSlotContainer, ISlotContainerListener>
    implements IACTREvent<INotifyingSlotContainer, ISlotContainerListener>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SlotEvent.class);

  static public enum Type {
    ADDED, REMOVED, CHANGED
  };

  private final Type  _type;

  private final ISlot _slot;

  private Object      _oldValue;

  public SlotEvent(INotifyingSlotContainer container, Type type, ISlot slot)
  {
    super(container);
    _type = type;
    _slot = slot;
  }

  public SlotEvent(INotifyingSlotContainer container, ISlot slot,
      Object oldValue)
  {
    this(container, Type.CHANGED, slot);
    _oldValue = oldValue;
  }

  public Type getType()
  {
    return _type;
  }

  public ISlot getSlot()
  {
    return _slot;
  }

  public Object getOldValue()
  {
    return _oldValue;
  }

  @Override
  public void fire(ISlotContainerListener listener)
  {
    switch (getType())
    {
      case ADDED:
        listener.slotAdded(this);
        break;
      case REMOVED:
        listener.slotRemoved(this);
        break;
      case CHANGED:
        listener.slotChanged(this);
        break;
    }

  }

}
