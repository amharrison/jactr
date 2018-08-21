package org.jactr.fluent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;

/**
 * fluent chunk builder. Entry point {@link #from(IChunkType)}, with terminal
 * operations {@link #build()}, {@link #encode()}, {@link #encodeIfAbsent()}.
 * 
 * @author harrison
 */
public class FluentChunk
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
      .getLog(FluentChunk.class);

  private IChunkType                 _parent;

  private String                     _name;

  private Collection<ISlot>          _slots = new ArrayList<>();

  static public FluentChunk from(IChunkType chunkType)
  {
    return new FluentChunk(chunkType);
  }

  private FluentChunk(IChunkType chunkType)
  {
    _parent = chunkType;
  }

  public FluentChunk named(String name)
  {
    _name = name;
    return this;
  }

  /**
   * implicit {@link #encodeIfAbsent()}
   * 
   * @param chunkNames
   * @return
   */
  public Map<String, IChunk> chunks(String... chunkNames)
  {
    Map<String, IChunk> rtn = new TreeMap<>();
    for (String chunkName : chunkNames)
      rtn.put(chunkName, named(chunkName).encodeIfAbsent());
    return rtn;
  }

  public FluentChunk slot(String slotName, Object slotValue)
  {
    _slots.add(new BasicSlot(slotName, slotValue));
    return this;
  }

  public FluentChunk slot(String slotName)
  {
    _slots.add(new BasicSlot(slotName, null));
    return this;
  }

  /**
   * build the chunk but donot encode it
   * 
   * @return
   */
  public IChunk build()
  {
    try
    {
      IChunk chunk = _parent.getModel().getDeclarativeModule()
          .createChunk(_parent, _name).get();
      _slots.forEach(s -> {
        chunk.getSymbolicChunk().addSlot(s);
      });
      _name = null;
      // leave the slots in case we want multiple copies with the same values
//      _slots.clear();
      return chunk;
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }

  /**
   * based on the name of the chunk only. If it exists, that chunk is returned,
   * otherwise this is encoded.
   * 
   * @return
   */
  public IChunk encodeIfAbsent()
  {
    try
    {
      Future<IChunk> existing = _parent.getModel().getDeclarativeModule()
          .getChunk(_name);
      if (existing.get() == null)
        return encode();
      else
        return existing.get();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }

  }

  /**
   * build and encode.
   * 
   * @return
   */
  public IChunk encode()
  {
    try
    {
      IChunk chunk = build();
      return _parent.getModel().getDeclarativeModule().addChunk(chunk).get();
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
