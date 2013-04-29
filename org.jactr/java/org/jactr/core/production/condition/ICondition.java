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

package org.jactr.core.production.condition;


import org.jactr.core.model.IModel;
import org.jactr.core.production.VariableBindings;

/**
 * Basic definition of a matching condition for the production. equals() and
 * hashCode() for these classes should be dependent upon all the conditions that
 * are immutable (i.e., not slots that will change value during binding and
 * resolution)
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface ICondition
{

  /**
   * called when we are sure we are done with this condition
   */
  public void dispose();

  /**
   * attempt to clone this condition before it will be bound in the
   * instantiation phase. We pass the current bindings so that the condition can
   * do an early rejection if possible.
   * 
   * @param model
   * @param variableBindings
   * @return a writable copy of the condition that will be bound
   * @throws CannotMatchException
   *             if there is no way this condition can be matched
   */
  public ICondition clone(IModel model, VariableBindings variableBindings)
      throws CannotMatchException;

  /**
   * Iteratively perform the resolution and binding for this condition. If this
   * condition defines any variables, they are placed into the bindings map for
   * other conditions to exploit. Similarly, it will resolve any bindings that
   * it needs in order to be matched. If at any point the condition determines
   * that it cannot be matched, the exception is to be thrown. Similarly, if
   * isIterative is false, and there are unresolved bindings, the exception
   * should be thrown. <br>
   * Otherwise, the number of unresolved bindings is returned which allows the
   * instantiation calculation determine if another resolution round is
   * required.
   * 
   * @return number of unresolved variables
   */
  public int bind(IModel model, VariableBindings variableBindings,
      boolean isIterative) throws CannotMatchException;
}
