package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.visual.DefaultVisualPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.modules.pm.common.memory.map.AbstractFeatureMap;

public abstract class AbstractVisualFeatureMap<T> extends AbstractFeatureMap<T>
{
  /**
   * Logger definition
   */
  static private final transient Log                LOGGER           = LogFactory
                                                                         .getLog(AbstractVisualFeatureMap.class);

  static private final DefaultVisualPropertyHandler _propertyHandler = new DefaultVisualPropertyHandler();

  static protected IVisualPropertyHandler getHandler()
  {
    return _propertyHandler;
  }


  public AbstractVisualFeatureMap(String requestSlotName, String crPropertyName)
  {
    super(requestSlotName, crPropertyName);
  }


  public boolean isInterestedIn(IAfferentObject object)
  {
    if (!getHandler().hasModality(object)) return false;

    String crPropertyName = getRelevantPropertyName();
    if (crPropertyName != null && object.hasProperty(crPropertyName))
      return true;

    return (crPropertyName == null);
  }

  
}
