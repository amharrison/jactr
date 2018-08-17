package org.jactr.fluent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;

/**
 * Fluent chunktype builder. Entry point {@link #from(IChunkType)} or
 * {@link #from(IModel)}, requires a name, and maybe some slots. Terminal
 * operations {@link #build()} {@link #encode()}
 * 
 * @author harrison
 */
public class FluentChunkType
{

  private IModel                     _model;

  private Collection<ISlot>          _slots   = new ArrayList<>();

  private String                     _name;

  private Collection<IChunkType>     _parents = new ArrayList<>();

  private FluentChunkType(IModel model)
  {
    _model = model;
  }

  static public FluentChunkType from(IModel model)
  {
    return new FluentChunkType(model);
  }

  static public FluentChunkType from(IChunkType parent)
  {
    FluentChunkType builder = new FluentChunkType(parent.getModel());
    builder.childOf(parent);
    return builder;
  }

  public FluentChunkType named(String name)
  {
    _name = name;
    return this;
  }

  public FluentChunkType childOf(IChunkType chunkType)
  {
    _parents.add(chunkType);
    return this;
  }

  public FluentChunkType slot(String slotName, Object slotValue)
  {
    _slots.add(new BasicSlot(slotName, slotValue));
    return this;
  }

  public FluentChunkType slot(String slotName)
  {
    _slots.add(new BasicSlot(slotName, null));
    return this;
  }

  public FluentChunkType slots(String... slotNames)
  {
    for (String slotName : slotNames)
      slot(slotName);
    return this;
  }

  public IChunkType build()
  {
    try
    {
      IChunkType ct = _model.getDeclarativeModule()
          .createChunkType(_parents, _name).get();
      _slots.forEach(s -> {
        ct.getSymbolicChunkType().addSlot(s);
      });
      _name = null;
      _slots.clear();
      return ct;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  public IChunkType encode()
  {
    try
    {
      return _model.getDeclarativeModule().addChunkType(build()).get();
    }
    catch (InterruptedException | ExecutionException e)
    {
      throw new RuntimeException(e);
    }
  }
}
