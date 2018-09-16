/*
 * Created on Nov 16, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.runtime.event;

import java.util.EventListener;


public interface IACTRRuntimeListener extends EventListener
{

  /**
   * called anytime a model is added to the runtime
   * 
   * @param event
   */
  public void modelAdded(ACTRRuntimeEvent event);
  
  /**
   * when model is removed
   * 
   * @param event
   */
  public void modelRemoved(ACTRRuntimeEvent event);
  
  /**
   * after at least one model has starteed
   * 
   * @param event
   */
  public void runtimeStarted(ACTRRuntimeEvent event);
  
  /**
   * after the last model has stopped
   * 
   * @param event
   */
  public void runtimeStopped(ACTRRuntimeEvent event);
  
  /**
   * after last model has suspended
   * 
   * @param event
   */
  public void runtimeSuspended(ACTRRuntimeEvent event);
  
  /**
   * after last model resumes
   * 
   * @param event
   */
  public void runtimeResumed(ACTRRuntimeEvent event);

  /**
   * after model has started
   * 
   * @param event
   */
  public void modelStarted(ACTRRuntimeEvent event);

  /**
   * after model has stopped, and all its events (stopped, disconnected) have
   * been fired, this will be called (but before runtimeStopped if this is th
   * elast model)
   * 
   * @param event
   */
  public void modelStopped(ACTRRuntimeEvent event);
}


