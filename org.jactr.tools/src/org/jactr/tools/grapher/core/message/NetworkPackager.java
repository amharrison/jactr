package org.jactr.tools.grapher.core.message;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class NetworkPackager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NetworkPackager.class);

  private Map<String, Long>          _stringTable;

  public NetworkPackager()
  {
    _stringTable = new TreeMap<String, Long>();
  }

  synchronized public Collection<ITransformedEvent> process(String modelName,
      Map<String, Object> probeContents, double when, double windowSize)
  {
    Map<Long, String> newStrings = new HashMap<Long, String>();
    Map<Long, Number> actualData = new TreeMap<Long, Number>();

    process(modelName, probeContents, newStrings, actualData);

    if (newStrings.size() == 0 && actualData.size() == 0)
      return Collections.emptyList();

    if (LOGGER.isDebugEnabled())
    {
      LOGGER.debug("new strings : " + newStrings);
      LOGGER.debug(" data : " + actualData);
    }

    Collection<ITransformedEvent> rtn = new ArrayList<ITransformedEvent>(2);
    if (newStrings.size() > 0)
      rtn.add(new StringTableMessage(modelName, when, newStrings));
    if (actualData.size() > 0)
      rtn.add(new ProbeContainerUpdate(modelName, when, actualData, windowSize));

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Packaged " + rtn.size() + " messages");

    return rtn;
  }

  private Long getStringId(String key, Map<Long, String> newStrings)
  {
    Long rtn = _stringTable.get(key);
    if (rtn == null)
    {
      rtn = new Long(_stringTable.size());
      _stringTable.put(key, rtn);
      newStrings.put(rtn, key);
    }

    return rtn;
  }

  protected void process(String root, Map<String, Object> probeContents,
      Map<Long, String> newStrings, Map<Long, Number> actualData)
  {
    for (Map.Entry<String, Object> entry : probeContents.entrySet())
    {
      String key = root + "." + entry.getKey();
      Object value = entry.getValue();

      if (value instanceof String) try
      {
        value = Double.parseDouble((String) value);
      }
      catch (NumberFormatException nfe)
      {

      }

      if (value instanceof Number)
      {
        Long stringId = getStringId(key, newStrings);
        actualData.put(stringId, (Number) value);
      }
      else if (value instanceof Map) try
      {
          process(entry.getKey(), (Map<String, Object>) value, newStrings,
              actualData);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not process ", e);
      }
    }
  }
}
