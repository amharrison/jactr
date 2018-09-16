package org.jactr.core.slot;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility for building slots quickly with a builder pattern. Start with the
 * static accessor for the type of slot you need {@link #slot(String)},
 * {@link #conditionalSlot(String)}, {@link #mutableSlot(String)},
 * {@link #logicalSlot()}), then provide the conditional portion (
 * {@link #eq(Object)}, {@link #gt(Object)}, {@link #gte(Object)},
 * {@link #lt(Object)}, {@link #lte(Object)}, {@link #notEq(Object)} for
 * conditionals, {@link #eq(Object)} for basic slot, {@link #and(ISlot, ISlot)},
 * {@link #or(ISlot, ISlot)}, {@link #not(ISlot)} for logical) and invoke
 * {@link #build()}. <br/>
 * There are no safe guards to verify that incorrect conditionals are not called
 * for the particular flavor of slot being generated. <br/>
 * builders for a given type can be recycled to generate more of that type
 * without generating additional garbage by calling {@link #reset()}. But be
 * sure to call {@link #slotName(String)} before use.<br/>
 * <code>
 *  SlotBuilder<ISlot> sBuilder = SlotBuilder.slot("s1");
 *  ISlot slot1 = sBuilder.eq(value1).build();
 *  ISlot slot2 = sBuilder.reset().slotName("s2").eq(value2).build();
 * </code> <br/>
 * But if you are attentive and aware of the internal states, the builder can be
 * reused. <br/>
 * <code>
 *  SlotBuilder<IConditionalSlot> sBuilder = SlotBuilder.conditionalSlot("when");
 *  IConditionalSlot past = sBuilder.lte(referenceTime).build();
 *  IConditionalSlot future = sBuilder.gte(referenceTime).build();
 *  //both have the same slot name and reference times
 * </code>
 * 
 * @author harrison
 * @param <T>
 */
public class SlotBuilder<T>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(SlotBuilder.class);

  private String                     _slotName;

  private int                        _condition = IConditionalSlot.EQUALS;     // IConditionalSlot,
                                                                                // or
                                                                                // ILogicalSlot

  private Object                     _value     = null;

  private List<ISlot>                _children;

  private SlotBuilder(String slotName)
  {
    _slotName = slotName;
  }

  protected String getSlotName()
  {
    return _slotName;
  }

  protected int getCondition()
  {
    return _condition;
  }

  protected Object getValue()
  {
    return _value;
  }

  protected List<ISlot> getChildren()
  {
    return _children;
  }

  /**
   * reset the builder.
   * 
   * @param slotName
   * @return
   */
  public SlotBuilder<T> reset()
  {
    _children = null;
    _value = null;
    _slotName = null;
    _condition = IConditionalSlot.EQUALS;
    return this;
  }

  /**
   * start builder chain for conditional slot <code>
   *  IConditionalSlot slot = SlotBuilder.conditionalSlot("slot1").lte(100.0).build();
   * </code>
   * 
   * @return
   */
  static public SlotBuilder<IConditionalSlot> conditionalSlot(String slotName)
  {
    return new SlotBuilder<IConditionalSlot>(slotName) {
      @Override
      public IConditionalSlot build()
      {
        return new DefaultConditionalSlot(getSlotName(), getCondition(),
            getValue());
      }
    };
  }

  /**
   * start builder chain for logical slot <code>
   *  SlotBuilder<ILogicalSlot> sBuilder = SlotBuilder.logicalSlot();
   *  ILogicalSlot notSlot = sBuilder.reset(null).not(oSlot).build();
   *  ILogicalSlot andSlot = sBuilder.reset(null).and(slot1, slot2).build();
   *  ILogicalSlot orSlotNested = sBuilder.reset(null).or(notSlot, andSlot).build();
   * </code>
   * 
   * @return
   */
  static public SlotBuilder<ILogicalSlot> logicalSlot()
  {
    return new SlotBuilder<ILogicalSlot>(":logical") {
      @Override
      public ILogicalSlot build()
      {
        return new DefaultLogicalSlot(getCondition(), getChildren());
      }
    };
  }

  /**
   * start builder chain for mutable slot <code>
   *  IMutableSlot slot = SlotBuilder.mutableSlot("slot1").eq(value).build();
   * </code>
   * 
   * @return
   */
  static public SlotBuilder<IMutableSlot> mutableSlot(String slotName)
  {
    return new SlotBuilder<IMutableSlot>(slotName) {
      @Override
      public IMutableSlot build()
      {
        return new DefaultMutableSlot(getSlotName(), getValue());
      }
    };
  }

  /**
   * start builder chain for general equality slot <code>
   *  ISlot slot = SlotBuilder.slot("slot1").eq(value).build();
   * </code>
   * 
   * @return
   */
  static public SlotBuilder<ISlot> slot(String slotName)
  {
    return new SlotBuilder<ISlot>(slotName) {
      @Override
      public ISlot build()
      {
        return new BasicSlot(getSlotName(), getValue());
      }
    };
  }

  public T build()
  {
    return null;
  }

  /**
   * specify slotName
   * 
   * @param slotName
   * @return
   */
  public SlotBuilder<T> slotName(String slotName)
  {
    _slotName = slotName;
    return this;
  }

  private SlotBuilder<T> conditionAndValue(int condition, Object value)
  {
    _condition = condition;
    _value = value;
    return this;
  }

  /**
   * Conditional equality to value. <br/>
   * for conditional, mutable, or simple slots.
   * 
   * @param value
   * @return
   */
  public SlotBuilder<T> eq(Object value)
  {
    return conditionAndValue(IConditionalSlot.EQUALS, value);
  }

  /**
   * Conditional greater than value. <br/>
   * for conditional slots.
   * 
   * @param value
   * @return
   */
  public SlotBuilder<T> gt(Object value)
  {
    return conditionAndValue(IConditionalSlot.GREATER_THAN, value);
  }

  /**
   * Conditional greater than/equals value. <br/>
   * for conditional slots.
   * 
   * @param value
   * @return
   */
  public SlotBuilder<T> gte(Object value)
  {
    return conditionAndValue(IConditionalSlot.GREATER_THAN_EQUALS, value);
  }

  /**
   * Conditional less than value. <br/>
   * for conditional slots.
   * 
   * @param value
   * @return
   */
  public SlotBuilder<T> lt(Object value)
  {
    return conditionAndValue(IConditionalSlot.LESS_THAN, value);
  }

  /**
   * Conditional less than/equals value. <br/>
   * for conditional slots.
   * 
   * @param value
   * @return
   */
  public SlotBuilder<T> lte(Object value)
  {
    return conditionAndValue(IConditionalSlot.LESS_THAN_EQUALS, value);
  }

  /**
   * not equals (but not logical negation {@link #not(ISlot)} <br/>
   * for conditional slots.
   * 
   * @param value
   * @return
   */
  public SlotBuilder<T> notEq(Object value)
  {
    return conditionAndValue(IConditionalSlot.NOT_EQUALS, value);
  }

  private SlotBuilder<T> logicalAndValue(int condition, ISlot... slots)
  {
    _condition = condition;
    _children = Arrays.asList(slots);
    return this;
  }

  /**
   * logical not
   * 
   * @param slot
   * @return
   */
  public SlotBuilder<T> not(ISlot slot)
  {
    return logicalAndValue(ILogicalSlot.NOT, slot);
  }

  /**
   * logical and
   * 
   * @param slot1
   * @param slot2
   * @return
   */
  public SlotBuilder<T> and(ISlot slot1, ISlot slot2)
  {
    return logicalAndValue(ILogicalSlot.AND, slot1, slot2);
  }

  /**
   * logical or
   * 
   * @param slot1
   * @param slot2
   * @return
   */
  public SlotBuilder<T> or(ISlot slot1, ISlot slot2)
  {
    return logicalAndValue(ILogicalSlot.OR, slot1, slot2);
  }
}
