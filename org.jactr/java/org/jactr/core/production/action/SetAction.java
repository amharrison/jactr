package org.jactr.core.production.action;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;
import org.jactr.core.slot.DefaultMutableSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.utils.StringUtilities;

/**
 * a zero-time buffer content set operator
 * 
 * @author harrison
 */
public class SetAction extends DefaultAction implements ISlotContainer,
    IBufferAction
{
  /**
   * Logger definition
   */
  static private final transient Log     LOGGER = LogFactory
                                                    .getLog(SetAction.class);

  private String                         _bufferName;

  final private Collection<IMutableSlot> _slots;

  private IChunk                         _boundChunk;

  private Object                         _referant;

  @SuppressWarnings("unchecked")
  public SetAction(String bufferName, Object referant)
  {
    this(bufferName, referant, Collections.EMPTY_LIST);
  }

  public SetAction(String bufferName, Object referant,
      Collection<? extends ISlot> slots)
  {
    setReferant(referant);
    setBufferName(bufferName);
    _slots = new ArrayList<IMutableSlot>();
    for (ISlot slot : slots)
      addSlot(slot);
  }

  @Override
  public void dispose()
  {
    super.dispose();
    _slots.clear();
  }

  public void addSlot(ISlot slot)
  {
    _slots.add(new DefaultMutableSlot(slot));
  }

  public Collection<? extends ISlot> getSlots()
  {
    return getSlots(null);
  }

  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {
    if (container == null) container = new ArrayList<ISlot>();
    container.addAll(_slots);
    return container;
  }

  public void removeSlot(ISlot slot)
  {
    _slots.remove(slot);
  }

  public String getBufferName()
  {
    return _bufferName;
  }

  public void setBufferName(String bufferName)
  {
    _bufferName = bufferName.toLowerCase();
  }

  public Object getReferant()
  {
    return _referant;
  }

  public void setReferant(Object ref)
  {
    _referant = ref;
  }

  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    SetAction act = new SetAction(getBufferName(), _referant, _slots);
    act.bindSlotValues(variableBindings, act._slots);
    act.bindChunk(variableBindings);
    return act;
  }

  protected void bindChunk(VariableBindings bindings)
      throws CannotInstantiateException
  {
    Object ref = getReferant();
    if (ref instanceof String && ((String) ref).startsWith("="))
    {
      /*
       * the punk is a variable name..
       */
      Object resolved = resolve((String) ref, bindings);

      if (resolved == null)
        throw new CannotInstantiateException("Could not resolve variable name "
            + ref + " possible:" + bindings);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Resolved " + ref + " to " + resolved + "("
            + resolved.getClass().getName() + ") " + bindings);

      setReferant(resolved);
      ref = resolved;
    }

    if (ref instanceof IChunk) _boundChunk = (IChunk) ref;

    if (_boundChunk == null)
      throw new CannotInstantiateException("Could not get chunk bound to ="
          + ref);
  }

  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    IModel model = instantiation.getModel();
    double fireAt = firingTime
        + model.getProceduralModule().getDefaultProductionFiringTime();
    IActivationBuffer buffer = model.getActivationBuffer(getBufferName());

    SetActionTimedEvent modify = new SetActionTimedEvent(firingTime, fireAt,
        buffer, _boundChunk, getSlots());

    model.getTimedEventQueue().enqueue(modify);

    return 0;
  }

  public class SetActionTimedEvent extends AbstractTimedEvent implements
      IBufferBasedTimedEvent
  {
    IChunk                            _chunkToModify;

    final Collection<? extends ISlot> _slotsToChange;

    final IActivationBuffer           _buffer;

    final String                      _label;

    public SetActionTimedEvent(double now, double whenToFire,
        IActivationBuffer buffer, IChunk chunkToModify,
        Collection<? extends ISlot> slots)
    {
      setTimes(now, whenToFire);
      _buffer = buffer;
      _chunkToModify = chunkToModify;
      _slotsToChange = new ArrayList<ISlot>(slots);
      _label = String.format("Set(%s into %s @ %.2f)", _chunkToModify
          .getSymbolicChunk().getName(), _buffer, whenToFire);
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

      /*
       * add the chunk to the buffer, and update the slot values
       */
      _chunkToModify = _buffer.addSourceChunk(_chunkToModify);

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
