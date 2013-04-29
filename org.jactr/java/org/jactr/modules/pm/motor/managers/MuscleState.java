package org.jactr.modules.pm.motor.managers;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.DefaultMutableSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.NotifyingSlotContainer;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.motor.IMotorModule;

public class MuscleState extends NotifyingSlotContainer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(MuscleState.class);


  private double[]                   _position = { 0, 0, 0 };

  private IIdentifier                _identifier;

  private String                     _name;

  public MuscleState(IIdentifier identifier, String name, IChunk freeChunk)
  {
    _identifier = identifier;
    _name = name;
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



  public boolean canModify()
  {
    return false;
  }



  // public Collection<IConditionalSlot> matchesStatus(
  // Collection<IConditionalSlot> slots, VariableBindings bindings)
  // throws CannotMatchException
  // {
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Checking conditional slots against buffer status : "
  // + slots);
  //
  // Collection<IConditionalSlot> slotsToRemove = new
  // ArrayList<IConditionalSlot>(
  // slots.size());
  //
  // for (IConditionalSlot cSlot : slots)
  // {
  // IConditionalSlot slot = cSlot;
  //
  // if (hasSlot(slot.getName()))
  // {
  // Object testValue = testStatusSlot(slot);
  //
  // if (cSlot.isVariableValue())
  // bindings.bind((String) cSlot.getValue(), testValue,
  // getSlot(slot.getName()), this);
  //
  // slotsToRemove.add(slot);
  // }
  // }
  // return slotsToRemove;
  // }
  //
  // private Object testStatusSlot(IConditionalSlot slot)
  // throws CannotMatchException
  // {
  // ISlot statusSlot = getSlot(slot.getName());
  //
  // // we have something named this..
  // if (!slot.matchesCondition(statusSlot.getValue()))
  // {
  // String message = String.format("%s.%s doesn't match condition %s",
  // getName(), statusSlot, slot);
  // if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
  //
  // SlotMatchFailure smf = new SlotMatchFailure(null, this, slot, statusSlot);
  //
  // throw new CannotMatchException(smf);
  // }
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(getName() + " : " + statusSlot
  // + " matches conditional slot " + slot);
  //
  // return statusSlot.getValue();
  // }
}
