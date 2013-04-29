package org.jactr.core.module.declarative.basic;

/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;

/**
 * @author harrison
 */
public abstract class AbstractDeclarativeModule extends AbstractModule
    implements IDeclarativeModule
{
  /**
   * Logger definition
   */
  static private final transient Log                                          LOGGER = LogFactory
                                                                                         .getLog(AbstractDeclarativeModule.class);

  private IChunk                                                              _freeChunk;

  private IChunk                                                              _busyChunk;

  private IChunk                                                              _errorChunk;

  private IChunk                                                              _emptyChunk;

  private IChunk                                                              _fullChunk;

  private IChunk                                                              _newChunk;

  private IChunk                                                              _requestedChunk;

  private IChunk                                                              _unrequestedChunk;

  private ACTREventDispatcher<IDeclarativeModule, IDeclarativeModuleListener> _eventDispatcher;

  public AbstractDeclarativeModule(String name)
  {
    super(name);
    _eventDispatcher = new ACTREventDispatcher<IDeclarativeModule, IDeclarativeModuleListener>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize()
  {
  }

  public void addListener(IDeclarativeModuleListener listener, Executor executor)
  {
    _eventDispatcher.addListener(listener, executor);
  }

  public void removeListener(IDeclarativeModuleListener listener)
  {
    _eventDispatcher.removeListener(listener);
  }

  protected void fireChunkCreated(IChunk chunk)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_CREATED, chunk));
  }

  protected void fireChunkTypeCreated(IChunkType chunkType)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_TYPE_CREATED, chunkType));
  }

  protected void fireChunkAdded(IChunk chunk)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_ADDED, chunk));
  }

  protected void fireChunkTypeAdded(IChunkType chunkType)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_TYPE_ADDED, chunkType));
  }

  protected void fireChunksMerged(IChunk original, IChunk duplicateChunk)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNKS_MERGED, original, duplicateChunk));
  }

  protected void fireChunkTypesMerged(IChunkType original, IChunkType duplicate)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_TYPES_MERGED, original, duplicate));
  }

  protected boolean hasListeners()
  {
    return _eventDispatcher.hasListeners();
  }

  protected void dispatch(DeclarativeModuleEvent event)
  {
    _eventDispatcher.fire(event);
  }

  public IChunk getBusyChunk()
  {
    return _busyChunk;
  }

  public IChunk getEmptyChunk()
  {
    return _emptyChunk;
  }

  public IChunk getErrorChunk()
  {
    return _errorChunk;
  }

  public IChunk getFreeChunk()
  {
    return _freeChunk;
  }

  public IChunk getFullChunk()
  {
    return _fullChunk;
  }

  public IChunk getNewChunk()
  {
    return _newChunk;
  }

  public IChunk getRequestedChunk()
  {
    return _requestedChunk;
  }

  public IChunk getUnrequestedChunk()
  {
    return _unrequestedChunk;
  }

  /**
   * create a chunk by delegating to
   * {@link #createChunkInternal(IChunkType, String)} on {@link #getExecutor()}
   * 
   * @param parent
   * @param name
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#createChunk(org.jactr.core.chunktype.IChunkType,
   *      java.lang.String)
   */
  public Future<IChunk> createChunk(final IChunkType parent, final String name)
  {
    if (parent == null)
      throw new NullPointerException("IChunkType cannot be null");

    return delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        return createChunkInternal(parent, name);
      }

    }, getExecutor());
  }

  /**
   * create the chunk
   * 
   * @param parent
   * @param name
   * @return
   */
  abstract protected IChunk createChunkInternal(IChunkType parent, String name);

  /**
   * add chunk to DM. merely delegates to {@link #addChunkInternal(IChunk)} on
   * {@link #getExecutor()}
   * 
   * @param chunk
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#addChunk(org.jactr.core.chunk.IChunk)
   */
  public Future<IChunk> addChunk(final IChunk chunk)
  {
    if (chunk.isEncoded())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(chunk + " has already been encoded, silly");
      return immediateReturn(chunk);
    }

    return delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        try
        {
          IChunk rtn = addChunkInternal(chunk);

          String name = rtn.getSymbolicChunk().getName();
          if (_busyChunk == null && name.equals("busy"))
            _busyChunk = rtn;
          else if (_emptyChunk == null && name.equals("empty"))
            _emptyChunk = rtn;
          else if (_errorChunk == null && name.equals("error"))
            _errorChunk = rtn;
          else if (_freeChunk == null && name.equals("free"))
            _freeChunk = rtn;
          else if (_fullChunk == null && name.equals("full"))
            _fullChunk = rtn;
          else if (_newChunk == null && name.equals("new"))
            _newChunk = rtn;
          else if (_requestedChunk == null && name.equals("requested"))
            _requestedChunk = rtn;
          else if (_unrequestedChunk == null && name.equals("unrequested"))
            _unrequestedChunk = rtn;
          return rtn;
        }
        catch (Exception e)
        {
          LOGGER.error("Error while encoding chunk "+chunk+" ",e);
          throw e;
        }
      }

    }, getExecutor());
  }

  /**
   * add the chunk to DM on the module's executor. If the executor is INLINE or
   * multiply threaded, thread safety is a must.
   * 
   * @param chunkToAdd
   * @return
   */
  abstract protected IChunk addChunkInternal(IChunk chunkToAdd);

  public Future<IChunk> copyChunk(final IChunk sourceChunk)
  {
    if (sourceChunk == null)
      throw new NullPointerException("sourceChunk cannot be null");

    return delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        String name = sourceChunk.getSymbolicChunk().getName();
        IChunkType parent = sourceChunk.getSymbolicChunk().getChunkType();
        IChunk copy = createChunkInternal(parent, name);
        copyChunkInternal(sourceChunk, copy);
        return copy;
      }

    }, getExecutor());
  }

  /**
   * copy source to copy
   * 
   * @param sourceChunk
   * @param copy
   */
  abstract protected void copyChunkInternal(IChunk sourceChunk, IChunk copy);

  /**
   * delegated
   * 
   * @param name
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#getChunk(java.lang.String)
   */
  public Future<IChunk> getChunk(final String name)
  {
    Callable<IChunk> callable = new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        return getChunkInternal(name);
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  abstract protected IChunk getChunkInternal(String chunkName);

  /**
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#getChunks()
   */
  public Future<Collection<IChunk>> getChunks()
  {
    Callable<Collection<IChunk>> callable = new Callable<Collection<IChunk>>() {

      public Collection<IChunk> call() throws Exception
      {
        return getChunksInternal();
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  abstract protected Collection<IChunk> getChunksInternal();

  /**
   * create chunktype, delegates to
   * {@link #createChunkTypeInternal(IChunkType, String)} on
   * {@link #getExecutor()}
   * 
   * @param parent
   * @param name
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#createChunkType(org.jactr.core.chunktype.IChunkType,
   *      java.lang.String)
   */
  public Future<IChunkType> createChunkType(final IChunkType parent,
      final String name)
  {
    return delayedFuture(new Callable<IChunkType>() {

      public IChunkType call() throws Exception
      {
        return createChunkTypeInternal(parent, name);
      }
    }, getExecutor());
  }

  /**
   * @param parent
   * @param name
   * @return
   */
  abstract protected IChunkType createChunkTypeInternal(IChunkType parent,
      String name);

  /**
   * add chunktype to DM, delegated to {@link #addChunkTypeInternal(IChunkType)}
   * on {@link #getExecutor()}
   * 
   * @param chunkType
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#addChunkType(org.jactr.core.chunktype.IChunkType)
   */
  public Future<IChunkType> addChunkType(final IChunkType chunkType)
  {
    if (chunkType.isEncoded())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(chunkType + " has already been encoded silly");
      return immediateReturn(chunkType);
    }

    return delayedFuture(new Callable<IChunkType>() {

      public IChunkType call() throws Exception
      {
        return addChunkTypeInternal(chunkType);
      }

    }, getExecutor());
  }

  /**
   * add the chunktype DM on the module's executor. If the executor is INLINE or
   * multithreaded, thread safety is a must.
   * 
   * @param chunkType
   * @return
   */
  abstract protected IChunkType addChunkTypeInternal(IChunkType chunkType);

  /**
   * delegated
   * 
   * @param name
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#getChunkType(java.lang.String)
   */
  public Future<IChunkType> getChunkType(final String name)
  {
    Callable<IChunkType> callable = new Callable<IChunkType>() {

      public IChunkType call() throws Exception
      {
        return getChunkTypeInternal(name);
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  abstract protected IChunkType getChunkTypeInternal(String name);

  /**
   * delegated
   * 
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#getChunkTypes()
   */
  public Future<Collection<IChunkType>> getChunkTypes()
  {
    Callable<Collection<IChunkType>> callable = new Callable<Collection<IChunkType>>() {

      public Collection<IChunkType> call() throws Exception
      {
        return getChunkTypesInternal();
      }
    };
    return delayedFuture(callable, getExecutor());
  }

  abstract protected Collection<IChunkType> getChunkTypesInternal();

}
