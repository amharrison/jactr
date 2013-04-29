package org.jactr.core.production.condition;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;

/**
 * condition that checks the values of system properties. The System properties
 * are returned via {@link System#getProperty(String)} and merely checks the
 * string values (no resolution to primitives is performed).
 * 
 * @author harrison
 */
public class SystemPropertyCondition extends AbstractSlotCondition
{
  static private final transient Log         LOGGER       = LogFactory
                                                              .getLog(SystemPropertyCondition.class
                                                                  .getName());

  static private SystemPropertySlotContainer _systemSlots = new SystemPropertySlotContainer();

  public SystemPropertyCondition()
  {
    super();
    setRequest(new SlotBasedRequest());
  }

  protected SystemPropertyCondition(SlotBasedRequest request)
  {
    super();
    setRequest(new SlotBasedRequest(request.getSlots()));
  }

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Attempting to bind " + this);
    try
    {
      return getRequest().bind(model, "system", _systemSlots, variableBindings,
          isIterative);
    }
    catch (CannotMatchException cme)
    {
      cme.getMismatch().setCondition(this);
      throw cme;
    }
  }

  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    return new SystemPropertyCondition(getRequest());
  }

  static private class SystemPropertySlotContainer implements
      IUniqueSlotContainer
  {

    public ISlot getSlot(String slotName)
    {
      return getPropertyAsSlot(slotName);
    }

    public void addSlot(ISlot slot)
    {
      // Noop
    }

    /*
     * create slots for all the properties - this shouldn't ever actually be
     * called under normal circumstances so the lack of caching is not an issue
     */
    public Collection<? extends ISlot> getSlots()
    {
      return getSlots(null);
    }

    public Collection<ISlot> getSlots(Collection<ISlot> container)
    {
      if (container == null) // at least 30 properties by
        container = new ArrayList<ISlot>(30);
      getPropertiesAsSlots(container);
      return container;
    }

    public void removeSlot(ISlot slot)
    {
      // Noop
    }

    protected void getPropertiesAsSlots(Collection<ISlot> container)
    {
      for (Object keyObj : System.getProperties().keySet())
        container.add(getPropertyAsSlot((String) keyObj));
    }

    protected ISlot getPropertyAsSlot(String propertyName)
    {
      String value = System.getProperty(propertyName);
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Returning system slot : " + propertyName + " = " + value);
      return new BasicSlot(propertyName, value);
    }
    
    public boolean hasSlot(String propertyName) {
    	return (System.getProperty(propertyName) != null);
    }
  }
}
