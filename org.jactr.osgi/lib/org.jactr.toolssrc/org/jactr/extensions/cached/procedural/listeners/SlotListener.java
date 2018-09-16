package org.jactr.extensions.cached.procedural.listeners;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.slot.INotifyingSlotContainer;
import org.jactr.core.slot.event.ISlotContainerListener;
import org.jactr.core.slot.event.SlotEvent;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.extensions.cached.procedural.invalidators.IInvalidator;
import org.jactr.extensions.cached.procedural.invalidators.SlotInvalidator;

public class SlotListener implements ISlotContainerListener
{
  /**
   * Logger definition
   */
  static private final transient Log                              LOGGER = LogFactory
                                                                             .getLog(SlotListener.class);

  private final INotifyingSlotContainer                           _container;

  private final ConcurrentHashMap<String, List<IInvalidator>> _invalidators;

  public SlotListener(INotifyingSlotContainer container)
  {
    _container = container;

    _invalidators = new ConcurrentHashMap<String, List<IInvalidator>>();

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
    /*
     * we can mostly do this safely since it is called at the end of the model
     * cycle, and there better not be any instantiation tasks on going.
     */
    Iterator<Map.Entry<String, List<IInvalidator>>> mapItr = _invalidators
        .entrySet().iterator();
    while (mapItr.hasNext())
    {
      Map.Entry<String, List<IInvalidator>> entry = mapItr.next();
      if (entry.getValue().size() == 0)
      {
        mapItr.remove();

        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("Recycle : %s used list:%d",
              entry.getKey(), entry.getValue().hashCode()));

        FastListFactory.recycle(entry.getValue());
      }
    }

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
    try
    {
      invalidate(se.getSlot().getName());
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to invalidate ", e);
    }
  }

  public void register(SlotInvalidator invalidator)
  {
    List<IInvalidator> list = FastListFactory.newInstance();
    Collection<IInvalidator> invalidators = _invalidators.putIfAbsent(
        invalidator.getSlotName().toLowerCase(), list);

    // if null, there was nothing, but now list is in list in the map
    if (invalidators == null) invalidators = list;

    synchronized (invalidators)
    {
      invalidators.add(invalidator);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("updating for %s list:%d ",
            invalidator.getSlotName(), invalidator.hashCode()));
    }

    //
    if (list != invalidators) FastListFactory.recycle(list);
  }

  public void unregister(SlotInvalidator invalidator)
  {
    String slotName = invalidator.getSlotName().toLowerCase();
    Collection<IInvalidator> invalidators = _invalidators.get(slotName);

    if (invalidators != null)
      synchronized (invalidators)
      {
        invalidators.remove(invalidator);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("removed for %s list:%d ",
              invalidator.getSlotName(), invalidator.hashCode()));
      }
  }

  protected List<IInvalidator> getInvalidators(String slotName)
  {
    slotName = slotName.toLowerCase();
    Collection<IInvalidator> invalidators = _invalidators.get(slotName);
    List<IInvalidator> rtn = FastListFactory.newInstance();
    if (invalidators != null) synchronized (invalidators)
    {
      rtn.addAll(invalidators);
    }

    return rtn;
  }

  protected void invalidate(String slotName)
  {
    List<IInvalidator> invalidators = getInvalidators(slotName);

    try
    {
      if (invalidators.size() == 0) return;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Invalidating due to slot change for %s.%s",
            _container, slotName));

      for (IInvalidator validator : invalidators)
        validator.invalidate();
    }
    finally
    {
      FastListFactory.recycle(invalidators);
    }

  }
}
