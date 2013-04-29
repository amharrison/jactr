package org.jactr.modules.pm.aural.memory.impl.map;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.jactr.modules.pm.aural.IAuralModule;

public class DurationFeatureMap extends OffsetFeatureMap
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DurationFeatureMap.class);

  private Map<IIdentifier, Double>   _onsetMap;

  public DurationFeatureMap()
  {
    super(IAuralModule.DURATION_SLOT);
    _onsetMap = new HashMap<IIdentifier, Double>();
  }

  protected void not(double time, Set<IIdentifier> results)
  {
    for(Map.Entry<IIdentifier, Double> onsetEntry : _onsetMap.entrySet())
      if(time!=getCurrentValue(onsetEntry.getKey()))
        results.add(onsetEntry.getKey());
  }

  protected void equals(double time, Set<IIdentifier> results)
  {
    for(Map.Entry<IIdentifier, Double> onsetEntry : _onsetMap.entrySet())
      if(time==getCurrentValue(onsetEntry.getKey()))
        results.add(onsetEntry.getKey());
  }

  protected void lessThan(double time, Set<IIdentifier> results)
  {
    for(Map.Entry<IIdentifier, Double> onsetEntry : _onsetMap.entrySet())
      if(time>getCurrentValue(onsetEntry.getKey()))
        results.add(onsetEntry.getKey());
  }

  protected void greaterThan(double time, Set<IIdentifier> results)
  {
    for(Map.Entry<IIdentifier, Double> onsetEntry : _onsetMap.entrySet())
      if(time<getCurrentValue(onsetEntry.getKey()))
        results.add(onsetEntry.getKey());
  }

  /**
   * returns current time or end time if available
   * 
   * @param identifier
   * @return
   * @see org.jactr.modules.pm.common.memory.map.AbstractFeatureMap#getCurrentValue(org.commonreality.identifier.IIdentifier)
   */
  @Override
  protected Double getCurrentValue(IIdentifier identifier)
  {
    double completedTime = super.getCurrentValue(identifier);
    double onset = getOnset(identifier);

    return completedTime - onset;
  }

  @Override
  protected Double extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      _onsetMap.put(afferentObject.getIdentifier(), getHandler().getOnset(
          afferentObject));
    }
    catch (Exception e)
    {

    }
    return super.extractInformation(afferentObject);
  }
  
  protected void objectRemoved(IAfferentObject afferentObject, Double offset)
  {
    super.objectRemoved(afferentObject, offset);
    _onsetMap.remove(afferentObject.getIdentifier());
  }
  
  protected double getOnset(IIdentifier identifier)
  {
    try
    {
      return _onsetMap.get(identifier); 
    }
    catch(NullPointerException npe)
    {
      return getCurrentTime();
    }
  }
}
