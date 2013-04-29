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

package org.jactr.core.chunk;

/*
 * thrown when something funky is attempted with a chunk - setting/getting a
 * slot value for an impossible slot..
 */
/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class IllegalChunkStateException extends IllegalStateException
{

  /**
   * 
   */
  private static final long serialVersionUID = 2197207335390017443L;

  /**
   * Constructor for the IllegalChunkStateException object
   * 
   * @param message
   *            Description of the Parameter
   */
  public IllegalChunkStateException(String message)
  {
    super(message);
  }

  public IllegalChunkStateException(String message, Exception e)
  {
    this(message);
    initCause(e);
  }
}
