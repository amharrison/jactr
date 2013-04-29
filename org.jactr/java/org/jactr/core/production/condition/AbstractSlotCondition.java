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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.ISlotContainer;

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
    setRequest(new SlotBasedRequest());
  }

  protected void setRequest(SlotBasedRequest request)
  {
    _request = request;
  }

  protected SlotBasedRequest getRequest()
  {
    return _request;
  }


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



  public Collection<ISlot> getSlots(Collection<ISlot> container)
  {

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

    getRequest().addSlot(slot);
    clearToString();
  }

  /**
   * @see org.jactr.core.slot.ISlotContainer#removeSlot(ISlot)
   */
  synchronized public void removeSlot(ISlot slot)
  {
    getRequest().removeSlot(slot);
    clearToString();
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
