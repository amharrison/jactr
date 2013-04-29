/*
 * Created on Dec 18, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * Log event
 * 
 * @author developer
 */
public class LogEvent extends AbstractACTREvent<IModel, ILogger>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(LogEvent.class);

  final private IModel     _model;

  final private String     _streamName;

  final private String     _message;

  public LogEvent(IModel model, String streamName, String message)
  {
    super(model, ACTRRuntime.getRuntime().getClock(model).getTime());
    _model = model;
    _streamName = streamName;
    _message = message;
  }

  /**
   * @see org.jactr.core.event.AbstractACTREvent#fire(java.lang.Object)
   */
  @Override
  public void fire(ILogger listener)
  {
    listener.log(this);
  }

  final public IModel getModel()
  {
    return _model;
  }

  final public String getStreamName()
  {
    return _streamName;
  }

  final public String getMessage()
  {
    return _message;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("<");
    sb.append(_model.getName()).append(".").append(_streamName).append(":")
        .append(_message).append(">");
    return sb.toString();
  }
}
