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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.model.IModel;
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
    return new QueryCondition(getBufferName(),getRequest().getSlots());
  }

  public QueryCondition clone(IModel model, Map<String,Object> bindings) throws CannotMatchException
  {
    IActivationBuffer buffer = getActivationBuffer(model);
    if(!(buffer instanceof IStatusBuffer))
      throw new CannotMatchException(getBufferName()+" has no status slots to check");
    
    return clone();
  }
  

//  /**
//   * test and bind the conditional slots
//   *
//   * @see org.jactr.core.production.condition.ICondition#bind(org.jactr.core.model.IModel,
//   *      Map)
//   */
//  protected void checkAndBindSlots(IModel model,
//      Map<String, Object> variableBindings) throws CannotMatchException
//  {
//    IActivationBuffer buffer = model.getActivationBuffer(getBufferName());
//    if (buffer instanceof IStatusBuffer)
//    {
//      IStatusBuffer statusCapableBuffer = (IStatusBuffer) buffer;
//      Collection<IConditionalSlot> querySlots = getConditionalSlots();
//      /*
//       * check the query slots for variables that need resolving
//       */
//      for (IConditionalSlot conditionalSlot : querySlots)
//        if (conditionalSlot.isVariable())
//        {
//          String variableName = (String) conditionalSlot.getValue();
//          variableName = variableName.toLowerCase();
//          if (variableBindings.containsKey(variableName))
//          {
//            Object value = variableBindings.get(variableName);
//            if (LOGGER.isDebugEnabled())
//              LOGGER.debug("resolving " + variableName + " to " + value);
//            conditionalSlot.setValue(value);
//            clearToString();
//          }
//          else
//          {
//            /*
//             * variable has not been bound.. leave until down below...
//             */
//          }
//        }
//
//      /*
//       * this returns a list of slots that could be removed if this were a
//       * hybrid condition (a ChunkTypeCondition that also checks the status
//       * slots)
//       */
//      Collection<IConditionalSlot> actualStatusSlots = statusCapableBuffer
//          .matchesStatus(querySlots, variableBindings, true);
//
//      if (actualStatusSlots.size() != querySlots.size())
//      {
//        if (LOGGER.isDebugEnabled())
//          LOGGER.debug("There were unknown slots in the condition ("
//              + querySlots + ") buffer recognized " + actualStatusSlots);
//        throw new CannotMatchException(
//            "There were unknown slots in the condition (" + querySlots
//                + ") buffer recognized " + actualStatusSlots);
//      }
//
//      for (IConditionalSlot slot : actualStatusSlots)
//        if (slot.isVariable())
//        {
//          /*
//           * ok, now let's do some binding if there is anything left to bind
//           */
//          String variableName = (String) slot.getValue();
//          variableName = variableName.toLowerCase();
//          ISlot statusSlot = statusCapableBuffer.getStatusSlot(slot.getName());
//          Object value = statusSlot.getValue();
//          if (value != null)
//          {
//            if (LOGGER.isDebugEnabled())
//              LOGGER.debug("Resolving " + variableName + " to " + value);
//            slot.setValue(value);
//            clearToString();
//            variableBindings.put(variableName, value);
//          }
//        }
//
//    }
//    else
//      throw new CannotMatchException(getBufferName()
//          + " must be a valid 6.0 buffer with status support");
//  }

//  /**
//   * @see org.jactr.core.production.condition.ICondition#bind(org.jactr.core.model.IModel,
//   *      Map)
//   */
//  public ICondition bind(IModel model, Map<String, Object> variableBindings)
//      throws CannotMatchException
//  {
//    QueryCondition qc = new QueryCondition(getBufferName());
//    qc.duplicateSlots(getSlots());
//    qc.checkAndBindSlots(model, variableBindings);
//    return qc;
//  }
  
  public int bind(IModel model, Map<String,Object> variableBindings, boolean isIterative) throws CannotMatchException
  {
    IActivationBuffer buffer = getActivationBuffer(model);
    if(!(buffer instanceof IStatusBuffer))
      throw new CannotMatchException(getBufferName()+" has no status slots to check");
    
    return ((IStatusBuffer) buffer).bind(getRequest(), variableBindings, isIterative);
  }

}
