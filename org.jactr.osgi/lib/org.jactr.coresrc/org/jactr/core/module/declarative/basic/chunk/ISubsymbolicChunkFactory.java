package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunktype.IChunkType;

/**
 * factory for the creation, binding, and destruction of theoretically motivated
 * subsymbolic chunk components
 * 
 * @author harrison
 */
public interface ISubsymbolicChunkFactory
{
  static public final String SUBSYMBOLICS_COPIED_KEY = "SubsymbolicsCopied";

  public ISubsymbolicChunk newSubsymbolicChunk();

  public void bind(ISubsymbolicChunk subsymbolic, IChunk wrapper,
      IChunkType type);

  /**
   * merge copy into master
   * 
   * @param master
   * @param copy
   */
  public void merge(ISubsymbolicChunk master, ISubsymbolicChunk copy);

  public void unbind(ISubsymbolicChunk subsymbolic);

  public void dispose(ISubsymbolicChunk subsymbolic);

  public void copy(ISubsymbolicChunk sourceSSC, ISubsymbolicChunk destinationSSC);
}
