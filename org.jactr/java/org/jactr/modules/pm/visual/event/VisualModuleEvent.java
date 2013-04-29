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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.visual.IVisualModule;

public class VisualModuleEvent extends
    AbstractACTREvent<IVisualModule, IVisualModuleListener> implements
    IParameterEvent<IVisualModule, IVisualModuleListener>,
    IPerceptualMemoryModuleEvent<IVisualModule, IVisualModuleListener>
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(VisualModuleEvent.class);

  static public enum Type {
    START_TRACKING, STOP_TRACKING, TRACKING_MOVED, ENCODED, SEARCHED, RESET, PARAMETER_CHANGED
  }

  private final Type _type;

  private String     _parameterName;

  private Object     _oldValue;

  private Object     _newValue;

  public VisualModuleEvent(IVisualModule source, String parameterName,
      Object oldValue, Object newValue)
  {
    this(source, Type.PARAMETER_CHANGED);
    _parameterName = parameterName;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  public VisualModuleEvent(IVisualModule source, Type type, IChunk chunk)
  {
    this(source, type);
    _newValue = chunk;
  }

  /**
   * constructor for visual search
   * 
   * @param source
   * @param chunk
   * @param wasStuff
   */
  public VisualModuleEvent(IVisualModule source, IChunk chunk, boolean wasStuff)
  {
    this(source, Type.SEARCHED);
    _newValue = chunk;
    _oldValue = wasStuff;
  }

  public VisualModuleEvent(IVisualModule source, Type type)
  {
    super(source, ACTRRuntime.getRuntime().getClock(source.getModel())
        .getTime());
    _type = type;
  }

  public IChunk getChunk()
  {
    return (IChunk) _newValue;
  }

  public boolean wasStuff()
  {
    if (getType() == Type.SEARCHED) return (Boolean) _oldValue;
    return false;
  }

  /**
   * @see org.jactr.core.event.AbstractACTREvent#fire(java.lang.Object)
   */
  @Override
  public void fire(final IVisualModuleListener listener)
  {
    switch (this.getType())
    {
      case ENCODED:
        listener.perceptAttended(this);
        break;
      case SEARCHED:
        listener.perceptIndexFound(this);
        break;
      case RESET:
        listener.moduleReset(this);
        break;
      case PARAMETER_CHANGED:
        listener.parameterChanged(this);
        break;
      case START_TRACKING:
        listener.trackingObjectStarted(this);
        break;
      case STOP_TRACKING:
        listener.trackingObjectStopped(this);
        break;
      case TRACKING_MOVED:
        listener.trackedObjectMoved(this);
        break;
      default:
        LOGGER.warn("No clue what to do with " + this.getType());
    }
  }

  public Type getType()
  {
    return _type;
  }

  /**
   * @see org.jactr.core.event.IParameterEvent#getNewParameterValue()
   */
  public Object getNewParameterValue()
  {
    return _newValue;
  }

  /**
   * @see org.jactr.core.event.IParameterEvent#getOldParameterValue()
   */
  public Object getOldParameterValue()
  {
    return _oldValue;
  }

  /**
   * @see org.jactr.core.event.IParameterEvent#getParameterName()
   */
  public String getParameterName()
  {
    return _parameterName;
  };

}
