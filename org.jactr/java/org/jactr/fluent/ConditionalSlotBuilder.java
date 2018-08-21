package org.jactr.fluent;

import java.util.function.Consumer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;

/**
 * slot(test).eq(1).and().slot(test2).not(1).build()
 * 
 * @author harrison
 */
public class ConditionalSlotBuilder<R>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
      .getLog(ConditionalSlotBuilder.class);

  private String                     _currentSlotName;

  private int                        _currentCondition = IConditionalSlot.EQUALS;

  private IConditionalSlot           _currentSlot      = null;

  private R                          _returnBack;

  private Consumer<ISlot>            _slotConsumer;

  public ConditionalSlotBuilder(R returnBack, Consumer<ISlot> slotConsumer)
  {
    _returnBack = returnBack;
    _slotConsumer = slotConsumer;
    if (returnBack == null) _returnBack = (R) this;
  }

  protected ISlot newSlot(String slotName, int slotCondition, Object slotValue)
  {
    _currentSlot = new DefaultConditionalSlot(slotName, slotCondition,
        slotValue);

    if (_slotConsumer != null) _slotConsumer.accept(_currentSlot);

    return _currentSlot;
  }

  protected R returnBuilder()
  {
    return _returnBack;
  }

  public ConditionalSlotBuilder<R> slot(String name)
  {
    _currentSlotName = name;
    return this;
  }

  public R slot(String slotName, Object slotValue)
  {
    _currentSlotName = slotName;
    _currentCondition = IConditionalSlot.EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  public R eq(Object slotValue)
  {
    _currentCondition = IConditionalSlot.EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  public R not(Object slotValue)
  {
    _currentCondition = IConditionalSlot.NOT_EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  public R lt(Object slotValue)
  {
    _currentCondition = IConditionalSlot.LESS_THAN;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  public R lte(Object slotValue)
  {
    _currentCondition = IConditionalSlot.LESS_THAN_EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  public R gt(Object slotValue)
  {
    _currentCondition = IConditionalSlot.GREATER_THAN;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  public R gte(Object slotValue)
  {
    _currentCondition = IConditionalSlot.GREATER_THAN_EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

}
