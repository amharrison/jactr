package org.jactr.modules.pm.aural.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.memory.IAuralMemory;
import org.jactr.modules.pm.aural.memory.impl.AuralEventIndexManager;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

public abstract class AbstractAuralEncoder implements IPerceptualEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractAuralEncoder.class);

  static public IChunk getAudioEvent(IAfferentObject afferent,
      IAuralMemory memory)
  {
    try
    {
      if (!getHandler().isAudible(afferent)) return null;
      return ((AuralEventIndexManager) memory.getIndexManager())
          .getAuralEvent(afferent);
    }
    catch (Exception e)
    {
      LOGGER.error(String.format("Could not find audio-event for afferent %s",
          afferent.getIdentifier()), e);

      return null;
    }
  }

  static private final IAuralPropertyHandler _propertyHandler = new DefaultAuralPropertyHandler();

  private final String                       _chunkTypeName;

  private IChunkType                         _chunkType;

  public AbstractAuralEncoder(String chunkTypeName)
  {
    _chunkTypeName = chunkTypeName;
  }

  protected String getChunkTypeName()
  {
    return _chunkTypeName;
  }

  protected String guessChunkName(IAfferentObject afferentObject)
  {
    String candidateName = afferentObject.getIdentifier().getName();
    if (candidateName == null || candidateName.length() == 0)
      candidateName = _chunkTypeName;
    candidateName += "-"
        + _chunkType.getSymbolicChunkType().getNumberOfChunks();
    return candidateName;
  }

  public IChunk encode(IAfferentObject afferentObject, IPerceptualMemory memory)
  {
    IChunkType chunkType = getAuralObjectChunkType(memory);
    IChunk encoding = newChunk(chunkType, guessChunkName(afferentObject));

    IModel model = memory.getModule().getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.AURAL, "Precoded " + encoding);

    /*
     * we are the only ones with access.. but just in case
     */
    try
    {
      encoding.getWriteLock().lock();

      updateSlots(afferentObject, encoding, (IAuralMemory) memory);
    }
    finally
    {
      encoding.getWriteLock().unlock();
    }

    return encoding;
  }

  protected void updateSlots(IAfferentObject afferent, IChunk encoding,
      IAuralMemory memory)
  {
    try
    {
      ISymbolicChunk sc = encoding.getSymbolicChunk();
      IChunk auralEvent = getAudioEvent(afferent, memory);


      /*
       * event, content, and kind...
       */
      ((IMutableSlot) sc.getSlot(IAuralModule.KIND_SLOT)).setValue(sc
          .getChunkType());
      ((IMutableSlot) sc.getSlot(IAuralModule.EVENT_SLOT)).setValue(auralEvent);

      IAuralModule aModule = memory.getAuralModule();
      Object boundSymbol = aModule.getSymbolGrounder().getSymbolForPercept(
          afferent, aModule,
          memory.getModule().getModel().getDeclarativeModule());

      ((IMutableSlot) sc.getSlot(IAuralModule.CONTENT_SLOT))
          .setValue(boundSymbol);
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Could not set slot values for "
          + _chunkTypeName + " encoding of " + afferent.getIdentifier(), e);
    }
  }

  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    return false;
  }

  public boolean isInterestedIn(IAfferentObject afferentObject)
  {
    return getHandler().hasModality(afferentObject)
        && canEncodeAuralObjectType(afferentObject);
  }

  protected boolean canEncodeAuralObjectType(IAfferentObject afferentObject)
  {
    try
    {
      String targetType = getChunkTypeName();
      for (String type : getHandler().getTypes(afferentObject))
        if (type.equalsIgnoreCase(targetType)) return true;
      return false;
    }
    catch (Exception e)
    {
      return false;
    }
  }

  public IChunk update(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    return oldChunk;
  }

  static protected IAuralPropertyHandler getHandler()
  {
    return _propertyHandler;
  }

  /**
   * return the chunktype
   * 
   * @param auralMemory
   * @return
   */
  final private IChunkType getAuralObjectChunkType(IPerceptualMemory auralMemory)
  {
    if (_chunkType == null)
      try
      {
        _chunkType = auralMemory.getModule().getModel().getDeclarativeModule()
            .getChunkType(_chunkTypeName).get();
      }
      catch (Exception e)
      {
        throw new IllegalStateException(
            "Failed to retrieve reference to chunktype " + _chunkTypeName, e);
      }
    return _chunkType;
  }

  /**
   * create a new chunk. If something goes wrong, it will throw an
   * illegalStateException
   * 
   * @param chunkType
   * @param name
   * @return
   */
  final private IChunk newChunk(IChunkType chunkType, String name)
  {
    IDeclarativeModule decM = chunkType.getModel().getDeclarativeModule();
    try
    {
      return decM.createChunk(chunkType, name).get();
    }
    catch (Exception e)
    {
      throw new IllegalStateException("Could not create chunk of " + chunkType,
          e);
    }
  }
}
