package org.jactr.fluent;

import java.util.function.Consumer;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.IAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.scripting.IScriptableFactory;
import org.jactr.scripting.ScriptingManager;
import org.jactr.scripting.action.ScriptableAction;

/**
 * Fluent builder for common actions.
 * 
 * @author harrison
 */
public class FluentAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(FluentAction.class);

  private ISlotContainer             _slotContainer;

  private IAction                    _action;

  static public FluentAction add(String bufferName, IChunkType chunkType)
  {
    FluentAction ab = new FluentAction();
    ab._action = new AddAction(bufferName, chunkType);
    ab._slotContainer = (ISlotContainer) ab._action;
    return ab;
  }

  static public FluentAction add(String bufferName, IChunk chunk)
  {
    FluentAction ab = new FluentAction();
    ab._action = new AddAction(bufferName, chunk);
    ab._slotContainer = (ISlotContainer) ab._action;
    return ab;
  }

  static public FluentAction add(String bufferName, String variableName)
  {
    FluentAction ab = new FluentAction();
    ab._action = new AddAction(bufferName, variableName);
    ab._slotContainer = (ISlotContainer) ab._action;
    return ab;
  }

  static public FluentAction modify(String bufferName)
  {
    FluentAction ab = new FluentAction();
    ab._action = new ModifyAction(bufferName);
    ab._slotContainer = (ISlotContainer) ab._action;
    return ab;
  }

  static public FluentAction remove(String bufferName)
  {
    FluentAction ab = new FluentAction();
    ab._action = new RemoveAction(bufferName);
    ab._slotContainer = (ISlotContainer) ab._action;
    return ab;
  }

  static public FluentAction output(String message)
  {
    FluentAction ab = new FluentAction();
    ab._action = new OutputAction(message);
    ab._slotContainer = null;
    return ab;
  }

  static IAction script(String language, String script) throws Exception
  {
    IScriptableFactory factory = ScriptingManager.getFactory(language);
    return new ScriptableAction(factory.createActionScript(script));
  }

  public FluentAction slot(String slotName, Object slotValue)
  {
    SlotBuilder sb = new SlotBuilder(this, _slotContainer::addSlot);
    sb.slot(slotName).eq(slotValue);

    return this;
  }

  public SlotBuilder slot(String slotName)
  {
    SlotBuilder sb = new SlotBuilder(this, _slotContainer::addSlot);
    sb.slot(slotName);

    return sb;
  }

  /**
   * needs to be called by terminal action
   * 
   * @return
   */
  public IAction build()
  {
    return _action;
  }

  static public class SlotBuilder
      extends org.jactr.fluent.ConditionalSlotBuilder<FluentAction>
  {
    public SlotBuilder(FluentAction returnBack, Consumer<ISlot> slotConsumer)
    {
      super(returnBack, slotConsumer);
    }

  }
}
