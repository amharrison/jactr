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

package org.jactr.core.utils;

/**
 * Thrown when an exception has occured that is probably the result of a
 * modeling error and not a problem with the system.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class ModelerException extends RuntimeException
{

  /**
   * 
   */
  private static final long serialVersionUID = 1744724588879032451L;

  /**
   * @param message
   * @param explanation
   */
  public ModelerException(String message, String explanation)
  {
    this(message, null, explanation);
  }

  /**
   * Description of the Field
   */
  protected Exception _exception;

  /**
   * Description of the Field
   */
  protected String    _explanation;

  /**
   * Constructor for the ModelerException object
   * 
   * @param message
   *            Description of the Parameter
   */
  public ModelerException(String message)
  {
    this(message, null, null);
  }

  /**
   * Constructor for the ModelerException object
   * 
   * @param message
   *            Description of the Parameter
   * @param e
   *            Description of the Parameter
   */
  public ModelerException(String message, Exception e)
  {
    this(message, e, null);
  }

  /**
   * Constructor for the ModelerException object
   * 
   * @param message
   *            Description of the Parameter
   * @param e
   *            Description of the Parameter
   * @param explanation
   *            Description of the Parameter
   */
  public ModelerException(String message, Exception e, String explanation)
  {
    super(message);
    if (e != null) initCause(e);
    _exception = e;
    _explanation = explanation;
  }

  /**
   * Gets the actualException attribute of the ModelerException object
   * 
   * @return The actualException value
   */
  public Exception getActualException()
  {
    return _exception;
  }

  /**
   * Gets the explanation attribute of the ModelerException object
   * 
   * @return The explanation value
   */
  public String getExplanation()
  {
    return _explanation;
  }

}