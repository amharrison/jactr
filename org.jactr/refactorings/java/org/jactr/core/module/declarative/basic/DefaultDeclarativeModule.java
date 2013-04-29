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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.ChunkActivationComparator;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.basic.AbstractChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.five.DefaultChunk5;
import org.jactr.core.chunk.five.ISubsymbolicChunk5;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.five.DefaultChunkType5;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.search.local.DefaultSearchSystem;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.ChunkSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.StringUtilities;
import org.jactr.core.utils.parameter.IParameterized;

public class DefaultDeclarativeModule extends AbstractDeclarativeModule
    implements IDeclarativeModule, IParameterized
{
  /**
   * logger definition
   */
  static final Log            LOGGER               = LogFactory
                                                       .getLog(DefaultDeclarativeModule.class);

  static private final String COPIED_FROM_KEY      = "CopiedFrom";

  static private final String SUSPEND_DISPOSAL_KEY = DefaultDeclarativeModule.class
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
  static public void setDisposalSuspended(IChunk chunk, boolean suspend)
  {
    if (suspend)
      chunk.setMetaData(SUSPEND_DISPOSAL_KEY, Boolean.TRUE);
    else
      chunk.setMetaData(SUSPEND_DISPOSAL_KEY, null);
  }

  static public boolean isDisposalSuspended(IChunk chunk)
  {
    return Boolean.TRUE.equals(chunk.getMetaData(SUSPEND_DISPOSAL_KEY));
  }

  protected ReentrantReadWriteLock    _chunkTypeLock;

  protected ReentrantReadWriteLock    _chunkLock;

  protected DefaultSearchSystem       _searchSystem;

  protected Map<String, IChunk>       _allChunks;

  protected Map<String, IChunkType>   _allChunkTypes;

  protected ChunkActivationComparator _activationSorter;

  protected List<IChunk>              _chunksToDispose;

  /**
   * used to encode chunks after removal
   */
  protected IActivationBufferListener _encodeChunksOnRemove;

  /**
   * delayed chunks to encode in response to buffer remove
   */
  protected List<IChunk>              _chunksToEncode;

  /**
   * we actually encode the chunks at the end of the cycle
   */
  protected IModelListener            _chunkEncoder;

  public DefaultDeclarativeModule()
  {
    super("declarative");
    _allChunks = new TreeMap<String, IChunk>();
    _allChunkTypes = new TreeMap<String, IChunkType>();
    _activationSorter = new ChunkActivationComparator();

    _chunksToDispose = FastList.newInstance();

    _searchSystem = new DefaultSearchSystem(this);

    _chunkLock = new ReentrantReadWriteLock();
    _chunkTypeLock = new ReentrantReadWriteLock();

    _chunksToEncode = FastList.newInstance();

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
        try
        {
          _chunkLock.writeLock().lock();

          if (!abe.getSource().handlesEncoding())
            _chunksToEncode.addAll(abe.getSourceChunks());
        }
        finally
        {
          _chunkLock.writeLock().unlock();
        }
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

    _chunkEncoder = new ModelListenerAdaptor() {

      @Override
      public void cycleStopped(ModelEvent event)
      {
        /**
         * encode those that need encoding and dispose of those that need
         * disposing
         */
        FastList<IChunk> chunkList = FastList.newInstance();

        // assignment, check thread safety
        try
        {
          _chunkLock.writeLock().lock();
          chunkList.addAll(_chunksToEncode);
          _chunksToEncode.clear();
        }
        finally
        {
          _chunkLock.writeLock().unlock();
        }

        FastList<IActivationBuffer> containingBuffers = FastList.newInstance();

        // fast, destructive iterator where processing order does not matter
        for (IChunk chunk = null; !chunkList.isEmpty()
            && (chunk = chunkList.removeLast()) != null;)
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
              chunk.getSubsymbolicChunk().accessed(event.getSimulationTime());
            else
            {
              BufferUtilities.getContainingBuffers(chunk, true,
                  containingBuffers);
              if (containingBuffers.size() == 0) addChunk(chunk);
            }
          }
          finally
          {
            lock.unlock();
            containingBuffers.clear();
          }
        }

        /**
         * now for the disposal
         */
        // assignment, check thread safety
        try
        {
          _chunkLock.writeLock().lock();
          chunkList.addAll(_chunksToDispose);
          _chunksToDispose.clear();
        }
        finally
        {
          _chunkLock.writeLock().unlock();
        }

        // fast, destructive iterator where processing order does not matter
        for (IChunk chunk = null; !chunkList.isEmpty()
            && (chunk = chunkList.removeLast()) != null;)
          try
          {
            chunk.getWriteLock().lock();

            if (chunk.isEncoded()) continue;
            if (chunk.hasBeenDisposed()) continue;
            // requeue
            if (isDisposalSuspended(chunk))
              dispose(chunk);
            else
            {
              BufferUtilities.getContainingBuffers(chunk, true,
                  containingBuffers);

              if (containingBuffers.size() == 0) disposeInternal(chunk);
            }
          }
          finally
          {
            containingBuffers.clear();
            chunk.getWriteLock().unlock();
          }

        FastList.recycle(containingBuffers);
        FastList.recycle(chunkList);
      }
    };
  }

  public boolean willEncode(IChunk chunk)
  {
    try
    {
      _chunkLock.readLock().lock();
      return _chunksToEncode.contains(chunk);
    }
    finally
    {
      _chunkLock.readLock().unlock();
    }
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

  protected IChunk merge(IChunk originalChunk, IChunk newChunk)
  {
    /**
     * double check
     */
    if (originalChunk.equals(newChunk)) return originalChunk;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Merging new chunk " + newChunk + " into existing chunk "
          + originalChunk);

    String msg = null;

    if (Logger.hasLoggers(getModel()))
      msg = "Merged " + newChunk + " into "
          + StringUtilities.toString(originalChunk);

    originalChunk.dispatch(new ChunkEvent(originalChunk,
        ChunkEvent.Type.MERGING_WITH, newChunk));

    newChunk.dispatch(new ChunkEvent(newChunk, ChunkEvent.Type.MERGING_INTO,
        originalChunk));
    try
    {
      _chunkLock.writeLock().lock();
      mergeChunks(originalChunk, newChunk);
    }
    finally
    {
      _chunkLock.writeLock().unlock();
    }

    if (msg != null) Logger.log(getModel(), Logger.Stream.DECLARATIVE, msg);

    fireChunksMerged(originalChunk, newChunk);
    return originalChunk;
  }

  /**
   * actually do the work.
   * 
   * @param originalChunk
   * @param newChunk
   */
  protected void mergeChunks(IChunk originalChunk, IChunk newChunk)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Replacing the guts of " + newChunk + " with "
          + originalChunk);

    /*
     * ok, here we do something that is a little strange.. think of meta data -
     * who's metadata (which may be different) should take precedence? the new
     * or the old? Because the perceptual modules use the metadata to keep track
     * of connections to actual objects in the environment, we actually want to
     * copy the new metadata over the existing metadata
     */
    for (String meta : newChunk.getMetaDataKeys())
      originalChunk.setMetaData(meta, newChunk.getMetaData(meta));

    if (newChunk instanceof AbstractChunk)
      ((AbstractChunk) newChunk).replaceContents(originalChunk);
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
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Disposing of " + chunk);
      chunk.dispose();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to dispose of chunk " + chunk + " ", e);
    }
  }

  @Override
  protected IChunk addChunkInternal(IChunk chunk)
  {

    IChunkType chunkType = chunk.getSymbolicChunk().getChunkType();

    Collection<? extends ISlot> slots = chunk.getSymbolicChunk().getSlots();

    /*
     * we don't check for duplicates when a chunk has no slots these chunks are
     * often flags for some bit of simplistic knowledge such as "new"
     */
    if (slots.size() != 0)
    {
      Collection<IChunk> matches = _searchSystem.findExact(
          new ChunkTypeRequest(chunkType, slots), _activationSorter);

      if (matches.size() > 0)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("chunk " + chunk + " has yielded " + matches.size()
              + " matches " + matches);

        return merge(matches.iterator().next(), chunk);
      } // matches.size() == 0
    } // slots.size() > 0

    /*
     * we're here, so either we aren't checking for duplicates or there was no
     * matching chunk
     */

    double now = ACTRRuntime.getRuntime().getClock(getModel()).getTime();

    boolean added = false;
    try
    {
      _chunkLock.writeLock().lock();

      String name = chunk.getSymbolicChunk().getName();
      String newName = getSafeName(name, _allChunks);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Safe name for chunk " + name + " is " + newName);

      chunk.getSymbolicChunk().setName(newName);

      /*
       * notify the chunktype of this chunk
       */
      chunk.setMetaData(COPIED_FROM_KEY, null);
      chunk.getSymbolicChunk().getChunkType().getSymbolicChunkType().addChunk(
          chunk);

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
          Logger.log(getModel(), Logger.Stream.DECLARATIVE, "Encoded "
              + StringUtilities.toString(chunk));
        /*
         * we index after since an unencoded chunk wont be indexed
         */
        _searchSystem.index(chunk);
        fireChunkAdded(chunk);
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
      String name = sct.getName();
      name = getSafeName(name, _allChunkTypes);
      sct.setName(name);

      _allChunkTypes.put(name.toLowerCase(), chunkType);

      IChunkType parent = chunkType.getSymbolicChunkType().getParent();
      while (parent != null)
      {
        parent.getSymbolicChunkType().addChild(chunkType);
        parent = parent.getSymbolicChunkType().getParent();
      }

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

        if (Logger.hasLoggers(getModel()))
          Logger.log(getModel(), Logger.Stream.DECLARATIVE, "Encoded "
              + chunkType);
        fireChunkTypeAdded(chunkType);
      }
    }
  }

  @Override
  protected IChunk createChunkInternal(IChunkType parent, String name)
  {
    IChunk rtn = new DefaultChunk5(parent);
    rtn.getSymbolicChunk().setName(name);
    // rtn.addListener(new ChunkListenerAdaptor() {
    // @Override
    // public void slotChanged(ChunkEvent nce)
    // {
    // /*
    // * we only index encoded chunks..
    // */
    // IChunk source = nce.getSource();
    // if (source.isEncoded() && !source.isMutable())
    // _searchSystem.update(nce.getSource(), nce.getSlotName(), nce
    // .getOldSlotValue(), nce.getNewSlotValue());
    // }
    // }, getExecutor());
    fireChunkCreated(rtn);
    return rtn;
  }

  @Override
  protected IChunkType createChunkTypeInternal(IChunkType parent, String name)
  {
    IModel model = getModel();
    IChunkType rtn = null;

    if (parent != null)
      rtn = new DefaultChunkType5(model, parent);
    else
      rtn = new DefaultChunkType5(model);

    rtn.getSymbolicChunkType().setName(name);

    fireChunkTypeCreated(rtn);

    return rtn;
  }

  protected Collection<IChunk> findExactMatchesInternal(
      ChunkTypeRequest pattern, final Comparator<IChunk> sorter,
      double activationThreshold, boolean bestOne)
  {
    Collection<IChunk> candidates = _searchSystem.findExact(pattern, sorter);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("find exact matches (" + pattern + ") evaluating "
          + candidates.size() + " candidates");

    StringBuilder logMessage = null;

    if (Logger.hasLoggers(getModel()))
    {
      logMessage = new StringBuilder("Evaluating exact search matches ");
      logMessage.append(candidates).append("\n ");
    }

    ArrayList<IChunk> finalChunks = new ArrayList<IChunk>();
    /*
     * we can't be sure that the sorting used is actually relevant to us so we
     * have to zip through the entire results
     */
    double highestActivation = Double.NEGATIVE_INFINITY;
    IChunk bestChunk = null;
    for (IChunk chunk : candidates)
    {
      /*
       * snag the activation and see if this is the highest chunk so far
       */
      double tmpAct = chunk.getSubsymbolicChunk().getActivation();
      if (tmpAct > highestActivation)
      {
        bestChunk = chunk;
        highestActivation = tmpAct;
        if (logMessage != null)
          logMessage.append(bestChunk).append(" has highest activation (")
              .append(highestActivation).append(")\n ");
      }
      else if (logMessage != null)
        logMessage.append(chunk).append(" doesn't have highest activation (")
            .append(tmpAct).append(")\n ");

      /*
       * if we are selecting the best one only, don't add it to the list
       */
      if (!bestOne && tmpAct >= activationThreshold) finalChunks.add(chunk);
    }

    /*
     * here's the best one, assuming we only want one
     */
    if (bestOne && bestChunk != null
        && highestActivation >= activationThreshold)
      finalChunks.add(bestChunk);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("find exact matches returning " + finalChunks);

    if (logMessage != null)
      Logger.log(getModel(), Logger.Stream.DECLARATIVE, logMessage.toString());

    return finalChunks;
  }

  protected Collection<IChunk> findPartialMatchesInternal(
      ChunkTypeRequest pattern, Comparator<IChunk> sorter,
      double activationThreshold, boolean bestOne)
  {
    Collection<IChunk> candidates = _searchSystem.findFuzzy(pattern, sorter);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("find partial matches evaluating " + candidates.size()
          + " candidates");
    ArrayList<IChunk> finalChunks = new ArrayList<IChunk>();
    StringBuilder logMessage = null;
    if (Logger.hasLoggers(getModel()))
    {
      logMessage = new StringBuilder("Evaluating partial search matches ");
      logMessage.append(candidates).append("\n ");
    }

    /*
     * we can't be sure that the sorting used is actually relevant to us so we
     * have to zip through the entire results
     */
    double highestActivation = Double.NEGATIVE_INFINITY;
    IChunk bestChunk = null;
    for (IChunk chunk : candidates)
    {
      /*
       * snag the activation and see if this is the highest chunk so far we need
       * to use the pattern to evaluate mismatch with activation
       */
      double tmpAct = ((ISubsymbolicChunk5) chunk.getSubsymbolicChunk())
          .getActivation(pattern);
      if (tmpAct > highestActivation)
      {
        bestChunk = chunk;
        highestActivation = tmpAct;
        if (logMessage != null)
          logMessage.append(bestChunk).append(" has highest activation (")
              .append(highestActivation).append(")\n ");
      }
      else if (logMessage != null)
        logMessage.append(chunk).append(" doesn't have highest activation (")
            .append(tmpAct).append(")\n ");

      /*
       * if we are selecting the best one only, don't add it to the list
       */
      if (!bestOne && tmpAct >= activationThreshold) finalChunks.add(chunk);
    }

    /*
     * here's the best one, assuming we only want one
     */
    if (bestOne && bestChunk != null
        && highestActivation >= activationThreshold)
      finalChunks.add(bestChunk);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("find partial matches returning " + finalChunks);

    if (logMessage != null)
      Logger.log(getModel(), Logger.Stream.DECLARATIVE, logMessage.toString());

    return finalChunks;
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
    model.addListener(_chunkEncoder, ExecutorServices.INLINE_EXECUTOR);

    for (IActivationBuffer buffer : model.getActivationBuffers())
      buffer.addListener(_encodeChunksOnRemove,
          ExecutorServices.INLINE_EXECUTOR);
  }

  /**
   * @see org.jactr.core.module.declarative.six.AbstractDeclarativeModule#copyChunkInternal(org.jactr.core.chunk.IChunk,
   *      org.jactr.core.chunk.IChunk)
   */

  @Override
  protected void copyChunkInternal(IChunk sourceChunk, IChunk destination)
  {
    /*
     * copy the meta data
     */
    for (String key : sourceChunk.getMetaDataKeys())
      destination.setMetaData(key, sourceChunk.getMetaData(key));

    /*
     * set the symbolic contents
     */
    ISymbolicChunk sourceSC = sourceChunk.getSymbolicChunk();
    ISymbolicChunk destinationSC = destination.getSymbolicChunk();

    String newName = sourceSC.getName();
    try
    {
      _chunkLock.readLock().lock();
      newName = getSafeName(newName, _allChunks);
    }
    finally
    {
      _chunkLock.readLock().unlock();
    }

    destinationSC.setName(newName);

    for (ISlot slot : sourceSC.getSlots())
    {
      // this is the actual backing slot..
      ChunkSlot cs = (ChunkSlot) destinationSC.getSlot(slot.getName());
      cs.setValue(slot.getValue());
    }

    /*
     * we need to deal with the subsymbolics since it will have associative
     * links necessary for the propogation of activation when a chunk copy is in
     * a buffer...
     */
    ISubsymbolicChunk destinationSSC = destination.getSubsymbolicChunk();

    /*
     * set all the parameters this should handle the associative links as
     * well...
     */
    for (String parameterName : destinationSSC.getSetableParameters())
    {
      String parameterValue = destinationSSC.getParameter(parameterName);
      try
      {
        destinationSSC.setParameter(parameterName, parameterValue);
      }
      catch (Exception e)
      {
        LOGGER.warn("Could not set parameter " + parameterName + " to "
            + parameterValue, e);
      }
    }

    if (Logger.hasLoggers(getModel()))
      Logger.log(getModel(), Logger.Stream.DECLARATIVE, "Copied "
          + StringUtilities.toString(destination));

    destination.setMetaData(COPIED_FROM_KEY, sourceChunk);
  }

  /**
   * @see org.jactr.core.module.declarative.IDeclarativeModule#findExactMatches(ChunkTypeRequest,
   *      java.util.Comparator, double, boolean)
   */
  public Future<Collection<IChunk>> findExactMatches(
      final ChunkTypeRequest request, final Comparator<IChunk> sorter,
      final double activationThreshold, final boolean bestOne)
  {
    return delayedFuture(new Callable<Collection<IChunk>>() {

      public Collection<IChunk> call() throws Exception
      {
        return findExactMatchesInternal(request, sorter, activationThreshold,
            bestOne);
      }

    }, getExecutor());
  }

  /**
   * @see org.jactr.core.module.declarative.IDeclarativeModule#findPartialMatches(ChunkTypeRequest,
   *      java.util.Comparator, double, boolean)
   */
  public Future<Collection<IChunk>> findPartialMatches(
      final ChunkTypeRequest request, final Comparator<IChunk> sorter,
      final double activationThreshold, final boolean bestOne)
  {
    return delayedFuture(new Callable<Collection<IChunk>>() {

      public Collection<IChunk> call() throws Exception
      {
        return findPartialMatchesInternal(request, sorter, activationThreshold,
            bestOne);
      }

    }, getExecutor());
  }

  public void dispose(IChunk chunk)
  {
    try
    {
      _chunkLock.writeLock().lock();
      _chunksToDispose.add(chunk);
    }
    finally
    {
      _chunkLock.writeLock().unlock();
    }

  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.emptyList();
  }

  public void setParameter(String key, String value)
  {

  }

  public void reset()
  {// noop
  }

}
