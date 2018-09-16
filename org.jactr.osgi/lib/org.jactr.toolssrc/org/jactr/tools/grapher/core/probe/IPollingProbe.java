package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */

public interface IPollingProbe<T> extends IProbe<T>
{

  public void update();
}
