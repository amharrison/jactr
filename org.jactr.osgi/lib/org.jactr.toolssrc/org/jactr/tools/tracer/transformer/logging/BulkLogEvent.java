package org.jactr.tools.tracer.transformer.logging;

/*
 * default logging
 */
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

public class BulkLogEvent extends AbstractTransformedEvent
{
  /**
   * 
   */
  private static final long          serialVersionUID = -2341017794798880473L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BulkLogEvent.class);

  private final Map<String, String>  _logData;

  private final boolean              _endOfCycle;

  public BulkLogEvent(String modelName, double simulationTime,
      Map<String, StringBuilder> logs, boolean endOfCycle)
  {
    super(modelName, modelName, System.currentTimeMillis(), simulationTime,
        null);
    _logData = new TreeMap<String, String>();
    for (Map.Entry<String, StringBuilder> entry : logs.entrySet())
      _logData.put(entry.getKey(), entry.getValue().toString());

    _endOfCycle = endOfCycle;
  }

  public Map<String, String> getLogData()
  {
    return Collections.unmodifiableMap(_logData);
  }

  public boolean isEndOfCycle()
  {
    return _endOfCycle;
  }
}
