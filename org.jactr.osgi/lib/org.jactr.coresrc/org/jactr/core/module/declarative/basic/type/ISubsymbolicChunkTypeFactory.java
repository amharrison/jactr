package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import java.util.Collection;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;

public interface ISubsymbolicChunkTypeFactory
{

  public ISubsymbolicChunkType newSubsymbolicChunkType();

  public void bind(ISubsymbolicChunkType subsymbolic, IChunkType wrapper,
      Collection<IChunkType> parents);

  public void unbind(ISubsymbolicChunkType subsymbolic);

  public void merge(ISubsymbolicChunkType master, ISubsymbolicChunkType mergie);

  public void dispose(ISubsymbolicChunkType subsymbolic);

}
