package org.jactr.core.module.procedural;

/*
 * default logging
 */
import java.util.Set;

import org.jactr.core.production.IProduction;

/**
 * code responsible for assembling the list of productions that may be
 * instantiated given the contents of the buffers.
 * 
 * @author harrison
 */
public interface IConflictSetAssembler
{

  public void setProceduralModule(IProceduralModule module);

  public Set<IProduction> getConflictSet(Set<IProduction> container);
}
