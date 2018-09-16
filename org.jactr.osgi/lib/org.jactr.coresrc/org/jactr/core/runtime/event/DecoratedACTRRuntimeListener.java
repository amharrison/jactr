/*
 * Created on Feb 20, 2007
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/**
 * @author developer
 *
 */
public class DecoratedACTRRuntimeListener implements IACTRRuntimeListener
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(DecoratedACTRRuntimeListener.class);

  
  private IACTRRuntimeListener _listener;
  
  public DecoratedACTRRuntimeListener()
  {
    this(null);
  }
  
  public DecoratedACTRRuntimeListener(IACTRRuntimeListener listener)
  {
    _listener = listener;
  }
  
  /**
   * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelAdded(org.jactr.core.runtime.event.ACTRRuntimeEvent)
   */
  public void modelAdded(ACTRRuntimeEvent event)
  {
    if(_listener!=null)
      _listener.modelAdded(event);
  }

  /**
   * @see org.jactr.core.runtime.event.IACTRRuntimeListener#modelRemoved(org.jactr.core.runtime.event.ACTRRuntimeEvent)
   */
  public void modelRemoved(ACTRRuntimeEvent event)
  {
    if(_listener!=null)
      _listener.modelRemoved(event);
  }


  /**
   * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeResumed(org.jactr.core.runtime.event.ACTRRuntimeEvent)
   */
  public void runtimeResumed(ACTRRuntimeEvent event)
  {
    if(_listener!=null)
      _listener.runtimeResumed(event);
  }

  /**
   * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeStarted(org.jactr.core.runtime.event.ACTRRuntimeEvent)
   */
  public void runtimeStarted(ACTRRuntimeEvent event)
  {
    if(_listener!=null)
      _listener.runtimeStarted(event);
  }

  /**
   * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeStopped(org.jactr.core.runtime.event.ACTRRuntimeEvent)
   */
  public void runtimeStopped(ACTRRuntimeEvent evnet)
  {
    if(_listener!=null)
      _listener.runtimeStopped(evnet);
  }

  /**
   * @see org.jactr.core.runtime.event.IACTRRuntimeListener#runtimeSuspended(org.jactr.core.runtime.event.ACTRRuntimeEvent)
   */
  public void runtimeSuspended(ACTRRuntimeEvent event)
  {
    if(_listener!=null)
      _listener.runtimeSuspended(event);
  }

  public void modelStarted(ACTRRuntimeEvent event)
  {
    if (_listener != null) _listener.modelStarted(event);

  }

  public void modelStopped(ACTRRuntimeEvent event)
  {
    if (_listener != null) _listener.modelStopped(event);

  }

}


