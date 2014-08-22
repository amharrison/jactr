package org.jactr.tools.misc;

/*
 * default logging
 */
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;

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
   * convenience interface for manipulative a chunk safely
   * 
   * @author harrison
   */
  @FunctionalInterface
  public interface IChunkModifier
  {
    public void modify(IChunk chunk, IActivationBuffer buffer);
  }
  
  static private class ModifyRunnable implements Runnable
  {
    private final IActivationBuffer _buffer;
    private final IChunkModifier _modifier;
    
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
