/*
 * Created on Feb 6, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.common.buffer;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6;
import org.jactr.core.buffer.delegate.IDelegatedRequestableBuffer;
import org.jactr.core.buffer.delegate.IRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.IModule;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.event.TimedEventEvent;
import org.jactr.core.queue.event.TimedEventListenerAdaptor;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.utils.collections.FastCollectionFactory;
import org.jactr.modules.pm.buffer.IEventTrackingActivationBuffer;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;

/**
 * @author developer
 */
public abstract class AbstractPMActivationBuffer6 extends
    DefaultDelegatedRequestableBuffer6 implements IDelegatedRequestableBuffer,
    IPerceptualBuffer, IEventTrackingActivationBuffer, ICompilableBuffer
{
  /**
   * logger definition
   */
  static private final Log          LOGGER = LogFactory
                                               .getLog(AbstractPMActivationBuffer6.class);

  /**
   * we track all timed events used for this buffer so that if a clear comes in,
   * we can abort
   */
  protected Collection<ITimedEvent> _pendingTimedEvents;

  public AbstractPMActivationBuffer6(String name, IModule module)
  {
    super(name, module);
    _pendingTimedEvents = new ArrayList<ITimedEvent>();
    addSlot(new BasicSlot(MODALITY_SLOT));
    addSlot(new BasicSlot(PREPARATION_SLOT));
    addSlot(new BasicSlot(PROCESSOR_SLOT));
    addSlot(new BasicSlot(EXECUTION_SLOT));
  }

  @Override
  public void dispose()
  {
    super.dispose();

    _pendingTimedEvents.clear();
    _pendingTimedEvents = null;
  }

  @Override
  public void initialize()
  {
    super.initialize();
    getModel().getTimedEventQueue().addTimedEventListener(
        new TimedEventListenerAdaptor() {
          @Override
          public void eventFired(TimedEventEvent tee)
          {
            eventAborted(tee);
          }

          @Override
          public void eventAborted(TimedEventEvent tee)
          {
            ITimedEvent event = tee.getTimedEvent();
            try
            {
              getLock().writeLock().lock();
              _pendingTimedEvents.remove(event);
            }
            finally
            {
              getLock().writeLock().unlock();
            }
          }
        }, ExecutorServices.INLINE_EXECUTOR);

  }

  @Override
  protected void grabReferences()
  {
    setStatusSlotContent(MODALITY_SLOT, getFreeChunk());
    setStatusSlotContent(PREPARATION_SLOT, getFreeChunk());
    setStatusSlotContent(PROCESSOR_SLOT, getFreeChunk());
    setStatusSlotContent(EXECUTION_SLOT, getFreeChunk());
    super.grabReferences();
  }

  /**
   * clear the buffer and abort any timed events that are pending
   * 
   * @see org.jactr.core.buffer.six.AbstractActivationBuffer6#clear()
   */
  @Override
  protected Collection<IChunk> clearInternal()
  {
    setModalityChunk(getFreeChunk());
    setProcessorChunk(getFreeChunk());
    setExecutionChunk(getFreeChunk());
    setPreparationChunk(getFreeChunk());

    return super.clearInternal();
  }

  @Override
  public void clear()
  {
    // will call clear internal
    super.clear();

    /*
     * we handle the aborting timed events here so that it doesn't occur in the
     * write lock which could result in deadlock since the events might have
     * locks of their own
     */
    Collection<ITimedEvent> events = FastCollectionFactory.newInstance();
    try
    {
      getLock().readLock().lock();
      if (_pendingTimedEvents.size() != 0)
 events.addAll(_pendingTimedEvents);
    }
    finally
    {
      getLock().readLock().unlock();
    }

    for (ITimedEvent element : events)
      element.abort();

    FastCollectionFactory.recycle(events);
  }

  /**
   * the super type uses a more specific test, but since we know perceptual
   * buffers are only going to be continaing the sanctioned perceptual chunks,
   * we don't need to do all the buffer containment tests and just the encoding
   * test should suffice.
   */
  @Override
  protected boolean shouldCopyOnInsertion(IChunk chunk)
  {
    return chunk.isEncoded();
  }

  /**
   * called when we want to queue a timed event onto the model's event queue
   * this also tracks all the pending events so that if they fire or get aborted
   * we are notified, and it permits us to abort when a clear is called
   * 
   * @param timedEvent
   */
  public void enqueueTimedEvent(ITimedEvent timedEvent)
  {
    try
    {
      getLock().writeLock().lock();
      _pendingTimedEvents.add(timedEvent);
    }
    finally
    {
      getLock().writeLock().unlock();
    }
    getModel().getTimedEventQueue().enqueue(timedEvent);
  }

  /**
   * check to see if a chunk of this chunktype can be added as a source chunk
   * 
   * @param chunkType
   * @return
   */
  abstract protected boolean isValidChunkType(IChunkType chunkType);

  @Override
  protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("attempting to insert " + chunkToInsert);

    IChunk currentSource = getSourceChunk();
    /*
     * ok, something will be changing..
     */
    IChunk errorChunk = getErrorChunk();

    /*
     * did something go wrong? set the states..
     */
    if (errorChunk.equals(chunkToInsert)
        || !isValidChunkType(chunkToInsert.getSymbolicChunk().getChunkType()))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(chunkToInsert + " was error or invalid of chunk type");

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

      setStateChunk(getFreeChunk());
      setBufferChunk(getFullChunk());
      setSourceChunkInternal(chunkToInsert);
    }

    return chunkToInsert;
  }

  /**
   * @see org.jactr.core.buffer.AbstractActivationBuffer#removeSourceChunkInternal(org.jactr.core.chunk.IChunk)
   */
  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("attempting to remove " + chunkToRemove);
    IChunk current = getSourceChunk();
    if (current != null && current.equals(chunkToRemove))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("removing and setting status slots");

      setSourceChunkInternal(null);
      setBufferChunk(getEmptyChunk());
      setStateChunk(getFreeChunk());
      setErrorChunk(null);
      return true;
    }

    if (LOGGER.isDebugEnabled()) LOGGER.debug("nothing to remove");
    return false;
  }

  @Override
  public boolean willAccept(IRequest request)
  {
    for (IRequestDelegate processor : getRequestDelegates())
      if (processor.willAccept(request)) return true;
    return false;
  }

  public boolean isModalityFree()
  {
    return checkStatusSlotContent(MODALITY_SLOT, getFreeChunk());
  }

  public boolean isProcessorFree()
  {
    return checkStatusSlotContent(PROCESSOR_SLOT, getFreeChunk());
  }

  public boolean isPreparationFree()
  {
    return checkStatusSlotContent(PREPARATION_SLOT, getFreeChunk());
  }

  public boolean isExecutionFree()
  {
    return checkStatusSlotContent(EXECUTION_SLOT, getFreeChunk());
  }

  public boolean isModalityBusy()
  {
    return checkStatusSlotContent(MODALITY_SLOT, getBusyChunk());
  }

  public boolean isProcessorBusy()
  {
    return checkStatusSlotContent(PROCESSOR_SLOT, getBusyChunk());
  }

  public boolean isPreparationBusy()
  {
    return checkStatusSlotContent(PREPARATION_SLOT, getBusyChunk());
  }

  public boolean isExecutionBusy()
  {
    return checkStatusSlotContent(EXECUTION_SLOT, getBusyChunk());
  }

  public void setModalityChunk(IChunk chunk)
  {
    setStatusSlotContent(MODALITY_SLOT, chunk);
  }

  public void setExecutionChunk(IChunk chunk)
  {
    setStatusSlotContent(EXECUTION_SLOT, chunk);
  }

  public void setPreparationChunk(IChunk chunk)
  {
    setStatusSlotContent(PREPARATION_SLOT, chunk);
  }

  public void setProcessorChunk(IChunk chunk)
  {
    setStatusSlotContent(PROCESSOR_SLOT, chunk);
  }

  public ICompilableContext getCompilableContext()
  {
    return null;
  }

}
