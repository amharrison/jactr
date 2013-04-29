package org.jactr.tools.masterslave.slave;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.tools.masterslave.master.MasterExtension;

/**
 * for the setting of variables in the slave state
 * 
 * @author harrison
 */
public class SlaveVariableAction extends DefaultSlotAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(SlaveVariableAction.class);

  static private final String        ALIAS_SLOT = SlaveStateCondition.ALIAS_SLOT;

  private final IUniqueSlotContainer _slaveVariables;

  public SlaveVariableAction()
  {
    _slaveVariables = null;
  }

  private SlaveVariableAction(IModel model,
 VariableBindings variableBindings,
      Collection<? extends ISlot> slots)
      throws CannotInstantiateException
  {
    super(variableBindings, slots);

    IUniqueSlotContainer slotContainer = null;

    ISlot aliasSlot = getSlot(ALIAS_SLOT);

    /*
     * if alias is defined, we are manipulating from the master
     */

    if (aliasSlot != null && aliasSlot.getValue() != null)
    {
      String alias = aliasSlot.getValue().toString();

      MasterExtension me = MasterExtension.getMaster(model);
      if (me == null)
        throw new CannotInstantiateException(String.format(
            "%s is not the master of %s", model, alias));

      slotContainer = me.getSlaveVariables(alias);
      if (slotContainer == null)
        throw new CannotInstantiateException(String.format(
            "Could not find variable container for %s", alias));
    }
    else
    {
      SlaveExtension se = SlaveExtension.getSlaveExtension(model);
      if (se == null)
        throw new CannotInstantiateException(String.format("%s is not a slave",
            model));

      slotContainer = se.getVariables();
      if (slotContainer == null)
        throw new CannotInstantiateException(String.format(
            "Could not find variable container for %s", model));
    }

    _slaveVariables = slotContainer;
  }

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return new SlaveVariableAction((IModel) variableBindings.get("=model"),
        variableBindings, getSlots());
  }

  @Override
  public void dispose()
  {

  }

  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    /*
     * set some values..
     */
    for (ISlot slot : getSlots())
    {
      String name = slot.getName();
      // ignore system variables
      if (name.equalsIgnoreCase(SlaveStateCondition.ALIAS_SLOT)
          || name.equalsIgnoreCase(SlaveStateCondition.IS_LOADED_SLOT)
          || name.equalsIgnoreCase(SlaveStateCondition.IS_RUNNING_SLOT)
          || name.equalsIgnoreCase(SlaveStateCondition.HAS_COMPLETED_SLOT))
        continue;

      IMutableSlot mSlot = (IMutableSlot) _slaveVariables.getSlot(name);
      if (mSlot != null)
        mSlot.setValue(slot.getValue());
      else
        _slaveVariables.addSlot(new BasicSlot(name, slot.getValue()));
    }

    return 0;
  }
}
