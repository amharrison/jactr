package org.jactr.modules.pm.common.memory.impl;

import org.jactr.core.chunk.IChunk;

/*
 * default logging
 */

/**
 * interface for the handling of index chunks
 * 
 * @author harrison
 */
public interface IIndexManager
{

  public IChunk getIndexChunk(IChunk encodedChunk);
}
