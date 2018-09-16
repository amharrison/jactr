package org.jactr.core.utils;

/*
 * default logging
 */

/**
 * Most adapted interfaces will extend this
 * 
 * @author harrison
 */
public interface VersionedWrapper<T>
{

  /**
   * returns string array, to support major.minor.revision, etc.
   * 
   * @return
   */
  public String[] getVersion();

  public T getSource();
}
