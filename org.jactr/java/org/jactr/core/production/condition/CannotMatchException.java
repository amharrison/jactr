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

import org.jactr.core.production.condition.match.GeneralMatchFailure;
import org.jactr.core.production.condition.match.IMatchFailure;

/**
 * Description of the Class
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class CannotMatchException extends Exception
{

  private final IMatchFailure _cause;
  /**
   * Constructor for the CannotMatchException object
   * 
   * @param message
   *            Description of the Parameter
   */
  public CannotMatchException(String message)
  {
    super(message);
    _cause = new GeneralMatchFailure(null, message);
  }

  public CannotMatchException(String message, Exception e)
  {
    super(message, e);
    _cause = new GeneralMatchFailure(null, message);
  }

  /**
   * provides a more detailed explanation of the failure
   * 
   * @param failure
   */
  public CannotMatchException(IMatchFailure failure)
  {
    super(failure.toString());
    _cause = failure;
  }


  public IMatchFailure getMismatch()
  {
    return _cause;
  }

  /**
   * no need for stack traces here
   * 
   * @see java.lang.Throwable#fillInStackTrace()
   */
  @Override
  public Throwable fillInStackTrace()
  {
    return this;
  }

  @Override
  public String toString()
  {
    return getMessage();
  }

}