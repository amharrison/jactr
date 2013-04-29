/*
 * Created on Jul 24, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.common.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;

/**
 * permits listening to a perceptual module. these events are called when the
 * module finishes the action. that means these events might be received BEFORE
 * the model actually recognizes the occurrence. For instance: perceptAttended
 * will be called after the encoding request, but will likely arrive before the
 * chunk is actually inserted into the buffer.
 * 
 * @author developer
 */
public interface IPerceptualMemoryModuleListener extends EventListener,
    IParameterListener
{

  /**
   * called after a percept has been attended to, but before it is actually
   * inserted into the appropriate buffer
   */
  public void perceptAttended(IPerceptualMemoryModuleEvent event);

  /**
   * fired when searchPattern has been processed yielding a perceptual index
   * chunk
   */
  public void perceptIndexFound(IPerceptualMemoryModuleEvent event);

  /**
   * called when the module is reset
   * 
   * @param event
   */
  public void moduleReset(IPerceptualMemoryModuleEvent event);

}
