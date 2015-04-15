package org.jactr.tools.misc;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ModelCycleExecutor;
import org.jactr.core.concurrent.ModelCycleExecutor.When;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.IMutableSlot;

/**
 * safe way to manipulate the source chunk of a buffer. This will be executed
 * via {@link ExecutionUtilities} so that the changes occur on the model thread
 * either before or after the normal production cycle sequence.
 * 
 * @author harrison
 */
public class ChunkUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkUtilities.class);

  /**
   * manipulate using
   * {@link ExecutionUtilities#executeNow(org.jactr.core.model.IModel, Runnable)}
   * 
   * @param buffer
   * @param modifier
   * @return
   */
  static public Future<Boolean> manipulateChunkNow(IActivationBuffer buffer,
      final IChunkModifier modifier)
  {
    return ExecutionUtilities.executeNow(buffer.getModel(), new ModifyRunnable(
        buffer, modifier));
  }

  /**
   * modify using
   * {@link ExecutionUtilities#executeLater(org.jactr.core.model.IModel, Runnable)}
   * 
   * @param buffer
   * @param modifier
   * @return
   */
  static public Future<Boolean> manipulateChunkLater(IActivationBuffer buffer,
      IChunkModifier modifier)
  {
    return ExecutionUtilities.executeLater(buffer.getModel(),
        new ModifyRunnable(buffer, modifier));
  }

  /**
   * Standard search and returned named, if found, otherwise create (and
   * possibly encode). This is not atomic, merely streamlined.
   * 
   * @param chunkName
   * @param model
   * @param type
   * @param modifier
   * @param encode
   * @return
   */
  static public CompletableFuture<IChunk> getOrCreate(String chunkName,
      IModel model, IChunkType type, IChunkModifier modifier, boolean encode)
  {
    final IDeclarativeModule decM = model.getDeclarativeModule();
    CompletableFuture<IChunk> find = decM.getChunk(chunkName);
    CompletableFuture<IChunk> create = find.thenCompose((c) -> {
      if (c == null)
        return decM.createChunk(type, chunkName);
      else
        return CompletableFuture.completedFuture(c);
    });

    CompletableFuture<IChunk> modify = create.thenApply((c) -> {
      if (!c.isEncoded() && modifier != null) modifier.modify(c, null);
      return c;
    });

    CompletableFuture<IChunk> encoder = modify.thenCompose((c) -> {
      if (encode && !c.isEncoded())
        return decM.addChunk(c);
      else
        return CompletableFuture.completedFuture(c);
    });

    return encoder;
  }

  /**
   * Functionally a single call to create and set the slot values of a chunk.
   * Merely attach a callback to process when complete, or (and this is not
   * recommended) call get(). <br/>
   * <br/>
   * <code>
   *  ChunkUtilities.createAndConfigure(type, name, slots).thenAccept((c)->ChunkUtilities.addToBuffer(c,buffer));
   * </code>
   * 
   * @param type
   * @param nameTemplate
   * @param slotValues
   * @return
   */
  static public CompletableFuture<IChunk> createAndConfigure(IChunkType type,
      String nameTemplate, Map<String, Object> slotValues)
  {
    return createAndConfigure(type, nameTemplate, new MapChunkModifier(
        slotValues));
  }

  static public CompletableFuture<IChunk> createAndConfigure(IChunkType type,
      String nameTemplate, final IChunkModifier modifier)
  {
    final IDeclarativeModule decM = type.getModel().getDeclarativeModule();
    /*
     * first, we create the chunk
     */
    CompletableFuture<IChunk> creation = decM.createChunk(type, nameTemplate);
    /*
     * then, we immediately set the slot values
     */
    CompletableFuture<IChunk> modified = creation
        .thenApply((c) -> configureChunk(c, modifier));

    return modified;
  }

  static public CompletableFuture<IChunk> encode(IChunk chunk)
  {
    if (chunk.isEncoded())
      return CompletableFuture.completedFuture(chunk);
    else
    {
      IDeclarativeModule decM = chunk.getModel().getDeclarativeModule();
      return decM.addChunk(chunk);
    }
  }

  /**
   * Create configure and encode.
   * 
   * @param type
   * @param nameTemplate
   * @param slotValues
   * @return
   */
  static public CompletableFuture<IChunk> createConfigureAndEncode(
      final IChunkType type, String nameTemplate, Map<String, Object> slotValues)
  {
    return createConfigureAndEncode(type, nameTemplate, new MapChunkModifier(
        slotValues));
  }

  static public CompletableFuture<IChunk> createConfigureAndEncode(
      final IChunkType type, String nameTemplate, IChunkModifier modifier)
  {
    /*
     * create and configure
     */
    CompletableFuture<IChunk> modified = createAndConfigure(type, nameTemplate,
        modifier);
    /*
     * then immediate add it
     */
    CompletableFuture<IChunk> encode = modified.thenCompose((c) -> {
      return encode(c);
    });

    return encode;
  }

  /**
   * @param chunk
   * @param buffer
   * @return
   */
  static public CompletableFuture<IChunk> addToBuffer(final IChunk chunk,
      final IActivationBuffer buffer)
  {
    CompletableFuture<IChunk> added = CompletableFuture.supplyAsync(() -> {
      return buffer.addSourceChunk(chunk);
    }, new ModelCycleExecutor(chunk.getModel(), When.ASAP));
    return added;
  }

  static public CompletableFuture<Void> removeFromBuffer(final IChunk chunk,
      final IActivationBuffer buffer)
  {
    CompletableFuture<Void> removed = CompletableFuture.runAsync(
        () -> {
          if (!buffer.getSourceChunks().contains(chunk))
            throw new IllegalStateException(String.format(
                "%s is not currently in %s",
                chunk.getSymbolicChunk().getName(), buffer.getName()));
          buffer.removeSourceChunk(chunk);
        }, new ModelCycleExecutor(chunk.getModel(), When.ASAP));
    return removed;
  }

  static public CompletableFuture<Void> modifyInBuffer(final IChunk chunk,
      final IActivationBuffer buffer, final IChunkModifier modifier)
  {
    CompletableFuture<Void> modified = CompletableFuture.runAsync(
        () -> {
          if (!buffer.getSourceChunks().contains(chunk))
            throw new IllegalStateException(String.format(
                "%s is not currently in %s",
                chunk.getSymbolicChunk().getName(), buffer.getName()));

          if (chunk.isEncoded() && !chunk.isMutable())
            throw new IllegalStateException(String.format(
                "Chunk %s is encoded and not mutable, cannot modify.", chunk
                    .getSymbolicChunk().getName()));

          configureChunk(chunk, modifier);

        }, new ModelCycleExecutor(chunk.getModel(), When.ASAP));
    return modified;
  }

  static private IChunk configureChunk(IChunk chunk, IChunkModifier modifier)
  {

    Lock l = chunk.getWriteLock();
    try
    {
      l.lock();
      modifier.modify(chunk, null);
    }
    finally
    {
      l.unlock();
    }
    return chunk;
  }

  /**
   * convenience interface for manipulative a chunk safely
   * 
   * @author harrison
   */
  @FunctionalInterface
  public interface IChunkModifier
  {
    public void modify(IChunk chunk, IActivationBuffer buffer);
  }

  static private class MapChunkModifier implements IChunkModifier
  {
    private TreeMap<String, Object> _slots;

    public MapChunkModifier(Map<String, Object> slots)
    {
      _slots = new TreeMap<String, Object>(slots);
    }

    @Override
    public void modify(IChunk chunk, IActivationBuffer buffer)
    {
      ISymbolicChunk sc = chunk.getSymbolicChunk();
      // for (Map.Entry<String, Object> slot : _slots.entrySet())
      // try
      // {
      // IMutableSlot mSlot = (IMutableSlot) sc.getSlot(slot.getKey());
      // mSlot.setValue(slot.getValue());
      // }
      // catch (Exception e)
      // {
      // LOGGER.error(
      // String.format("Failed to set %s.%s = %s", sc.getName(),
      // slot.getKey(), slot.getValue()), e);
      // }

      _slots
          .forEach((k, v) -> {
            try
            {
              IMutableSlot mSlot = (IMutableSlot) sc.getSlot(k);
              mSlot.setValue(v);
            }
            catch (Exception e)
            {
              LOGGER.error(
                  String.format("Failed to set %s.%s = %s", sc.getName(), k, v),
                  e);
            }
          });
    }

  }

  static private class ModifyRunnable implements Runnable
  {
    private final IActivationBuffer _buffer;

    private final IChunkModifier    _modifier;

    public ModifyRunnable(IActivationBuffer buffer, IChunkModifier modifier)
    {
      _buffer = buffer;
      _modifier = modifier;
    }

    public void run()
    {
      IChunk chunk = _buffer.getSourceChunk();

      if (chunk == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format(
              "No chunk available in %s, ignoring request", _buffer));
        return;
      }

      if (chunk.isEncoded() && !chunk.isMutable())
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format(
              "%s is encoded and not immutable, cannot modify", chunk));
        return;
      }

      /*
       * now write lock and do our thing
       */
      try
      {
        chunk.getWriteLock().lock();

        _modifier.modify(chunk, _buffer);
      }
      finally
      {
        chunk.getWriteLock().unlock();
      }

    }
  }
}
