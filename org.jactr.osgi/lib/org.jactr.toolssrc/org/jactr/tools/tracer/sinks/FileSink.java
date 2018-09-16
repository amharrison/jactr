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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

/**
 * @author developer
 */
public class FileSink implements ITraceSink
{
  /**
   * logger definition
   */
  static private final Log             LOGGER = LogFactory
                                                  .getLog(FileSink.class);

  private ArrayList<ITransformedEvent> _queue;

  private ObjectOutputStream           _outputStream;

  private boolean                      _independentClocksEnabled = Boolean
                                                                     .getBoolean("connector.independentClocks");

  public FileSink(URL file) throws IOException
  {
    if (_independentClocksEnabled
        && ACTRRuntime.getRuntime().getModels().size() > 1)
      throw new IOException(
          "Cannot use the file sink with independentClocks and multiple models. Reduce to one model, or enable dependent clocks");

    _queue = new ArrayList<ITransformedEvent>();
    _outputStream = new ObjectOutputStream(new BufferedOutputStream(file
        .openConnection().getOutputStream()));
  }

  @Override
  public void finalize() throws Throwable
  {
    _outputStream.close();
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
    synchronized (_queue)
    {
      _queue.add(event);
    }
  }

  public void flush() throws Exception
  {
    synchronized (_queue)
    {
      ListIterator<ITransformedEvent> itr = _queue.listIterator();
      while (itr.hasNext())
      {
        _outputStream.writeObject(itr.next());
        itr.remove();
      }
    }
    _outputStream.flush();
  }
  
  public boolean isOpen()
  {
    return _outputStream!=null;
  }
}
