package org.jactr.modules.pm.common.memory.impl;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener;
import org.jactr.modules.pm.common.afferent.IAfferentObjectListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
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

  private final IChunkListener           _chunkListener;

  /**
   * to track add/remove of cached chunks
   */
  // private final IActivationBufferListener _bufferListener;
  // private final ConcurrentHashMap<IChunk, AtomicInteger> _activeChunks;
  private final AbstractPerceptualMemory _memory;

  public PerceptualEncoderBridge(IPerceptualEncoder encoder,
      AbstractPerceptualMemory memory)
  {
    _memory = memory;
    _encoder = encoder;
    _cache = new HashMap<IIdentifier, IChunk>();
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
        remove(getIdentifier(event.getSource()));
      }

      public void mergingInto(ChunkEvent event)
      {
        remove(getIdentifier(event.getSource()));
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

    /**
     * buffer listener is used to managed the activeChunks container which
     * allows us to keep track of which chunks of ours are in buffers which we
     * use to signal a reencoding event
     */
    // _bufferListener = new IActivationBufferListener() {
    //
    // public void chunkMatched(ActivationBufferEvent abe)
    // {
    //
    // }
    //
    // public void requestAccepted(ActivationBufferEvent abe)
    // {
    //
    // }
    //
    // public void sourceChunkAdded(ActivationBufferEvent abe)
    // {
    // checkContents(abe.getSourceChunks(), true);
    // }
    //
    // public void sourceChunkRemoved(ActivationBufferEvent abe)
    // {
    // checkContents(abe.getSourceChunks(), false);
    // }
    //
    // public void sourceChunksCleared(ActivationBufferEvent abe)
    // {
    // checkContents(abe.getSourceChunks(), false);
    // }
    //
    // public void statusSlotChanged(ActivationBufferEvent abe)
    // {
    //
    // }
    //
    // @SuppressWarnings("unchecked")
    // public void parameterChanged(IParameterEvent pe)
    // {
    //
    // }
    //
    // };
    //
    // for (IActivationBuffer buffer : _memory.getModule().getModel()
    // .getActivationBuffers())
    // buffer.addListener(_bufferListener, ExecutorServices.INLINE_EXECUTOR);
  }

  final public void clear()
  {
    // _activeChunks.clear();

    FastList<IIdentifier> ids = FastList.newInstance();
    ids.addAll(_cache.keySet());

    for (IIdentifier id : ids)
      remove(id);

    _cache.clear();
    FastList.recycle(ids);
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
    if (_encoder.isInterestedIn(object))
    {
      IIdentifier id = object.getIdentifier();
      IChunk chunk = _encoder.encode(object, _memory);
      if (chunk != null)
      {
        add(id, chunk);
        if (_memory.hasListeners())
          _memory.dispatch(new ActivePerceptEvent(_memory,
              ActivePerceptEvent.Type.NEW, id, chunk));
      }
    }
  }

  final public void afferentObjectRemoved(IAfferentObject object)
  {
    if (_encoder.isInterestedIn(object))
    {
      IIdentifier identifier = object.getIdentifier();
      IChunk oldChunk = remove(identifier);
      // if (_activeChunks.containsKey(oldChunk))
      if (oldChunk != null && !oldChunk.isEncoded()
          && BufferUtilities.getContainingBuffers(oldChunk, true).size() != 0)
        if (_memory.hasListeners())
          _memory.dispatch(new ActivePerceptEvent(_memory,
              ActivePerceptEvent.Type.REMOVED, identifier, oldChunk));
    }
  }

  final public void afferentObjectUpdated(IAfferentObject object,
      IObjectDelta delta)
  {
    if (_encoder.isInterestedIn(object))
    {
      IIdentifier id = object.getIdentifier();
      IChunk oldChunk = get(id, false);
      /*
       * could be null if it was attended and the percept was encoded (cache
       * emptied), so we need to reencode it
       */
      if (oldChunk == null || oldChunk.isEncoded()
          || oldChunk.hasBeenDisposed())
      {
        oldChunk = _encoder.encode(object, _memory);
        if (oldChunk != null) add(id, oldChunk);
        // _listener.newPercept(id, oldChunk);
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
            // if (_activeChunks.containsKey(oldChunk))
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

            remove(id);
            add(id, updated);
          }
          else if (BufferUtilities.getContainingBuffers(oldChunk, true).size() != 0)
            if (_memory.hasListeners())
              _memory.dispatch(new ActivePerceptEvent(_memory,
                  ActivePerceptEvent.Type.UPDATED, id, oldChunk));
        }
      }
    }

  }

  final public boolean isInterestedIn(IAfferentObject object)
  {
    return _encoder.isInterestedIn(object);
  }

  final protected void add(IIdentifier identifier, IChunk chunk)
  {
    chunk.setMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY,
        identifier);
    chunk.addListener(_chunkListener, ExecutorServices.INLINE_EXECUTOR);
    _cache.put(identifier, chunk);
  }

  final protected IChunk remove(IIdentifier identifier)
  {
    IChunk chunk = _cache.remove(identifier);

    if (chunk != null)
    {
      chunk.removeListener(_chunkListener);
      if (!chunk.isEncoded() && !chunk.hasBeenDisposed()
          && BufferUtilities.getContainingBuffers(chunk, true).size() == 0)
        _memory.getModule().getModel().getDeclarativeModule().dispose(chunk);
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
    if (chunk == null && createIfAbsent)
    {
      IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(
          _memory.getModule().getModel());

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
