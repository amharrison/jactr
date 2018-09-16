package org.jactr.core.module.procedural;

/*
 * default logging
 */
import java.util.Collection;

import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.VariableBindings;

/**
 * code responsible for instantiating a production (possibly multiple times)
 * given the provisional bindings (which are defined by the buffer contents)
 * 
 * @author harrison
 */
public interface IProductionInstantiator
{

  public void setProceduralModule(IProceduralModule module);

  public Collection<IInstantiation> instantiate(IProduction production,
      Collection<VariableBindings> provisionalBindings)
      throws CannotInstantiateException;
}
