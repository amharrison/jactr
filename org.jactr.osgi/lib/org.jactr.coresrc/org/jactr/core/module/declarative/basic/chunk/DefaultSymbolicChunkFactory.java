package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.basic.BasicSymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

public class DefaultSymbolicChunkFactory implements ISymbolicChunkFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultSymbolicChunkFactory.class);

  public ISymbolicChunk newSymbolicChunk()
  {
    return new BasicSymbolicChunk();
  }

  public void bind(ISymbolicChunk symbolicChunk, IChunk chunkWrapper,
      IChunkType type)
  {
    BasicSymbolicChunk bsc = (BasicSymbolicChunk) symbolicChunk;
    bsc.bind(chunkWrapper, type);
  }

  public void unbind(ISymbolicChunk symbolicChunk)
  {
    // noop
  }

  public void dispose(ISymbolicChunk symbolicChunk)
  {
    symbolicChunk.dispose();
  }

  public void merge(ISymbolicChunk master, ISymbolicChunk copy)
  {
    /*
     * if a merge is to occur, by definition the symbolics should be equal, so
     * this is a noop
     */
  }

  public void copy(ISymbolicChunk source, ISymbolicChunk destination)
  {
    for (ISlot slot : source.getSlots())
    {
      // this is the actual backing slot..
      IMutableSlot cs = (IMutableSlot) destination.getSlot(slot.getName());
      cs.setValue(slot.getValue());
    }
  }

}
