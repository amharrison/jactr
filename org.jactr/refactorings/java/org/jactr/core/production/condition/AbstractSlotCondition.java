/*
 * Created on Sep 21, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.production.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.production.action.DefaultAction;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;
import org.jactr.core.utils.collections.CachedCollection;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AbstractSlotCondition implements ICondition,
    ISlotContainer
{

  private static transient Log LOGGER = LogFactory
                                          .getLog(AbstractSlotCondition.class
                                              .getName());

  // private Collection<IConditionalSlot> _slotList;

  private String               _toString;

  private SlotBasedRequest     _request;

  public AbstractSlotCondition()
  {
  }

  protected void setRequest(SlotBasedRequest request)
  {
    _request = request;
  }

  protected SlotBasedRequest getRequest()
  {
    return _request;
  }

  // synchronized protected void duplicateSlots(Collection<? extends ISlot>
  // slots)
  // {
  // if (_slotList == null)
  // _slotList = new CachedCollection<IConditionalSlot>(
  // new ArrayList<IConditionalSlot>(slots.size()));
  //
  // for (ISlot slot : slots)
  // addSlot(slot);
  // }
  //
  // /**
  // * @param referenceChunk
  // * @param bindings
  // * @throws CannotMatchException
  // */
  // protected void checkAndBindSlots(IChunk referenceChunk,
  // Map<String, Object> bindings) throws CannotMatchException
  // {
  // ISymbolicChunk symbolicChunk = referenceChunk.getSymbolicChunk();
  // for (IConditionalSlot conditionalSlot : getConditionalSlots())
  // if (!conditionalSlot.getName().startsWith(":"))
  // {
  // /*
  // * we need to make sure that we are only checking real slots and not
  // * status slots. The status slots should be checked before we even get
  // * here if they exist
  // */
  // ISlot referenceSlot = symbolicChunk.getSlot(conditionalSlot.getName());
  //
  // if (conditionalSlot.isVariable())
  // {
  // String variableName = (String) conditionalSlot.getValue();
  // variableName = variableName.toLowerCase();
  // Object slotValue = referenceSlot.getValue();
  // if (!bindings.containsKey(variableName) && slotValue != null)
  // {
  // /*
  // * variable has not yet been bound, so we snag the value from
  // * referenceChunk
  // */
  // bindings.put(variableName, slotValue);
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(variableName
  // + " was not previously resolved, binding to " + slotValue);
  //
  // /*
  // * we now change the value of the slot
  // */
  // conditionalSlot.setValue(slotValue);
  // clearToString();
  // }
  // else
  // {
  // /*
  // * variable has been bound
  // */
  // Object resolvedValue = DefaultAction
  // .resolve(variableName, bindings);
  //
  // if (resolvedValue != null)
  // {
  // conditionalSlot.setValue(resolvedValue);
  // clearToString();
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("resolved " + variableName + " to "
  // + resolvedValue);
  // }
  // }
  // }
  //
  // if (!conditionalSlot.matchesCondition(referenceSlot))
  // {
  // StringBuilder msg = new StringBuilder(100);
  // msg.append(referenceChunk.toString());
  // msg.append(".").append(referenceSlot).append(
  // " doesn't match the conditional slot ");
  // msg.append(conditionalSlot);
  // String message = msg.toString();
  //
  // if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
  //
  // throw new CannotMatchException(message);
  // }
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug(referenceChunk + "." + referenceSlot + " does match "
  // + conditionalSlot);
  // }
  // }

  /**
   * @see org.jactr.core.production.condition.ICondition#dispose()
   */
  synchronized public void dispose()
  {
    // if (_slotList != null) _slotList.clear();
  }

  /**
   * violates the ISlotContainer#getSlots() contract that they be a copy why?
   * because each condition is fully duplicated during instantiation so we will
   * be the only modifiers of this..
   * 
   * @returns an unmodifiable collection of the <b>actual</b> Slots backing
   *          this.
   * @see org.jactr.core.slot.ISlotContainer#getSlots()
   */
  synchronized public Collection<? extends ISlot> getSlots()
  {
    // if (_slotList == null) return Collections.EMPTY_LIST;
    //
    // return Collections.unmodifiableCollection(_slotList);
    if (_request != null) return _request.getSlots();
    return Collections.emptyList();
  }

  // /**
  // * the actual collections of the conditional slots - so changing these, will
  // * change the pattern used..
  // *
  // * @return
  // */
  // synchronized public Collection<IConditionalSlot> getConditionalSlots()
  // {
  // // if (_slotList == null) return Collections.EMPTY_LIST;
  // //
  // // return Collections.unmodifiableCollection(_slotList);
  // return getRequest().getConditionalSlots();
  // }

  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {
    // if(container==null)
    // {
    // if(_slotList!=null)
    // container = new ArrayList<ISlot>(_slotList.size()+1);
    // else
    // container = new ArrayList<ISlot>();
    // }
    //    
    // if (_slotList != null) container.addAll(_slotList);
    // return container;
    if (_request != null) return _request.getSlots(container);
    return container;
  }

  public Collection<? extends IConditionalSlot> getConditionalSlots()
  {
    if (_request != null) return _request.getConditionalSlots();
    return Collections.emptyList();
  }

  /**
   * @see org.jactr.core.slot.ISlotContainer#addSlot(ISlot)
   */
  synchronized public void addSlot(ISlot slot)
  {
    // if (_slotList == null)
    // _slotList = new CachedCollection<IConditionalSlot>(
    // new ArrayList<IConditionalSlot>());
    //
    // /*
    // * if slot is conditional slot, DefaultConditionalSlot will snag the
    // * appropriate values
    // */
    // _slotList.add(new DefaultConditionalSlot(slot));
    getRequest().addSlot(slot);
    clearToString();
  }

  /**
   * @see org.jactr.core.slot.ISlotContainer#removeSlot(ISlot)
   */
  synchronized public void removeSlot(ISlot slot)
  {
    getRequest().removeSlot(slot);
    // if (_slotList != null)
    // {
    // _slotList.remove(slot);
    clearToString();
    // }
  }

  @Override
  public String toString()
  {
    synchronized (this)
    {
      if (_toString == null) _toString = createToString();
    }
    return _toString;
  }

  protected void clearToString()
  {
    synchronized (this)
    {
      _toString = null;
    }
  }

  synchronized protected String createToString()
  {
    // if (_slotList == null) return "";

    return getSlots().toString();
  }
}
