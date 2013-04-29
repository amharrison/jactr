/*
 * Created on May 28, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.event;

import org.jactr.core.utils.parameter.IParameterized;

/**
 * @author developer
 */
public class ParameterEvent extends
    AbstractACTREvent<IParameterized, IParameterListener> implements
    IParameterEvent<IParameterized, IParameterListener>
{

  private String _parameterName;

  private Object _oldValue;

  private Object _newValue;

  public ParameterEvent(IParameterized source, double simTime,
      String parameterName, Object oldValue, Object newValue)
  {
    super(source, simTime);
    _parameterName = parameterName;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  /**
   * @see org.jactr.core.event.AbstractACTREvent#fire(java.lang.Object)
   */
  public void fire(IParameterListener listener)
  {
    listener.parameterChanged(this);
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
  }

}
