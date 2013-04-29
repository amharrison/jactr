package org.jactr.modules.pm.aural.memory.impl.map;

/*
 * default logging
 */
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.UnknownPropertyNameException;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.ISlot;

public class AudibleFeatureMap extends
    AbstractSortedAuralFeatureMap<Boolean>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AudibleFeatureMap.class);

  public AudibleFeatureMap()
  {
    super(null, IAuralPropertyHandler.IS_AUDIBLE);
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
      return getHandler().isAudible(afferentObject);
    }
    catch (UnknownPropertyNameException e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String
            .format("Could not extract isAudible, returning false"));
      return Boolean.FALSE;
    }
  }
  
  @Override
  protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results)
  {
    equals(Boolean.TRUE, results);
  }

}
