package org.jactr.tools.grapher.core.container;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.grapher.core.probe.IProbe;

public class ProbeContainer implements IProbeContainer
{
  /**
   * Logger definition
   */
  static private final transient Log  LOGGER = LogFactory
                                                 .getLog(ProbeContainer.class);

  private final String                _name;

  private final IProbeContainer       _parent;

  private Collection<IProbeContainer> _children;

  private Collection<IProbe>          _probes;

  public ProbeContainer(String name, IProbeContainer parent)
  {
    _name = name;
    _parent = parent;
    _children = new ArrayList<IProbeContainer>();
    _probes = new ArrayList<IProbe>();
    if (_parent != null) _parent.add(this);
  }

  public void add(IProbeContainer child)
  {
    _children.add(child);
  }

  public void add(IProbe probe)
  {
    _probes.add(probe);
  }

  public Collection<IProbeContainer> getChildren(
      Collection<IProbeContainer> container)
  {
    if (container == null)
      container = new ArrayList<IProbeContainer>(_children.size());
    container.addAll(_children);
    return container;
  }

  public String getName()
  {
    return _name;
  }

  public IProbeContainer getParent()
  {
    return _parent;
  }

  public Collection<IProbe> getProbes(Collection<IProbe> container)
  {
    if (container == null) container = new ArrayList<IProbe>(_probes.size());
    container.addAll(_probes);
    return container;
  }

}
