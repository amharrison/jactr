package org.jactr.modules.pm.aural.memory.impl.map;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.modalities.aural.DefaultAuralPropertyHandler;
import org.commonreality.modalities.aural.IAuralPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.jactr.modules.pm.common.memory.map.AbstractSortedFeatureMap;

public abstract class AbstractSortedAuralFeatureMap<T> extends
    AbstractSortedFeatureMap<T>
{

  /**
   * Logger definition
   */
  static private final transient Log     LOGGER = LogFactory
                                                    .getLog(AbstractSortedAuralFeatureMap.class);

  static private final DefaultAuralPropertyHandler _propertyHandler = new DefaultAuralPropertyHandler();

  static protected IAuralPropertyHandler getHandler()
  {
    return _propertyHandler;
  }

  public AbstractSortedAuralFeatureMap(String requestSlotName,
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

    return true;
  }

  @Override
  protected void objectAdded(IAfferentObject object, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Added " + object.getIdentifier() + " = " + data);
  }

  @Override
  protected void objectRemoved(IAfferentObject object, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Removed " + object.getIdentifier() + " = " + data);
  }

  @Override
  protected void objectUpdated(IAfferentObject object, T oldData, T newData)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Updated " + object.getIdentifier() + "  " + oldData
          + " " + newData);
  }
}
