package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.modalities.visual.geom.Dimension2D;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * meta-map with dimensional info
 * @author harrison
 *
 */
public class DimensionFeatureMap extends AbstractVisualFeatureMap<Dimension2D>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DimensionFeatureMap.class);
  
  private Map<IIdentifier, Dimension2D> _currentValues;

  public DimensionFeatureMap()
  {
    super(null, IVisualPropertyHandler.RETINAL_SIZE);
    _currentValues = new HashMap<IIdentifier, Dimension2D>();
  }

  @Override
  protected void addInformation(IIdentifier identifier, Dimension2D data)
  {
    _currentValues.put(identifier, data);
  }

  @Override
  protected void clearInternal()
  {
    _currentValues.clear();
  }
  
  /**
   * this feature map isn't used in visual searches
   * @param request
   * @return
   * @see org.jactr.modules.pm.common.memory.map.AbstractFeatureMap#isInterestedIn(org.jactr.core.production.request.ChunkTypeRequest)
   */
  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    return false;
  }

  @Override
  protected Dimension2D extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      Dimension2D dim = getHandler().getRetinalSize(afferentObject);
      return dim;
    }
    catch (UnknownPropertyNameException e)
    {
      LOGGER.error("Exception ", e);
      return null;
    }
  }

  
  

  @Override
  protected Dimension2D getCurrentValue(IIdentifier identifier)
  {
    return _currentValues.get(identifier);
  }

  @Override
  protected Dimension2D removeInformation(IIdentifier identifier)
  {
    return _currentValues.remove(identifier);
  }

  @Override
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    // TODO Auto-generated method stub
    
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {
    // TODO Auto-generated method stub
    
  }

}
