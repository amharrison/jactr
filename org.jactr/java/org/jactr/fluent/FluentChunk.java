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
 * Bulk creation of chunks is possible with {@link #chunks(String...)}
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

  private IChunk                     _source;

  private IChunkType                 _parent;

  private String                     _name;

  private Collection<ISlot>          _slots = new ArrayList<>();

  /**
   * Entry point, all chunks must be derived from a chunktype
   * 
   * @param chunkType
   * @return builder
   */
  static public FluentChunk from(IChunkType chunkType)
  {
    return new FluentChunk(chunkType);
  }

  /**
   * when a chunk needs further modification after build
   * 
   * @param chunk
   * @return
   * @throws IllegalArgumentException
   *           if the chunk is already encoded
   */
  static public FluentChunk from(IChunk chunk)
  {
    if (chunk.isEncoded())
      throw new IllegalArgumentException("chunk is already encoded");

    return new FluentChunk(chunk);
  }

  private FluentChunk(IChunkType chunkType)
  {
    _parent = chunkType;
  }

  private FluentChunk(IChunk chunk)
  {
    _source = chunk;
  }

  /**
   * specify the symbolic name of the chunk
   * 
   * @param name
   * @return
   */
  public FluentChunk named(String name)
  {
    if (_source != null) throw new IllegalStateException(
        "Cannot call from builder configured to modify an existing chunk");
    _name = name;
    return this;
  }

  /**
   * Bulk encoding of chunks {@link #from(IChunkType)}. implicit
   * {@link #encodeIfAbsent()}
   * 
   * @param chunkNames
   * @return map of chunks keyed on name
   */
  public Map<String, IChunk> chunks(String... chunkNames)
  {
    if (_source != null) throw new IllegalStateException(
        "Cannot call from builder configured to modify an existing chunk");
    Map<String, IChunk> rtn = new TreeMap<>();
    for (String chunkName : chunkNames)
      rtn.put(chunkName, named(chunkName).encodeIfAbsent());
    return rtn;
  }

  /**
   * add a slot to the chunk
   * 
   * @param slotName
   * @param slotValue
   * @return
   */
  public FluentChunk slot(String slotName, Object slotValue)
  {
    _slots.add(new BasicSlot(slotName, slotValue));
    return this;
  }

  /**
   * add an empty slot to the chunk
   * 
   * @param slotName
   * @return
   */
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
      IChunk chunk = _source;
      if (chunk == null) chunk = _parent.getModel().getDeclarativeModule()
          .createChunk(_parent, _name).get();

      final IChunk fChunk = chunk;
      _slots.forEach(s -> {
        fChunk.getSymbolicChunk().addSlot(s);
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
