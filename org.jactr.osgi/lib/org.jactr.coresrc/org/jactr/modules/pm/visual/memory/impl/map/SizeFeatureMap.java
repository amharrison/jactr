package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.modalities.visual.geom.Dimension2D;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.visual.IVisualModule;

public class SizeFeatureMap extends AbstractSortedVisualFeatureMap<Double>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(SizeFeatureMap.class);

  public SizeFeatureMap()
  {
    super(IVisualModule.SIZE_SLOT, IVisualPropertyHandler.RETINAL_SIZE);
  }
  
  @Override
  protected Double extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      Dimension2D dim = getHandler().getRetinalSize(afferentObject);
      return dim.getWidth() * dim.getHeight();
    }
    catch (UnknownPropertyNameException e)
    {
      LOGGER.error("Exception ", e);
      return null;
    }
  }

  @Override
  protected boolean isValidValue(ISlot slot)
  {
    return slot.getValue() instanceof Number;
  }

  @Override
  protected Double toData(ISlot slot)
  {
    return ((Number) slot.getValue()).doubleValue();
  }

  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest)
  {
    Double size = getCurrentValue(identifier);
    
    if (size != null)
      mutableRequest.addSlot(new BasicSlot(IVisualModule.SIZE_SLOT, size));
    else
      if (LOGGER.isWarnEnabled()) LOGGER.warn("No size information for "+identifier);
  }

}
