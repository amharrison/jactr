package org.jactr.tools.marker.impl;

/*
 * default logging
 */
import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.tools.marker.IMarker;
import org.jactr.tools.marker.MarkerManager;

public class DefaultMarker implements IMarker, Serializable
{
  /**
   * 
   */
  private static final long          serialVersionUID = -173058682947783431L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultMarker.class);

  static public final String         DESCRIPTION = "description";

  static public final String         OPEN_TIME        = "openTime";

  static public final String         CLOSE_TIME       = "closeTime";

  static public final String         NAME             = "name";

  static public final String         TYPE             = "type";

  static public final String         MODEL_NAME       = "modelName";

  static public final String         ID               = "id";

  private final long                 _id;

  private final String               _name;

  private final String               _type;

  private double                     _startTime = Double.NaN;

  private double                     _endTime   = Double.NaN;

  private final Map<String, String>  _properties;

  private final IModel               _model;

  public DefaultMarker(IModel model, String name, String type)
  {
    _model = model;
    _id = MarkerManager.get().newId();
    _name = name;
    _type = type;
    _properties = new TreeMap<String, String>();
    setProperty(NAME, _name);
    setProperty(TYPE, _type);
    setProperty(MODEL_NAME, _model.getName());
    setProperty(ID, String.format("%d", _id));
  }

  public long getId()
  {
    return _id;
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getName()
  {
    return _name;
  }

  public String getType()
  {
    return _type;
  }

  public void setDescription(String description)
  {
    setProperty(DESCRIPTION, description);
  }

  public String getDescription()
  {
    return getProperty(DESCRIPTION);
  }

  public Map<String, String> getProperties(Map<String, String> container)
  {
    if (container == null) container = new TreeMap<String, String>();
    container.putAll(_properties);
    return container;
  }

  protected String getProperty(String property)
  {
    return _properties.get(property);
  }

  protected void setProperty(String property, String value)
  {
    _properties.put(property, value);
  }

  public double getStartTime()
  {
    return _startTime;
  }

  public double getEndTime()
  {
    return _endTime;
  }

  public void open(double time)
  {
    _startTime = time;
    setProperty(OPEN_TIME, String.format("%.3f", _startTime));
    MarkerManager.get().opened(this);
  }

  public boolean isOpen()
  {
    return !Double.isNaN(_startTime);
  }

  public void close(double time)
  {
    _endTime = time;
    setProperty(CLOSE_TIME, String.format("%.3f", _endTime));
    MarkerManager.get().closed(this);
  }

  public void instantanious(double time)
  {
    open(time);
    close(time);
  }

}
