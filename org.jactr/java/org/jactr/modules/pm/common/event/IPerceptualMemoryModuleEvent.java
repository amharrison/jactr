package org.jactr.modules.pm.common.event;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.IACTREvent;
import org.jactr.modules.pm.IPerceptualModule;

public interface IPerceptualMemoryModuleEvent<S extends IPerceptualModule, L extends IPerceptualMemoryModuleListener>
    extends IACTREvent<S, L>
{

  /**
   * returns the attended perceptual chunk or found perceptual index chunk
   * 
   * @return
   */
  public IChunk getChunk();

}
