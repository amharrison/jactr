package org.jactr.core.utils;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * default impl of IAdaptable that will handle the object's class heirarchhy
 * 
 * @author harrison
 */
public class DefaultAdaptable implements IAdaptable
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultAdaptable.class);

  public Object getAdapter(Class adapterClass)
  {
    if (adapterClass.isAssignableFrom(getClass())) return this;

    // for (Class cl : getClass().getClasses())
    // if (cl.equals(adapterClass)) return this;

    return null;
  }

}
