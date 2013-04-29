package org.jactr.modules.pm.motor.managers;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.DefaultMutableSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.motor.IMotorModule;

public class MuscleState implements IUniqueSlotContainer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(MuscleState.class);

  private Map<String, ISlot>         _slotMap;

  private double[]                   _position = { 0, 0, 0 };

  private IIdentifier                _identifier;

  private String                     _name;

  public MuscleState(IIdentifier identifier, String name, IChunk freeChunk)
  {
    _identifier = identifier;
    _name = name;
    _slotMap = new TreeMap<String, ISlot>();
    addSlot(new BasicSlot(IMotorModule.MUSCLE_SLOT, name));
    addSlot(new DefaultMutableSlot(IStatusBuffer.STATE_SLOT, freeChunk));
    addSlot(new DefaultMutableSlot(IPerceptualBuffer.PREPARATION_SLOT,
        freeChunk));
    addSlot(new DefaultMutableSlot(IPerceptualBuffer.PROCESSOR_SLOT, freeChunk));
    addSlot(new DefaultMutableSlot(IPerceptualBuffer.EXECUTION_SLOT, freeChunk));

    addSlot(new DefaultMutableSlot("p1", null));
    addSlot(new DefaultMutableSlot("p2", null));
    addSlot(new DefaultMutableSlot("p3", null));
    addSlot(new DefaultMutableSlot("p4", null));
  }

  public IIdentifier getIdentifier()
  {
    return _identifier;
  }

  public String getName()
  {
    return _name;
  }

  @Override
  public String toString()
  {
    return getName();
  }

  public void set(IChunk chunk, String... slotNames)
  {
    for (String slotName : slotNames)
    {
      IMutableSlot slot = (IMutableSlot) getSlot(slotName);
      if (slot != null)
      {
        slot.setValue(chunk);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("%s.%s = %s", _name, slotName, chunk));
      }
    }
  }

  public double[] getPosition(double[] container)
  {
    System.arraycopy(_position, 0, container, 0, Math.min(container.length,
        _position.length));
    return container;
  }

  void setPosition(double[] position)
  {
    System.arraycopy(position, 0, _position, 0, Math.min(position.length,
        _position.length));
    for (int i = 0; i < position.length; i++)
    {
      String slotName = String.format("p%d", i + 1);
      IMutableSlot slot = (IMutableSlot) getSlot(slotName);
      if (slot == null)
      {
        slot = new DefaultMutableSlot(slotName, null);
        addSlot(slot);
      }

      slot.setValue(position[i]);
    }
  }

  public ISlot getSlot(String slotName)
  {
    return _slotMap.get(slotName.toLowerCase());
  }

  public void addSlot(ISlot slot)
  {
    _slotMap.put(slot.getName().toLowerCase(), slot);
  }

  public boolean canModify()
  {
    return false;
  }

  public Collection<? extends ISlot> getSlots()
  {
    return new ArrayList<ISlot>(_slotMap.values());
  }

  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if (slots == null) slots = new ArrayList<ISlot>();
    slots.addAll(_slotMap.values());
    return slots;
  }

  public void removeSlot(ISlot slot)
  {
    _slotMap.remove(slot.getName().toLowerCase());
  }

  public Collection<IConditionalSlot> matchesStatus(
      Collection<IConditionalSlot> slots, Map<String, Object> bindings)
      throws CannotMatchException
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Checking conditional slots against buffer status : "
          + slots);

    Collection<IConditionalSlot> slotsToRemove = new ArrayList<IConditionalSlot>(
        slots.size());

    for (IConditionalSlot cSlot : slots)
    {
      IConditionalSlot slot = cSlot;

      if (_slotMap.containsKey(slot.getName().toLowerCase()))
      {
        Object testValue = testStatusSlot(slot);

        if (cSlot.isVariableValue())
          bindings.put((String) cSlot.getValue(), testValue);

        slotsToRemove.add(slot);
      }
    }
    return slotsToRemove;
  }

  private Object testStatusSlot(IConditionalSlot slot)
      throws CannotMatchException
  {
    ISlot statusSlot = getSlot(slot.getName());

    // we have something named this..
    if (!slot.matchesCondition(statusSlot.getValue()))
    {
      String message = String.format("%s.%s doesn't match condition %s",
          getName(), statusSlot, slot);
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
      throw new CannotMatchException(message);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(getName() + " : " + statusSlot
          + " matches conditional slot " + slot);

    return statusSlot.getValue();
  }
}
