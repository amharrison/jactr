package org.jactr.fluent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
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

  /**
   * Entry point, all chunktypes are tied to a model
   * 
   * @param model
   * @return
   */
  static public FluentChunkType from(IModel model)
  {
    return new FluentChunkType(model);
  }

  /**
   * entry point for a chunktype derived from another
   * 
   * @param parent
   * @return
   */
  static public FluentChunkType from(IChunkType parent)
  {
    FluentChunkType builder = new FluentChunkType(parent.getModel());
    builder.childOf(parent);
    return builder;
  }

  /**
   * builk creation of chunktypes. Bulk calling of
   * {@link #named(String)}.{@link #encode()}
   * 
   * @param typeNames
   * @return
   */
  public Map<String, IChunkType> types(String... typeNames)
  {
    Map<String, IChunkType> rtn = new TreeMap<>();
    for (String typeName : typeNames)
      rtn.put(typeName, named(typeName).encode());
    return rtn;
  }

  /**
   * Specify the symbolic name of the chunktype
   * 
   * @param name
   * @return
   */
  public FluentChunkType named(String name)
  {
    _name = name;
    return this;
  }

  /**
   * add a parent to this chunktype. Can have multiple parents
   * 
   * @param chunkType
   * @return
   */
  public FluentChunkType childOf(IChunkType chunkType)
  {
    _parents.add(chunkType);
    return this;
  }

  /**
   * add a default slot to the chunktype
   * 
   * @param slotName
   * @param slotValue
   * @return
   */
  public FluentChunkType slot(String slotName, Object slotValue)
  {
    _slots.add(new BasicSlot(slotName, slotValue));
    return this;
  }

  /**
   * add empty slot
   * 
   * @param slotName
   * @return
   */
  public FluentChunkType slot(String slotName)
  {
    _slots.add(new BasicSlot(slotName, null));
    return this;
  }

  /**
   * bulk creation of empty slots
   * 
   * @param slotNames
   * @return
   */
  public FluentChunkType slots(String... slotNames)
  {
    for (String slotName : slotNames)
      slot(slotName);
    return this;
  }

  /**
   * build but do not encode the chunktype
   * 
   * @return
   */
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
      // retain slots for possible repeated structure
      // _slots.clear();
      return ct;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * build and encode
   * 
   * @return
   */
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
