/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.declarative.basic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.IRemovableSymbolicChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.logging.IMessageBuilder;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.MessageBuilderFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.IRemovableDeclarativeModule;
import org.jactr.core.module.declarative.basic.chunk.DefaultChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.DefaultSubsymbolicChunkFactory5;
import org.jactr.core.module.declarative.basic.chunk.DefaultSymbolicChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.IChunkConfigurator;
import org.jactr.core.module.declarative.basic.chunk.IChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.IChunkNamer;
import org.jactr.core.module.declarative.basic.chunk.ISubsymbolicChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.ISymbolicChunkFactory;
import org.jactr.core.module.declarative.basic.chunk.NoOpChunkConfigurator;
import org.jactr.core.module.declarative.basic.chunk.NoOpChunkNamer;
import org.jactr.core.module.declarative.basic.type.DefaultChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.DefaultSubsymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.DefaultSymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.IChunkTypeConfigurator;
import org.jactr.core.module.declarative.basic.type.IChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.IChunkTypeNamer;
import org.jactr.core.module.declarative.basic.type.ISubsymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.ISymbolicChunkTypeFactory;
import org.jactr.core.module.declarative.basic.type.NoOpChunkTypeConfigurator;
import org.jactr.core.module.declarative.basic.type.NoOpChunkTypeNamer;
import org.jactr.core.module.declarative.search.ISearchSystem;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.module.declarative.search.filter.ILoggedChunkFilter;
import org.jactr.core.module.declarative.search.local.DefaultSearchSystem;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.utils.StringUtilities;
import org.jactr.core.utils.collections.SkipListSetFactory;
import org.jactr.core.utils.parameter.ClassNameParameterHandler;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;

/**
 * default declarative module that incorporates many useful features. This
 * module is full thread safe, and parameterizes the creation factories. <br/>
 * This module also uses the local {@link DefaultSearchSystem} for the indexing
 * of chunks.<br/>
 * This module provides encoding services for any buffer that returns false for
 * {@link IActivationBuffer#handlesEncoding()}.<br/>
 * This is the ideal point to start from when creating a new declarative module.
 * <br/>
 * <br/>
 * Most modelers should be content with this or it's version specific
 * subclasses. If you need to add theoretic behavior, it is recommended that you
 * start with customizing the symbolic/ subsymbolic factories (most likely just
 * the {@link ISubsymbolicChunkFactory}). Custom {@link IChunk}s should not be
 * required as they are simply wrappers to the theoretically relevant contents.
 * <br/>
 * <br/>
 * Most extensions will just extend this module to add their parameter
 * accessors, and any required listeners. If chunks do not need to be extended,
 * merely configured differently there is the {@link IChunkConfigurator}.
 * 
 * @see http://jactr.org/node/121
 * @author harrison
 */
public class DefaultDeclarativeModule extends AbstractDeclarativeModule
    implements IDeclarativeModule, IParameterized, IRemovableDeclarativeModule
{
  /**
   * logger definition
   */
  static final Log                    LOGGER                               = LogFactory
      .getLog(DefaultDeclarativeModule.class);

  static public final String          CHUNK_FACTORY_PARAM                  = "ChunkFactoryClass";

  static public final String          SYMBOLIC_CHUNK_FACTORY_PARAM         = "SymbolicChunkFactoryClass";

  static public final String          SUBSYMBOLIC_CHUNK_FACTORY_PARAM      = "SubsymbolicChunkFactoryClass";

  static public final String          CHUNK_TYPE_FACTORY_PARAM             = "ChunkTypeFactoryClass";

  static public final String          SYMBOLIC_CHUNK_TYPE_FACTORY_PARAM    = "SymbolicChunkTypeFactoryClass";

  static public final String          SUBSYMBOLIC_CHUNK_TYPE_FACTORY_PARAM = "SubsymbolicChunkTypeFactoryClass";

  static public final String          CHUNK_NAMER_PARAM                    = "ChunkNamerClass";

  static public final String          CHUNK_CONFIGURATOR_PARAM             = "ChunkConfiguratorClass";

  static public final String          CHUNK_TYPE_NAMER_PARAM               = "ChunkTypeNamerClass";

  static public final String          CHUNK_TYPE_CONFIGURATOR_PARAM        = "ChunkTypeConfiguratorClass";

  protected ReentrantReadWriteLock    _chunkTypeLock;

  protected ReentrantReadWriteLock    _chunkLock;

  protected DefaultSearchSystem       _searchSystem;

  protected Map<String, IChunk>       _allChunks;

  protected Map<String, IChunkType>   _allChunkTypes;

  /**
   * used to encode chunks after removal
   */
  protected IActivationBufferListener _encodeChunksOnRemove;

  public DefaultDeclarativeModule()
  {
    super("declarative");

    setAssociativeLinkageSystem(new DefaultAssociativeLinkageSystem());

    _allChunks = new TreeMap<String, IChunk>();
    _allChunkTypes = new TreeMap<String, IChunkType>();

    _searchSystem = new DefaultSearchSystem(this);

    _chunkLock = new ReentrantReadWriteLock();
    _chunkTypeLock = new ReentrantReadWriteLock();

    setChunkFactory(new DefaultChunkFactory());
    setSymbolicChunkFactory(new DefaultSymbolicChunkFactory());
    setSubsymbolicChunkFactory(new DefaultSubsymbolicChunkFactory5());

    setChunkTypeFactory(new DefaultChunkTypeFactory());
    setSymbolicChunkTypeFactory(new DefaultSymbolicChunkTypeFactory());
    setSubsymbolicChunkTypeFactory(new DefaultSubsymbolicChunkTypeFactory());

    _encodeChunksOnRemove = new IActivationBufferListener() {

      public void chunkMatched(ActivationBufferEvent abe)
      {
        // noop
      }

      public void requestAccepted(ActivationBufferEvent abe)
      {
        // noop

      }

      public void sourceChunkAdded(ActivationBufferEvent abe)
      {
        // noop

      }

      public void sourceChunkRemoved(ActivationBufferEvent abe)
      {
        /*
         * queue up the encoding. we dont encode it here so that any inline
         * listeners after this one will get the actual instance of the removed
         * chunk and not the merged version after encoding (if a merge occurs)
         */
        if (!abe.getSource().handlesEncoding())
          deferredEncode(abe.getSourceChunks());
      }

      public void sourceChunksCleared(ActivationBufferEvent abe)
      {
        sourceChunkRemoved(abe);
      }

      public void statusSlotChanged(ActivationBufferEvent abe)
      {
        // noop

      }

      @SuppressWarnings("unchecked")
      public void parameterChanged(IParameterEvent pe)
      {
        // noop

      }

    };

  }

  @Override
  synchronized public void dispose()
  {
    for (IActivationBuffer buffer : getModel().getActivationBuffers())
      buffer.removeListener(_encodeChunksOnRemove);

    _searchSystem.clear();
    _searchSystem = null;

    try
    {
      _chunkTypeLock.writeLock().lock();
      // dispose of all the chunktypes (and chunks by extension)
      for (IChunkType chunkType : _allChunkTypes.values())
        chunkType.dispose();

      _allChunkTypes.clear();
    }
    finally
    {
      _chunkTypeLock.writeLock().unlock();
    }
    try
    {
      _chunkLock.writeLock().lock();
      _allChunks.clear();
    }
    finally
    {
      _chunkLock.writeLock().unlock();
    }

    super.dispose();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected IChunk addChunkInternal(IChunk chunk,
      Collection<IChunk> possibleMatches)
  {
    if (possibleMatches.size() > 0)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("chunk " + chunk + " has yielded " + possibleMatches.size()
            + " matches " + possibleMatches);

      if (possibleMatches.size() > 1) if (LOGGER.isWarnEnabled())
        LOGGER.warn(String.format("Found multiple identical chunks to %s : %s",
            chunk, possibleMatches));

      IChunk mergeInto = possibleMatches.iterator().next();

      if (possibleMatches instanceof ConcurrentSkipListSet)
        SkipListSetFactory.recycle((ConcurrentSkipListSet) possibleMatches);

      return merge(mergeInto, chunk);
    }

    double now = getModel().getAge();

    boolean added = false;
    try
    {
      _chunkLock.writeLock().lock();

      String name = getChunkNamer().generateName(chunk);
      String newName = getSafeName(name, _allChunks);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Safe name for chunk " + name + " is " + newName);

      chunk.getSymbolicChunk().setName(newName);

      /*
       * notify the chunktype of this chunk
       */
      chunk.setMetaData(IChunkFactory.COPIED_FROM_KEY, null);
      chunk.getSymbolicChunk().getChunkType().getSymbolicChunkType()
          .addChunk(chunk);

      _allChunks.put(newName.toLowerCase(), chunk);
      added = true;
      return chunk;
    }
    finally
    {
      _chunkLock.writeLock().unlock();

      // this will fire some chunk event.. so get out of the lock
      if (added)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Encoding " + chunk);
        chunk.encode(now);

        if (Logger.hasLoggers(getModel()))
          Logger.log(getModel(), Logger.Stream.DECLARATIVE,
              "Encoded " + StringUtilities.toString(chunk));
        /*
         * we index after since an unencoded chunk wont be indexed
         */
        _searchSystem.index(chunk);
        fireChunkAdded(chunk);
      }
    }
  }

  /**
   * merely remove the chunk from the internal DM store and the chunktype store.
   * Other chunk references to the removed chunk will be unaffected. There is no
   * tombstone marking. The removed chunk itself is not disposed of. if all is
   * successful, this will fire the chunk removed event.
   * 
   * @param chunk
   */
  public void removeChunk(final IChunk chunk)
  {
    delayedFuture(new Callable<IChunk>() {

      public IChunk call() throws Exception
      {
        try
        {
          removeChunkInternal(chunk);
        }
        catch (Exception e)
        {
          LOGGER.error("Error while removing chunk " + chunk + " ", e);
          throw e;
        }
        return chunk;
      }
    }, getExecutor());
  }

  protected void removeChunkInternal(IChunk chunk)
  {
    boolean removed = false;
    // noop
    if (!chunk.isEncoded() || chunk.hasBeenDisposed())
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug(String.format(
          "%s has not been encoded or has been disposed of, no need to remove",
          chunk));
      return;
    }

    ISymbolicChunk sc = chunk.getSymbolicChunk();
    chunk.getSubsymbolicChunk();
    String chunkName = sc.getName();

    try
    {
      _chunkLock.writeLock().lock();

      /*
       * we merely remove the chunk from our indices
       */
      IChunk removedChunk = _allChunks.remove(chunkName.toLowerCase());
      removed = removedChunk != null;

      // we can only remove from chunktype if it is supported
      if (removed)
      {
        /*
         * just so that we are sure we are using the encoded chunk handle and
         * not a merged, restored, or otherwise indirect handle
         */
        chunk = removedChunk;

        ISymbolicChunkType ct = sc.getChunkType().getSymbolicChunkType();
        IRemovableSymbolicChunkType rct = ct
            .getAdapter(IRemovableSymbolicChunkType.class);

        if (rct != null) rct.removeChunk(chunk);

        _searchSystem.unindex(chunk);
      }
    }
    catch (Exception e)
    {
      LOGGER.error(String.format("Failed to remove chunk %s", chunk), e);
    }
    finally
    {
      _chunkLock.writeLock().unlock();
      if (removed)
      {
        if (Logger.hasLoggers(getModel()))
          Logger.log(getModel(), Logger.Stream.DECLARATIVE,
              "Removed " + StringUtilities.toString(chunk));

        fireChunkRemoved(chunk);
      }
    }
  }

  /**
   * create a callable that will do all the work of adding a chunktype to the
   * model and firing the appropriate events
   * 
   * @param chunkType
   * @return
   */
  @Override
  protected IChunkType addChunkTypeInternal(IChunkType chunkType)
  {

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Adding chunkType " + chunkType);
    boolean added = false;
    try
    {
      _chunkTypeLock.writeLock().lock();
      ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
      String name = getChunkTypeNamer().generateName(chunkType);
      name = getSafeName(name, _allChunkTypes);
      sct.setName(name);

      _allChunkTypes.put(name.toLowerCase(), chunkType);
      addChunkTypeToParents(chunkType,
          chunkType.getSymbolicChunkType().getParents());
      added = true;
      return chunkType;
    }
    finally
    {
      _chunkTypeLock.writeLock().unlock();

      // this will fire an event, so get out of the lock
      if (added)
      {
        chunkType.encode();

        if (Logger.hasLoggers(getModel())) Logger.log(getModel(),
            Logger.Stream.DECLARATIVE, "Encoded " + chunkType);
        fireChunkTypeAdded(chunkType);
      }
    }
  }

  private void addChunkTypeToParents(IChunkType chunkType,
      Collection<IChunkType> parents)
  {
    for (IChunkType parent : parents)
    {
      parent.getSymbolicChunkType().addChild(chunkType);
      addChunkTypeToParents(chunkType,
          parent.getSymbolicChunkType().getParents());
    }
  }

  public ISearchSystem getSearchSystem()
  {
    return _searchSystem;
  }

  protected Collection<IChunk> findExactMatchesInternal(
      ChunkTypeRequest pattern, final Comparator<IChunk> sorter,
      IChunkFilter filter)
  {
    IMessageBuilder logMessage = null;
    boolean recycle = false;

    SortedSet<IChunk> candidates = _searchSystem.findExact(pattern, sorter,
        filter);

    if (filter instanceof ILoggedChunkFilter)
      logMessage = ((ILoggedChunkFilter) filter).getMessageBuilder();

    // just incase a message builder wasn't created
    if (logMessage == null && Logger.hasLoggers(getModel()))
    {
      logMessage = MessageBuilderFactory.newInstance();
      recycle = true;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("find exact matches (" + pattern
        + ") evaluating " + candidates.size() + " candidates");

    if (Logger.hasLoggers(getModel())) logMessage
        .prepend(String.format("Evaluating exact matches : %s \n", candidates));

    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.DECLARATIVE, logMessage.toString());

    if (recycle) MessageBuilderFactory.recycle(logMessage);

    // clean up
    // if (candidates instanceof FastSet) FastSet.recycle((FastSet) candidates);

    // logMessage.delete(0, logMessage.length());

    return candidates;
  }

  protected Collection<IChunk> findPartialMatchesInternal(
      ChunkTypeRequest pattern, Comparator<IChunk> sorter, IChunkFilter filter)
  {

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("find partial matches (" + pattern + ")");

    SortedSet<IChunk> candidates = _searchSystem.findFuzzy(pattern, sorter,
        filter);

    boolean recycle = false;
    IMessageBuilder logMessage = null;

    if (filter instanceof ILoggedChunkFilter)
      logMessage = ((ILoggedChunkFilter) filter).getMessageBuilder();
    else
    {
      recycle = true;
      logMessage = MessageBuilderFactory.newInstance();
    }

    if (Logger.hasLoggers(getModel()))
    {
      logMessage.prepend(
          String.format("Evaluating partial matches : %s \n", candidates));

      Logger.log(getModel(), Logger.Stream.DECLARATIVE, logMessage.toString());
    }

    if (recycle) MessageBuilderFactory.recycle(logMessage);

    return candidates;
  }

  @Override
  protected IChunk getChunkInternal(String name)
  {
    return _allChunks.get(name.toLowerCase());
  }

  @Override
  protected IChunkType getChunkTypeInternal(String name)
  {
    return _allChunkTypes.get(name.toLowerCase());
  }

  @Override
  protected Collection<IChunkType> getChunkTypesInternal()
  {
    return new ArrayList<IChunkType>(_allChunkTypes.values());
  }

  @Override
  protected Collection<IChunk> getChunksInternal()
  {
    return new ArrayList<IChunk>(_allChunks.values());
  }

  public long getNumberOfChunks()
  {
    try
    {
      _chunkLock.readLock().lock();
      return _allChunks.size();
    }
    finally
    {
      _chunkLock.readLock().unlock();
    }
  }

  /**
   * here we attach a buffer listener to all the buffers and catch the removal
   * notifications to see if we should encode the chunk..
   * 
   * @see org.jactr.core.module.AbstractModule#initialize()
   */
  @Override
  public void initialize()
  {
    super.initialize();
    IModel model = getModel();

    for (IActivationBuffer buffer : model.getActivationBuffers())
      buffer.addListener(_encodeChunksOnRemove,
          ExecutorServices.INLINE_EXECUTOR);
  }

  /**
   * @see org.jactr.core.module.declarative.six.AbstractDeclarativeModule#copyChunkInternal(org.jactr.core.chunk.IChunk,
   *      org.jactr.core.chunk.IChunk)
   */

  /**
   * @see org.jactr.core.module.declarative.IDeclarativeModule#findExactMatches(ChunkTypeRequest,
   *      java.util.Comparator, IChunkFilter)
   */
  public CompletableFuture<Collection<IChunk>> findExactMatches(
      final ChunkTypeRequest request, final Comparator<IChunk> sorter,
      final IChunkFilter filter)
  {
    return delayedFuture(new Callable<Collection<IChunk>>() {

      public Collection<IChunk> call() throws Exception
      {
        return findExactMatchesInternal(request, sorter, filter);
      }

    }, getExecutor());
  }

  /**
   * @see org.jactr.core.module.declarative.IDeclarativeModule#findPartialMatches(ChunkTypeRequest,
   *      java.util.Comparator, IChunkFilter)
   */
  public CompletableFuture<Collection<IChunk>> findPartialMatches(
      final ChunkTypeRequest request, final Comparator<IChunk> sorter,
      final IChunkFilter filter)
  {
    return delayedFuture(new Callable<Collection<IChunk>>() {

      public Collection<IChunk> call() throws Exception
      {
        return findPartialMatchesInternal(request, sorter, filter);
      }

    }, getExecutor());
  }

  public String getParameter(String key)
  {
    if (CHUNK_FACTORY_PARAM.equalsIgnoreCase(key))
      return getChunkFactory().getClass().getName();
    else if (SYMBOLIC_CHUNK_FACTORY_PARAM.equalsIgnoreCase(key))
      return getSymbolicChunkFactory().getClass().getName();
    else if (SUBSYMBOLIC_CHUNK_FACTORY_PARAM.equalsIgnoreCase(key))
      return getSubsymbolicChunkFactory().getClass().getName();
    else if (CHUNK_TYPE_FACTORY_PARAM.equalsIgnoreCase(key))
      return getChunkTypeFactory().getClass().getName();
    else if (SYMBOLIC_CHUNK_TYPE_FACTORY_PARAM.equalsIgnoreCase(key))
      return getSymbolicChunkTypeFactory().getClass().getName();
    else if (SUBSYMBOLIC_CHUNK_TYPE_FACTORY_PARAM.equalsIgnoreCase(key))
      return getSubsymbolicChunkTypeFactory().getClass().getName();
    else if (CHUNK_NAMER_PARAM.equalsIgnoreCase(key))
      return getChunkNamer().getClass().getName();
    else if (CHUNK_CONFIGURATOR_PARAM.equalsIgnoreCase(key))
      return getChunkConfigurator().getClass().getName();
    else if (CHUNK_TYPE_NAMER_PARAM.equalsIgnoreCase(key))
      return getChunkTypeNamer().getClass().getName();
    else if (CHUNK_TYPE_CONFIGURATOR_PARAM.equalsIgnoreCase(key))
      return getChunkTypeConfigurator().getClass().getName();
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = new ArrayList<String>(10);

    rtn.add(CHUNK_FACTORY_PARAM);
    rtn.add(SYMBOLIC_CHUNK_FACTORY_PARAM);
    rtn.add(SUBSYMBOLIC_CHUNK_FACTORY_PARAM);
    rtn.add(CHUNK_NAMER_PARAM);
    rtn.add(CHUNK_CONFIGURATOR_PARAM);
    rtn.add(CHUNK_TYPE_FACTORY_PARAM);
    rtn.add(SYMBOLIC_CHUNK_TYPE_FACTORY_PARAM);
    rtn.add(SUBSYMBOLIC_CHUNK_TYPE_FACTORY_PARAM);
    rtn.add(CHUNK_TYPE_NAMER_PARAM);
    rtn.add(CHUNK_TYPE_CONFIGURATOR_PARAM);

    return rtn;
  }

  public void setParameter(String key, String value)
  {
    if (CHUNK_FACTORY_PARAM.equalsIgnoreCase(key))
    {
      IChunkFactory factory = (IChunkFactory) instantiate(value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new DefaultChunkFactory();
      }
      setChunkFactory(factory);
    }
    else if (SYMBOLIC_CHUNK_FACTORY_PARAM.equalsIgnoreCase(key))
    {
      ISymbolicChunkFactory factory = (ISymbolicChunkFactory) instantiate(
          value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new DefaultSymbolicChunkFactory();
      }
      setSymbolicChunkFactory(factory);
    }
    else if (SUBSYMBOLIC_CHUNK_FACTORY_PARAM.equalsIgnoreCase(key))
    {
      ISubsymbolicChunkFactory factory = (ISubsymbolicChunkFactory) instantiate(
          value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new DefaultSubsymbolicChunkFactory5();
      }
      setSubsymbolicChunkFactory(factory);
    }
    else if (CHUNK_TYPE_FACTORY_PARAM.equalsIgnoreCase(key))
    {
      IChunkTypeFactory factory = (IChunkTypeFactory) instantiate(value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new DefaultChunkTypeFactory();
      }
      setChunkTypeFactory(factory);
    }
    else if (SYMBOLIC_CHUNK_TYPE_FACTORY_PARAM.equalsIgnoreCase(key))
    {
      ISymbolicChunkTypeFactory factory = (ISymbolicChunkTypeFactory) instantiate(
          value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new DefaultSymbolicChunkTypeFactory();
      }
      setSymbolicChunkTypeFactory(factory);
    }
    else if (SUBSYMBOLIC_CHUNK_TYPE_FACTORY_PARAM.equalsIgnoreCase(key))
    {
      ISubsymbolicChunkTypeFactory factory = (ISubsymbolicChunkTypeFactory) instantiate(
          value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new DefaultSubsymbolicChunkTypeFactory();
      }
      setSubsymbolicChunkTypeFactory(factory);
    }
    else if (CHUNK_NAMER_PARAM.equalsIgnoreCase(key))
    {
      IChunkNamer factory = (IChunkNamer) instantiate(value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new NoOpChunkNamer();
      }
      setChunkNamer(factory);
    }
    else if (CHUNK_CONFIGURATOR_PARAM.equalsIgnoreCase(key))
    {
      IChunkConfigurator factory = (IChunkConfigurator) instantiate(value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new NoOpChunkConfigurator();
      }
      setChunkConfigurator(factory);
    }
    else if (CHUNK_TYPE_NAMER_PARAM.equalsIgnoreCase(key))
    {
      IChunkTypeNamer factory = (IChunkTypeNamer) instantiate(value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new NoOpChunkTypeNamer();
      }
      setChunkTypeNamer(factory);
    }
    else if (CHUNK_TYPE_CONFIGURATOR_PARAM.equalsIgnoreCase(key))
    {
      IChunkTypeConfigurator factory = (IChunkTypeConfigurator) instantiate(
          value);
      if (factory == null)
      {
        if (LOGGER.isWarnEnabled())
          LOGGER.warn(String.format("Could not instantiate %s", value));
        factory = new NoOpChunkTypeConfigurator();
      }
      setChunkTypeConfigurator(factory);
    }

    else if (LOGGER.isWarnEnabled()) LOGGER.warn(
        String.format("%s doesn't recognize %s. Available parameters : %s",
            getClass().getSimpleName(), key, getSetableParameters()));
  }

  private Object instantiate(String className)
  {
    ClassNameParameterHandler ph = ParameterHandler.classInstance();
    try
    {
      return ph.coerce(className).newInstance();
    }
    catch (Exception e)
    {
      return null;
    }
  }

  public void reset()
  {// noop
  }

}
