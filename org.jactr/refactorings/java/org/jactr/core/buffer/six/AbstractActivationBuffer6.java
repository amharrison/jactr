/*
 * Created on Aug 13, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.buffer.six;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.AbstractActivationBuffer;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.DefaultMutableSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotOwner;

/**
 * template for an activation buffer that handles it's own status and inserts
 * only copies of chunks
 * 
 * @author developer
 */
public abstract class AbstractActivationBuffer6 extends
    AbstractActivationBuffer implements IStatusBuffer
{

  /**
   * Logger definition
   */

  static private final transient Log      LOGGER = LogFactory
                                                     .getLog(AbstractActivationBuffer6.class);

  final private Map<String, IMutableSlot> _statusSlotMap;

  private IChunk                          _requestedChunk;

  private IChunk                          _unrequestedChunk;

  private IChunk                          _freeChunk;

  private IChunk                          _busyChunk;

  private IChunk                          _fullChunk;

  private IChunk                          _emptyChunk;

  private IChunk                          _errorChunk;

  private ISlotOwner                      _slotListener;

  public AbstractActivationBuffer6(String name, IModule module)
  {
    super(name, module.getModel(), module);
    _statusSlotMap = new TreeMap<String, IMutableSlot>();
    _slotListener = new ISlotOwner() {

      public void valueChanged(ISlot slot, Object oldValue, Object newValue)
      {
        boolean changed = oldValue != null && !oldValue.equals(newValue)
            || newValue != null && !newValue.equals(oldValue);

        if (changed)
        {
          IModel model = getModel();
          if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
          {
            String msg = String.format("%s.%s=%s (was %s)", getName(), slot
                .getName(), newValue, oldValue);
            if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
            if (Logger.hasLoggers(model))
              Logger.log(model, Logger.Stream.BUFFER, msg);
          }

          if (getEventDispatcher().hasListeners())
            getEventDispatcher().fire(
                new ActivationBufferEvent(AbstractActivationBuffer6.this,
                    ActivationBufferEvent.Type.STATUS_SLOT_CHANGED, slot
                        .getName(), oldValue, newValue));
        }
      }
    };

    /*
     * add default status slots
     */
    addSlot(new DefaultMutableSlot(BUFFER_SLOT, null));
    addSlot(new DefaultMutableSlot(STATE_SLOT, null));
  }

  @Override
  public void dispose()
  {
    super.dispose();
    try
    {
      getLock().writeLock().lock();
      _statusSlotMap.clear();
      _busyChunk = null;
      _emptyChunk = null;
      _errorChunk = null;
      _freeChunk = null;
      _fullChunk = null;
      _requestedChunk = null;
      _unrequestedChunk = null;
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  /**
   * clear the status slots too
   * 
   * @see org.jactr.core.buffer.AbstractActivationBuffer#clear()
   */
  @Override
  protected Collection<IChunk> clearInternal()
  {
    try
    {
      return super.clearInternal();
    }
    finally
    {
      setStateChunk(getFreeChunk());
      setBufferChunk(getEmptyChunk());
    }
  }

  public void addSlot(ISlot slot)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Adding status slot " + slot.getName());
    try
    {
      getLock().writeLock().lock();

      DefaultMutableSlot actualSlot = new DefaultMutableSlot(slot);
      actualSlot.setOwner(_slotListener);

      _statusSlotMap.put(slot.getName().toLowerCase(), actualSlot);
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  public void removeSlot(ISlot slot)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Removing status slot " + slot.getName());
    try
    {
      getLock().writeLock().lock();

      _statusSlotMap.remove(slot.getName().toLowerCase());
    }
    finally
    {
      getLock().writeLock().unlock();
    }
  }

  public ISlot getSlot(String name)
  {
    try
    {
      getLock().readLock().lock();
      return _statusSlotMap.get(name.toLowerCase());
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  public Collection<? extends ISlot> getSlots()
  {
    try
    {
      getLock().readLock().lock();
      return Collections.unmodifiableCollection(_statusSlotMap.values());
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if (slots == null) slots = new ArrayList<ISlot>();
    try
    {
      getLock().readLock().lock();
      slots.addAll(_statusSlotMap.values());
      return slots;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  /**
   * return true if this chunk should be copied, currently it just returns
   * {@link IChunk#isEncoded()} or
   * {@link BufferUtilities#getContainingBuffers(IChunk, boolean)}.size()!=0. In
   * other words, copy the chunk if its been encoded or is currently in another
   * buffer
   * 
   * @param chunk
   * @return
   */
  protected boolean shouldCopyOnInsertion(IChunk chunk)
  {
    return chunk.isEncoded()
        || BufferUtilities.getContainingBuffers(chunk, true).size() != 0;
  }

  public boolean isStateFree()
  {
    return checkStatusSlotContent(STATE_SLOT, getFreeChunk());
  }

  public boolean isStateBusy()
  {
    return checkStatusSlotContent(STATE_SLOT, getBusyChunk());
  }

  public boolean isStateError()
  {
    return checkStatusSlotContent(STATE_SLOT, getErrorChunk());
  }

  public boolean isBufferUnrequested()
  {
    return checkStatusSlotContent(BUFFER_SLOT, getUnrequestedChunk());
  }

  public boolean isBufferRequested()
  {
    return checkStatusSlotContent(BUFFER_SLOT, getRequestedChunk());
  }

  public boolean isBufferFull()
  {
    return checkStatusSlotContent(BUFFER_SLOT, getFullChunk());
  }

  /**
   * this actually checks the contents of the buffer slot which might not be the
   * best test in the case of buffers that share a common set of state slots
   * (i.e. visual and visual-location)
   * 
   * @see org.jactr.core.buffer.six.IStatusBuffer#isBufferEmpty()
   */
  public boolean isBufferEmpty()
  {
    return checkStatusSlotContent(BUFFER_SLOT, getEmptyChunk());
  }

  protected boolean checkStatusSlotContent(String slotName, Object value)
  {
    try
    {
      getLock().readLock().lock();
      ISlot slot = getSlot(slotName);
      if (slot != null && value != null) return value.equals(slot.getValue());

      if (slot != null) return slot.getValue() == null;

      return false;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  protected void setStatusSlotContent(String slotName, Object value)
  {
    IMutableSlot ms = (IMutableSlot) getSlot(slotName);
    // Object oldValue = null;
    // boolean changed = false;
    // try
    // {
    // getLock().writeLock().lock();
    // ms = ;
    //
    // oldValue = ms.getValue();
    // ms.setValue(value);
    //
    // changed = oldValue != null && !oldValue.equals(value) || value != null
    // && !value.equals(oldValue);
    //
    // }
    // finally
    // {
    // getLock().writeLock().unlock();
    // }

    if (ms != null)
      ms.setValue(value);
  }

  public void setStateChunk(IChunk chunk)
  {
    setStatusSlotContent(STATE_SLOT, chunk);
  }

  public void setBufferChunk(IChunk bufferState)
  {
    setStatusSlotContent(BUFFER_SLOT, bufferState);
  }

  public IChunk getFreeChunk()
  {
    /*
     * non-locking because even if these values get clobbered, the chunk will
     * always be the same
     */
    if (_freeChunk == null)
      _freeChunk = getModel().getDeclarativeModule().getFreeChunk();
    return _freeChunk;
  }

  public IChunk getBusyChunk()
  {
    /*
     * non-locking because even if these values get clobbered, the chunk will
     * always be the same
     */
    if (_busyChunk == null)
      _busyChunk = getModel().getDeclarativeModule().getBusyChunk();
    return _busyChunk;
  }

  public IChunk getEmptyChunk()
  {
    /*
     * non-locking because even if these values get clobbered, the chunk will
     * always be the same
     */
    if (_emptyChunk == null)
      _emptyChunk = getModel().getDeclarativeModule().getEmptyChunk();
    return _emptyChunk;
  }

  public IChunk getFullChunk()
  {
    /*
     * non-locking because even if these values get clobbered, the chunk will
     * always be the same
     */
    if (_fullChunk == null)
      _fullChunk = getModel().getDeclarativeModule().getFullChunk();
    return _fullChunk;
  }

  public IChunk getErrorChunk()
  {
    /*
     * non-locking because even if these values get clobbered, the chunk will
     * always be the same
     */
    if (_errorChunk == null)
      _errorChunk = getModel().getDeclarativeModule().getErrorChunk();
    return _errorChunk;
  }

  public IChunk getRequestedChunk()
  {
    /*
     * non-locking because even if these values get clobbered, the chunk will
     * always be the same
     */
    if (_requestedChunk == null)
      _requestedChunk = getModel().getDeclarativeModule().getRequestedChunk();
    return _requestedChunk;
  }

  public IChunk getUnrequestedChunk()
  {/*
    * non-locking because even if these values get clobbered, the chunk will
    * always be the same
    */
    if (_unrequestedChunk == null)
      _unrequestedChunk = getModel().getDeclarativeModule()
          .getUnrequestedChunk();
    return _unrequestedChunk;
  }

  /**
   * must be called after the model has started so that we can ensure that the
   * status slot values are properly initialized
   */
  @Override
  public void initialize()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("About to run, ensuring the status buffer slots");

    setStateChunk(getFreeChunk());
    setBufferChunk(getEmptyChunk());
  }

  /**
   * ensure that all chunks added are copies
   * 
   * @see org.jactr.core.buffer.IActivationBuffer#addSourceChunk(org.jactr.core.chunk.IChunk)
   */
  @Override
  public IChunk addSourceChunk(IChunk sourceChunk)
  {
    if (sourceChunk == null) return null;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Attempting to addSourceChunk " + sourceChunk);

    /*
     * unlikely, but let's log it.
     */
    if (sourceChunk.hasBeenDisposed())
    {
      LOGGER.warn(getName() + ".addSourceChunk() " + sourceChunk
          + " has been disposed of, setting error");
      setStateChunk(getErrorChunk());
      return null;
    }

    /*
     * do we already have it?
     */
    IChunk tmp = contains(sourceChunk);
    if (tmp != null) return tmp;

    if (shouldCopyOnInsertion(sourceChunk))
      /*
       * we only copy slotted chunks, zero slot chunks get inserted as they are
       * this keeps us from making unnnecessary copies of status chunks (error)
       */
      if (sourceChunk.getSymbolicChunk().getSlots().size() > 0)
        try
        {
          if (LOGGER.isDebugEnabled())
            LOGGER
                .debug("sourceChunk exists and has been encoded, creating a copy instead");
          sourceChunk = getModel().getDeclarativeModule()
              .copyChunk(sourceChunk).get();
        }
        catch (Exception e)
        {
          LOGGER.error("Failed to copy " + sourceChunk, e);
          return null;
        }

    // this will check addSourceChunkInternal
    return super.addSourceChunk(sourceChunk);
  }

  // /**
  // * check to see if any of the contained chunks match this pattern and
  // binding
  // * combination.
  // */
  // @Override
  // protected IChunk matchesInternal(ChunkPattern retr,
  // Map<String, Object> bindings) throws CannotMatchException
  // {
  // Collection<IChunk> sourceChunks = getSourceChunks();
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Attempting to match " + retr + " against " + sourceChunks
  // + " bindings:" + bindings);
  //
  // if (sourceChunks.size() == 0)
  // throw new CannotMatchException("No source chunks in " + getName()
  // + " to match against");
  //
  // IModel model = getModel();
  //
  // Map<String, Object> originalBindings = bindings;
  //
  // // bind all the currently defined variables
  // retr = (ChunkPattern) retr.bind(model, originalBindings);
  //
  // // first we check on any status queries
  // Collection<IConditionalSlot> querySlots = matchesStatus(retr
  // .getConditionalSlots(), originalBindings, false);
  //
  // // if we get this far, we've passed, however, the chunk pattern might
  // // contain query slots, which arent actual slots of this chunks chunktype
  // // so we have to remove them
  // for (IConditionalSlot cSlot : querySlots)
  // retr.removeSlot(cSlot);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Resolved pattern to match against : " + retr);
  //
  // StringBuilder failedMessage = new StringBuilder();
  //
  // for (IChunk source : sourceChunks)
  // {
  // /*
  // * since each source chunk might result in different bindings, we copy the
  // * original bindings, test against the copy. If we are good to go, we
  // * modified the original binding with the contents of the copied bindings.
  // * If we did not do this then the first chunk in the buffer would change
  // * the bindings for the subsequent chunks resulting in some really strange
  // * behavior.
  // */
  // Map<String, Object> copiedBindings = new TreeMap<String, Object>(
  // originalBindings);
  //
  // /*
  // * copy the retrieval since we are going to be using multiple times..
  // */
  // ChunkPattern retrCopy = (ChunkPattern) retr.bind(model, copiedBindings);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Checking against " + source + " bindings:"
  // + copiedBindings);
  //
  // try
  // {
  // // now we can use the standard match test
  // retrCopy.matches(source, copiedBindings);
  //
  // // we've passed, replace the contents of the bindings
  // originalBindings.clear();
  // originalBindings.putAll(copiedBindings);
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Returning " + source + " with bindings:"
  // + originalBindings);
  //
  // return source;
  // }
  // catch (CannotMatchException cme)
  // {
  // failedMessage.append("(").append(cme.getMessage()).append(")");
  // }
  // }
  //
  // throw new CannotMatchException(getName() + " : cant match "
  // + failedMessage.toString());
  // }

  public int bind(SlotBasedRequest request, Map<String, Object> bindings,
      boolean isIterative) throws CannotMatchException
  {
    return request.bind(getModel(), getName(), this, bindings, isIterative);
  }

  //
  // /**
  // * check the set of conditional slots against the status slots, handles ":"
  // * prefixes automatically. called by matches();
  // *
  // * @param slots
  // * @param pureStatusCheck
  // * if there are no chunk slots and only
  // * @return a collection of slots that can be removed from the original
  // * querying collection - i.e. the slots that only match against the
  // * status slots
  // * @throws CannotMatchException
  // */
  // public Collection<IConditionalSlot> matchesStatus(
  // Collection<IConditionalSlot> slots, Map<String, Object> bindings,
  // boolean pureStatusCheck) throws CannotMatchException
  // {
  // try
  // {
  // getLock().readLock().lock();
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Checking conditional slots against buffer status : "
  // + slots);
  //
  // Collection<IConditionalSlot> slotsToRemove = new
  // ArrayList<IConditionalSlot>(
  // slots.size());
  //
  // for (IConditionalSlot originalSlot : slots)
  // {
  // IConditionalSlot slot = originalSlot;
  //
  // if (!pureStatusCheck)
  // {
  // // if the name is :prefixed, we need to make a temp copy
  // if (originalSlot.getName().startsWith(":"))
  // {
  // slot = new DefaultConditionalSlot(slot.getName().substring(1,
  // slot.getName().length()), slot.getCondition(), slot.getValue());
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(originalSlot.getName()
  // + " is status conditional, stripping prefix to "
  // + slot.getName());
  // /*
  // * if pure, we remove all : prefixed slots, regardless of whether
  // * they are actually status slots
  // */
  // Object testValue = testStatusSlot(slot);
  //
  // if (originalSlot.isVariable())
  // bindings.put((String) originalSlot.getValue(), testValue);
  //
  // slotsToRemove.add(originalSlot);
  // }
  // }
  // else
  // {
  // Object testValue = testStatusSlot(slot);
  //
  // if (originalSlot.isVariable())
  // bindings.put((String) originalSlot.getValue(), testValue);
  // slotsToRemove.add(slot);
  // }
  // }
  // return slotsToRemove;
  // }
  // finally
  // {
  // getLock().readLock().unlock();
  // }
  // }
  //
  // private Object testStatusSlot(IConditionalSlot slot)
  // throws CannotMatchException
  // {
  // ISlot statusSlot = getStatusSlot(slot.getName());
  // // we have something named this..
  // if (!slot.matchesCondition(statusSlot.getValue()))
  // {
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(getName() + " : " + statusSlot
  // + " doesn't match the conditional slot " + slot);
  // throw new CannotMatchException(getName() + " : " + statusSlot
  // + " doesn't match the conditional slot " + slot);
  // }
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(getName() + " : " + statusSlot
  // + " matches conditional slot " + slot);
  //
  // return statusSlot.getValue();
  // }
}
