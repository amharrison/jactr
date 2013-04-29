package org.jactr.tools.marker.tracer;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.marker.impl.MarkerEvent;
import org.jactr.tools.marker.impl.MarkerEvent.Type;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

public class MarkerTransformedEvent extends AbstractTransformedEvent
{
  /**
   * 
   */
  private static final long          serialVersionUID = -6128344379838765819L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(MarkerTransformedEvent.class);

  private final Map<String, String>  _properties      = new TreeMap<String, String>();

  private long                       _id;

  private final boolean              _isClosed;

  public MarkerTransformedEvent(MarkerEvent me)
  {
    super(me.getMarker().getModel().getName(), "MarkerManager", me
        .getSystemTime(), me.getSimulationTime(), null);
    me.getMarker().getProperties(_properties);
    _id = me.getMarker().getId();
    _isClosed = me.getType() == Type.CLOSED;
  }


  public long getMarkerId()
  {
    return _id;
  }

  public boolean isOpen()
  {
    return !_isClosed;
  }

  public boolean isClosed()
  {
    return _isClosed;
  }

  public Map<String, String> getProperties(Map<String, String> container)
  {
    if (container == null) container = new TreeMap<String, String>();
    container.putAll(_properties);
    return container;
  }

}
