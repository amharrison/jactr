package org.jactr.core.module.procedural.map.instance;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.procedural.map.template.IInstantiationMapTemplate;
import org.jactr.core.production.IProduction;

public abstract class AbstractInstantiationMap<T> implements IInstaniationMap<T>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(AbstractInstantiationMap.class);

  final private T                    _root;

  final private IInstantiationMapTemplate<T>     _template;

  final private IInstaniationMap        _parent;

  private boolean                    _activated = false;

  protected AbstractInstantiationMap(T root, IInstantiationMapTemplate<T> template,
      IInstaniationMap parent)
  {
    _root = root;
    _template = template;
    _parent = parent;
  }

  public IInstantiationMapTemplate<T> getTemplate()
  {
    return _template;
  }

  public IInstaniationMap getParent()
  {
    return _parent;
  }

  public T getRoot()
  {
    return _root;
  }

  public ProductionTable getProductionTable()
  {
    return null;
  }

  public boolean isActivated()
  {
    return _activated;
  }

  public void activate()
  {
    if (isActivated()) throw new IllegalStateException("Already activated");
    _activated = true;
  }

  public void deactivate()
  {
    if(!isActivated()) throw new IllegalStateException("Already deactivated");
    _activated = false;
  }

  protected void dirty(IProduction production)
  {
    getProductionTable().setDirty(production, true);
  }

  protected void dirty(Collection<IProduction> productions)
  {
    ProductionTable table = getProductionTable();
    for (IProduction prod : productions)
      table.setDirty(prod, true);
  }
}
