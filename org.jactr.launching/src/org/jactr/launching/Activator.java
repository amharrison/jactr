package org.jactr.launching;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER    = LogFactory
                                                   .getLog(Activator.class);

  // The plug-in ID
  public static final String         PLUGIN_ID = "org.jactr.launching";

  // The shared instance
  private static Activator           plugin;

  /**
   * The constructor
   */
  public Activator()
  {
    plugin = this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
   */
  @Override
  public void start(BundleContext context) throws Exception
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Activating " + PLUGIN_ID);
    super.start(context);
  }

  /*
   * (non-Javadoc)
   * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
   */
  @Override
  public void stop(BundleContext context) throws Exception
  {
    plugin = null;
    super.stop(context);
  }

  /**
   * Returns the shared instance
   * 
   * @return the shared instance
   */
  public static Activator getDefault()
  {
    return plugin;
  }

  /**
   * used to modify the permissions of all bundles that are not installed into the default
   * locations..
   * @param optionValue
   */
  public void modifyPermissions(String optionValue)
  {
    

  }

}
