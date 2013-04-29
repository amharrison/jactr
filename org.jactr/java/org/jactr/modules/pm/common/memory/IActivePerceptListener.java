package org.jactr.modules.pm.common.memory;

import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;

/**
 * listener that will be called when a cached percept chunk that is in any buffer
 * is either reencoded or removed
 * @author harrison
 *
 */
public interface IActivePerceptListener
{
  /**
   * when an attended chunk (oldChunk) has changed so much that a new
   * chunk has to be encoded (newChunk)
   * @param identifier TODO
   * @param oldChunk
   * @param newChunk
   */
  public void reencoded(IIdentifier identifier, IChunk oldChunk, IChunk newChunk);

  /**
   * when chunk's percept is no longer available. This is only communicated to
   * the listener if the chunk is currently in a buffer or is the object of an
   * recent search
   * 
   * @param identifier
   *          TODO
   * @param chunk
   */
  public void removed(IIdentifier identifier, IChunk chunk);
  

  public void updated(IIdentifier identifier, IChunk chunk);
  
  public void newPercept(IIdentifier identifier, IChunk chunk);
}