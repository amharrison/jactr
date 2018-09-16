package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;

/**
 * a failure to match due to a conditional slot mismatch
 * 
 * @author harrison
 */
public class SlotMatchFailure extends AbstractMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                       = LogFactory
                                                                      .getLog(SlotMatchFailure.class);

  static private final String        NO_SLOT_IN_CONTAINER_MESSAGE = "%s does not exist in %s";

  static private final String        MISMATCHED_CONDITION_MESSAGE = "%s.%s=%s does not match %s";

  private final IUniqueSlotContainer _container;

  private final IConditionalSlot     _conditionalSlot;

  private final ISlot                _mismatchedSlot;

  private final Object               _variableDefinition;

  private boolean                    _requiredVariable;

  /**
   * call for when the container doesn't actually have the slot
   * 
   * @param container
   * @param cSlot
   */
  public SlotMatchFailure(IUniqueSlotContainer container, IConditionalSlot cSlot)
  {
    this(container, cSlot, null);
    _requiredVariable = false;
  }

  /**
   * failure when the mismatchedSlot in the container does not meet the
   * condition
   * 
   * @param container
   * @param cSlot
   * @param mismatchedSlot
   * @param variableDefinition
   *          the slot that bound the variable value (if any) or possibly the
   *          buffer
   */
  public SlotMatchFailure(ICondition condition, IUniqueSlotContainer container,
      IConditionalSlot cSlot, ISlot mismatchedSlot, Object variableDefinition)
  {
    super(condition);
    _conditionalSlot = cSlot;
    _container = container;
    _mismatchedSlot = mismatchedSlot;
    _variableDefinition = variableDefinition;
    _requiredVariable = true;
  }

  public SlotMatchFailure(IUniqueSlotContainer container,
      IConditionalSlot cSlot, ISlot mismatchedSlot)
  {
    this(null, container, cSlot, mismatchedSlot, null);
    _requiredVariable = false;
  }

  public IUniqueSlotContainer getSlotContainer()
  {
    return _container;
  }

  public IConditionalSlot getConditionalSlot()
  {
    return _conditionalSlot;
  }

  public ISlot getMismatchedSlot()
  {
    return _mismatchedSlot;
  }

  public boolean involvedVariableValue()
  {
    return _requiredVariable;
  }

  public Object getVariableDefinition()
  {
    return _variableDefinition;
  }

  @Override
  public String toString()
  {
    if (_mismatchedSlot == null)
      return String.format(NO_SLOT_IN_CONTAINER_MESSAGE,
          _conditionalSlot.getName(), _container);
    return String
        .format(MISMATCHED_CONDITION_MESSAGE, _container,
            _mismatchedSlot.getName(), _mismatchedSlot.getValue(),
            _conditionalSlot);
  }
}
