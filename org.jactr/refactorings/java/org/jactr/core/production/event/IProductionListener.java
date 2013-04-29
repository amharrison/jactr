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

package org.jactr.core.production.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;

/**
 * Description of the Interface
 * 
 * @author harrison
 * @created April 18, 2003
 */
public interface IProductionListener extends EventListener, IParameterListener
{
  
  /**
   * production has been added to the model
   * @param pe
   */
  public void productionEncoded(ProductionEvent pe);
  
  
  /**
   * production has been successfully instantiated
   * @param pe
   */
  public void productionInstantiated(ProductionEvent pe);
  
  /**
   * production has been fired, you can access the production or its
   * instantiation
   * @param pe
   */
  public void productionFired(ProductionEvent pe);
  
  
  /**
   * condition has been added, only available before encoding
   * @param pe
   */
  public void conditionAdded(ProductionEvent pe);
  
  /**
   * 
   * @param pe
   */
  public void conditionRemoved(ProductionEvent pe);
  
  
  public void actionAdded(ProductionEvent pe);
  
  
  public void actionRemoved(ProductionEvent pe);
}

