/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.production.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.StringUtilities;

/**
 * The ModifyAction is used to modify the slot values of a chunk residing within
 * a specific buffer. The IChunk is neither added nor removed. If upon firing
 * there is no chunk in the buffer, an IllegalActionStateException is thrown.
 * 
 * @author harrison
 * @created January 22, 2003
 */
public class ModifyAction extends DefaultAction implements IBufferAction,
    org.jactr.core.slot.ISlotContainer
{

  // logging mechanism
  private static transient Log     LOGGER = LogFactory
                                              .getLog(ModifyAction.class
                                                  .getName());

  /**
   * name of the buffer that is to have its contents modified
   * 
   * @since
   */
  private String                   _bufferName;

  private Collection<IMutableSlot> _slots;

  private IChunk                   _boundChunk;

  /**
   * Constructor merely takes the name of the buffer where the chunk will
   * reside.
   * 
   * @param bufferName
   *          Description of Parameter
   * @since
   */
  @SuppressWarnings("unchecked")
  public ModifyAction(String bufferName)
  {
    this(bufferName, Collections.EMPTY_LIST);
  }

  /**
   * Constructor for the ModifyAction object
   * 
   * @since
   */
  public ModifyAction()
  {
    this(IActivationBuffer.GOAL);
  }

  public ModifyAction(String bufferName, Collection<? extends ISlot> slots)
  {
    _bufferName = bufferName.toLowerCase();
    _slots = new ArrayList<IMutableSlot>();
    for (ISlot slot : slots)
      addSlot(slot);
  }

  /**
   * Description of the Method
   * 
   * @since
   */
  @Override
  public void dispose()
  {
    super.dispose();
    if (_slots != null) _slots.clear();
    _slots = null;
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Return Value
   */
  public boolean canModify()
  {
    return true;
  }

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    ModifyAction act = new ModifyAction(getBufferName(), getSlotsInternal());
    act.bindSlotValues(variableBindings, act.getSlotsInternal());
    act.bindChunk(variableBindings);
    return act;
  }

  protected void bindChunk(VariableBindings bindings)
      throws CannotInstantiateException
  {
    _boundChunk = (IChunk) bindings.get("=" + getBufferName());
    if (_boundChunk == null)
      throw new CannotInstantiateException("Could not get chunk bound to ="
          + getBufferName());
  }

  public String getBufferName()
  {
    return _bufferName;
  }

  public void setBufferName(String name)
  {
    _bufferName = name.toLowerCase();
  }

  /**
   * returns actual backing collection
   */
  protected Collection<IMutableSlot> getSlotsInternal()
  {
    return _slots;
  }

  public Collection<? extends ISlot> getSlots()
  {
    return Collections.unmodifiableCollection(_slots);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> slots)
  {
    if (slots == null) slots = new ArrayList<ISlot>();
    slots.addAll(_slots);
    return slots;
  }

  public void addSlot(ISlot s)
  {
    _slots.add(new DefaultVariableConditionalSlot(s));
  }

  public void removeSlot(ISlot s)
  {
    _slots.remove(s);
  }

  protected IChunk getBoundChunk()
  {
    return _boundChunk;
  }

  /**
   * fire this modify action this will actually post a timed event to the timed
   * event queue that will do the work..
   * 
   * @since
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    IModel model = instantiation.getModel();
    double fireAt = firingTime
        + model.getProceduralModule().getDefaultProductionFiringTime();
    IActivationBuffer buffer = model.getActivationBuffer(getBufferName());

    ModifyActionTimedEvent modify = new ModifyActionTimedEvent(firingTime,
        fireAt, buffer, getBoundChunk(), getSlots());

    model.getTimedEventQueue().enqueue(modify);

    return 0;
  }

  public class ModifyActionTimedEvent extends AbstractTimedEvent implements
      IBufferBasedTimedEvent
  {
    final IChunk                      _chunkToModify;

    final Collection<? extends ISlot> _slotsToChange;

    final IActivationBuffer           _buffer;

    final String                      _label;

    public ModifyActionTimedEvent(double now, double whenToFire,
        IActivationBuffer buffer, IChunk chunkToModify,
        Collection<? extends ISlot> slots)
    {
      setTimes(now, whenToFire);
      _buffer = buffer;
      _chunkToModify = chunkToModify;
      _slotsToChange = new ArrayList<ISlot>(slots);
      _label = String.format("Modify(%1$s in %2$s @ %3$.2f)", _chunkToModify
          .getSymbolicChunk().getName(), _buffer.getName(), whenToFire);
    }

    @Override
    public String toString()
    {
      return _label;
    }

    @SuppressWarnings("synthetic-access")
    @Override
    public void fire(double now)
    {
      super.fire(now);

      if (LOGGER.isDebugEnabled())
      {
        LOGGER.debug("Modifying " + _chunkToModify + " @ " + now + " "
            + StringUtilities.toString(_chunkToModify));
        LOGGER.debug("Using slots " + _slotsToChange);
      }

      if (!_buffer.getSourceChunks().contains(_chunkToModify))
      {
        IModel model = _chunkToModify.getModel();
        if (LOGGER.isWarnEnabled() || Logger.hasLoggers(model))
        {
          String msg = _chunkToModify + " is no longer in " + _buffer
              + " cannot modify";
          Logger.log(model, Logger.Stream.EXCEPTION, msg);
          LOGGER.warn(msg);
        }
        return;
      }

      if (_chunkToModify.isEncoded() && !_chunkToModify.isMutable()
          && _slotsToChange.size() != 0)
      {
        IModel model = _chunkToModify.getModel();
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.EXCEPTION, String
.format(
              "%s in %s is encoded and immutable, cannot be modified",
              _chunkToModify, _bufferName));
      }
      else
        updateSlots(_chunkToModify, _slotsToChange);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Result " + StringUtilities.toString(_chunkToModify));
    }

    /**
     * @see org.jactr.core.queue.timedevents.IBufferBasedTimedEvent#getBuffer()
     */
    public IActivationBuffer getBuffer()
    {
      return _buffer;
    }

    public IChunk getBoundChunk()
    {
      return _chunkToModify;
    }

  }
}