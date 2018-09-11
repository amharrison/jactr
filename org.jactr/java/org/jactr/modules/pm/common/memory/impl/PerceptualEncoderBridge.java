package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener;
import org.jactr.modules.pm.common.afferent.IAfferentObjectListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.PerceptualSearchResult;
import org.jactr.modules.pm.common.memory.event.ActivePerceptEvent;

/**
 * simple delegate that sits between the afferent object listener
 * {@link DefaultAfferentObjectListener} and the {@link IPerceptualEncoder} to
 * manage notification and caching..
 * 
 * @author harrison
 */
public class PerceptualEncoderBridge implements IAfferentObjectListener
{
  /**
   * Logger definition
   */
  static private final transient Log     LOGGER = LogFactory
                                                    .getLog(PerceptualEncoderBridge.class);

  private final IPerceptualEncoder       _encoder;

  private final Map<IIdentifier, IChunk> _cache;

  private final Map<IIdentifier, Double> _onsetTime;

  private final IChunkListener           _chunkListener;

  private final IDeclarativeModule       _declarativeModule;

  private IChunk                         _removedErrorChunk;

  /**
   * to track add/remove of cached chunks
   */
  // private final IActivationBufferListener _bufferListener;
  // private final ConcurrentHashMap<IChunk, AtomicInteger> _activeChunks;
  private final AbstractPerceptualMemory _memory;

  public PerceptualEncoderBridge(IPerceptualEncoder encoder,
      AbstractPerceptualMemory memory, IChunk removeErrorChunk)
  {
    _memory = memory;
    _encoder = encoder;
    _declarativeModule = _memory.getModule().getModel().getDeclarativeModule();
    _cache = new HashMap<IIdentifier, IChunk>();
    _onsetTime = new HashMap<IIdentifier, Double>();
    _removedErrorChunk = removeErrorChunk;
    // _activeChunks = new ConcurrentHashMap<IChunk, AtomicInteger>();

    /**
     * we remove the cached element when it is encoded
     */
    _chunkListener = new IChunkListener() {

      public void chunkAccessed(ChunkEvent event)
      {

      }

      public void chunkEncoded(ChunkEvent event)
      {
        /*
         * remove from the cache.
         */
        remove(getIdentifier(event.getSource()), true);
      }

      public void mergingInto(ChunkEvent event)
      {
        remove(getIdentifier(event.getSource()), true);
      }

      public void mergingWith(ChunkEvent event)
      {
        // noop

      }

      public void similarityChanged(ChunkEvent event)
      {

      }

      public void slotChanged(ChunkEvent event)
      {

      }

    };

  }

  final public void clear()
  {
    // _activeChunks.clear();

    List<IIdentifier> ids = FastListFactory.newInstance();
    ids.addAll(_cache.keySet());

    for (IIdentifier id : ids)
      remove(id, true);

    _onsetTime.clear();
    _cache.clear();
    FastListFactory.recycle(ids);
  }

  final public IPerceptualEncoder getEncoder()
  {
    return _encoder;
  }

  final public Set<IIdentifier> getCachedIdentifiers(Set<IIdentifier> container)
  {
    if (container == null) container = new HashSet<IIdentifier>();
    container.addAll(_cache.keySet());
    return container;
  }

  final public Set<IChunk> getCacheContents(Set<IChunk> container)
  {
    if (container == null) container = new HashSet<IChunk>();
    container.addAll(_cache.values());
    return container;
  }

  final public void afferentObjectAdded(IAfferentObject object)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Added %s", object.getIdentifier()));

    if (isInterestedIn(object))
    {
      IIdentifier id = object.getIdentifier();
      IChunk chunk = _encoder.encode(object, _memory);
      if (chunk != null)
      {
        _onsetTime.put(id, chunk.getModel().getAge());
        add(id, chunk);
        if (_memory.hasListeners())
          _memory.dispatch(new ActivePerceptEvent(_memory,
              ActivePerceptEvent.Type.NEW, id, chunk));
      }
    }
  }

  final public void afferentObjectRemoved(IAfferentObject object)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Removed %s", object.getIdentifier()));

    if (isInterestedIn(object))
    {
      IIdentifier identifier = object.getIdentifier();
      _onsetTime.remove(identifier);

      IChunk oldChunk = remove(identifier, true);

      if (oldChunk == null || oldChunk.hasBeenDisposed()) return;

      /*
       * currently in a buffer
       */
      boolean removedFired = false;

      try
      {
        if (BufferUtilities.getContainingBuffers(oldChunk, true).size() != 0)
          if (_memory.hasListeners())
          {
            _memory.dispatch(new ActivePerceptEvent(_memory,
                ActivePerceptEvent.Type.REMOVED, identifier, oldChunk));
            removedFired = true;
          }
      }
      catch (NullPointerException e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "priorPercept %s has already been disposed of, ignoring.",
              identifier));

        removedFired = true;
      }
      catch (IllegalChunkStateException e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "priorPercept %s has already been disposed of, ignoring.",
              identifier));

        removedFired = true;
      }

      /*
       * is it part of a search result?
       */
      List<PerceptualSearchResult> results = FastListFactory.newInstance();
      try
      {
        _memory.getRecentSearchResults(results);
        for (PerceptualSearchResult result : results)
          if (result.getPerceptIdentifier().equals(identifier))
          {
            result.setErrorCode(_removedErrorChunk);
            if (!removedFired && _memory.hasListeners())
              _memory.dispatch(new ActivePerceptEvent(_memory,
                  ActivePerceptEvent.Type.REMOVED, identifier, oldChunk));
            break;
          }
      }
      finally
      {
        FastListFactory.recycle(results);
      }

    }
    else if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s doesn't care about %s, ignoring removal",
          _encoder, object.getIdentifier()));
  }

  final public void afferentObjectUpdated(IAfferentObject object,
      IObjectDelta delta)
  {
    if (isInterestedIn(object))
    {
      IIdentifier id = object.getIdentifier();
      IChunk oldChunk = get(id, false);
      /*
       * could be null if it was attended and the percept was encoded (cache
       * emptied), so we need to reencode it
       */
      if (oldChunk == null || oldChunk.isEncoded()
          || _declarativeModule.willEncode(oldChunk)
          || oldChunk.hasBeenDisposed())
      {
        oldChunk = _encoder.encode(object, _memory);
        if (oldChunk != null)
        {
          _onsetTime.put(id, oldChunk.getModel().getAge());
          add(id, oldChunk);
        }
      }
      else
      {
        /*
         * we need to lock old chunk it we are going to update it
         */
        boolean isDirty = _encoder.isDirty(object, oldChunk, _memory);

        if (isDirty)
        {
          IChunk updated = null;
          try
          {
            updated = _encoder.update(object, oldChunk, _memory);
          }
          catch (Exception e)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Failed to update " + oldChunk
                  + " reencoding instead", e);
            /*
             * it's not uncommon for this to fail because the chunk has been
             * encoded.. we could use the chunk lock but that is for fine-graned
             * locking.. instead, we'll just create a new chunk
             */
            updated = _encoder.encode(object, _memory);
          }

          if (updated != oldChunk)
          {
            /*
             * the chunk has changed entirely.
             */
            /*
             * since we are operating in a separate thread (CR), it is possible
             * that this condition will be true and then false by the time the
             * processing is actually completed. so, it's just an early test..
             */
            if (!oldChunk.isEncoded()
                && BufferUtilities.getContainingBuffers(oldChunk, true).size() != 0)
              if (_memory.hasListeners())
                _memory.dispatch(new ActivePerceptEvent(_memory, id, updated,
                    oldChunk));

            _onsetTime.put(id, updated.getModel().getAge());
            // remove from cache, but keep metadata around
            remove(id, false);
            add(id, updated);
          }
          else
          {
            // still record thec ahnge, but we've got to update the meta data
            // ourselves
            // since add isn't called
            double age = oldChunk.getModel().getAge();
            _onsetTime.put(id, age);
            oldChunk.setMetaData(
                IPerceptualEncoder.COMMONREALITY_ONSET_TIME_KEY, age);

            if (BufferUtilities.getContainingBuffers(oldChunk, true).size() != 0)
              if (_memory.hasListeners())
                _memory.dispatch(new ActivePerceptEvent(_memory,
                    ActivePerceptEvent.Type.UPDATED, id, oldChunk));
          }
        }
      }
    }

  }

  final public boolean isInterestedIn(IAfferentObject object)
  {
    return _cache.containsKey(object.getIdentifier())
        || _encoder.isInterestedIn(object);
  }

  final protected void add(IIdentifier identifier, IChunk chunk)
  {
    chunk.setMetaData(IPerceptualEncoder.COMMONREALITY_ONSET_TIME_KEY,
        _onsetTime.get(identifier));
    chunk.setMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY,
        identifier);
    chunk.addListener(_chunkListener, ExecutorServices.INLINE_EXECUTOR);
    _cache.put(identifier, chunk);
  }

  /**
   * if clearAndDetach is true, the identifier meta tag and listener are also
   * removed
   * 
   * @param identifier
   * @param clearAndDetach
   * @return
   */
  final protected IChunk remove(IIdentifier identifier, boolean clearAndDetach)
  {
    IChunk chunk = _cache.remove(identifier);

    if (chunk != null && !chunk.hasBeenDisposed())
      try
      {
        if (clearAndDetach)
        {
          chunk.removeListener(_chunkListener);
          chunk.setMetaData(
              IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY, null);
          chunk.setMetaData(IPerceptualEncoder.COMMONREALITY_ONSET_TIME_KEY,
              null);
        }

        if (!chunk.isEncoded()
            && BufferUtilities.getContainingBuffers(chunk, true).size() == 0)
        {
          /*
           * so there is a gap here. A model finds the chunk to
           * 'encode'(preencoded, actually), and then adds it to its buffer for
           * some theoretical amount time has passed. If it was removed in this
           * gap, we could dispose it just before adding to the buffer. The
           * buffer will catch this, but wouldn't it just be better to defer the
           * disposal? the declarative module does this already, but only
           * delaying up until the end of the cycle.
           */

          // instead of doing this, we will delay it with a timed event..
          // _memory.getModule().getModel().getDeclarativeModule().dispose(chunk);

          double oneSecond = _memory.getModule().getModel().getAge() + 1;

          RunnableTimedEvent rte = new RunnableTimedEvent(
              oneSecond,
              () -> {
                // double check.
                if (!chunk.isEncoded()
                    && BufferUtilities.getContainingBuffers(chunk, true).size() == 0)
                  _memory.getModule().getModel().getDeclarativeModule()
                      .dispose(chunk);
              });

          _memory.getModule().getModel().getTimedEventQueue().enqueue(rte);
        }
      }
      finally
      {

      }

    return chunk;
  }

  /**
   * fetch cached encoding
   * 
   * @param identifier
   * @return
   */
  final public IChunk get(IIdentifier identifier, boolean createIfAbsent)
  {
    IChunk chunk = _cache.get(identifier);

    /*
     * the chunk may have been scheduled for encoding or disposale before the
     * listener has been notified..
     */
    if (chunk != null)
      if (chunk.isEncoded() || chunk.hasBeenDisposed()
          || _declarativeModule.willEncode(chunk)) chunk = null;

    if (chunk == null && createIfAbsent)
    {
      IAgent agent = ACTRRuntime.getRuntime().getConnector()
          .getAgent(_memory.getModule().getModel());

      if (agent == null) return null;

      IAfferentObject afferentObject = agent.getAfferentObjectManager().get(
          identifier);

      if (afferentObject == null) return null;

      if (_encoder.isInterestedIn(afferentObject))
      {
        chunk = _encoder.encode(afferentObject, _memory);
        if (chunk != null) add(identifier, chunk);
      }
    }
    return chunk;
  }

  final protected IIdentifier getIdentifier(IChunk chunk)
  {
    return (IIdentifier) chunk
        .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
  }

  // final protected void checkContents(Collection<IChunk> sourceChunks,
  // boolean added)
  // {
  // for (IChunk chunk : sourceChunks)
  // {
  // IIdentifier identifier = getIdentifier(chunk);
  // if (identifier == null) continue;
  //
  // /*
  // * do we have this chunk cached?
  // */
  // IChunk cached = get(identifier, false);
  // if (cached == null) continue;
  //
  // if (added)
  // {
  // AtomicInteger latch = _activeChunks.putIfAbsent(chunk,
  // new AtomicInteger(1));
  // if (latch != null) latch.incrementAndGet();
  // }
  // else
  // {
  // AtomicInteger latch = _activeChunks.get(chunk);
  // if (latch != null && latch.decrementAndGet() <= 0)
  // _activeChunks.remove(chunk);
  // }
  //
  // }
  // }
}
