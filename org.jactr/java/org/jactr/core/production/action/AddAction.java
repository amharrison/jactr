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
import org.jactr.core.buffer.IRequestableBuffer;
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.buffer.delegate.AddChunkTypeRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;

/**
 * AddAction represents the consequence of adding a chunk to a buffer. It can
 * add an existing chunk to a buffer, add a chunk that is referenced by a
 * variable name, or to create a new chunk in the buffer.
 * 
 * @author harrison
 * @created January 22, 2003
 */
public class AddAction extends DefaultAction implements IBufferAction,
    org.jactr.core.slot.ISlotContainer
{

  private static transient Log               LOGGER = LogFactory
                                                        .getLog(AddAction.class
                                                            .getName());

  static private AddChunkTypeRequestDelegate _chunkTypeDelegate;

  static private AddChunkRequestDelegate     _chunkDelegate;

  static
  {
    _chunkTypeDelegate = new AddChunkTypeRequestDelegate();
    _chunkTypeDelegate.setAsynchronous(true);
    _chunkDelegate = new AddChunkRequestDelegate(true);
    _chunkDelegate.setAsynchronous(true);
  }

  /**
   * the name of the buffer for the chunk to be inserted into
   * 
   * @since
   */
  private String                             _bufferName;

  // required
  /**
   * a map of slots to be assigned to the to be inserted chunk
   * 
   * @since
   */
  private Collection<IMutableSlot>           _slots;

  /**
   * referant can be either a chunkname, a variable name, a chunktype or a chunk
   * proper
   * 
   * @since
   */
  private Object                             _referant;

  /**
   * default constructor equivalent to AddAction("goal", null)
   * 
   * @since
   */
  public AddAction()
  {
    this(IActivationBuffer.GOAL, (Object) null);
  }

  /**
   * add a chunk (ref) to bufferName buffer
   * 
   * @param bufferName
   *          name of the buffer to insert into
   * @param ref
   *          IChunk, IChunkType, chunkName, or variable name
   * @since
   */
  public AddAction(String bufferName, Object ref,
      Collection<? extends ISlot> slots)
  {
    _bufferName = bufferName.toLowerCase();
    _slots = new ArrayList<IMutableSlot>(slots.size());
    _referant = ref;
    for (ISlot slot : slots)
      addSlot(slot);
  }

  @SuppressWarnings("unchecked")
  public AddAction(String bufferName, Object ref)
  {
    this(bufferName, ref, Collections.EMPTY_LIST);
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Returned Value
   * @since
   */
  @Override
  public String toString()
  {
    return String.format("Add(%s, %s[%s])", _bufferName, _referant, _slots);
  }

  /**
   * Description of the Method
   * 
   * @since
   */
  @Override
  public void dispose()
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("dispose : " + this);
    super.dispose();

    if (_slots != null) _slots.clear();
    _slots = null;
    _referant = null;
  }

  /**
   * Gets the referant attribute of the AddAction object
   * 
   * @return The referant value
   * @since
   */
  public Object getReferant()
  {
    return _referant;
  }

  /**
   * Sets the referant attribute of the AddAction object
   * 
   * @param o
   *          The new referant value
   * @since
   */
  public void setReferant(Object o)
  {
    _referant = o;
  }

  public IAction bind(VariableBindings bindings)
      throws CannotInstantiateException
  {
    AddAction action = new AddAction(getBufferName(), getReferant(), _slots);
    action.bindChunk(bindings);
    action.bindSlotValues(bindings, action._slots);
    return action;
  }

  protected void bindChunk(VariableBindings bindings)
      throws CannotInstantiateException
  {
    String name = getChunkName();
    if (name != null)
      if (name.startsWith("="))
      {
        /*
         * the punk is a variable name..
         */
        Object resolved = resolve(name, bindings);

        if (resolved == null)
          throw new CannotInstantiateException(
              "Could not resolve variable name " + name + " possible:"
                  + bindings);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Resolved " + name + " to " + resolved + "("
              + resolved.getClass().getName() + ") " + bindings);
        setReferant(resolved);
      }
  }

  /**
   * return the name of the buffer that the chunk will be added to
   * 
   * @return The bufferName value
   * @since
   */
  public String getBufferName()
  {
    return _bufferName;
  }

  /**
   * override the constructor and set the buffer name
   * 
   * @param name
   *          The new bufferName value
   * @since
   */
  public void setBufferName(String name)
  {
    _bufferName = name.toLowerCase();
  }

  /**
   * set the chunktype of the chunk to be created
   * 
   * @param ct
   *          The new chunkType value
   * @since
   */
  public void setChunkType(IChunkType ct)
  {
    _referant = ct;
  }

  /**
   * set the variable name of the chunk to be added. This must be a valid
   * variable name and must be previously defined by a condition or another
   * action
   * 
   * @param name
   *          The new chunkName value
   * @since
   */
  public void setChunkName(String name)
  {
    _referant = name;
  }

  /**
   * set the chunk that will be added to the buffer
   * 
   * @param c
   *          The new chunk value
   * @since
   */
  public void setChunk(IChunk c)
  {
    _referant = c;
  }

  /**
   * return the variable name of the chunk if it has been specified, null
   * otherwise
   * 
   * @return The chunkName value
   * @since
   */
  public String getChunkName()
  {
    if (_referant instanceof String) return (String) _referant;
    return null;
  }

  /**
   * return the chunktype of the to-be created chunk if it has been specified,
   * null otherwise
   * 
   * @return The chunkType value
   * @since
   */
  public IChunkType getChunkType()
  {
    if (_referant instanceof IChunkType) return (IChunkType) _referant;
    return null;
  }

  /**
   * return the chunk to be added if it was specified.
   * 
   * @return The chunk value
   * @since
   */
  public IChunk getChunk()
  {
    if (_referant instanceof IChunk) return (IChunk) _referant;
    return null;
  }

  protected Collection<IMutableSlot> getSlotsInternal()
  {
    return _slots;
  }
  
  /**
   * Return all the slots that this addaction will attempt to set for the to be
   * added chunk.
   * 
   * @return The slots value
   * @since
   */
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

  /**
   * In addition to adding chunks to a buffer, the IChunk?s slot values can be
   * changed at the same time. This prevents the need to an additional action
   * just to change the slot values. The slot added will be used to modify the
   * same named slot in the chunk.
   * 
   * @param s
   *          The feature to be added to the ISlot attribute
   * @since
   */
  public void addSlot(ISlot s)
  {
    _slots.add((IMutableSlot)s.clone());
  }

  /**
   * Remove a specific slot from the addaction.
   * 
   * @param s
   *          Description of Parameter
   * @since
   */
  public void removeSlot(ISlot s)
  {
    _slots.remove(s);
  }

  /**
   * return a mutable copy of the request that underlies this action.
   * 
   * @return
   */
  public IRequest getRequest()
  {
    return createRequest();
  }

  protected IRequest createRequest()
  {
    IRequest request = null;
    Object referant = getReferant();

    if (referant instanceof IRequest)
      request = (IRequest) referant;
    else if (referant instanceof IChunk)
      /*
       * +buffer> chunk (or =chunk)
       */
      request = new ChunkRequest((IChunk) referant, _slots);
    else if (referant instanceof IChunkType) /*
     * +buffer> isa chunk
     */
    request = new ChunkTypeRequest((IChunkType) referant, _slots);
    else
      /*
       * +buffer> slot value
       */
      request = new SlotBasedRequest(_slots);

    return request;
  }

  @Override
  public double fire(IInstantiation instantiation, double firingTime)
  {
    /*
     * this handles two basic set ups.. if the buffer handles requests, the
     * request is created and passed on directly. otherwise, it is redirected
     * through the request delegates..
     */

    IModel model = instantiation.getModel();
    IActivationBuffer buffer = model.getActivationBuffer(getBufferName());

    IRequest request = createRequest();
    IRequestableBuffer rb = (IRequestableBuffer) buffer
        .getAdapter(IRequestableBuffer.class);

    if (rb != null)
    {
      if (rb.willAccept(request))
        rb.request(request, firingTime);
      else
        throw new IllegalActionStateException(rb.getName()
            + " rejected processing of request " + request);
    }
    else /*
     * wasn't accepted directly.. we'll do so indirectly
     */
    if (request instanceof ChunkRequest)
      _chunkDelegate.request(request, buffer, firingTime);
    else if (request instanceof ChunkTypeRequest)
      _chunkTypeDelegate.request(request, buffer, firingTime);
    else
      throw new IllegalActionStateException(buffer.getName()
          + " cannot accept slot only requests");



    return 0;
  }
}