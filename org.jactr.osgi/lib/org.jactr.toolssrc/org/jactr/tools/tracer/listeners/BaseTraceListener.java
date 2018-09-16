/*
 * Created on Mar 7, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.tracer.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.event.IACTREvent;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

/**
 * @author developer
 */
public abstract class BaseTraceListener implements ITraceListener
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(BaseTraceListener.class);

  ITraceSink                         _sink;

  IEventTransformer                  _transformer;

  /**
   * 
   */
  public BaseTraceListener()
  {
    super();
  }

  /**
   * @see org.jactr.tools.tracer.listeners.ITraceListener#setTraceSink(org.jactr.tools.tracer.ITraceSink)
   */
  public void setTraceSink(ITraceSink sink)
  {
    _sink = sink;
  }

  protected void setEventTransformer(IEventTransformer transformer)
  {
    _transformer = transformer;
  }

  protected IEventTransformer getEventTransformer()
  {
    return _transformer;
  }

  protected void redirectEvent(IACTREvent pme)
  {
    if (_transformer != null)
      try
      {
        ITransformedEvent event = _transformer.transform(pme);
        if (event != null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("transformed " + pme + " into " + event + " sinking");
          sink(event);
        }
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not transform event "
              + pme.getClass().getSimpleName(), e);
      }
  }

  protected void sink(ITransformedEvent event)
  {
    if (_sink != null && _sink.isOpen())
    _sink.add(event);
  }

}
