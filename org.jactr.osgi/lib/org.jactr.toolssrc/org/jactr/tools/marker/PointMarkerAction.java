package org.jactr.tools.marker;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.DefaultSlotAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.slot.ISlot;
import org.jactr.tools.marker.impl.DefaultMarker;

/**
 * drops an instananeous marker. requires slot values of "name" and "type", with
 * a possible "description"
 * 
 * @author harrison
 */
public class PointMarkerAction extends DefaultSlotAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PointMarkerAction.class);

  static public final String         NAME        = "name";

  static public final String         TYPE        = "type";

  static public final String         DESCRIPTION = "description";

  public PointMarkerAction()
  {
  }

  public PointMarkerAction(VariableBindings variableBindings,
      Collection<? extends ISlot> slots) throws CannotInstantiateException
  {
    super(variableBindings, slots);
    checkForRequiredSlots(NAME, TYPE);
  }

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return new PointMarkerAction(variableBindings, getSlots());
  }

  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    String markerName = getSlot(NAME).getValue().toString();
    String type = getSlot(TYPE).getValue().toString();

    DefaultMarker marker = new DefaultMarker(instantiation.getModel(),
        markerName, type);
    ISlot desc = getSlot(DESCRIPTION);
    if (desc != null && desc.getValue() != null)
      marker.setDescription(desc.getValue().toString());

    marker.instantanious(firingTime);

    return 0;
  }

}
