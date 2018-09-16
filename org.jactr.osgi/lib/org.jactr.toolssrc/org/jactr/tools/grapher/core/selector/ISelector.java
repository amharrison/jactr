package org.jactr.tools.grapher.core.selector;

import org.jactr.tools.grapher.core.container.IProbeContainer;
import org.jactr.tools.grapher.core.probe.IProbe;
import org.jactr.tools.marker.markerof.IMarkerOf;

/*
 * default logging
 */

public interface ISelector<T>
{

  public boolean matches(T element);

  public IProbeContainer install(T element, IProbeContainer parent);
  
  public void installMarkersOf(T element);

  public void add(ISelector selector);
  
  public void add(IProbe<T> trackerTemplate);

  public void add(IMarkerOf<T> markerOf);

  public String getGroupId();

  public void setGroupId(String group);
}
