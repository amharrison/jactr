package org.jactr.extensions.cached.procedural.invalidators;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.IProduction;
import org.jactr.core.slot.INotifyingSlotContainer;
import org.jactr.extensions.cached.procedural.internal.InstantiationCache;
import org.jactr.extensions.cached.procedural.internal.ListenerHub;

public class SlotInvalidator extends AbstractInvalidator
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SlotInvalidator.class);
  
  private final INotifyingSlotContainer _container;
  private final String _slotName;

  public SlotInvalidator(InstantiationCache cache, IProduction production, INotifyingSlotContainer container, String slotName)
  {
    super(cache, production);
    _container = container;
    _slotName = slotName.toLowerCase();
  }
  
  public String getSlotName()
  {
    return _slotName;
  }

  public void register(ListenerHub hub)
  {
    hub.getSlotListener(_container).register(this);
  }

  public void unregister(ListenerHub hub)
  {
    hub.getSlotListener(_container).unregister(this);
  }

}
