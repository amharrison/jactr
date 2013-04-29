package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.model.IModel;

public interface IChunkTypeFactory
{

  public IChunkType newChunkType(IModel model);

  public void bind(IChunkType type, ISymbolicChunkType symbolic,
      ISubsymbolicChunkType subsymbolic);

  public void unbind(IChunkType type, ISymbolicChunkType symbolic,
      ISubsymbolicChunkType subsymbolic);

  public void merge(IChunkType master, IChunkType mergie);

  public void dispose(IChunkType type);
}
