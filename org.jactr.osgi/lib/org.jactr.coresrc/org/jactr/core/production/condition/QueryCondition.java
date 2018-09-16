/*
 * Created on Jan 16, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.production.condition;

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.match.GeneralMatchFailure;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.ISlot;

/**
 * condition for checking the state of a buffer
 * 
 * @author developer
 */
public class QueryCondition extends AbstractBufferCondition implements
    Cloneable
{
  /**
   * Logger definition
   */

  static private final Log LOGGER = LogFactory.getLog(QueryCondition.class);

  public QueryCondition(String bufferName)
  {
    this(bufferName, Collections.EMPTY_LIST);
  }

  public QueryCondition(String bufferName, Collection<? extends ISlot> slots)
  {
    super(bufferName);
    setRequest(new SlotBasedRequest(slots));
  }

  @Override
  public QueryCondition clone()
  {
    return new QueryCondition(getBufferName(), getRequest().getSlots());
  }

  public QueryCondition clone(IModel model, VariableBindings bindings)
      throws CannotMatchException
  {
    IActivationBuffer buffer = getActivationBuffer(model);
    IStatusBuffer sb = (IStatusBuffer) buffer.getAdapter(IStatusBuffer.class);
    if (sb == null)
      throw new CannotMatchException(new GeneralMatchFailure(this,
          String.format("%s has no status slots to check", buffer.getName())));

    return clone();
  }


  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    IActivationBuffer buffer = getActivationBuffer(model);

    IStatusBuffer sb = (IStatusBuffer) buffer.getAdapter(IStatusBuffer.class);
    if (sb == null)
      throw new CannotMatchException(new GeneralMatchFailure(this,
          String.format("%s has no status slots to check", buffer.getName())));

    try
    {
      return sb.bind(getRequest(), variableBindings,
          isIterative);
    }
    catch (CannotMatchException cme)
    {
      cme.getMismatch().setCondition(this);
      throw cme;
    }
  }

}
