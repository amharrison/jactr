package org.jactr.core.module.procedural;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;

import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;

public interface IProductionInstantiator
{

  public Collection<IInstantiation> instantiate(IProduction production,
      Collection<Map<String, Object>> provisionalBindings)
      throws CannotInstantiateException;
}
