package org.jactr.core.module.procedural;

import java.util.Collection;

import org.jactr.core.production.IInstantiation;

/**
 * code that is responsible for selecting the winning (to be fired)
 * instantiation from the conflict set
 * 
 * @author harrison
 */
public interface IProductionSelector
{

  public void setProceduralModule(IProceduralModule module);

  public IInstantiation select(Collection<IInstantiation> instantiations);
}
