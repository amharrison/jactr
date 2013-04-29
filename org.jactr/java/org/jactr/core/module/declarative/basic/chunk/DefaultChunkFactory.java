package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.basic.DefaultChunk;
import org.jactr.core.model.IModel;

public class DefaultChunkFactory implements IChunkFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultChunkFactory.class);

  public IChunk newChunk(IModel model)
  {
    return new DefaultChunk(model);
  }

  public void bind(IChunk chunk, ISymbolicChunk symbolic,
      ISubsymbolicChunk subsymbolic)
  {
    DefaultChunk c5 = (DefaultChunk) chunk;

    c5.bind(symbolic, subsymbolic);
  }

  public void unbind(IChunk chunk, ISymbolicChunk symbolic,
      ISubsymbolicChunk subsymbolic)
  {
    DefaultChunk c5 = (DefaultChunk) chunk;

    c5.bind(null, null);
  }

  public void dispose(IChunk chunk)
  {
    chunk.dispose();
  }

  public void merge(IChunk master, IChunk mergie)
  {
    DefaultChunk m = (DefaultChunk) master;
    DefaultChunk s = (DefaultChunk) mergie;

    s.mergeInto(m);
    /*
     * still need to do the bindings
     */
  }

  public void copy(IChunk source, IChunk destination)
  {
    for (String key : source.getMetaDataKeys())
      destination.setMetaData(key, source.getMetaData(key));

    destination.setMetaData(COPIED_FROM_KEY, source);
  }

}
