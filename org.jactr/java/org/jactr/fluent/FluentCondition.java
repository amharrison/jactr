package org.jactr.fluent;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.function.Consumer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.condition.AbstractSlotCondition;
import org.jactr.core.production.condition.ChunkCondition;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.production.condition.VariableCondition;
import org.jactr.core.slot.DefaultLogicalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptingManager;
import org.jactr.scripting.condition.ScriptableCondition;

/**
 * Fluent builder for common conditions: match, query, and scriptable. Supports
 * chaining addition conditional slots via {@link #slot(String)} and
 * {@link #slot(String, Object)}. Boolean logical operators are also permitted
 * on slots.
 * 
 * @author harrison
 */
public class FluentCondition
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
      .getLog(FluentCondition.class);

  private Deque<ISlot>               _addedSlots       = new ArrayDeque<>();

  private Deque<Integer>             _logicalOperators = new ArrayDeque<>();

  private AbstractSlotCondition      _slotBasedCondition;

  /**
   * entry point for queries. ":slotName" short cuts are not permitted in
   * matches, requiring explicit queries to be constructed.
   * 
   * @param bufferName
   * @return
   */
  static public SlotBuilder query(String bufferName)
  {
    FluentCondition cb = new FluentCondition();
    cb._slotBasedCondition = new QueryCondition(bufferName);
    return new SlotBuilder(cb, cb::addSlot);
  }

  /**
   * is the content of bufferName a chunkType
   * 
   * @param bufferName
   * @param chunkType
   * @return
   */
  static public SlotBuilder match(String bufferName, IChunkType chunkType)
  {
    FluentCondition cb = new FluentCondition();
    cb._slotBasedCondition = new ChunkTypeCondition(bufferName, chunkType);
    return new SlotBuilder(cb, cb::addSlot);
  }

  /**
   * is chunk in bufferName
   * 
   * @param bufferName
   * @param chunk
   * @return
   */
  static public SlotBuilder match(String bufferName, IChunk chunk)
  {
    FluentCondition cb = new FluentCondition();
    cb._slotBasedCondition = new ChunkCondition(bufferName, chunk);
    return new SlotBuilder(cb, cb::addSlot);
  }

  /**
   * is variableName in bufferName
   * 
   * @param bufferName
   * @param variableName
   * @return
   */
  static public SlotBuilder match(String bufferName, String variableName)
  {
    FluentCondition cb = new FluentCondition();
    cb._slotBasedCondition = new VariableCondition(bufferName, variableName);
    return new SlotBuilder(cb, cb::addSlot);
  }

  /**
   * create a scriptable condition
   * 
   * @param language
   * @param script
   * @return
   * @throws Exception
   */
  static public ICondition script(String language, String script)
      throws Exception
  {
    IScriptableFactory factory = ScriptingManager.getFactory(language);
    return new ScriptableCondition(factory.createConditionScript(script));
  }

  private void notComplete()
  {
    ISlot slotToNot = _addedSlots.pop();
    _logicalOperators.pop();
    addSlot(new DefaultLogicalSlot(ILogicalSlot.NOT,
        Collections.singleton(slotToNot)));
  }

  private void andComplete()
  {
    _logicalOperators.pop();
    ISlot arg2 = _addedSlots.pop();
    ISlot arg1 = _addedSlots.pop();
    try
    {
      addSlot(new DefaultLogicalSlot(ILogicalSlot.AND, arg1, arg2));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  private void orComplete()
  {
    _logicalOperators.pop();
    ISlot arg2 = _addedSlots.pop();
    ISlot arg1 = _addedSlots.pop();
    try
    {
      addSlot(new DefaultLogicalSlot(ILogicalSlot.OR, arg1, arg2));
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * internal callback for tracking slots
   * 
   * @param slot
   */
  private void addSlot(ISlot slot)
  {
    _addedSlots.push(slot);

    if (!_logicalOperators.isEmpty())
      switch (_logicalOperators.peek())
      {
        case ILogicalSlot.NOT:
          notComplete();
          break;
        case ILogicalSlot.AND:
          andComplete();
          break;
        case ILogicalSlot.OR:
          orComplete();
          break;
      }
    else
      // easy mode, no logical slots
      _slotBasedCondition.addSlot(slot);
  }

  /**
   * logical AND of the prior and next slot defined via {@link #slot(String)} or
   * {@link #slot(String, Object)}
   * 
   * @return
   */
  public SlotBuilder and()
  {
    _logicalOperators.push(ILogicalSlot.AND);

    return new SlotBuilder(this, this::addSlot);
  }

  /**
   * logical OR of the prior and next slot defined via {@link #slot(String)} or
   * {@link #slot(String, Object)}
   */
  public SlotBuilder or()
  {
    _logicalOperators.push(ILogicalSlot.OR);

    return new SlotBuilder(this, this::addSlot);
  }

  /**
   * logical NOT of the next slot defined via {@link #slot(String)} or
   * {@link #slot(String, Object)}
   * 
   * @return
   */
  public SlotBuilder not()
  {
    _logicalOperators.push(ILogicalSlot.NOT);

    return new SlotBuilder(this, this::addSlot);
  }

  /**
   * add an equality slot
   * 
   * @param slotName
   * @param slotValue
   * @return
   */
  public FluentCondition slot(String slotName, Object slotValue)
  {
    SlotBuilder sb = new SlotBuilder(this, this::addSlot);
    sb.slot(slotName).eq(slotValue);

    return this;
  }

  /**
   * add a conditional slot that will be further refined by the returned
   * {@link SlotBuilder}
   * 
   * @param slotName
   * @return
   */
  public SlotBuilder slot(String slotName)
  {
    SlotBuilder sb = new SlotBuilder(this, this::addSlot);
    sb.slot(slotName);

    return sb;
  }

  /**
   * terminal operation to build the slot
   * 
   * @return
   */
  public ICondition build()
  {
    return _slotBasedCondition;
  }

  static public class SlotBuilder
      extends org.jactr.fluent.ConditionalSlotBuilder<FluentCondition>
  {

    public SlotBuilder(FluentCondition returnBack, Consumer<ISlot> consumer)
    {
      super(returnBack, consumer);
    }

  }

}
