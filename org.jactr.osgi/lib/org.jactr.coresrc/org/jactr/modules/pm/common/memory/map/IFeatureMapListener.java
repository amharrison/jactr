package org.jactr.modules.pm.common.memory.map;

/*
 * default logging
 */

public interface IFeatureMapListener
{

  public void featureUpdated(FeatureMapEvent event);
  public void featureRemoved(FeatureMapEvent event);
  public void featureAdded(FeatureMapEvent event);
}
