/*
 * Created on Oct 21, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.model.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.extensions.IExtension;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.instrument.IInstrument;

public class ModelEvent extends AbstractACTREvent<IModel, IModelListener>
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelEvent.class);

  static public enum Type {
    MODULE_INSTALLED, EXTENSION_INSTALLED, INSTRUMENT_INSTALLED, BUFFER_INSTALLED, INITIALIZED, CONNECTED, DISCONNECTED, STARTED, SUSPENDED, RESUMED, STOPPED, CYCLE_STARTED, CYCLE_STOPPED, EXCEPTION
  };

  protected Type   _type;

  // protected String _parameterName;

  protected Object _oldValue;

  protected Object _newValue;

  public ModelEvent(IModel model, Type type)
  {
    super(model, ACTRRuntime.getRuntime().getClock(model).getTime());
    _type = type;
  }

  public ModelEvent(IModel model, IModule module)
  {
    this(model, Type.MODULE_INSTALLED);
    _newValue = module;
  }

  public ModelEvent(IModel model, IExtension extension)
  {
    this(model, Type.EXTENSION_INSTALLED);
    _newValue = extension;
  }

  public ModelEvent(IModel model, IActivationBuffer buffer)
  {
    this(model, Type.BUFFER_INSTALLED);
    _newValue = buffer;
  }

  public ModelEvent(IModel model, IInstrument instrument)
  {
    this(model, Type.INSTRUMENT_INSTALLED);
    _newValue = instrument;
  }

  public ModelEvent(IModel model, Throwable thrown)
  {
    this(model, Type.EXCEPTION, thrown);
  }
  
  public ModelEvent(IModel model, Type type, Throwable thrown)
  {
    this(model, type);
    _newValue = thrown;
  }

  // public ModelEvent(IModel model, String parameterName, Object newValue,
  // Object oldValue)
  // {
  // this(model, Type.PARAMETER_CHANGED);
  // _parameterName = parameterName;
  // _newValue = newValue;
  // _oldValue = oldValue;
  // }

  public String getMessage()
  {
    return (String) _newValue;
  }

  @Override
  public void fire(final IModelListener listener)
  {
    switch (getType())
    {
      case MODULE_INSTALLED:
        listener.moduleInstalled(this);
        break;
      case EXTENSION_INSTALLED:
        listener.extensionInstalled(this);
        break;
      case INSTRUMENT_INSTALLED:
        listener.instrumentInstalled(this);
        break;
      case BUFFER_INSTALLED:
        listener.bufferInstalled(this);
        break;
      case INITIALIZED:
        listener.modelInitialized(this);
        break;
      case CONNECTED:
        listener.modelConnected(this);
        break;
      case DISCONNECTED:
        listener.modelDisconnected(this);
        break;
      case STARTED:
        listener.modelStarted(this);
        break;
      case SUSPENDED:
        listener.modelSuspended(this);
        break;
      case RESUMED:
        listener.modelResumed(this);
        break;
      case STOPPED:
        listener.modelStopped(this);
        break;
      case EXCEPTION:
        listener.exceptionThrown(this);
        break;
      // case PARAMETER_CHANGED:
      // listener.parameterChanged(this);
      // break;
      case CYCLE_STARTED:
        listener.cycleStarted(this);
        break;
      case CYCLE_STOPPED:
        listener.cycleStopped(this);
        break;
      default:
        LOGGER.warn("No clue what to do with event of type " + this.getType());
    }
  }

  public Type getType()
  {
    return _type;
  }

  public Throwable getException()
  {
    return (Throwable) _newValue;
  }

  public IModule getModule()
  {
    return (IModule) _newValue;
  }

  public IExtension getExtension()
  {
    return (IExtension) _newValue;
  }

  public IActivationBuffer getBuffer()
  {
    return (IActivationBuffer) _newValue;
  }

  public IInstrument getInstrument()
  {
    return (IInstrument) _newValue;
  }

  // public Object getNewParameterValue()
  // {
  // return _newValue;
  // }
  //
  // public Object getOldParameterValue()
  // {
  // return _oldValue;
  // }
  //
  // public String getParameterName()
  // {
  // return _parameterName;
  // }
}
