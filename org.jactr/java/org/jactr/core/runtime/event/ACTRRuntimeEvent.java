/*
 * Created on Nov 16, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;

public class ACTRRuntimeEvent extends
    AbstractACTREvent<ACTRRuntime, IACTRRuntimeListener>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(ACTRRuntimeEvent.class);

  static public enum Type {
    MODEL_ADDED, MODEL_REMOVED, STARTED, STOPPED, SUSPENDED, RESUMED, MODEL_STARTED, MODEL_STOPPED
  };

  private Type   _type;

  private IModel _model;
  
  private Exception _exception;

  public ACTRRuntimeEvent(IModel model, Type type)
  {
    this(model, type, null);
  }

  public ACTRRuntimeEvent(Type type)
  {
    this(null, type, null);
  }
  
  public ACTRRuntimeEvent(IModel model, Type type, Exception exception)
  {
    super(ACTRRuntime.getRuntime(), ACTRRuntime.getRuntime().getClock(model)
        .getTime());
    _model = model;
    _type = type;
    _exception = exception;
  }
  
  public Exception getException()
  {
    return _exception;
  }

  public Type getType()
  {
    return _type;
  }

  public IModel getModel()
  {
    return _model;
  }

  @Override
  public void fire(final IACTRRuntimeListener listener)
  {
    switch (this.getType())
    {
      case MODEL_ADDED:
        listener.modelAdded(this);
        break;
      case MODEL_REMOVED:
        listener.modelRemoved(this);
        break;
      case STARTED:
        listener.runtimeStarted(this);
        break;
      case SUSPENDED:
        listener.runtimeSuspended(this);
        break;
      case RESUMED:
        listener.runtimeResumed(this);
        break;
      case STOPPED:
        listener.runtimeStopped(this);
        break;
      case MODEL_STARTED:
        listener.modelStarted(this);
        break;
      case MODEL_STOPPED:
        listener.modelStopped(this);
        break;
      default:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("No clue what to do with event type " + this.getType());
    }
  }

}
