package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;

/**
 * interface to configure or tweak newly created chunks. If you replace an
 * existing configurator, it might be necessary to call the original
 * configurator's configure method as well.
 * 
 * @author harrison
 */
public interface IChunkConfigurator
{

  public void configure(IChunk chunk);
}
