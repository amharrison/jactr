/*
 * Created on Feb 7, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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

import java.util.WeakHashMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.logging.impl.MessageBuilderFactory;
import org.jactr.core.model.IModel;

/**
 * @author developer
 */
public class Logger
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(Logger.class);

  static public enum Stream {
    TIME, OUPUT, GOAL, IMAGINAL, RETRIEVAL, PROCEDURAL, DECLARATIVE, BUFFER, EXACT_MATCH, OUTPUT, PARTIAL_MATCH, VISUAL, AURAL, MOTOR, VOCAL, EVENT, EXCEPTION, ACTIVATION
  };

  static private WeakHashMap<IModel, ACTREventDispatcher<IModel, ILogger>> _loggers = new WeakHashMap<IModel, ACTREventDispatcher<IModel, ILogger>>();

  static public boolean hasLoggers(IModel model)
  {
    synchronized (_loggers)
    {
      return _loggers.containsKey(model) && _loggers.get(model).hasListeners();
    }
  }

  static public IMessageBuilder messageBuilder()
  {
    return MessageBuilderFactory.newInstance();
  }

  /**
   * loggers are always added on the background executor
   * 
   * @param model
   * @param logger
   */
  static public void addLogger(IModel model, ILogger logger)
  {
    addLogger(model, logger, ExecutorServices
        .getExecutor(ExecutorServices.BACKGROUND));
  }

  static public void addLogger(IModel model, ILogger logger, Executor executor)
  {
    synchronized (_loggers)
    {
      ACTREventDispatcher<IModel, ILogger> dispatcher = _loggers.get(model);
      if (dispatcher == null)
      {
        dispatcher = new ACTREventDispatcher<IModel, ILogger>();
        _loggers.put(model, dispatcher);
      }
      dispatcher.addListener(logger, executor);
    }

  }

  static public void removeLogger(IModel model, ILogger logger)
  {
    synchronized (_loggers)
    {
      ACTREventDispatcher<IModel, ILogger> dispatcher = _loggers.get(model);
      if (dispatcher != null) dispatcher.removeListener(logger);
    }
  }

  /**
   * log the message and recycle the message builder
   * 
   * @param model
   * @param stream
   * @param message
   */
  static public void log(IModel model, Stream stream, IMessageBuilder message)
  {
    log(model, stream, message.toString());
    MessageBuilderFactory.recycle(message);
  }

  static public void log(IModel model, String stream, IMessageBuilder message)
  {
    log(model, stream, message.toString());
    MessageBuilderFactory.recycle(message);
  }

  static public void log(IModel model, Stream stream, String message)
  {
    log(model, stream.name(), message);
  }

  static public void log(IModel model, String stream, String message)
  {
    synchronized (_loggers)
    {
      if (hasLoggers(model))
        _loggers.get(model).fire(new LogEvent(model, stream, message));
    }
  }
}
