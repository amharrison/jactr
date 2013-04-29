package org.jactr.tools.grapher.core.message;

/*
 * default logging
 */
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.tracer.transformer.AbstractTransformedEvent;

public class StringTableMessage extends AbstractTransformedEvent
{

  /**
   * 
   */
  private static final long          serialVersionUID = -5237044356867150955L;

  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(StringTableMessage.class);

  private final Map<Long, String>    _stringTable;

  public StringTableMessage(String modelName, double simulationTime,
      Map<Long, String> stringTable)
  {
    super(modelName, null, System.currentTimeMillis(), simulationTime, null);
    _stringTable = Collections.unmodifiableMap(new TreeMap<Long, String>(
        stringTable));

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("StringTable : %s", _stringTable));
  }

  public Map<Long, String> getStringTable()
  {
    return _stringTable;
  }
}
