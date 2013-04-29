/*
 * Created on Feb 20, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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


/**
 * @author developer
 */
public class DecoratedModelListener implements IModelListener
{
 
  private IModelListener   _listener;

  public DecoratedModelListener()
  {
    this(null);
  }

  public DecoratedModelListener(IModelListener listener)
  {
    _listener = listener;
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#bufferInstalled(org.jactr.core.model.event.ModelEvent)
   */
  public void bufferInstalled(ModelEvent me)
  {
    if (_listener != null) _listener.bufferInstalled(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#cycleStarted(org.jactr.core.model.event.ModelEvent)
   */
  public void cycleStarted(ModelEvent me)
  {
    if (_listener != null) _listener.cycleStarted(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#exceptionThrown(org.jactr.core.model.event.ModelEvent)
   */
  public void exceptionThrown(ModelEvent me)
  {
    if (_listener != null) _listener.exceptionThrown(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#extensionInstalled(org.jactr.core.model.event.ModelEvent)
   */
  public void extensionInstalled(ModelEvent me)
  {
    if (_listener != null) _listener.extensionInstalled(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#instrumentInstalled(org.jactr.core.model.event.ModelEvent)
   */
  public void instrumentInstalled(ModelEvent me)
  {
    if (_listener != null) _listener.instrumentInstalled(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelConnected(org.jactr.core.model.event.ModelEvent)
   */
  public void modelConnected(ModelEvent me)
  {
    if (_listener != null) _listener.modelConnected(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelInitialized(org.jactr.core.model.event.ModelEvent)
   */
  public void modelInitialized(ModelEvent me)
  {
    if (_listener != null) _listener.modelInitialized(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelResumed(org.jactr.core.model.event.ModelEvent)
   */
  public void modelResumed(ModelEvent me)
  {
    if (_listener != null) _listener.modelResumed(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelStarted(org.jactr.core.model.event.ModelEvent)
   */
  public void modelStarted(ModelEvent me)
  {
    if (_listener != null) _listener.modelStarted(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelStopped(org.jactr.core.model.event.ModelEvent)
   */
  public void modelStopped(ModelEvent me)
  {
    if (_listener != null) _listener.modelStopped(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelSuspended(org.jactr.core.model.event.ModelEvent)
   */
  public void modelSuspended(ModelEvent me)
  {
    if (_listener != null) _listener.modelSuspended(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#moduleInstalled(org.jactr.core.model.event.ModelEvent)
   */
  public void moduleInstalled(ModelEvent me)
  {
    if (_listener != null) _listener.moduleInstalled(me);
  }

  // /**
  // * @see
  // org.jactr.core.event.IParameterListener#parameterChanged(org.jactr.core.event.IParameterEvent)
  // */
  // @SuppressWarnings("unchecked")
  // public void parameterChanged(IParameterEvent pe)
  // {
  // if (_listener != null) _listener.parameterChanged(pe);
  // }

  /**
   * @see org.jactr.core.model.event.IModelListener#cycleStopped(org.jactr.core.model.event.ModelEvent)
   */
  public void cycleStopped(ModelEvent me)
  {
    _listener.cycleStopped(me);
  }

  /**
   * @see org.jactr.core.model.event.IModelListener#modelDisconnected(org.jactr.core.model.event.ModelEvent)
   */
  public void modelDisconnected(ModelEvent me)
  {
    _listener.modelDisconnected(me);
  }

}
