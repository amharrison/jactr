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
 * Conditional slot builder with a generic return on terminal operation.
 * Terminal operations are {@link #eq(Object)}, {@link #not(Object)},
 * {@link #lt(Object)}, {@link #lte(Object)}, {@link #gt(Object)},
 * {@link #gte(Object)}. slot(test).eq(1)
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

  /**
   * specify the name of the slot, a terminal action is required.
   * 
   * @param name
   * @return
   */
  public ConditionalSlotBuilder<R> slot(String name)
  {
    _currentSlotName = name;
    return this;
  }

  /**
   * short cut for slot(name).eq(object)
   * 
   * @param slotName
   * @param slotValue
   * @return nested parent builder
   */
  public R slot(String slotName, Object slotValue)
  {
    _currentSlotName = slotName;
    _currentCondition = IConditionalSlot.EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  /**
   * equals slotValue terminal operation
   * 
   * @param slotValue
   * @return nested parent builder
   */
  public R eq(Object slotValue)
  {
    _currentCondition = IConditionalSlot.EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  /**
   * not slotValue terminal operation
   * 
   * @param slotValue
   * @return nested parent builder
   */
  public R not(Object slotValue)
  {
    _currentCondition = IConditionalSlot.NOT_EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  /**
   * less than
   * 
   * @param slotValue
   * @return
   */
  public R lt(Object slotValue)
  {
    _currentCondition = IConditionalSlot.LESS_THAN;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  /**
   * less than equals
   * 
   * @param slotValue
   * @return
   */
  public R lte(Object slotValue)
  {
    _currentCondition = IConditionalSlot.LESS_THAN_EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  /**
   * greater than
   * 
   * @param slotValue
   * @return
   */
  public R gt(Object slotValue)
  {
    _currentCondition = IConditionalSlot.GREATER_THAN;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

  /**
   * reater than or equals
   * 
   * @param slotValue
   * @return
   */
  public R gte(Object slotValue)
  {
    _currentCondition = IConditionalSlot.GREATER_THAN_EQUALS;
    newSlot(_currentSlotName, _currentCondition, slotValue);
    return returnBuilder();
  }

}
