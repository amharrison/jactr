package org.jactr.tools.marker.impl;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.tools.marker.IMarker;
import org.jactr.tools.marker.IMarkerListener;
import org.jactr.tools.marker.MarkerManager;

public class MarkerEvent extends
    AbstractACTREvent<MarkerManager, IMarkerListener>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MarkerEvent.class);

  static public enum Type {
    OPENED, CLOSED
  };

  private final Type    _type;

  private final IMarker _marker;

  public MarkerEvent(MarkerManager source, double simulationTime, Type type,
      IMarker marker)
  {
    super(source, simulationTime);
    _marker = marker;
    _type = type;
  }

  public Type getType()
  {
    return _type;
  }

  public IMarker getMarker()
  {
    return _marker;
  }

  @Override
  public void fire(IMarkerListener listener)
  {
    switch (getType())
    {
      case OPENED:
        listener.markerOpened(this);
        break;
      case CLOSED:
        listener.markerClosed(this);
        break;
    }
  }

}
