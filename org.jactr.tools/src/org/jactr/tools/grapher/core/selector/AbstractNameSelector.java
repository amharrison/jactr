package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.tools.grapher.core.container.IProbeContainer;
import org.jactr.tools.grapher.core.container.ProbeContainer;
import org.jactr.tools.grapher.core.probe.IProbe;
import org.jactr.tools.marker.markerof.IMarkerOf;

public abstract class AbstractNameSelector<T> implements ISelector<T>
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER = LogFactory
                                                  .getLog(AbstractNameSelector.class);

  private Matcher                      _patternMatcher;

  private Collection<IProbe<T>>        _trackerTemplates;

  private Collection<IMarkerOf<T>>     _markersOf;

  private Map<String, IProbeContainer> _containers;

  private String                       _groupId = "";

  public AbstractNameSelector(String regex)
  {
    _patternMatcher = Pattern.compile(regex).matcher("");
    _trackerTemplates = new ArrayList<IProbe<T>>();
    _containers = new TreeMap<String, IProbeContainer>();
    _markersOf = new ArrayList<IMarkerOf<T>>();
  }

  public void setGroupId(String group)
  {
    _groupId = group;
  }

  public String getGroupId()
  {
    return _groupId;
  }

  public void add(IProbe<T> tracker)
  {
    _trackerTemplates.add(tracker);
  }

  public void add(IMarkerOf<T> markerOf)
  {
    _markersOf.add(markerOf);
  }

  abstract protected String getName(T element);

  public boolean matches(T element)
  {
    String name = getName(element);
    if (name != null)
    {
      _patternMatcher.reset(name);
      boolean matched = _patternMatcher.matches();
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(_patternMatcher + " matches " + name + " : " + matched);
      return matched;
    }
    else if (LOGGER.isDebugEnabled()) LOGGER.debug("null name for " + element);

    return false;
  }

  protected IProbeContainer getProbeContainer(T element)
  {
    return _containers.get(getContainerName(element));
  }

  protected String getContainerName(T element)
  {
    return getName(element);
  }

  public IProbeContainer install(T element, IProbeContainer container)
  {
    String name = getContainerName(element);
    container = new ProbeContainer(name, container);
    _containers.put(name, container);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Installed probe container into " + name);

    Executor executor = ExecutorServices
        .getExecutor(ExecutorServices.BACKGROUND);
    // Executor executor = ExecutorServices.INLINE_EXECUTOR;
    for (IProbe<T> tracker : _trackerTemplates)
    {
      tracker = tracker.instantiate(element);
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Installing probe " + tracker);
      tracker.install(element, executor);
      container.add(tracker);
    }

    installMarkersOf(element);

    return container;
  }

  public void installMarkersOf(T element)
  {
    for (IMarkerOf<T> marker : _markersOf)
      marker.install(element);
  }

}
