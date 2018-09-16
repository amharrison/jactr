/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.tracer.sinks;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.async.controller.RemoteInterface;
import org.jactr.tools.async.message.BulkMessage;
import org.jactr.tools.async.message.IMessage;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

/**
 * @author developer
 */
public class NetworkedSink implements ITraceSink
{
  /**
   * logger definition
   */
  static private final Log     LOGGER              = LogFactory
                                                       .getLog(NetworkedSink.class);

  private long                 _maximumDelay       = 500;                           // ms

  private int                  MAXIMUM_BUFFER_SIZE = 25;


  private Collection<IMessage> _messageBuffer;

  private Runnable             _autoFlush;

  private RemoteInterface      _interface;

  private volatile boolean     _scheduled          = false;

  public NetworkedSink()
  {
    _interface = RemoteInterface.getActiveRemoteInterface();
    if (_interface == null)
      throw new RuntimeException(
          "A RemoteInterface must be active before instantiating this sink");

    try
    {
      MAXIMUM_BUFFER_SIZE = Integer.parseInt(System
          .getProperty("jACTR:MaximumBufferSize"));
    }
    catch (NumberFormatException nfe)
    {
      MAXIMUM_BUFFER_SIZE = 25;
    }

    _messageBuffer = Collections
        .synchronizedCollection(FastListFactory.newInstance());

    _autoFlush = new Runnable() {

      public void run()
      {
        // no need for this to be safe..
        _scheduled = false;
        try
        {
          flush();
        }
        catch (Exception e)
        {
          LOGGER.error(".run threw Exception : ", e);
        }
      }

    };
  }

  protected boolean shouldFlush()
  {
    synchronized (_messageBuffer)
    {
      return _messageBuffer.size() >= MAXIMUM_BUFFER_SIZE;
    }
  }

  /*
   * use a delay to trigger the flush..
   */
  protected void scheduleFlush(long delay)
  {
    if (_scheduled) return;
    _scheduled = true;

    ScheduledExecutorService executor = (ScheduledExecutorService) ExecutorServices
        .getExecutor(ExecutorServices.PERIODIC);
    try
    {
      executor.schedule(_autoFlush, delay, TimeUnit.MILLISECONDS);
    }
    catch (RejectedExecutionException ree)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER
            .debug(
                String
                    .format("Networked flush rejected, probably in the mids of shutdown"),
                ree);
    }
  }

  /**
   * @see org.jactr.tools.tracer.ITraceSink#add(org.jactr.tools.tracer.transformer.ITransformedEvent)
   */
  public void add(ITransformedEvent event)
  {
    if (event == null)
    {
      LOGGER.error("null message received ", new NullPointerException());
      return;
    }
    _messageBuffer.add(event);
    if (shouldFlush())
      try
      {
        flush();
      }
      catch (Exception e)
      {
        LOGGER.error("Could not flush after capacity is exceeded ", e);
      }
    else
      scheduleFlush(_maximumDelay);
  }

  /**
   * @see org.jactr.tools.tracer.ITraceSink#flush()
   */
  public void flush() throws Exception
  {
    if (!isOpen())
    {
      _messageBuffer.clear();
      return;
    }

    BulkMessage message = null;
    synchronized (_messageBuffer)
    {
      if (_messageBuffer.size() > 0)
      {
        message = new BulkMessage(_messageBuffer);
        _messageBuffer.clear();
      }
    }

    if (message != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Sending bulk message " + message.getSize());
      _interface.getActiveSession().write(message);
    }
  }

  public boolean isOpen()
  {
    return _interface.getActiveSession() != null;
  }
}
