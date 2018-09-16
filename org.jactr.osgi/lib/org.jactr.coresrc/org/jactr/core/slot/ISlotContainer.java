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
package org.jactr.core.slot;

import java.util.Collection;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created January 22, 2003
 */
public interface ISlotContainer
{

  /**
   * return a duplicate copy of all the slots
   * 
   * @return The slots value
   * @since
   */
  public Collection<? extends ISlot> getSlots();
  
  public Collection<ISlot> getSlots(Collection<ISlot> container);

  /**
   * Adds a slot
   * 
   * @param slot
   *            The feature to be added to the ISlot attribute
   * @since
   */
  public void addSlot(ISlot slot);

  /**
   * remove a slot
   * 
   * @param slot
   *            Description of Parameter
   * @since
   */
  public void removeSlot(ISlot slot);

}
