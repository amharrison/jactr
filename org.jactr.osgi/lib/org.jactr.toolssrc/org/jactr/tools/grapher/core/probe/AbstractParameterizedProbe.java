package org.jactr.tools.grapher.core.probe;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.utils.parameter.IParameterized;

public abstract class AbstractParameterizedProbe<T> implements IProbe<T>,
    IPollingProbe<T>
{
  /**
   * Logger definition
   */
  static private final transient Log    LOGGER      = LogFactory
                                                        .getLog(AbstractParameterizedProbe.class);

  final protected T                     _parameterized;

  final private String                  _name;

  final protected TreeMap<String, Object> _parameterMap;

  final private TreeSet<String>         _added;

  final private TreeSet<String>         _removed;

  final private Collection<Pattern>     _parameterPatterns;

  private boolean                       _shouldPoll = false;

  protected AbstractParameterizedProbe(String name, T parameterized)
  {
    _name = name;
    _parameterized = parameterized;
    _parameterMap = new TreeMap<String, Object>();
    _added = new TreeSet<String>();
    _removed = new TreeSet<String>();
    _parameterPatterns = new ArrayList<Pattern>();
  }

  public void setPollable(boolean pollable)
  {
    _shouldPoll = pollable;
  }

  public boolean isPolling()
  {
    return _shouldPoll;
  }

  public void update()
  {
    if (!_shouldPoll)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Not polling");
      return;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Polling " + getTrackedName());

    IParameterized parameterized = asParameterized(_parameterized);
    for (String parameter : parameterized.getPossibleParameters())
      if (isValidParameterName(parameter))
        set(parameter, parameterized.getParameter(parameter));
  }

  abstract protected IParameterized asParameterized(T parameterizedObject);

  abstract protected AbstractParameterizedProbe<T> newInstance(T parameterized);

  public IProbe<T> instantiate(T parameterized)
  {
    AbstractParameterizedProbe<T> tracker = newInstance(parameterized);
    for (Pattern pattern : _parameterPatterns)
      tracker._parameterPatterns.add(pattern);
    tracker.setPollable(_shouldPoll);
    return tracker;
  }

  public void addPattern(String regex)
  {
    _parameterPatterns.add(Pattern.compile(regex));
  }

  abstract public void install(T parameterized, Executor executor);

  synchronized public boolean getChanges(Set<String> additions,
      Map<String, Object> changes, Set<String> removed)
  {
    boolean changed = false;
    changed |= additions.addAll(_added);
    _added.clear();

    changed |= !_parameterMap.isEmpty();
    for (Map.Entry<String, Object> entry : _parameterMap.entrySet())
      changes.put(entry.getKey(), entry.getValue());
    _parameterMap.clear();

    changed |= removed.addAll(_removed);
    _removed.clear();

    return changed;
  }

  public String getTrackedName()
  {
    return _name;
  }

  protected boolean isValidParameterName(String parameter)
  {
    for (Pattern pattern : _parameterPatterns)
      if (pattern.matcher(parameter).matches()) return true;
    return false;
  }

  synchronized protected void set(String parameter, Object value)
  {
    if (!isValidParameterName(parameter))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug(parameter + " is not being tracked in " + getTrackedName());
      return;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Updated " + getTrackedName() + "." + parameter + " = "
          + value);

    if (!_parameterMap.containsKey(parameter))
    {
      _added.add(parameter);
      _removed.remove(parameter);
    }

    _parameterMap.put(parameter, value);
  }

  synchronized protected void remove(String parameter)
  {
    if (!isValidParameterName(parameter)) return;

    _removed.add(parameter);
    _parameterMap.remove(parameter);
  }

}
