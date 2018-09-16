package org.jactr.tools.marker;

import org.jactr.tools.marker.impl.MarkerEvent;

/*
 * default logging
 */

public interface IMarkerListener
{

  public void markerOpened(MarkerEvent me);

  public void markerClosed(MarkerEvent me);
}
