package org.jactr.extensions.cached.procedural.listeners;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.slot.INotifyingSlotContainer;
import org.jactr.core.slot.event.ISlotContainerListener;
import org.jactr.core.slot.event.SlotEvent;
import org.jactr.extensions.cached.procedural.invalidators.IInvalidator;
import org.jactr.extensions.cached.procedural.invalidators.SlotInvalidator;

public class SlotListener implements ISlotContainerListener
{
  /**
   * Logger definition
   */
  static private final transient Log                  LOGGER = LogFactory
                                                                 .getLog(SlotListener.class);

  private final INotifyingSlotContainer               _container;

  private final Map<String, Collection<IInvalidator>> _invalidators;

  public SlotListener(INotifyingSlotContainer container)
  {
    _container = container;

    _invalidators = new TreeMap<String, Collection<IInvalidator>>();

    _container.addListener(this, null);

  }

  /**
   * return true if there are no registered invalidators. This is used to signal
   * when we can detach.
   * 
   * @return
   */
  public boolean isEmpty()
  {
    return _invalidators.size() == 0;
  }

  public void dispose()
  {
    _container.removeListener(this);
    _invalidators.clear();
  }

  public INotifyingSlotContainer getContainer()
  {
    return _container;
  }

  public void slotAdded(SlotEvent se)
  {
    // noop
  }

  public void slotRemoved(SlotEvent se)
  {
    // noop
  }

  public void slotChanged(SlotEvent se)
  {
    invalidate(se.getSlot().getName().toLowerCase());
  }

  public void register(SlotInvalidator invalidator)
  {
    Collection<IInvalidator> invalidators = _invalidators.get(invalidator
        .getSlotName());
    if (invalidators == null)
    {
      invalidators = FastList.newInstance();
      _invalidators.put(invalidator.getSlotName(), invalidators);
    }

    invalidators.add(invalidator);
  }

  public void unregister(SlotInvalidator invalidator)
  {
    Collection<IInvalidator> invalidators = _invalidators.get(invalidator
        .getSlotName());
    if (invalidators != null)
    {
      invalidators.remove(invalidator);
      if (invalidators.size() == 0)
      {
        _invalidators.remove(invalidator.getSlotName());
        FastList.recycle((FastList) invalidators);
      }
    }
  }

  protected void invalidate(String slotName)
  {
    Collection<IInvalidator> source = _invalidators.get(slotName);
    if (source != null)
    {

      FastList<IInvalidator> invalidators = FastList.newInstance();
      invalidators.addAll(source);
      if (invalidators.size() > 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String
              .format("Invalidating due to slot change for %s.%s", _container,
                  slotName));
        for (IInvalidator validator : invalidators)
          validator.invalidate();
      }
      FastList.recycle(invalidators);
    }
  }
}
