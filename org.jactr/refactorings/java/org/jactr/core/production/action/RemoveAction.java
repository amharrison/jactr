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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;
import org.jactr.core.slot.ISlot;

/**
 * The remove action does just that. It removes a chunk from a named buffer.
 * Since buffers currently only contain one chunk, remove needs not specify
 * anything other than the buffer name. If the named buffer doesn?t contain a
 * chunk, no action is taken.
 */
public class RemoveAction extends ModifyAction implements IBufferAction
{

  // logger class
  private static transient Log LOGGER = LogFactory.getLog(RemoveAction.class
                                          .getName());

  /**
   * Constructor for the RemoveAction object
   * 
   * @param bufferName
   *          Description of the Parameter
   */
  public RemoveAction(String bufferName)
  {
    super(bufferName);
  }

  /**
   * Constructor for the RemoveAction object
   */
  public RemoveAction()
  {
    super();
  }

  public RemoveAction(String bufferName, Collection<? extends ISlot> slots)
  {
    super(bufferName, slots);
  }

  @Override
  public IAction bind(Map<String, Object> variableBindings)
      throws CannotInstantiateException
  {
    RemoveAction ra = new RemoveAction(getBufferName(), getSlotsInternal());
    ra.bindSlotValues(variableBindings, ra.getSlotsInternal());
    /*
     * the binding of the chunk name is actually optional.. the only thing that
     * matters is if there is soemthing in the buffer
     */
    try
    {
      ra.bindChunk(variableBindings);
    }
    catch (CannotInstantiateException cie)
    {

    }

    return ra;
  }

  /**
   * post an event that will remove the contents of the buffer. We post an event
   * because this is SOP.
   */
  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    IModel model = instantiation.getModel();
    double fireAt = firingTime
        + model.getProceduralModule().getDefaultProductionFiringTime();

    ITimedEvent timedEvent = null;
    final IActivationBuffer ab = model.getActivationBuffer(getBufferName());

    if (ab == null)
      throw new IllegalActionStateException("No buffer named "
          + getBufferName() + " found, cannot remove");

    IChunk bc = getBoundChunk();
    if (bc == null) bc = ab.getSourceChunk();

    timedEvent = new RemoveActionTimedEvent(firingTime, fireAt, ab, bc,
        getSlots());

    model.getTimedEventQueue().enqueue(timedEvent);

    return 0;
  }

  public class RemoveActionTimedEvent extends AbstractTimedEvent implements
      IBufferBasedTimedEvent
  {
    final IChunk                      _chunkToRemove;

    final IActivationBuffer           _buffer;

    final Collection<? extends ISlot> _slotsToChange;

    final String                      _label;

    public RemoveActionTimedEvent(double now, double removeTime,
        IActivationBuffer buffer, IChunk chunk,
        Collection<? extends ISlot> slots)
    {
      super();
      setTimes(now, removeTime);
      _buffer = buffer;
      _chunkToRemove = chunk;
      _slotsToChange = new ArrayList<ISlot>(slots);
      if (_chunkToRemove != null)
        _label = String.format("Remove(%1$s from %2$s @ %3$.2f)",
            _chunkToRemove.getSymbolicChunk().getName(), _buffer.getName(),
            removeTime);
      else
        _label = String.format("Clear(%s @ %.2f)", _buffer.getName(),
            removeTime);
    }

    @Override
    public String toString()
    {
      return _label;
    }

    @Override
    public void fire(double currentTime)
    {
      super.fire(currentTime);

      if (_chunkToRemove != null)
      {

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Removing " + _chunkToRemove + " from " + _buffer);

        if (!_buffer.getSourceChunks().contains(_chunkToRemove))
        {
          IModel model = _chunkToRemove.getModel();
          if (LOGGER.isWarnEnabled() || Logger.hasLoggers(model))
          {
            String msg = _chunkToRemove + " is no longer in " + _buffer
                + " cannot remove";
            Logger.log(model, Logger.Stream.EXCEPTION, msg);
            LOGGER.warn(msg);
          }
          return;
        }

        /*
         * change the slots first, then remove..
         */
        updateSlots(_chunkToRemove, _slotsToChange);

        _buffer.removeSourceChunk(_chunkToRemove);
      }
      else
        _buffer.clear();

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
      return _chunkToRemove;
    }
  }
}