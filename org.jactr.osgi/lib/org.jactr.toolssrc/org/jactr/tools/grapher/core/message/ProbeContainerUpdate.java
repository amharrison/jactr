package org.jactr.tools.grapher.core.message;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

public class ProbeContainerUpdate extends AbstractTransformedEvent
{
  /**
   * 
   */
  private static final long          serialVersionUID = 1526277260289701595L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(ProbeContainerUpdate.class);

  private final Map<Long, Number>    _data;

  private final double               _windowSize;

  public ProbeContainerUpdate(String modelName,
 double simulationTime,
      Map<Long, Number> data, double windowSize)
  {
    super(modelName, null, System.currentTimeMillis(), simulationTime, null);
    _data = new TreeMap<Long, Number>(data);
    _windowSize = windowSize;
  }

  public Map<Long, Number> getData()
  {
    return _data;
  }

  public double getWindowSize()
  {
    return _windowSize;
  }
}
