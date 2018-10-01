package org.jactr.core.utils.parameter;

/*
 * default logging
 */
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ParameterHelper
{
  /**
   * Logger definition
   */
  static private final transient Log         LOGGER = LogFactory
                                                        .getLog(ParameterHelper.class);

  private Map<String, ParameterProcessor<?>> _parameterProcessors;

  private Map<String, String>                _deferredParameters;

  public ParameterHelper()
  {
    // lazy
    // _parameterProcessors = new TreeMap<String, ParameterProcessor<?>>();
    // _deferredParameters = new TreeMap<String, String>();
  }

  public boolean hasProcessor(String parameterName)
  {
    if (_parameterProcessors != null)
      return _parameterProcessors.get(parameterName.toLowerCase()) != null;
    return false;
  }

  public void addProcessor(ParameterProcessor<?> processor)
  {
    if (_parameterProcessors == null)
      _parameterProcessors = new TreeMap<String, ParameterProcessor<?>>();

    ParameterProcessor<?> old = _parameterProcessors.put(processor
        .getParameterName().toLowerCase(), processor);
    if (old != null)
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Replaced processor[%s] for %s with [%s]",
            old, processor.getParameterName(), processor));
  }

  public void removeProcessor(ParameterProcessor<?> processor)
  {
    if (_parameterProcessors != null)
      _parameterProcessors.remove(processor.getParameterName().toLowerCase());
  }

  public void setParameter(String name, String value) throws ParameterException
  {
    if (_parameterProcessors == null)
    {
      if (_deferredParameters == null)
        _deferredParameters = new TreeMap<String, String>();
      _deferredParameters.put(name, value);
      return;
    }

    String pName = name.toLowerCase();
    ParameterProcessor<?> processor = _parameterProcessors.get(pName);
    if (processor != null)
    {
      if (_deferredParameters != null) _deferredParameters.remove(name);
      processor.setParameter(value);
    }
    else
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "%s not currently known. Saving as deferred", name));

      if (_deferredParameters == null)
        _deferredParameters = new TreeMap<String, String>();

      _deferredParameters.put(name, value);
    }
  }

  public String getParameter(String name) throws ParameterException
  {
    String pName = name.toLowerCase();
    String value = null;

    if (_deferredParameters != null) value = _deferredParameters.get(name);

    if (value == null && _parameterProcessors != null)
    {
      ParameterProcessor<?> processor = _parameterProcessors.get(pName);
      if (processor != null) value = processor.getParameter();
    }

    return value;
  }

  /**
   * returns all the parameter names for all parameter handlers that are setable
   * and any known deferred parameters
   * 
   * @param container
   */
  public Set<String> getSetableParameterNames(Set<String> container)
  {
    if (_deferredParameters != null)
      container.addAll(_deferredParameters.keySet());

    if (_parameterProcessors != null)
      _parameterProcessors.values().forEach(p -> {
        if (p.isSetable()) container.add(p.getParameterName());
      });

    return container;
  }

  /**
   * return all the known parameter names
   * 
   * @param container
   */
  public Set<String> getParameterNames(Set<String> container)
  {
    if (_deferredParameters != null)
      container.addAll(_deferredParameters.keySet());

    if (_parameterProcessors != null)
      _parameterProcessors.values().forEach(p -> {
        container.add(p.getParameterName());
      });
    return container;
  }

  public void getDeferredParameters(Map<String, String> container)
  {
    if (_deferredParameters != null) container.putAll(_deferredParameters);
  }
}
