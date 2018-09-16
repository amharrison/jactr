/*
 * Created on Mar 13, 2007
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
package org.jactr.core.runtime.controller.debug;

import java.util.concurrent.Executor;

import org.jactr.core.model.IModel;
import org.jactr.core.production.IProduction;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.event.IBreakpointListener;

/**
 * An extension to a controller that permits breakpoint management
 * @author developer
 *
 */
public interface IDebugController extends IController
{

  /**
   * when a break point is reached, the breakpoint listeners are called
   * 
   */
  public abstract void addListener(IBreakpointListener listener,
      Executor executor);

  /**
   * remove said listener
   * @param listener
   */
  public abstract void removeListener(IBreakpointListener listener);

  /**
   * clear all of the break points permitting uninterrupted execution,
   * unless an exception occurs
   *
   */
  public abstract void clearBreakpoints();

  /**
   * clear the specified breakpoints
   * @param model
   * @param type
   */
  public abstract void clearBreakpoints(IModel model, BreakpointType type);

  /**
   * add a breakpoint
   * @param model
   * @param type
   * @param value
   */
  public abstract void addBreakpoint(IModel model, BreakpointType type,
      Object value);

  /**
   * remove a breakpoint
   * @param model
   * @param type
   * @param value
   */
  public abstract void removeBreakpoint(IModel model, BreakpointType type,
      Object value);

  /**
   * is this object a breakpoint
   * @param model
   * @param type
   * @param value
   * @return
   */
  public abstract boolean isBreakpoint(IModel model, BreakpointType type,
      Object value);

  
  public void setEnabled(IProduction production, boolean enabled);
  
  /**
   * invoke suspend on a specific model
   * @param model
   */
//  public void suspend(IModel model);
  
  /**
   *  invoke resume on a specific model
   */
//  public void resume(IModel model);
  
  /**
   * stop a specific model. No start method is provided since
   * models should be started by the runtime
   */
//  public void stop(IModel model);
}
