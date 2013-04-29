package org.jactr.modules.threaded.goal.buffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.buffer.delegate.AddChunkTypeRequestDelegate;
import org.jactr.core.buffer.delegate.AsynchronousRequestDelegate;
import org.jactr.core.buffer.delegate.IDelegatedRequestableBuffer;
import org.jactr.core.buffer.delegate.IRequestDelegate;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.six.AbstractCapacityBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.utils.parameter.ParameterHandler;

public class DefaultCapacityGoalBuffer6 extends AbstractCapacityBuffer6
    implements IDelegatedRequestableBuffer
{

  /**
   * Logger definition
   */
  static private transient Log         LOGGER               = LogFactory
                                                                .getLog(DefaultCapacityGoalBuffer6.class);

  static final public String           CHUNK_CAPACITY_PARAM = "ChunkCapacity";

  int                                  _hardChunkCapacity   = 1;

  private Collection<IRequestDelegate> _delegates;

  public DefaultCapacityGoalBuffer6(String name, IModule module)
  {
    super(name, module);
    setG(20);
    setActivation(1);
    setEjectionPolicy(EjectionPolicy.MostRecentlyMatched);
    _delegates = new ArrayList<IRequestDelegate>();
    
    /*
     * by default we accept +goal> =chunk and +goal> isa chunk as add operations
     * immediately
     */

    AsynchronousRequestDelegate ard = new AddChunkRequestDelegate() {
      protected double computeCompletionTime(double startTime,
          IRequest request, IActivationBuffer buffer)
      {
        // ensures that adds occur with modify & remove, otherwise,
        // they'd be off by defAct time
        return startTime;
      }
    };
    ard.setAsynchronous(true);
    ard.setUseBlockingTimedEvents(false);
    addRequestDelegate(ard);

    ard = new AddChunkTypeRequestDelegate() {
      protected double computeCompletionTime(double startTime,
          IRequest request, IActivationBuffer buffer)
      {
        // ensures that adds occur with modify & remove
        return startTime;
      }
    };
    ard.setAsynchronous(true);
    ard.setUseBlockingTimedEvents(false);
    addRequestDelegate(ard);
  }

  @Override
  protected void chunkInserted(IChunk insertedChunk)
  {
    super.chunkInserted(insertedChunk);
    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.GOAL, "Added " + insertedChunk + " "
          + getSourceChunks());
  }

  @Override
  protected void chunkRemoved(IChunk removedChunk)
  {
    super.chunkRemoved(removedChunk);
    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.GOAL, "Removed " + removedChunk + " "
          + getSourceChunks());
  }

  /**
   * has capacity been reached?
   * 
   * @return
   */
  protected boolean isCapacityReached()
  {
    try
    {
      getLock().readLock().lock();
      return getTimesAndChunks().size() >= _hardChunkCapacity;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  /**
   * goal buffers always accept any chunk
   */
  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return true;
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Collection<String> rtn = new ArrayList<String>(super
        .getPossibleParameters());
    rtn.add(CHUNK_CAPACITY_PARAM);
    return rtn;
  }

  @Override
  public Collection<String> getSetableParameters()
  {
    Collection<String> rtn = new ArrayList<String>(super.getSetableParameters());
    rtn.add(CHUNK_CAPACITY_PARAM);
    return rtn;
  }

  @Override
  public String getParameter(String key)
  {
    if (CHUNK_CAPACITY_PARAM.equalsIgnoreCase(key))
      return "" + _hardChunkCapacity;

    return super.getParameter(key);
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (CHUNK_CAPACITY_PARAM.equalsIgnoreCase(key))
    {
      setHardChunkCapacity(((Number) ParameterHandler.numberInstance().coerce(
          value)).intValue());
    }
    else
      super.setParameter(key, value);
  }

  public void setHardChunkCapacity(int numberOfChunks)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Setting capacity to " + numberOfChunks);
    int old = _hardChunkCapacity;
    try
    {
      getLock().writeLock().lock();
      _hardChunkCapacity = numberOfChunks;
    }
    finally
    {
      getLock().writeLock().unlock();
    }

    if (getEventDispatcher().hasListeners())
      getEventDispatcher().fire(
          new ActivationBufferEvent(this,
              ActivationBufferEvent.Type.PARAMETER_CHANGED,
              CHUNK_CAPACITY_PARAM, old, numberOfChunks));
  }

  @Override
  protected boolean requestInternal(IRequest request, double requestTime)
      throws IllegalArgumentException
  {
    for (IRequestDelegate delegate : _delegates)
      if (delegate.willAccept(request) && delegate.request(request, this, requestTime))
        return true;
    return false;
  }

  public boolean willAccept(IRequest request)
  {
    for (IRequestDelegate delegate : _delegates)
      if (delegate.willAccept(request)) return true;
    return false;
  }

  public void addRequestDelegate(IRequestDelegate processor)
  {
    _delegates.add(processor);
  }

  public Collection<IRequestDelegate> getRequestDelegates()
  {
    return Collections.unmodifiableCollection(_delegates);
  }

  public void removeRequestDelegate(IRequestDelegate processor)
  {
    _delegates.remove(processor);
  }

}
