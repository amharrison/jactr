package org.jactr.core.buffer;

import java.util.Set;

import org.jactr.core.chunk.IChunk;

/**
 * logic that is responsible for the setting, updating and tracking of source
 * activation for a given buffer. To override the
 * {@link DefaultSourceActivationSpreader} call
 * {@link AbstractActivationBuffer#setActivationSpreader(IActivationSpreader)}.
 * 
 * @author harrison
 */
public interface ISourceActivationSpreader
{

  public IActivationBuffer getBuffer();

  public void spreadSourceActivation();

  public void clearSourceActivation();

  public Set<IChunk> getActivatedChunks(Set<IChunk> container);
}
