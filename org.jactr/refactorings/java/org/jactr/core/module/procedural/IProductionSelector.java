package org.jactr.core.module.procedural;

import java.util.Collection;

import org.jactr.core.production.IInstantiation;

/*
 * default logging
 */

public interface IProductionSelector
{

  public IInstantiation select(Collection<IInstantiation> instantiations);
}
