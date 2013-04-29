package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.BasicSlot;
import org.jactr.modules.pm.common.memory.map.DefaultFINSTFeatureMap;
import org.jactr.modules.pm.visual.IVisualModule;

public class FINSTVisualFeatureMap extends DefaultFINSTFeatureMap
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(FINSTVisualFeatureMap.class);

  public FINSTVisualFeatureMap(IModel model)
  {
    super(model, IVisualModule.ATTENDED_STATUS_SLOT);
  }
  
  @Override
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk, 
      ChunkTypeRequest originalSearchRequest)
  {
    super.fillSlotValues(mutableRequest, identifier, encodedChunk, originalSearchRequest);
    FINST finst = getFINST(identifier);

    if (finst != null)
      mutableRequest.addSlot(new BasicSlot(IVisualModule.TIME_STATUS_SLOT,
          finst.getTime()));
  }
  
  public boolean isInterestedIn(IAfferentObject object)
  {
    return AbstractVisualFeatureMap.getHandler().hasModality(object);
  }
}
