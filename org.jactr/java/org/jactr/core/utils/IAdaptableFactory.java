package org.jactr.core.utils;

/*
 * default logging
 */

/**
 * an interface for creating an adaptor for a given object
 * 
 * @author harrison
 */
public interface IAdaptableFactory
{

  public <T> T adapt(Object sourceObject);

  /**
   * return true if we should cache this value for the life of the source object
   * 
   * @return
   */
  public boolean shouldCache();

  /**
   * return true if we should use a soft cache (clears on memory pressure)
   * 
   * @return
   */
  public boolean shouldSoftCache();
}
