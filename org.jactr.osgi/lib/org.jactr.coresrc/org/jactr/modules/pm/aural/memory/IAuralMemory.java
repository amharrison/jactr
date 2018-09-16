package org.jactr.modules.pm.aural.memory;

/*
 * default logging
 */
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

public interface IAuralMemory extends IPerceptualMemory
{

  public IAuralModule getAuralModule();
}
