package org.jactr.modules.pm.aural.memory.impl.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.aural.IAuralModule;

public class OnsetFeatureMap extends AbstractSortedAuralFeatureMap<Double>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(OnsetFeatureMap.class);

  public OnsetFeatureMap(String requestSlotName, String crPropertyName)
  {
    super(IAuralModule.ONSET_SLOT, IAuralPropertyHandler.ONSET);
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

  @Override
  protected Double extractInformation(IAfferentObject afferentObject)
  {
    try
    {
      return getHandler().getOnset(afferentObject);
    }
    catch(Exception e)
    {
      LOGGER.error("could not extract "+IAuralPropertyHandler.ONSET, e);
      return null;
    }
  }

}
