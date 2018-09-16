package org.jactr.modules.pm.aural.memory.impl.map;

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
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.map.DefaultFINSTFeatureMap;

public class FINSTAuralFeatureMap extends DefaultFINSTFeatureMap
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(FINSTAuralFeatureMap.class);

  public FINSTAuralFeatureMap(IModel model)
  {
    super(model, IAuralModule.ATTENDED_STATUS_SLOT);
  }
  
  @Override
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest)
  {
    super.fillSlotValues(mutableRequest, identifier, encodedChunk, originalSearchRequest);
  }
  
  @Override
  public boolean isInterestedIn(IAfferentObject object)
  {
    return AbstractAuralFeatureMap.getHandler().hasModality(object);
  }
}
