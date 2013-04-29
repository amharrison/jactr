package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import org.jactr.core.chunktype.IChunkType;

public interface IChunkTypeNamer
{

  public String generateName(IChunkType chunkType);
}
