package org.jactr.tools.grapher.core.container;

/*
 * default logging
 */
import java.util.Collection;

import org.jactr.tools.grapher.core.probe.IProbe;

public interface IProbeContainer
{
  
  public IProbeContainer getParent();

  public Collection<IProbeContainer> getChildren(
      Collection<IProbeContainer> container);

  public void add(IProbeContainer child);

  public String getName();

  public void add(IProbe probe);

  public Collection<IProbe> getProbes(Collection<IProbe> container);
  
  
}
