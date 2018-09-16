package org.jactr.core.buffer.misc;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.AbstractActivationBuffer;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.utils.collections.FastCollectionFactory;

/**
 * a proxy buffer that merely wraps an existing buffer with a new name, and
 * potentially new parameters. Otherwise, the contents will be the same.
 * 
 * @author harrison
 */
public class ProxyActivationBuffer extends AbstractActivationBuffer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProxyActivationBuffer.class);

  private final IActivationBuffer    _buffer;

  public ProxyActivationBuffer(String proxyName, IModel model, IModule module,
      IActivationBuffer actualBuffer)
  {
    super(proxyName, model, module);
    _buffer = actualBuffer;
  }

  @Override
  public Object getAdapter(Class adapterClass)
  {
    Object rtn = super.getAdapter(adapterClass);

    if (rtn == null) rtn = _buffer.getAdapter(adapterClass);

    return rtn;
  }

  /**
   * this will prevent automatic encoding when the chunk is removed from the
   * proxy, the behavior of the source buffer will determine what happens
   */
  @Override
  public boolean handlesEncoding()
  {
    return true;
  }

  /**
   * adds are just passed to the source buffer
   */
  @Override
  protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  {
    return _buffer.addSourceChunk(chunkToInsert);
  }

  /**
   * after verifying that the chunk is in the wrapped buffer, it delegates to
   * the wrapped buffer
   */
  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    Collection<IChunk> sources = FastCollectionFactory.newInstance();
    try
    {
      if (_buffer.getSourceChunks(sources).contains(chunkToRemove))
      {
        _buffer.removeSourceChunk(chunkToRemove);
        return true;
      }
      return false;
    }
    finally
    {
      FastCollectionFactory.recycle(sources);
    }
  }

  @Override
  protected IChunk getSourceChunkInternal()
  {
    return _buffer.getSourceChunk();
  }

  @Override
  protected Collection<IChunk> getSourceChunksInternal(
      Collection<IChunk> container)
  {
    return _buffer.getSourceChunks(container);
  }

}
