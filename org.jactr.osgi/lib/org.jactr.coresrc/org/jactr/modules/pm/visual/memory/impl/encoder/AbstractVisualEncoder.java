package org.jactr.modules.pm.visual.memory.impl.encoder;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.visual.Color;
import org.commonreality.modalities.visual.DefaultVisualPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.modalities.visual.geom.Dimension2D;
import org.commonreality.modalities.visual.geom.Point2D;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.common.memory.IPerceptualEncoder;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.memory.IVisualMemory;
import org.jactr.modules.pm.visual.memory.VisualUtilities;
import org.jactr.modules.pm.visual.memory.impl.DefaultVisualMemory;

/**
 * abstract base implementation of a visual chunk encoder. Extenders must
 * implement {@link #canEncodeVisualObjectType(IAfferentObject)} which tests to
 * see if this encoder can encode that type of percept. If there is any actual
 * content in the encoded chunk,
 * {@link #updateSlots(IAfferentObject, IChunk, IVisualMemory)},
 * {@link #isDirty(IAfferentObject, IChunk, IPerceptualMemory)}, and
 * {@link #isTooDirty(IAfferentObject, IChunk, IVisualMemory)} should be
 * extended as well. This implementation handles the creation of new chunks as
 * well as updating if they are dirty.
 * 
 * @author harrison
 */
public abstract class AbstractVisualEncoder implements IPerceptualEncoder
{
  /**
   * Logger definition
   */
  static private final transient Log          LOGGER   = LogFactory
                                                           .getLog(AbstractVisualEncoder.class);

  static private DefaultVisualPropertyHandler _handler = new DefaultVisualPropertyHandler();

  private final String                        _chunkTypeName;

  private IChunkType                          _chunkType;

  /**
   * @param chunkTypeName
   *          name of the chunktype that is to be created
   */
  public AbstractVisualEncoder(String chunkTypeName)
  {
    _chunkTypeName = chunkTypeName;
  }

  /**
   * return the visual location (defined by
   * {@link IVisualPropertyHandler#RETINAL_LOCATION}) of the object. If no
   * location is defined, it is outside the visual field, or it is not visible,
   * null is returned
   * 
   * @param afferentObject
   * @param visualMemory
   * @return
   */
  static public IChunk getVisualLocation(IAfferentObject afferentObject,
      IVisualMemory visualMemory)
  {
    try
    {
      boolean isVisible = getHandler().isVisible(afferentObject);
      if (!isVisible) return null;

      Point2D location = getHandler().getRetinalLocation(afferentObject);
      IChunk visualLocation = visualMemory.getVisualLocationChunkAt(
          location.getX(), location.getY());
      return visualLocation;
    }
    catch (UnknownPropertyNameException e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No retinal location defined for "
            + afferentObject.getIdentifier());
      return null;
    }
  }

  /**
   * return the contents of screen-pos, but only if it is a visual-location
   * chunk
   * 
   * @param visualChunk
   * @return
   */
  static public IChunk getVisualLocation(IChunk visualChunk,
      IVisualMemory visualMemory)
  {
    Object loc = visualChunk.getSymbolicChunk()
        .getSlot(IVisualModule.SCREEN_POSITION_SLOT).getValue();

    if (loc != null && loc instanceof IChunk)
      if (((IChunk) loc).isA(visualMemory.getVisualModule()
          .getVisualLocationChunkType())) return (IChunk) loc;

    return null;
  }

  /**
   * only works if visualMemory is DefaultVisualMemory. Will return the existing
   * color chunk for this color, or create it. null if no default visual
   * 
   * @param color
   * @param visualMemory
   * @return
   */
  static public IChunk getColor(Color color, IVisualMemory visualMemory)
  {
    if (visualMemory instanceof DefaultVisualMemory)
      return ((DefaultVisualMemory) visualMemory).getColorChunkCache()
          .getColorChunk(color);

    LOGGER.warn("getColor only properly functions with DefaultVisualMemory ");

    return null;
  }

  static protected IVisualPropertyHandler getHandler()
  {
    return _handler;
  }

  /**
   * checks the expected visual location against the previously encoded visual
   * location. The read lock will already have been acquired.
   * 
   * @param afferentObject
   * @param oldChunk
   * @param memory
   * @return
   * @see org.jactr.modules.pm.common.memory.IPerceptualEncoder#isDirty(org.commonreality.object.IAfferentObject,
   *      org.jactr.core.chunk.IChunk,
   *      org.jactr.modules.pm.common.memory.IPerceptualMemory)
   */
  public boolean isDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    IChunk oldLoc = getVisualLocation(oldChunk, (IVisualMemory) memory);
    IChunk newLoc = getVisualLocation(afferentObject, (IVisualMemory) memory);

    return oldLoc != newLoc;
  }

  /**
   * used to trigger a reencoding if the old chunk is too dirty. default returns
   * true only if old and new visuallocations exceed movement tolerance.
   * 
   * @param afferentObject
   * @param oldChunk
   * @param visualMemory
   * @return
   */
  protected boolean isTooDirty(IAfferentObject afferentObject, IChunk oldChunk,
      IVisualMemory visualMemory)
  {
    if (oldChunk.isEncoded() || oldChunk.hasBeenDisposed()) return true;

    IChunk oldLoc = getVisualLocation(oldChunk, visualMemory);
    IChunk newLoc = getVisualLocation(afferentObject, visualMemory);

    /*
     * originally this returned oldLoc!=newLoc, however, what we really want in
     * this case (the percept has disappeared or appeared) is to update the slot
     * values as usual and not generate a new encoding. The updated chunk will
     * then be passed to the DefaultPerceptListener which will determine if the
     * encoded chunk should trigger an error (due to disappearing)
     */
    if (oldLoc == null || newLoc == null) return false;

    if (isAttendedSticky(afferentObject.getIdentifier(), oldChunk, visualMemory))
      return false;

    return exceedsMovementTolerance(oldLoc, newLoc, visualMemory);
  }

  /**
   * if {@link IVisualMemory#isStickyAttentionEnabled()} and the chunk is in the
   * visual buffer. OR, if the latest search found the matching percept
   * 
   * @param encoding
   * @param visualMemory
   * @return
   */
  protected boolean isAttendedSticky(IIdentifier perceptId, IChunk encoding,
      IVisualMemory visualMemory)
  {
    if (VisualUtilities.isCurrentlySticky(perceptId, visualMemory))
      return true;

    if (VisualUtilities.isCurrentlySticky(encoding, visualMemory, visualMemory
        .getVisualModule().getVisualActivationBuffer())) return true;

    return false;
  }

  /**
   * returns true if the visual locations are separated by more than
   * {@link IVisualMemory#getMovementTolerance()},
   * 
   * @param oldVisualLocation
   * @param newVisualLocation
   * @param visualMemory
   * @return
   */
  static public boolean exceedsMovementTolerance(IChunk oldVisualLocation,
      IChunk newVisualLocation, IVisualMemory visualMemory)
  {
    double[] oLoc = getLocation(oldVisualLocation);
    double[] nLoc = getLocation(newVisualLocation);

    double distSq = Math.pow(oLoc[0] - nLoc[0], 2)
        + Math.pow(oLoc[1] - nLoc[1], 2);

    return distSq > Math.pow(visualMemory.getMovementTolerance(), 2);
  }

  static public double[] getLocation(IChunk visualLocation)
  {
    try
    {
      double x = ((Number) visualLocation.getSymbolicChunk()
          .getSlot(IVisualModule.SCREEN_X_SLOT).getValue()).doubleValue();
      double y = ((Number) visualLocation.getSymbolicChunk()
          .getSlot(IVisualModule.SCREEN_Y_SLOT).getValue()).doubleValue();
      return new double[] { x, y };
    }
    catch (Exception e)
    {
      return null;
    }
  }

  /**
   * returns true if the percept has the
   * {@link IVisualPropertyHandler#IS_VISUAL} property.
   * 
   * @param afferentObject
   * @return
   * @see org.jactr.modules.pm.common.memory.IPerceptualEncoder#isInterestedIn(org.commonreality.object.IAfferentObject)
   */
  public boolean isInterestedIn(IAfferentObject afferentObject)
  {
    return getHandler().hasModality(afferentObject)
        && canEncodeVisualObjectType(afferentObject);
  }

  /**
   * returns true if this particular encoder can be used for this object
   * 
   * @param afferentObject
   * @return
   */
  abstract protected boolean canEncodeVisualObjectType(
      IAfferentObject afferentObject);

  /**
   * fill the slot values of the encoded chunk. The default impl handles
   * screen-pos, width, height, token, and type. The write lock for the chunk
   * will have already been acqquired.
   * 
   * @param afferentObject
   * @param encoding
   * @param memory
   */
  protected void updateSlots(IAfferentObject afferentObject, IChunk encoding,
      IVisualMemory memory)
  {
    try
    {
      ISymbolicChunk sc = encoding.getSymbolicChunk();
      IChunk visualLocation = getVisualLocation(afferentObject, memory);

      ((IMutableSlot) sc.getSlot(IVisualModule.SCREEN_POSITION_SLOT))
          .setValue(visualLocation);

      Dimension2D size = getHandler().getRetinalSize(afferentObject);
      ((IMutableSlot) sc.getSlot(IVisualModule.HEIGHT_SLOT)).setValue(size
          .getHeight());
      ((IMutableSlot) sc.getSlot(IVisualModule.WIDTH_SLOT)).setValue(size
          .getWidth());

      IChunk color = getColor(getHandler().getColors(afferentObject)[0], memory);
      ((IMutableSlot) sc.getSlot(IVisualModule.COLOR_SLOT)).setValue(color);

      IVisualModule vModule = memory.getVisualModule();

      Object boundSymbol = vModule.getSymbolGrounder().getSymbolForPercept(
          afferentObject, vModule,
          memory.getModule().getModel().getDeclarativeModule());

      if (boundSymbol != null)
      {
        ((IMutableSlot) sc.getSlot(IVisualModule.TOKEN_SLOT))
            .setValue(boundSymbol);
        ((IMutableSlot) sc.getSlot(IVisualModule.VALUE_SLOT))
            .setValue(boundSymbol);
      }

      ((IMutableSlot) sc.getSlot(IVisualModule.TYPE_SLOT)).setValue(sc
          .getChunkType());

      IModel model = memory.getModule().getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, "Updated precoding of "
            + encoding);

    }
    catch (Exception e)
    {
      throw new IllegalStateException("Could not set slot values for "
          + _chunkTypeName + " encoding of " + afferentObject.getIdentifier(),
          e);
    }
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
    IChunkType chunkType = getVisualObjectChunkType((IVisualMemory) memory);
    IChunk encoding = newChunk(chunkType, guessChunkName(afferentObject));

    IModel model = memory.getModule().getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.VISUAL, "Precoded " + encoding);

    /*
     * we are the only ones with access.. but just in case
     */
    try
    {
      encoding.getWriteLock().lock();

      updateSlots(afferentObject, encoding, (IVisualMemory) memory);
    }
    finally
    {
      encoding.getWriteLock().unlock();
    }

    return encoding;
  }

  /**
   * called to update the encoding of a chunk. A new chunk may be returned if
   * the encoding {@link #isTooDirty(IAfferentObject, IChunk, IVisualMemory)}.
   * The write lock will have already been acquired.
   * 
   * @param afferentObject
   * @param oldChunk
   * @param memory
   * @return
   * @see org.jactr.modules.pm.common.memory.IPerceptualEncoder#update(org.commonreality.object.IAfferentObject,
   *      org.jactr.core.chunk.IChunk,
   *      org.jactr.modules.pm.common.memory.IPerceptualMemory)
   */
  public IChunk update(IAfferentObject afferentObject, IChunk oldChunk,
      IPerceptualMemory memory)
  {
    if (isTooDirty(afferentObject, oldChunk, (IVisualMemory) memory))
      oldChunk = encode(afferentObject, memory);
    else
      updateSlots(afferentObject, oldChunk, (IVisualMemory) memory);

    return oldChunk;
  }

  /**
   * return the chunktype
   * 
   * @param visualMemory
   * @return
   */
  final private IChunkType getVisualObjectChunkType(IVisualMemory visualMemory)
  {
    if (_chunkType == null)
      try
      {
        _chunkType = visualMemory.getModule().getModel().getDeclarativeModule()
            .getChunkType(_chunkTypeName).get();

        if (_chunkType == null) throw new NullPointerException();
      }
      catch (Exception e)
      {
        throw new IllegalStateException(
            "Failed to retrieve reference to chunktype " + _chunkTypeName
                + ". Has it been installed?", e);
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
