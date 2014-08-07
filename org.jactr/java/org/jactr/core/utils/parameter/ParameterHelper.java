package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParameterHelper
{
  /**
   * Logger definition
   */
  static private final transient Log            LOGGER = LogFactory
                                                           .getLog(ParameterHelper.class);

  final private Map<String, ParameterProcessor> _parameterProcessors;

  final private Map<String, String>             _deferredParameters;

  public ParameterHelper()
  {
    _parameterProcessors = new TreeMap<String, ParameterProcessor>();
    _deferredParameters = new TreeMap<String, String>();
  }

  public void addProcessor(ParameterProcessor processor)
  {
    ParameterProcessor old = _parameterProcessors.put(processor
        .getParameterName().toLowerCase(), processor);
    if (old != null)
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Replaced processor[%s] for %s with [%s]",
            old, processor.getParameterName(), processor));
  }

  public void removeProcessor(ParameterProcessor processor)
  {
    _parameterProcessors.remove(processor.getParameterName().toLowerCase());
  }

  public void setParameter(String name, String value) throws ParameterException
  {
    String pName = name.toLowerCase();
    ParameterProcessor processor = _parameterProcessors.get(pName);
    if (processor != null)
    {
      _deferredParameters.remove(name);
      processor.setParameter(value);
    }
    else
      _deferredParameters.put(name, value);
  }

  public String getParameter(String name) throws ParameterException
  {
    String pName = name.toLowerCase();
    String value = null;
    ParameterProcessor processor = _parameterProcessors.get(pName);
    if (processor != null)
      value = processor.getParameter();
    else
      value = _deferredParameters.get(name);

    return value;
  }


}
