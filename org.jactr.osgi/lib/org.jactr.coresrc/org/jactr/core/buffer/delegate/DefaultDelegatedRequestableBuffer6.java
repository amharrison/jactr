package org.jactr.core.buffer.delegate;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.six.AbstractRequestableBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.IMessageBuilder;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.production.request.IRequest;

public abstract class DefaultDelegatedRequestableBuffer6 extends
    AbstractRequestableBuffer6 implements IDelegatedRequestableBuffer
{

  /**
   * Logger definition
   */
  private static final transient Log   LOGGER = LogFactory
                                                  .getLog(DefaultDelegatedRequestableBuffer6.class);

  private Collection<IRequestDelegate> _processors;

  private IChunk                       _sourceChunk;

  public DefaultDelegatedRequestableBuffer6(String name, IModule module)
  {
    super(name, module);
    _processors = new ArrayList<IRequestDelegate>();
  }

  public void addRequestDelegate(IRequestDelegate processor)
  {
    _processors.add(processor);
  }

  public Collection<IRequestDelegate> getRequestDelegates()
  {
    return _processors;
  }

  public void removeRequestDelegate(IRequestDelegate processor)
  {
    _processors.remove(processor);
  }

  public boolean willAccept(IRequest request)
  {
    for (IRequestDelegate delegate : getRequestDelegates())
      if (delegate.willAccept(request)) return true;
    return false;
  }

  @Override
  protected boolean requestInternal(IRequest request, double requestTime)
      throws IllegalArgumentException
  {
    for (IRequestDelegate delegate : getRequestDelegates())
      if (delegate.willAccept(request) && delegate.request(request, this, requestTime))
        return true;

    IModel model = getModel();
    if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
    {
      IMessageBuilder sb = Logger.messageBuilder();
      sb.append(getName()).append(" could not handle this request:");
      sb.append(request.toString()).append(
          ", ignoring. The module's log may have additional details.");
      String msg = sb.toString();

      LOGGER.debug(msg);
      Logger.log(model, Logger.Stream.BUFFER, sb);
    }

    return false;
  }

  protected void setSourceChunkInternal(IChunk sourceChunk)
  {
    _sourceChunk = sourceChunk;
  }

  @Override
  protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  {
    IChunk currentSource = getSourceChunk();
    IChunk errorChunk = getErrorChunk();

    /*
     * did something go wrong? set the states..
     */
    if (errorChunk.equals(chunkToInsert))
    {
      if (currentSource != null) removeSourceChunk(currentSource);
      setStateChunk(errorChunk);

      chunkToInsert = null;
    }

    /*
     * all is good, let's set the chunk
     */
    if (chunkToInsert != null)
    {
      if (currentSource != null) removeSourceChunk(currentSource);

      setSourceChunkInternal(chunkToInsert);
      setStateChunk(getFreeChunk());
      setBufferChunk(getFullChunk());
    }

    return chunkToInsert;
  }

  @Override
  protected IChunk getSourceChunkInternal()
  {
    return _sourceChunk;
  }

  @Override
  protected Collection<IChunk> getSourceChunksInternal(
      Collection<IChunk> container)
  {
    if (_sourceChunk != null) container.add(_sourceChunk);

    return container;
  }

  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    if (chunkToRemove == null) return false;

    IChunk current = getSourceChunk();

    if (current != null && current.equals(chunkToRemove))
    {
      setSourceChunkInternal(null);
      setStateChunk(getFreeChunk());
      setBufferChunk(getEmptyChunk());
      setErrorChunk(null);
      return true;
    }

    return false;
  }
  
  @Override
  protected Collection<IChunk> clearInternal()
  {
    for(IRequestDelegate delegate : _processors)
      delegate.clear();
    
    Collection<IChunk> rtn = super.clearInternal();
    return rtn;
  }

}