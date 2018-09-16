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

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.IMutableVariableNameSlot;
import org.jactr.core.slot.ISlot;

/**
 * DefaultAction is the general base class for all the of the Actions presented
 * (aside, of course, for IAction itself). It provides general implementations
 * of bind(), fire(), and duplicate(). The most important of these is the bind()
 * method which handles all the variable resolution for Actions.
 */

public abstract class DefaultAction implements IAction
{

  private static transient Log LOGGER = LogFactory.getLog(DefaultAction.class
                                          .getName());

  /**
   * Constructor for the DefaultAction object
   */
  public DefaultAction()
  {
    // NoOp, but all Actions must have a zero param constructor
  }

  /**
   * Description of the Method
   */
  public void dispose()
  {
    // NoOp for now
  }

  /**
   * Description of the Method
   * 
   * @param instantiation
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public abstract double fire(IInstantiation instantiation, double firingTime);

  /**
   * resolve the value of variableName
   * 
   * @param variableName
   *          Description of the Parameter
   * @param bindings
   *          Description of the Parameter
   * @return the value of variableName or null if undefined. it the variableName
   *         points to a ISlot, the ISlot.getSlotValue() is returned.
   */
  static public Object resolve(String variableName, VariableBindings bindings)
  {
    Object rtn = bindings.get(variableName.toLowerCase());
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(variableName + " is bound to " + rtn);
    return rtn;
  }

  protected void bindSlotValues(VariableBindings bindings,
      Collection<? extends IMutableSlot> slots)
      throws CannotInstantiateException
  {
    for (IMutableSlot ms : slots)
    {
      /*
       * resolve the name if applicable
       */
      if (ms instanceof IMutableVariableNameSlot
          && ((IMutableVariableNameSlot) ms).isVariableName())
      {
        /*
         * if we can resolve, do so, otherwise skip until we can.
         */
        String variableName = ms.getName().toLowerCase();
        if (bindings.isBound(variableName))
          ((IMutableVariableNameSlot) ms).setName(bindings.get(variableName)
              .toString());
        else
          continue;
      }

      Object sVal = ms.getValue();
      if (ms.isVariableValue())
      {
        Object o = resolve((String) sVal, bindings);
        if (o != null)
        {
          ms.setValue(o);
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("resolving " + sVal + " to " + o);
        }
      }
      else if (sVal instanceof String)
      {
        /*
         * automatically resolve string contents
         */
        String rewrote = OutputAction.replaceVariables((String) sVal, bindings);
        ms.setValue(rewrote);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Rewrote string '" + sVal + "' to '" + rewrote + "'");
      }
    }
  }

  /**
   * updates chunk's slots with these slots. If any slots is still a variable,
   * IllegalActionStateException is thrown
   * 
   * @param chunk
   * @param slots
   *          slots that should be set in chunk
   */
  protected void updateSlots(IChunk chunk, Collection<? extends ISlot> slots)
  {
    /**
     * we need the write lock here
     */
    try
    {
      chunk.getWriteLock().lock();
      ISymbolicChunk symChunk = chunk.getSymbolicChunk();
      /*
       * now we actually set the slot values
       */
      for (ISlot slot : slots)
        if (!slot.isVariableValue())
        {
          String slotName = slot.getName();
          Object slotValue = slot.getValue();

          ((IMutableSlot) symChunk.getSlot(slotName)).setValue(slotValue);
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Changed " + symChunk.getName() + "." + slotName
                + " to " + slotValue);
        }
        else
          throw new IllegalActionStateException(
              slot
                  + " is still a variable, variable bindings were not complete somewhere");
    }
    finally
    {
      chunk.getWriteLock().unlock();
    }
  }
}
