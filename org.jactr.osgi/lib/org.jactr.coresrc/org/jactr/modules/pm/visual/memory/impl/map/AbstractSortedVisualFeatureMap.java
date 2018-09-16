package org.jactr.modules.pm.visual.memory.impl.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.visual.DefaultVisualPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.modules.pm.common.memory.map.AbstractSortedFeatureMap;

public abstract class AbstractSortedVisualFeatureMap<T> extends
    AbstractSortedFeatureMap<T>
{

  /**
   * Logger definition
   */
  static private final transient Log     LOGGER = LogFactory
                                                    .getLog(AbstractSortedVisualFeatureMap.class);

  static private final DefaultVisualPropertyHandler _propertyHandler = new DefaultVisualPropertyHandler();

  static protected IVisualPropertyHandler getHandler()
  {
    return _propertyHandler;
  }

  public AbstractSortedVisualFeatureMap(String requestSlotName,
      String crPropertyName)
  {
    super(requestSlotName, crPropertyName);
  }

  public boolean isInterestedIn(IAfferentObject object)
  {
    if (!getHandler().hasModality(object)) return false;

    String crPropertyName = getRelevantPropertyName();
    if (crPropertyName != null && object.hasProperty(crPropertyName))
      return true;

    return crPropertyName == null;
  }

  @Override
  protected void objectAdded(IAfferentObject object, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Added " + object.getIdentifier() + " = " + data);
  }

  @Override
  protected void objectUpdated(IAfferentObject object, T oldData, T newData)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Updated " + object.getIdentifier() + "  " + oldData
          + " " + newData);
  }

  @Override
  protected void objectRemoved(IAfferentObject object, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Removed " + object.getIdentifier() + " = " + data);
  }
}
