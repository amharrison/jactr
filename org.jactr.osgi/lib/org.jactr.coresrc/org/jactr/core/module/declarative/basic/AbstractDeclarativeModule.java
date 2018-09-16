package org.jactr.core.module.declarative.basic;

import java.util.ArrayList;
/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.ChunkActivationComparator;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.basic.chunk.ChainedChunkConfigurator;
import org.jactr.core.module.declarative.basic.chunk.IChunkConfigurator;
import org.jactr.core.module.declarative.basic.chunk.IChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.IChunkNamer;
import org.jactr.core.module.declarative.basic.chunk.ISubsymbolicChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.ISymbolicChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.NoOpChunkNamer;
import org.jactr.core.module.declarative.basic.type.IChunkTypeConfigurator;
import org.jactr.core.module.declarative.basic.type.IChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.IChunkTypeNamer;
import org.jactr.core.module.declarative.basic.type.ISubsymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.ISymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.NoOpChunkTypeConfigurator;
import org.jactr.core.module.declarative.basic.type.NoOpChunkTypeNamer;
import org.jactr.core.module.declarative.event.DeclarativeModuleEvent;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.StringUtilities;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * Abstract declarative module that provides most of the functionality required
 * of the {@link IDeclarativeModule} including creation, merging and disposal of
 * chunks and types. However, the actual adding to the containers or searching
 * is left to subclasses. <br/>
 * The factories used by this class are not set by default.
 * 
 * @see http://jactr.org/node/120
 * @author harrison
 */
public abstract class AbstractDeclarativeModule extends AbstractModule
    implements IDeclarativeModule
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER               = LogFactory
      .getLog(AbstractDeclarativeModule.class);

  static public final String         SUSPEND_DISPOSAL_KEY = DefaultDeclarativeModule.class
      + ".suspendDisposal";

  /**
   * there is a grey area between the creation of a chunk and it's use in a
   * buffer or encoding. Most never encounter it, but it can occur in the time
   * between a perceptual search (i.e. visual-location) and encoding, where the
   * system may want to dispose of the chunk (i.e. the underlying percept has
   * changed too much) in order to create a new one. However, if the encoding
   * process has already started, it is possible that the system will try to add
   * the disposed chunk to a buffer.<br>
   * This mechanism is a recommendation only, that the declarative module can
   * use to temporarily suspend disposal.
   * 
   * @param chunk
   */
  public static void setDisposalSuspended(IChunk chunk, boolean suspend)
  {
    if (suspend)
      chunk.setMetaData(SUSPEND_DISPOSAL_KEY, Boolean.TRUE);
    else
      chunk.setMetaData(SUSPEND_DISPOSAL_KEY, null);
  }

  public static boolean isDisposalSuspended(IChunk chunk)
  {
    return Boolean.TRUE.equals(chunk.getMetaData(SUSPEND_DISPOSAL_KEY));
  }

  private IChunk                                                              _freeChunk;

  private IChunk                                                              _busyChunk;

  private IChunk                                                              _errorChunk;

  private IChunk                                                              _emptyChunk;

  private IChunk                                                              _fullChunk;

  private IChunk                                                              _newChunk;

  private IChunk                                                              _requestedChunk;

  private IChunk                                                              _unrequestedChunk;

  private ACTREventDispatcher<IDeclarativeModule, IDeclarativeModuleListener> _eventDispatcher;

  /**
   * factory instances
   */
  private IChunkFactory                                                       _chunkFactory;

  private ISymbolicChunkFactory                                               _symbolicChunkFactory;

  private ISubsymbolicChunkFactory                                            _subsymbolicChunkFactory;

  private IChunkConfigurator                                                  _chunkConfigurator = new ChainedChunkConfigurator();

  private IChunkNamer                                                         _chunkNamer;

  private IChunkTypeFactory                                                   _chunkTypeFactory;

  private ISymbolicChunkTypeFactory                                           _symbolicChunkTypeFactory;

  private ISubsymbolicChunkTypeFactory                                        _subsymbolicChunkTypeFactory;

  private IChunkTypeNamer                                                     _chunkTypeNamer;

  private IChunkTypeConfigurator                                              _chunkTypeConfigurator;

  private IAssociativeLinkageSystem                                           _associativeLinkageSystem;

  /**
   * list that contains chunks to be disposed by
   * {@link #processPendingDisposals()}
   */
  private List<IChunk>                                                        _chunksToDispose;

  /**
   * lock for _chunksToDispose
   */
  private ReentrantLock                                                       _disposalLock      = new ReentrantLock();

  private IModelListener                                                      _disposalListener;

  /**
   * list for the chunks that should be encoded at the earliest convenience
   */
  private List<IChunk>                                                        _deferredEncodings;

  private ReentrantLock                                                       _encodingLock      = new ReentrantLock();

  protected ChunkActivationComparator                                         _activationSorter  = new ChunkActivationComparator();

  public AbstractDeclarativeModule(String name)
  {
    super(name);
    _eventDispatcher = new ACTREventDispatcher<IDeclarativeModule, IDeclarativeModuleListener>();
    _chunksToDispose = new ArrayList<>();
    _deferredEncodings = new ArrayList<>();

    _disposalListener = new ModelListenerAdaptor() {

      @Override
      public void cycleStarted(ModelEvent event)
      {
        flush();
      }

      @Override
      public void cycleStopped(ModelEvent event)
      {
        flush();
      }

      @Override
      public void modelStopped(ModelEvent event)
      {
        flush();
      }
    };
  }

  public void flush()
  {
    processPendingEncodingAndDisposals();
  }

  @Override
  public void install(IModel model)
  {
    super.install(model);
    // inline to the main model
    model.addListener(_disposalListener, null);
  }

  @Override
  public void uninstall(IModel model)
  {
    model.removeListener(_disposalListener);

    super.uninstall(model);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void initialize()
  {
  }

  public void addListener(IDeclarativeModuleListener listener,
      Executor executor)
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

  protected void fireChunkRemoved(IChunk chunk)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_REMOVED, chunk));
  }

  protected void fireChunkDisposed(IChunk chunk)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_DISPOSED, chunk));
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

  protected void fireChunkTypeDisposed(IChunkType chunkType)
  {
    if (_eventDispatcher.hasListeners())
      _eventDispatcher.fire(new DeclarativeModuleEvent(this,
          DeclarativeModuleEvent.Type.CHUNK_TYPE_DISPOSED, chunkType));
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

  public void setChunkFactory(IChunkFactory chunkFactory)
  {
    _chunkFactory = chunkFactory;
  }

  public IChunkFactory getChunkFactory()
  {
    return _chunkFactory;
  }

  public void setSymbolicChunkFactory(ISymbolicChunkFactory factory)
  {
    _symbolicChunkFactory = factory;
  }

  public ISymbolicChunkFactory getSymbolicChunkFactory()
  {
    return _symbolicChunkFactory;
  }

  public void setSubsymbolicChunkFactory(ISubsymbolicChunkFactory factory)
  {
    _subsymbolicChunkFactory = factory;
  }

  public ISubsymbolicChunkFactory getSubsymbolicChunkFactory()
  {
    return _subsymbolicChunkFactory;
  }

  public void setChunkConfigurator(IChunkConfigurator configurator)
  {
    _chunkConfigurator = configurator;
  }

  /**
   * the default from this class is to return a ChainedChunkConfigurator. It is
   * preferrable to use this configurator and add yours to it, allowing more
   * than one to be installed and used.
   * 
   * @return
   */
  public IChunkConfigurator getChunkConfigurator()
  {
    if (_chunkConfigurator == null)
      _chunkConfigurator = new ChainedChunkConfigurator();

    return _chunkConfigurator;
  }

  public void setChunkNamer(IChunkNamer namer)
  {
    _chunkNamer = namer;
  }

  public IChunkNamer getChunkNamer()
  {
    if (_chunkNamer == null) _chunkNamer = new NoOpChunkNamer();
    return _chunkNamer;
  }

  public void setChunkTypeFactory(IChunkTypeFactory chunkTypeFactory)
  {
    _chunkTypeFactory = chunkTypeFactory;
  }

  public IChunkTypeFactory getChunkTypeFactory()
  {
    return _chunkTypeFactory;
  }

  public void setSymbolicChunkTypeFactory(ISymbolicChunkTypeFactory factory)
  {
    _symbolicChunkTypeFactory = factory;
  }

  public ISymbolicChunkTypeFactory getSymbolicChunkTypeFactory()
  {
    return _symbolicChunkTypeFactory;
  }

  public void setSubsymbolicChunkTypeFactory(
      ISubsymbolicChunkTypeFactory factory)
  {
    _subsymbolicChunkTypeFactory = factory;
  }

  public ISubsymbolicChunkTypeFactory getSubsymbolicChunkTypeFactory()
  {
    return _subsymbolicChunkTypeFactory;
  }

  public void setChunkTypeConfigurator(IChunkTypeConfigurator configurator)
  {
    _chunkTypeConfigurator = configurator;
  }

  public IChunkTypeConfigurator getChunkTypeConfigurator()
  {
    if (_chunkTypeConfigurator == null)
      _chunkTypeConfigurator = new NoOpChunkTypeConfigurator();

    return _chunkTypeConfigurator;
  }

  public void setChunkTypeNamer(IChunkTypeNamer namer)
  {
    _chunkTypeNamer = namer;
  }

  public IChunkTypeNamer getChunkTypeNamer()
  {
    if (_chunkTypeNamer == null) _chunkTypeNamer = new NoOpChunkTypeNamer();
    return _chunkTypeNamer;
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
  public CompletableFuture<IChunk> createChunk(final IChunkType parent,
      final String name)
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
  protected IChunk createChunkInternal(IChunkType parent, String name)
  {
    IChunkFactory cFactory = getChunkFactory();
    ISymbolicChunkFactory scFactory = getSymbolicChunkFactory();
    ISubsymbolicChunkFactory sscFactory = getSubsymbolicChunkFactory();

    IChunk rtn = cFactory.newChunk(getModel());
    ISymbolicChunk sc = scFactory.newSymbolicChunk();
    ISubsymbolicChunk ssc = sscFactory.newSubsymbolicChunk();

    cFactory.bind(rtn, sc, ssc);
    scFactory.bind(sc, rtn, parent);
    sscFactory.bind(ssc, rtn, parent);

    sc.setName(name);

    configure(rtn);

    fireChunkCreated(rtn);
    return rtn;
  }

  /**
   * calls the pluggable IChunkConfigurator
   * 
   * @param newChunk
   */
  protected void configure(IChunk newChunk)
  {
    getChunkConfigurator().configure(newChunk);
  }

  /**
   * add chunk to DM. performs a search for any matches, then merely delegates
   * to {@link #addChunkInternal(IChunk, Collection)} on {@link #getExecutor()}
   * 
   * @param chunk
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#addChunk(org.jactr.core.chunk.IChunk)
   */
  public CompletableFuture<IChunk> addChunk(final IChunk chunk)
  {
    if (chunk.isEncoded())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(chunk + " has already been encoded, silly");
      return immediateReturn(chunk);
    }

    ISymbolicChunk sc = chunk.getSymbolicChunk();
    Collection<? extends ISlot> slots = sc.getSlots();

    CompletableFuture<Collection<IChunk>> matches = null;

    /*
     * we don't do merge searches for slotless chunks.
     */
    if (slots.size() == 0)
      matches = immediateReturn((Collection<IChunk>) new LinkedList<IChunk>());
    else
    {
      final IChunkType parentType = chunk.getSymbolicChunk().getChunkType();
      matches = getModel().getDeclarativeModule().findExactMatches(
          new ChunkTypeRequest(sc.getChunkType(), slots), _activationSorter,
          new IChunkFilter() {

            /*
             * for the merge test, we need to do an isAStrict test.
             * (non-Javadoc)
             * @see
             * org.jactr.core.module.declarative.search.filter.IChunkFilter#
             * accept(org.jactr.core.chunk.IChunk)
             */
            public boolean accept(IChunk chunkToFilter)
            {
              return chunkToFilter.isAStrict(parentType);
            }

          });
    }

    final CompletableFuture<Collection<IChunk>> fMatches = matches;

    return delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        try
        {
          IChunk rtn = addChunkInternal(chunk, fMatches.get());

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
          LOGGER.error("Error while encoding chunk " + chunk + " ", e);
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
   * @param possibleMatches
   *          TODO
   * @param
   * @return
   */
  abstract protected IChunk addChunkInternal(IChunk chunkToAdd,
      Collection<IChunk> possibleMatches);

  /**
   * copy the specified chunk, by default this will also copy subsymbolics
   */
  public CompletableFuture<IChunk> copyChunk(IChunk sourceChunk)
  {
    return copyChunk(sourceChunk, true);
  }

  public CompletableFuture<IChunk> copyChunk(final IChunk sourceChunk,
      final boolean copySubsymbolics)
  {
    if (sourceChunk == null)
      throw new NullPointerException("sourceChunk cannot be null");

    return delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        String name = sourceChunk.getSymbolicChunk().getName();
        IChunkType parent = sourceChunk.getSymbolicChunk().getChunkType();
        IChunk copy = createChunkInternal(parent, name);
        copyChunkInternal(sourceChunk, copy, copySubsymbolics);
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
  protected void copyChunkInternal(IChunk sourceChunk, IChunk destination,
      boolean copySubsymbolics)
  {
    getChunkFactory().copy(sourceChunk, destination);

    /*
     * set the symbolic contents
     */
    ISymbolicChunk sourceSC = sourceChunk.getSymbolicChunk();
    ISymbolicChunk destinationSC = destination.getSymbolicChunk();

    getSymbolicChunkFactory().copy(sourceSC, destinationSC);

    if (copySubsymbolics)
    {
      ISubsymbolicChunk destinationSSC = destination.getSubsymbolicChunk();
      ISubsymbolicChunk sourceSSC = sourceChunk.getSubsymbolicChunk();

      getSubsymbolicChunkFactory().copy(sourceSSC, destinationSSC);
    }
    else
      destination.setMetaData(ISubsymbolicChunkFactory.SUBSYMBOLICS_COPIED_KEY,
          Boolean.FALSE);

    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.DECLARATIVE,
          "Copied " + StringUtilities.toString(destination));
  }

  /**
   * delegated
   * 
   * @param name
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#getChunk(java.lang.String)
   */
  public CompletableFuture<IChunk> getChunk(final String name)
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
  public CompletableFuture<Collection<IChunk>> getChunks()
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

  public CompletableFuture<IChunkType> createChunkType(
      final Collection<IChunkType> parents, final String name)
  {
    return delayedFuture(new Callable<IChunkType>() {

      public IChunkType call() throws Exception
      {
        return createChunkTypeInternal(parents, name);
      }
    }, getExecutor());
  }

  public CompletableFuture<IChunkType> createChunkType(final IChunkType parent,
      final String name)
  {
    if (parent != null)
      return createChunkType(Collections.singleton(parent), name);
    else
      return createChunkType(Collections.EMPTY_LIST, name);
  }

  protected IChunkType createChunkTypeInternal(Collection<IChunkType> parents,
      String name)
  {
    IChunkTypeFactory cFactory = getChunkTypeFactory();
    ISymbolicChunkTypeFactory scFactory = getSymbolicChunkTypeFactory();
    ISubsymbolicChunkTypeFactory sscFactory = getSubsymbolicChunkTypeFactory();

    IChunkType rtn = cFactory.newChunkType(getModel());
    ISymbolicChunkType sct = scFactory.newSymbolicChunkType();
    ISubsymbolicChunkType ssct = sscFactory.newSubsymbolicChunkType();

    cFactory.bind(rtn, sct, ssct);
    scFactory.bind(sct, rtn, parents);
    sscFactory.bind(ssct, rtn, parents);

    sct.setName(name);

    configure(rtn);

    fireChunkTypeCreated(rtn);

    return rtn;
  }

  /**
   * calls {@link IChunkTypeConfigurator}
   * 
   * @param newChunkType
   */
  protected void configure(IChunkType newChunkType)
  {
    getChunkTypeConfigurator().configure(newChunkType);
  }

  /**
   * add chunktype to DM, delegated to {@link #addChunkTypeInternal(IChunkType)}
   * on {@link #getExecutor()}
   * 
   * @param chunkType
   * @return
   * @see org.jactr.core.module.declarative.IDeclarativeModule#addChunkType(org.jactr.core.chunktype.IChunkType)
   */
  public CompletableFuture<IChunkType> addChunkType(final IChunkType chunkType)
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
  public CompletableFuture<IChunkType> getChunkType(final String name)
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
  public CompletableFuture<Collection<IChunkType>> getChunkTypes()
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

  public void setAssociativeLinkageSystem(
      IAssociativeLinkageSystem linkageSystem)
  {
    if (_associativeLinkageSystem != null)
      _associativeLinkageSystem.uninstall(getModel());

    _associativeLinkageSystem = linkageSystem;

    if (_associativeLinkageSystem != null)
      _associativeLinkageSystem.install(getModel());
  }

  public IAssociativeLinkageSystem getAssociativeLinkageSystem()
  {
    return _associativeLinkageSystem;
  }

  /**
   * actually perform the disposal
   * 
   * @param chunk
   */
  protected void disposeInternal(IChunk chunk)
  {
    try
    {
      fireChunkDisposed(chunk);

      if (LOGGER.isDebugEnabled()) LOGGER.debug("Disposing of " + chunk);

      IChunkFactory cFactory = getChunkFactory();
      ISymbolicChunkFactory scFactory = getSymbolicChunkFactory();
      ISubsymbolicChunkFactory sscFactory = getSubsymbolicChunkFactory();

      ISymbolicChunk sc = chunk.getSymbolicChunk();
      ISubsymbolicChunk ssc = chunk.getSubsymbolicChunk();

      // let the linkage system clean itself up
      IAssociativeLinkageSystem linkage = getAssociativeLinkageSystem();
      if (linkage != null) linkage.chunkWillBeDisposed(chunk);

      cFactory.unbind(chunk, sc, ssc);
      scFactory.unbind(sc);
      sscFactory.unbind(ssc);

      scFactory.dispose(sc);
      sscFactory.dispose(ssc);
      cFactory.dispose(chunk);
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to dispose of chunk " + chunk + " ", e);
    }
  }

  /**
   * entry point for merging chunks. This assumes that originalChunk is encoded,
   * newChunk was going to be encoded, but was determined to be merged into
   * originalChunk instead. newChunk may or maynot have been encoded. <br/>
   * This method handles the event dispatching, logging and ultimately calls
   * {@link #mergeChunksInternal(IChunk, IChunk)} to do the real work.
   * 
   * @param originalChunk
   * @param newChunk
   * @return
   */
  protected IChunk merge(IChunk originalChunk, IChunk newChunk)
  {
    /**
     * double check
     */
    if (originalChunk.equals(newChunk)) return originalChunk;

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Merging new chunk " + newChunk
        + " into existing chunk " + originalChunk);

    originalChunk.dispatch(
        new ChunkEvent(originalChunk, ChunkEvent.Type.MERGING_WITH, newChunk));

    newChunk.dispatch(
        new ChunkEvent(newChunk, ChunkEvent.Type.MERGING_INTO, originalChunk));

    mergeChunksInternal(originalChunk, newChunk);

    String msg = null;

    if (Logger.hasLoggers(getModel()))
      msg = String.format("Merged %s into %s (%.2f)", newChunk,
          StringUtilities.toString(originalChunk),
          originalChunk.getSubsymbolicChunk().getActivation());

    if (msg != null) Logger.log(getModel(), Logger.Stream.DECLARATIVE, msg);

    fireChunksMerged(originalChunk, newChunk);
    return originalChunk;
  }

  /**
   * actually do the work or merging newChunk into originalChunk.
   * 
   * @param originalChunk
   * @param newChunk
   */
  protected void mergeChunksInternal(IChunk originalChunk, IChunk newChunk)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Replacing the guts of " + newChunk + "(with slots"
          + newChunk.getSymbolicChunk().getSlots() + ") with " + originalChunk
          + "(with slots" + originalChunk.getSymbolicChunk().getSlots() + ")");

    ISymbolicChunk osc = originalChunk.getSymbolicChunk();
    ISubsymbolicChunk ossc = originalChunk.getSubsymbolicChunk();
    ISymbolicChunk nsc = newChunk.getSymbolicChunk();
    ISubsymbolicChunk nssc = newChunk.getSubsymbolicChunk();

    IChunkFactory chunkFactory = getChunkFactory();
    ISymbolicChunkFactory scFactory = getSymbolicChunkFactory();
    ISubsymbolicChunkFactory sscFactory = getSubsymbolicChunkFactory();

    /**
     * we need to review the locks here. We aren't using them at all, and that's
     * bad. Can we assume that the factories are locking correctly? probably
     * not..
     */

    Lock chunkLock = originalChunk.getWriteLock();

    /**
     * first, unbind. to hell with locking this one
     */
    chunkFactory.unbind(newChunk, nsc, nssc);

    try
    {
      chunkLock.lock();

      // merge at the toplevel
      chunkFactory.merge(originalChunk, newChunk);

      // merge symbolic, not strictly necessary, but good for completeness
      scFactory.merge(osc, nsc);

      // and subsymbolic
      sscFactory.merge(ossc, nssc);

      // rebind to the new contents. newChunk and masterChunk are now the same.
      chunkFactory.bind(newChunk, osc, ossc);
    }
    finally
    {
      chunkLock.unlock();
    }

    // dispose of the old pieces
    scFactory.dispose(nsc);
    sscFactory.dispose(nssc);

    // old code

    // /*
    // * ok, here we do something that is a little strange.. think of meta data
    // -
    // * who's metadata (which may be different) should take precedence? the new
    // * or the old? Because the perceptual modules use the metadata to keep
    // track
    // * of connections to actual objects in the environment, we actually want
    // to
    // * copy the new metadata over the existing metadata
    // */
    // for (String meta : newChunk.getMetaDataKeys())
    // originalChunk.setMetaData(meta, newChunk.getMetaData(meta));
    //
    // /*
    // * should this use adaptable pattern?
    // */
    // if (newChunk instanceof DefaultChunk)
    // ((DefaultChunk) newChunk).replaceContents(originalChunk);
  }

  /**
   * schedule this chunk to be disposed, at the module's earliest convenience
   */
  public void dispose(IChunk chunk)
  {
    try
    {
      _disposalLock.lock();
      _chunksToDispose.add(chunk);
    }
    finally
    {
      _disposalLock.unlock();
    }
  }

  /**
   * encode this chunk at some later time (top/bottom of the cycle).
   * 
   * @param chunk
   */
  protected void deferredEncode(IChunk chunk)
  {
    try
    {
      _encodingLock.lock();
      _deferredEncodings.add(chunk);
    }
    finally
    {
      _encodingLock.unlock();
    }
  }

  protected void deferredEncode(Collection<IChunk> chunks)
  {
    try
    {
      _encodingLock.lock();
      _deferredEncodings.addAll(chunks);
    }
    finally
    {
      _encodingLock.unlock();
    }
  }

  public boolean willEncode(IChunk chunk)
  {
    try
    {
      _encodingLock.lock();
      return chunk.isEncoded() || _deferredEncodings.contains(chunk);
    }
    finally
    {
      _encodingLock.unlock();
    }
  }

  protected void processPendingEncodingAndDisposals()
  {
    Collection<IChunk> chunkContainer = FastCollectionFactory.newInstance();
    Collection<IActivationBuffer> bufferContainer = FastCollectionFactory
        .newInstance();

    try
    {
      processPendingEncodings(chunkContainer, bufferContainer);
      processPendingDisposals(chunkContainer, bufferContainer);
    }
    finally
    {
      FastCollectionFactory.recycle(chunkContainer);
      FastCollectionFactory.recycle(bufferContainer);
    }
  }

  protected void processPendingEncodings(Collection<IChunk> chunkContainer,
      Collection<IActivationBuffer> bufferContainer)
  {
    /*
     * even though we are a declarative module, we may not be THE declarative
     * module. That is, the actual decM might be delegating to us, if so, we
     * should use it's addChunk and not ours (even though, ours will eventually
     * be called)
     */
    IDeclarativeModule decM = getModel().getDeclarativeModule();

    chunkContainer.clear();
    bufferContainer.clear();

    try
    {
      _encodingLock.lock();
      chunkContainer.addAll(_deferredEncodings);
      _deferredEncodings.clear();
    }
    finally
    {
      _encodingLock.unlock();
    }

    if (chunkContainer.size() == 0) return;

    double currentTime = ACTRRuntime.getRuntime().getClock(getModel())
        .getTime();

    if (LOGGER.isDebugEnabled()) LOGGER.debug(
        String.format("Deferred encoding %d chunks", chunkContainer.size()));

    for (IChunk chunk : chunkContainer)
    {
      /*
       * because this chunk might get merged, effectively changing the lock
       * instance, we do grab a reference to the lock temporarily
       */
      Lock lock = chunk.getWriteLock();
      try
      {
        lock.lock();
        if (chunk.hasBeenDisposed()) continue;

        if (chunk.isEncoded())
          chunk.getSubsymbolicChunk().accessed(currentTime);
        else
        {
          BufferUtilities.getContainingBuffers(chunk, true, bufferContainer);
          if (bufferContainer.size() == 0) decM.addChunk(chunk);
        }
      }
      finally
      {
        lock.unlock();
        bufferContainer.clear();
      }
    }
  }

  /**
   * called internally at the top & bottom of the cycle and at the end of a run.
   */
  protected void processPendingDisposals(Collection<IChunk> chunkContainer,
      Collection<IActivationBuffer> bufferContainer)
  {

    chunkContainer.clear();
    bufferContainer.clear();
    /*
     * disposals
     */
    try
    {
      _disposalLock.lock();
      chunkContainer.addAll(_chunksToDispose);
      _chunksToDispose.clear();
    }
    finally
    {
      _disposalLock.unlock();
    }

    if (LOGGER.isDebugEnabled()) LOGGER
        .debug(String.format("Disposing of %d chunks", chunkContainer.size()));

    // fast, destructive iterator where processing order does not matter
    for (IChunk chunk : chunkContainer)
    {
      Lock chunkLock = chunk.getWriteLock();
      try
      {
        chunkLock.lock();

        if (chunk.isEncoded()) continue;
        if (chunk.hasBeenDisposed()) continue;
        // requeue
        if (isDisposalSuspended(chunk))
          dispose(chunk);
        else
        {

          BufferUtilities.getContainingBuffers(chunk, true, bufferContainer);

          if (bufferContainer.size() == 0) disposeInternal(chunk);
        }
      }
      finally
      {
        chunkLock.unlock();
        bufferContainer.clear();
      }
    }
  }

}
