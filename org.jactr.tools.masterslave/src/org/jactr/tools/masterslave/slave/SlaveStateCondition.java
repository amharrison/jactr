package org.jactr.tools.masterslave.slave;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.AbstractSlotCondition;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.match.GeneralMatchFailure;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.core.slot.ProxyUniqueSlotContainer;
import org.jactr.tools.masterslave.master.MasterExtension;

public class SlaveStateCondition extends AbstractSlotCondition
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER             = LogFactory
                                                            .getLog(SlaveStateCondition.class);

  static public final String         IS_LOADED_SLOT     = "is-loaded";

  static public final String         IS_RUNNING_SLOT    = "is-running";

  static public final String         HAS_COMPLETED_SLOT = "has-completed";

  static public final String         ALIAS_SLOT         = "alias";

  static public final String         MODEL_SLOT         = "model";

  private final IUniqueSlotContainer _slaveVariables;

  private final String               _alias;

  public SlaveStateCondition()
  {
    setRequest(new SlotBasedRequest());
    _slaveVariables = null;
    _alias = null;
  }

  private SlaveStateCondition(SlaveStateCondition template, String alias,
      IUniqueSlotContainer slaveVariables)
  {
    _alias = alias;
    setRequest(new SlotBasedRequest(template.getSlots()));
    _slaveVariables = slaveVariables;
  }

  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException
  {
    String alias = null;

    for (IConditionalSlot cSlot : getRequest().getConditionalSlots())
      if (cSlot.getName().equalsIgnoreCase(ALIAS_SLOT)
          && cSlot.getCondition() == IConditionalSlot.EQUALS
          && cSlot.getValue() != null)
      {
        alias = cSlot.getValue().toString();
        break;
      }

    IUniqueSlotContainer slaveContainer = null;

    if (alias != null)
    {
      // assuming master
      MasterExtension me = MasterExtension.getMaster(model);
      if (me == null)
        throw new CannotMatchException(new GeneralMatchFailure(this,
            String.format("%s is not the master of %s", model, alias)));

      slaveContainer = me.getSlaveVariables(alias);

      if (slaveContainer == null)
        throw new CannotMatchException(new GeneralMatchFailure(this,
            String.format("Could not find variable container for %s", alias)));
    }
    else
    {
      // assuming slave
      SlaveExtension se = SlaveExtension.getSlaveExtension(model);
      if (se == null)
        throw new CannotMatchException(new GeneralMatchFailure(this,
            String.format("%s is not a slave", model)));

      slaveContainer = se.getVariables();

      if (slaveContainer == null)
        throw new CannotMatchException(new GeneralMatchFailure(this,
            String.format("Could not find variable container for %s", model)));
    }

    ProxyUniqueSlotContainer container = new ProxyUniqueSlotContainer(
        slaveContainer);
    // container.addSlotContainer(getRequest());

    return new SlaveStateCondition(this, alias, container);
  }

  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    try
    {
      return getRequest().bind(model, _alias, _slaveVariables,
          variableBindings, isIterative);
    }
    catch (CannotMatchException cme)
    {
      throw cme;
    }
    catch (Exception e)
    {
      LOGGER.error("OOPS", e);
      throw new CannotMatchException(e.getMessage());
    }
  }

  @Override
  public void dispose()
  {

  }

}
