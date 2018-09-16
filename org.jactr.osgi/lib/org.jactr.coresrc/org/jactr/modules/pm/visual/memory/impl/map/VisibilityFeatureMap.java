package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;

/**
 * meta-feature map with visibility
 */
public class VisibilityFeatureMap extends
    AbstractSortedVisualFeatureMap<Boolean>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(VisibilityFeatureMap.class);

  public VisibilityFeatureMap()
  {
    super(null, IVisualPropertyHandler.VISIBLE);
  }
  
  
  /**
   * this feature map isn't used in visual searches
   * @param request
   * @return
   * @see org.jactr.modules.pm.common.memory.map.AbstractFeatureMap#isInterestedIn(org.jactr.core.production.request.ChunkTypeRequest)
   */
  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    return true;
  }

  @Override
  protected boolean isValidValue(ISlot slot)
  {
    Object value = slot.getValue(); 
    if(value instanceof Boolean) return true;
    
    /*
     * check the name of the content
     */
    if(value!=null)
    {
      String str = value.toString();
      if("true".equalsIgnoreCase(str)) return true;
      if("false".equalsIgnoreCase(str)) return true;
    }
    return false;
  }

  @Override
  protected Boolean toData(ISlot slot)
  {
    Object value = slot.getValue(); 
    if(value instanceof Boolean) return (Boolean)value;
    
    /*
     * check the name of the content
     */
    if(value!=null)
    {
      String str = value.toString();
      if("true".equalsIgnoreCase(str)) return true;
      if("false".equalsIgnoreCase(str)) return false;
    }
    
    return false;
  }

  @Override
  protected Boolean extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      return getHandler().isVisible(afferentObject);
    }
    catch (UnknownPropertyNameException e)
    {
      LOGGER.error("Exception ", e);
      return Boolean.FALSE;
    }
  }
  
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    equals(Boolean.TRUE, results);
  }

}
