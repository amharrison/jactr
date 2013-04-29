package org.jactr.tools.marker;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.tools.marker.impl.MarkerEvent;

/**
 * singleton manager for IMarker's associated with the current runtime
 * 
 * @author harrison
 */
public class MarkerManager
{
  /**
   * Logger definition
   */
  static private final transient Log                                LOGGER    = LogFactory
                                                                                  .getLog(MarkerManager.class);

  static final MarkerManager                                        _instance = new MarkerManager();

  private final ACTREventDispatcher<MarkerManager, IMarkerListener> _dispatcher;

  private long                                                      _lastId   = 0;

  private Map<IModel, Set<IMarker>>                                 _activeMarkers;

  static public MarkerManager get()
  {
    return _instance;
  }

  protected MarkerManager()
  {
    _dispatcher = new ACTREventDispatcher<MarkerManager, IMarkerListener>();
    _activeMarkers = new HashMap<IModel, Set<IMarker>>();
  }

  public void addListener(IMarkerListener listener, Executor executor)
  {
    _dispatcher.addListener(listener, executor);
  }

  public void removeListener(IMarkerListener listener)
  {
    _dispatcher.removeListener(listener);
  }

  public Set<IMarker> getMarkers(IModel model, Set<IMarker> container)
  {
    if (container == null) container = new HashSet<IMarker>();
    Set<IMarker> forModel = _activeMarkers.get(model);
    if (forModel != null) container.addAll(forModel);
    return container;
  }

  public long newId()
  {
    return ++_lastId;
  }

  public void opened(IMarker marker)
  {
    IModel markerModel = marker.getModel();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s opened @ %.2f", marker.getName(),
          marker.getStartTime()));

    Set<IMarker> markers = _activeMarkers.get(markerModel);
    if (markers == null)
    {
      markers = new HashSet<IMarker>();
      _activeMarkers.put(markerModel, markers);
    }
    markers.add(marker);

    if (Logger.hasLoggers(markerModel))
      Logger.log(markerModel, "MARKERS", String.format("+ %s(%d)[%s]",
          marker.getName(), marker.getId(), marker.getType()));

    if (_dispatcher.hasListeners())
      _dispatcher.fire(new MarkerEvent(this, marker.getModel().getAge(),
          MarkerEvent.Type.OPENED, marker));
  }

  public void closed(IMarker marker)
  {
    IModel markerModel = marker.getModel();
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("%s closed @ %.2f", marker.getName(),
          marker.getEndTime()));

    Set<IMarker> markers = _activeMarkers.get(markerModel);
    if (markers != null)
    {
      markers.remove(marker);
      if (markers.size() == 0) _activeMarkers.remove(markerModel);
    }

    if (Logger.hasLoggers(markerModel))
      Logger.log(markerModel, "MARKERS", String.format("- %s(%d)[%s]",
          marker.getName(), marker.getId(), marker.getType()));

    if (_dispatcher.hasListeners())
      _dispatcher.fire(new MarkerEvent(this, marker.getModel().getAge(),
          MarkerEvent.Type.CLOSED, marker));
  }

}
