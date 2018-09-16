package org.jactr.core.module.declarative.basic.chunk;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.model.IModel;

/*
 * default logging
 */

/**
 * this is the factory responsible for creating, binding, merginv and destroying
 * IChunk wrappers. Usually, the default implementation is to be used. This is
 * separate from the theoretically motivated factories for symbolic and
 * subsymbolic components because the wrapper is largely an implementation
 * construct that derives its theoretic behavior from its contents.<br/>
 * <code>
 * IChunkType type = ...;
 * IChunkFactory cFac = ....;
 * ISymbolicChunkFactory scFac = ....;
 * ISubsymbolicChunkFacory sscFac = ....;
 * 
 * 
 * IChunk chunk = cFac.newChunk();
 * ISymbolicChunk sc = scFac.newSymbolicChunk();
 * ISubsymbolicChunk ssc = sscFac.newSubsymbolicChunk();
 * 
 * scFac.bind(sc, chunk, type);
 * sscFac.bind(ssc, chunk, type);
 * cFac.bind(chunk, sc, ssc);
 * 
 * 
 * </code>
 * 
 * @author harrison
 */
public interface IChunkFactory
{

  static public final String COPIED_FROM_KEY = "CopiedFrom";

  /**
   * create a new IChunk wrapper, its symbolic and subsymbolic contents should
   * not be set.
   * 
   * @return new, unbound (to sub/symbolic contents) chunk
   */
  public IChunk newChunk(IModel model);

  /**
   * bind this chunk wrapper to its related contents. This is a separate call so
   * that other factories can contribute their theoretically motivated contents.
   * 
   * @param chunk
   * @param symbolic
   * @param subsymbolic
   */
  public void bind(IChunk chunk, ISymbolicChunk symbolic,
      ISubsymbolicChunk subsymbolic);

  /**
   * disassociate the chunk from its contents
   * 
   * @param chunk
   * @param symbolic
   * @param subsymbolic
   */
  public void unbind(IChunk chunk, ISymbolicChunk symbolic,
      ISubsymbolicChunk subsymbolic);

  /**
   * dispose of an unneeded chunk wrapper. sub/symbolic contents will have
   * already been unbound
   * 
   * @param chunk
   */
  public void dispose(IChunk chunk);

  public void merge(IChunk master, IChunk mergie);

  /**
   * copy relevant metadata to a chunk that will (ultimately) be a copy
   * 
   * @param source
   * @param destination
   */
  public void copy(IChunk source, IChunk destination);
}
