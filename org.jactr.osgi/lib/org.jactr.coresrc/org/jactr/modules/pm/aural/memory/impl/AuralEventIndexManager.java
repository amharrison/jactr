package org.jactr.modules.pm.aural.memory.impl;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.afferent.DefaultAfferentObjectListener;
import org.jactr.modules.pm.common.afferent.IAfferentObjectListener;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.impl.IIndexManager;

/**
 * @author harrison
 */
public class AuralEventIndexManager implements IIndexManager
{
  /**
   * Logger definition
   */
  static private final transient Log     LOGGER                = LogFactory
                                                                   .getLog(AuralEventIndexManager.class);

  final private IAuralModule             _module;

  /*
   * listener allows us to create and remove audio-event chunks as they are
   * needed.
   */
  final private IAfferentObjectListener  _afferentListener;

  final private Map<IIdentifier, IChunk> _existingIndexChunks;

  final private IAuralPropertyHandler    _auralPropertyHandler = new DefaultAuralPropertyHandler();

  final private Lock                     _lock                 = new ReentrantLock();

  final private IChunkListener           _encodingListener;

  public AuralEventIndexManager(IAuralModule module)
  {
    _module = module;
    _existingIndexChunks = new HashMap<IIdentifier, IChunk>();

    _afferentListener = new IAfferentObjectListener() {

      public void afferentObjectAdded(IAfferentObject object)
      {
        addIndex(object);
      }

      public void afferentObjectRemoved(IAfferentObject object)
      {
        removeIndex(object);
      }

      public void afferentObjectUpdated(IAfferentObject object,
          IObjectDelta delta)
      {

      }

      public boolean isInterestedIn(IAfferentObject object)
      {
        return _auralPropertyHandler.hasModality(object);
      }

    };

    _encodingListener = new IChunkListener() {

      public void chunkAccessed(ChunkEvent event)
      {

      }

      public void chunkEncoded(ChunkEvent event)
      {
        IChunk indexChunk = event.getSource();
        indexChunk.removeListener(_encodingListener);
        removeIndex(getIdentifier(indexChunk));
      }

      public void mergingInto(ChunkEvent event)
      {
        IChunk indexChunk = event.getSource();
        indexChunk.removeListener(_encodingListener);
        removeIndex(getIdentifier(indexChunk));
      }

      public void mergingWith(ChunkEvent event)
      {
        IChunk indexChunk = event.getChunk();
        indexChunk.removeListener(_encodingListener);
        removeIndex(getIdentifier(indexChunk));
      }

      public void similarityChanged(ChunkEvent event)
      {

      }

      public void slotChanged(ChunkEvent event)
      {

      }

    };
  }

  protected void attach(DefaultAfferentObjectListener afferentListener)
  {
    afferentListener.add(_afferentListener);
  }

  protected void detach(DefaultAfferentObjectListener afferentListener)
  {
    afferentListener.remove(_afferentListener);
  }

  protected IChunk addIndex(IAfferentObject object)
  {
    /*
     * create the chunk first..
     */
    IChunkType audioEventType = _module.getAudioEventChunkType();
    IDeclarativeModule decM = audioEventType.getModel().getDeclarativeModule();
    IIdentifier identifier = object.getIdentifier();
    IChunk event = null;
    try
    {
      event = decM.createChunk(audioEventType,
          String.format("ae-%s", identifier.getName())).get();
    }
    catch (Exception e)
    {
      LOGGER.error(String.format("Failed to create aural index chunk for %s",
          identifier), e);
      return null;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("created audio-event for %s", identifier));

    try
    {
      _lock.lock();
      event.setMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY,
          identifier);
      _existingIndexChunks.put(identifier, event);
      return event;
    }
    finally
    {
      _lock.unlock();
    }

  }

  private IIdentifier getIdentifier(IChunk indexChunk)
  {
    return (IIdentifier) indexChunk
        .getMetaData(IPerceptualEncoder.COMMONREALITY_IDENTIFIER_META_KEY);
  }

  protected void removeIndex(IAfferentObject object)
  {
    removeIndex(object.getIdentifier());
  }

  protected void removeIndex(IIdentifier identifier)
  {
    if (identifier == null) return;

    IChunk index = null;
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("removing audio-event %s", identifier));
    try
    {
      _lock.lock();
      index = _existingIndexChunks.remove(identifier);
    }
    finally
    {
      _lock.unlock();
    }

    /*
     * it was never used, so dispose
     */
    if (index != null && !index.isEncoded() && !index.hasBeenDisposed())
      index.getModel().getDeclarativeModule().dispose(index);
  }

  public IChunk getIndexChunk(IChunk encodedChunk)
  {
    try
    {
      ISlot event = encodedChunk.getSymbolicChunk().getSlot(
          IAuralModule.EVENT_SLOT);
      return (IChunk) event.getValue();
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No valid audio-event for " + encodedChunk);
      return null;
    }
  }

  public IChunk getAuralEvent(IAfferentObject auralEvent)
  {
    try
    {
      _lock.lock();
      IChunk event = _existingIndexChunks.get(auralEvent.getIdentifier());
      if (event == null && _auralPropertyHandler.hasModality(auralEvent))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format(
              "System missed the encoding of aural event %s, coding now",
              auralEvent.getIdentifier()));

        event = addIndex(auralEvent);
      }
      return event;
    }
    finally
    {
      _lock.unlock();
    }
  }
}
