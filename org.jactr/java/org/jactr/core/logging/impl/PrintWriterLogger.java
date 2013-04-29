/*
 * Created on Feb 8, 2007
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
package org.jactr.core.logging.impl;

import java.io.PrintWriter;

import org.jactr.core.logging.ILogger;
import org.jactr.core.logging.LogEvent;
/**
 * @author developer
 *
 */
public class PrintWriterLogger implements ILogger
{

  private PrintWriter _printWriter;
  
  public PrintWriterLogger(PrintWriter output)
  {
    _printWriter = output;
  }
  
  /** 
   * @see org.jactr.core.logging.ILogger#log(org.jactr.core.logging.LogEvent)
   */
  public void log(LogEvent logEvent)
  {
    StringBuilder sb = new StringBuilder("[");
    sb.append(logEvent.getStreamName()).append("](").append(logEvent.getModel().getName()).append(") ");
    sb.append(logEvent.getMessage());
    _printWriter.println(sb.toString());
  }
  
  public void flush()
  {
    _printWriter.flush();
  }

}


