package org.jactr.modules.pm.common.memory.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.IPropertyHandler;
import org.jactr.core.slot.ISlot;

/**
 * A simple to use feature map that relies upon a single numeric (double) value.
 * The feature map is configured with the slotName used in the requests (i.e.,
 * visual-location.screen-x), the name of the CommonReality property associated
 * with the value (i.e., "visual.retinotopicLocation"), the boolean property
 * demarking the existence of valid modality information (i.e.,
 * "visual.isVisual"), a property handler to use to extract the data from the
 * afferent percept, and finally a default value to return should something go
 * wrong in extraction. <br/>
 * If configured with the above example values, the feature map would
 * automatically scan afferent objects with "visual.isVisual"=true, looking for
 * any that have "visual.retinotopicLocation". If it does, it will extract the
 * value as a double. You would then be able to make queries using
 * "visual-location.screen-x".
 * 
 * @author harrison
 */
public class DefaultNumericFeatureMap extends AbstractSortedFeatureMap<Double>
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultNumericFeatureMap.class);

  private final IPropertyHandler     _propertyHandler;

  private final String               _modalityName;

  private final double               _defaultValue;

  public DefaultNumericFeatureMap(String requestSlotName, String modalityName,
      String crPropertyName, IPropertyHandler handler, double defaultValue)
  {
    super(requestSlotName, crPropertyName);
    _propertyHandler = handler;
    _modalityName = modalityName;
    _defaultValue = defaultValue;
  }

  public IPropertyHandler getPropertyHandler()
  {
    return _propertyHandler;
  }

  public double getDefaultValue()
  {
    return _defaultValue;
  }

  public boolean isInterestedIn(IAfferentObject object)
  {
    try
    {
      if (!getPropertyHandler().getBoolean(_modalityName, object))
        return false;
    }
    catch (Exception e)
    {
      return false;
    }

    String crPropertyName = getRelevantPropertyName();
    if (crPropertyName != null && object.hasProperty(crPropertyName))
      return true;

    return false;
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

    String property = getRelevantPropertyName();
    try
    {
      return getPropertyHandler().getDouble(property, afferentObject);
    }
    catch (Exception e)
    {
      double defaultValue = getDefaultValue();

      LOGGER.warn(String.format(
          "could not extract %s. Using default value %.2f", property,
          defaultValue), e);
      return defaultValue;
    }
  }

}
