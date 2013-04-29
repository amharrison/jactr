/*******************************************************************************
 * Copyright (C) 2001, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 ******************************************************************************/
package org.jactr.core.extensions;

/**
 * @author harrison To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Generation - Code and Comments
 */
public class IllegalExtensionStateException extends RuntimeException
{

  /**
   * 
   */
  private static final long serialVersionUID = -6858208188175237358L;

  public IllegalExtensionStateException(String message)
  {
    super(message);
  }

  /**
   * @param message
   * @param e
   */
  public IllegalExtensionStateException(String message, Exception e)
  {
    super(message, e);
  }
}