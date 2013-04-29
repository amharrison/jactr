package org.jactr.core.slot;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.slot.event.ISlotContainerListener;
import org.jactr.core.slot.event.SlotEvent;

/**
 * generic unique slot container that can be listened to
 * 
 * @author harrison
 */
public class NotifyingSlotContainer extends UniqueSlotContainer implements
    INotifyingSlotContainer
{
  /**
   * Logger definition
   */
  static private final transient Log                                           LOGGER      = LogFactory
                                                                                               .getLog(NotifyingSlotContainer.class);

  private ACTREventDispatcher<INotifyingSlotContainer, ISlotContainerListener> _dispatcher = new ACTREventDispatcher<INotifyingSlotContainer, ISlotContainerListener>();

  // who do we say is the owner, in case we want to use this as a delegate
  private INotifyingSlotContainer                                              _delegateContainer;

  public NotifyingSlotContainer()
  {
    super(true);
    _delegateContainer = this;
  }

  /**
   * if you want to use this class as a delegate for another class, you can
   * retarget the source of the events to use this container
   * 
   * @param container
   */
  public void setDelegateContainer(INotifyingSlotContainer container)
  {
    _delegateContainer = container;
  }

  public void addListener(ISlotContainerListener listener, Executor executor)
  {
    _dispatcher.addListener(listener, executor);
  }

  public void removeListener(ISlotContainerListener listener)
  {
    _dispatcher.removeListener(listener);
  }

  public void valueChanged(ISlot slot, Object oldValue, Object newValue)
  {
    if (_dispatcher.hasListeners())
      _dispatcher.fire(new SlotEvent(_delegateContainer, slot, oldValue));
  }

  @Override
  protected ISlot createSlot(ISlot slot)
  {
    if (_useMutable)
      return new NotifyingSlot(slot.getName(), slot.getValue(), this);

    return new BasicSlot(slot.getName(), slot.getValue());
  }

  @Override
  public void addSlot(ISlot slot)
  {
    boolean shouldFire = true;
    try
    {
      shouldFire = getSlot(slot.getName()) == null;
    }
    catch (Exception e)
    {
      // chunks can throw an exception here
    }

    super.addSlot(slot);

    if (shouldFire && _dispatcher.hasListeners())
      _dispatcher.fire(new SlotEvent(_delegateContainer, slot,
          SlotEvent.Type.ADDED));
  }

  @Override
  public void removeSlot(ISlot slot)
  {
    boolean shouldFire = getSlot(slot.getName()) != null;
    if (shouldFire)
    {
      super.removeSlot(slot);
      if (_dispatcher.hasListeners())
        _dispatcher.fire(new SlotEvent(_delegateContainer, slot,
            SlotEvent.Type.REMOVED));
    }
  }

  public void dispose()
  {
    clear();
    _dispatcher.clear();
  }

}
