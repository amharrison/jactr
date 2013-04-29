/*
 * Created on Sep 22, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class VariableCondition extends AbstractBufferCondition
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(VariableCondition.class);

  private String                     _variableName;

  /**
   * @param bufferName
   */
  public VariableCondition(String bufferName, String variableName)
  {
    super(bufferName);
    _variableName = variableName;
  }

  public String getVariableName()
  {
    return _variableName;
  }

  public VariableCondition clone(IModel model, Map<String, Object> bindings)
      throws CannotMatchException
  {
    return new VariableCondition(getBufferName(), _variableName);
  }

  public int bind(IModel model, Map<String, Object> variableBindings,
      boolean isIterative) throws CannotMatchException
  {
    int unresolved = 0;

    IChunk resolvedChunk = (IChunk) variableBindings.get(getVariableName());

    if (resolvedChunk == null && !isIterative)
      throw new CannotMatchException(getVariableName() + " is undefined.");

    if (resolvedChunk == null)
      unresolved = getRequest().bind(model, variableBindings, isIterative) + 1;
    else
      unresolved = getRequest().bind(model,
          resolvedChunk.getSymbolicChunk().getName(),
          resolvedChunk.getSymbolicChunk(), variableBindings, isIterative);

    return unresolved;
  }

  // /**
  // * @see
  // org.jactr.core.production.condition.ICondition#bind(org.jactr.core.model.IModel,
  // * Map)
  // */
  // public ICondition bind(IModel model, Map<String, Object> variableBindings)
  // throws CannotMatchException
  // {
  // IActivationBuffer buffer = getActivationBuffer(model);
  // IChunk chunk = (IChunk) variableBindings.get(_variableName.toLowerCase());
  //
  // if (LOGGER.isDebugEnabled())
  // LOGGER.debug("Got " + chunk + " for " + _variableName);
  //
  // if (chunk == null)
  // throw new CannotMatchException(getVariableName() + " was not bound");
  //
  // chunk = buffer.contains(chunk);
  // if (chunk==null)
  // throw new CannotMatchException(getBufferName() + " does not contain " +
  // chunk);
  //
  //
  // Collection<IConditionalSlot> slots = new
  // ArrayList<IConditionalSlot>(getConditionalSlots());
  // if(buffer instanceof IStatusBuffer)
  // ((IStatusBuffer)buffer).matchesStatus(slots, variableBindings, false);
  //
  // ChunkCondition cc = new ChunkCondition(getBufferName(), chunk);
  // cc.duplicateSlots(slots);
  // cc.checkAndBindSlots(chunk, variableBindings);
  //
  // variableBindings.put("=" + getBufferName().toLowerCase(), chunk);
  // return cc;
  // }
}
