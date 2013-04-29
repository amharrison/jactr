/*
 * Created on Mar 13, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime.controller.debug.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.BreakpointType;

/**
 * @author developer
 */
public class BreakpointEvent extends
    AbstractACTREvent<IModel, IBreakpointListener>
{

  BreakpointType _type;

  Object         _details;

  public BreakpointEvent(IModel model, BreakpointType type, Object details)
  {
    super(model, ACTRRuntime.getRuntime().getClock(model).getTime());
    _type = type;
    _details = details;
  }

  public BreakpointType getType()
  {
    return _type;
  }

  public Object getDetails()
  {
    return _details;
  }

  /**
   * @see org.jactr.core.event.AbstractACTREvent#fire(java.lang.Object)
   */
  @Override
  public void fire(final IBreakpointListener listener)
  {
    listener.breakpointReached(this);
  }

}
