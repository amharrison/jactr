package org.jactr.tools.grapher.core.selector;

/*
 * default logging
 */
import java.util.Collection;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.chunktype.event.ChunkTypeListenerAdaptor;
import org.jactr.core.chunktype.event.IChunkTypeListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.grapher.core.container.IProbeContainer;

public class ChunkTypeSelector extends AbstractNameSelector<IChunkType>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkTypeSelector.class);

  private Collection<ChunkSelector>  _chunkSelectors;

  private IChunkTypeListener         _listener;

  public ChunkTypeSelector(String regex)
  {
    super(regex);
    _chunkSelectors = FastListFactory.newInstance();
    _listener = new ChunkTypeListenerAdaptor() {
      @Override
      public void chunkAdded(ChunkTypeEvent cte)
      {
        checkChunk(cte.getChunk(), getProbeContainer(cte.getChunk()
            .getSymbolicChunk().getChunkType()));
      }
    };
  }

  @Override
  protected String getName(IChunkType element)
  {
    return element.getSymbolicChunkType().getName();
  }

  public void add(ISelector selector)
  {
    _chunkSelectors.add((ChunkSelector) selector);
  }

  @Override
  public IProbeContainer install(IChunkType element, IProbeContainer container)
  {
    IProbeContainer rtnContainer = super.install(element, container);
    Executor executor = ExecutorServices.INLINE_EXECUTOR;
    element.addListener(_listener, executor);

    /*
     * process the known chunks
     */
    try
    {
      for (IChunk chunk : element.getSymbolicChunkType().getChunks())
        checkChunk(chunk, rtnContainer);
    }
    catch (Exception e)
    {
      LOGGER.error("Could not extract chunks from " + element, e);
    }

    return rtnContainer;
  }

  protected void checkChunk(IChunk chunk, IProbeContainer container)
  {
    for (ChunkSelector selector : _chunkSelectors)
      if (selector.matches(chunk)) selector.install(chunk, container);
  }
}
