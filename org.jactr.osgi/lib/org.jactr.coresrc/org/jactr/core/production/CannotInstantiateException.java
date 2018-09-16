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

package org.jactr.core.production;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jactr.core.production.action.IAction;
import org.jactr.core.production.condition.CannotMatchException;

/**
 * exception thrown by {@link IAction}s that are unable to be bound (rare) and
 * by {@link IProduction}s when they try to instantiate (common)
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class CannotInstantiateException extends Exception
{

  /**
   * 
   */
  private static final long                serialVersionUID = 3827300676978599038L;

  private Collection<CannotMatchException> _matchExceptions = Collections.EMPTY_LIST; ;

  private IProduction                      _production;

  /**
   * Constructor for the CannotInstantiateException object
   * 
   * @param message
   *          Description of the Parameter
   */
  // public CannotInstantiateException(String message)
  // {
  // super(message);
  // }
  //
  public CannotInstantiateException(String message, Exception cause)
  {
    super(message, cause);
  }

  public CannotInstantiateException(String message)
  {
    super(message);
  }

  public CannotInstantiateException(IProduction production, String message)
  {
    super(String.format("Could not instantiate %s because %s", production,
        message));
    setProduction(production);
  }

  /**
   * @param production
   * @param cmes
   *          collection of exceptions that is <b>copied</b>
   */
  public CannotInstantiateException(IProduction production,
      Collection<CannotMatchException> cmes)
  {
    super(String
        .format("Could not instantiate %s because %s", production, cmes));
    _matchExceptions = new ArrayList<CannotMatchException>(cmes);
    setProduction(production);
  }

  public IProduction getProduction()
  {
    return _production;
  }

  public void setProduction(IProduction production)
  {
    _production = production;
  }

  public Collection<CannotMatchException> getExceptions()
  {
    return _matchExceptions;
  }

  /**
   * we don't need stack trace information
   * 
   * @see java.lang.Throwable#fillInStackTrace()
   */
  @Override
  public Throwable fillInStackTrace()
  {
    return this;
  }
}