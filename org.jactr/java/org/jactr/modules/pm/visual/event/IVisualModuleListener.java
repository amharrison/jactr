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
package org.jactr.modules.pm.visual.event;

import java.util.EventListener;

import org.jactr.core.event.IParameterListener;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleListener;

/**
 * permits listening to the visual module these events are called when the
 * visual module finishes the action. that means these events might be received
 * BEFORE the model actually recognizes the occurrence. For instance:
 * encodedVisualObject will be called after IVisualModule#encodeVisualChunkAt(),
 * but will likely arrive before the chunk is actually inserted into the visual
 * buffer.
 * 
 * @author developer
 */
public interface IVisualModuleListener extends EventListener,
    IParameterListener, IPerceptualMemoryModuleListener
{

  public void trackingObjectStarted(VisualModuleEvent event);

  public void trackingObjectStopped(VisualModuleEvent event);

  public void trackedObjectMoved(VisualModuleEvent event);

}
