/*
 * Created on Nov 28, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.logging.impl;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.logging.ILogger;
import org.jactr.core.logging.LogEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.RollingFileWriter;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.instrument.IInstrument;

/**
 * demuxing logger
 * 
 * @author developer
 */
public class DefaultModelLogger implements IInstrument, ILogger, IParameterized
{
  /**
   * logger definition
   */
  static private final Log         LOGGER      = LogFactory
                                                   .getLog(DefaultModelLogger.class);

  static public final String       ALL         = "all";

  static public final String       BACKUPS     = "NumberOfBackups";

  static public final String       SIZE        = "MaxFileSize";

  private Map<String, ILogger>     _outputStreams;

  private Collection<IModel>       _listeningTo;

  private Map<String, PrintWriter> _commonStreams;

  private Map<String, String>      _logToStream;

  /*
   * so that we can flush intelligently.
   */
  private IModelListener           _modelListener;

  private long                     _maxSize    = 1024 * 1024;

  private int                      _maxBackups = 3;

  public DefaultModelLogger()
  {
    super();
    _commonStreams = new TreeMap<String, PrintWriter>();
    _outputStreams = new TreeMap<String, ILogger>();
    _logToStream = new TreeMap<String, String>();
    _listeningTo = new ArrayList<IModel>();
    createCommonStreams();

    _modelListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStopped(ModelEvent me)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Flushing");
        flush();
      }

      @Override
      public void modelStopped(ModelEvent me)
      {
        flush();
      }
    };
  }

  private void createCommonStreams()
  {
    _commonStreams.put("err", new PrintWriter(System.err, true));
    _commonStreams.put("out", new PrintWriter(System.out, true));
  }

  public void log(LogEvent log)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Got log event " + log);
    ILogger pw = getLoggerForStream(log.getStreamName());
    if (pw != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Logging to " + pw + " : " + log);
      pw.log(log);
      // pw.flush();
    }

    // if(log.getStreamName().equals(Logger.Stream.TIME.toString()))
    // flush();
  }

  public String getParameter(String key)
  {
    return null;
  }

  synchronized public void setLoggerForStream(String stream, ILogger logger)
  {
    if (logger != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Associative " + stream + " with " + logger);
      _outputStreams.put(stream, logger);
    }
    else
      _outputStreams.remove(stream);
  }

  synchronized public ILogger getLoggerForStream(String stream)
  {
    ILogger logger = _outputStreams.get(stream);
    if (logger == null) logger = _outputStreams.get(ALL);
    return logger;
  }

  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> streams = new ArrayList<String>(
        Logger.Stream.values().length);
    for (Logger.Stream stream : Logger.Stream.values())
      streams.add(stream.name());
    return Collections.unmodifiableCollection(streams);
  }

  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  public void setParameter(String key, String value)
  {
    boolean dirty = false;
    if (BACKUPS.equalsIgnoreCase(key))
    {
      _maxBackups = ParameterHandler.numberInstance().coerce(value).intValue();
      dirty = true;
    }
    else if (SIZE.equalsIgnoreCase(key))
    {
      _maxSize = (long) (ParameterHandler.numberInstance().coerce(value)
          .doubleValue() * 1024 * 1024);
      dirty = true;
    }
    else
    {
      value = value.trim();
      createStream(key, value);
      _logToStream.put(key, value);
    }

    if (dirty)
    {
      /*
       * we need to recreate the streams
       */
      _commonStreams.clear();
      _outputStreams.clear();

      createCommonStreams();

      for (Map.Entry<String, String> entry : _logToStream.entrySet())
        createStream(entry.getKey(), entry.getValue());
    }
  }

  private void createStream(String log, String stream)
  {

    PrintWriter pw = _commonStreams.get(stream);
    if (pw == null)
    {
      Writer w = new RollingFileWriter(ACTRRuntime.getRuntime()
          .getWorkingDirectory(), stream, _maxSize, _maxBackups);
      pw = new PrintWriter(w, true);
      _commonStreams.put(stream, pw);
    }

    if (pw != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Setting stream " + log + " to " + pw);
      setLoggerForStream(log, new PrintWriterLogger(pw));
    }
  }

  /**
   * @see org.jactr.core.utils.IInstallable#install(org.jactr.core.model.IModel)
   */
  public void install(IModel model)
  {
    Logger.addLogger(model, this);
    _listeningTo.add(model);
    model.addListener(_modelListener, ExecutorServices
        .getExecutor(ExecutorServices.BACKGROUND));
  }

  /**
   * @see org.jactr.core.utils.IInstallable#uninstall(org.jactr.core.model.IModel)
   */
  public void uninstall(IModel model)
  {
    _listeningTo.remove(model);
    model.removeListener(_modelListener);
    Logger.removeLogger(model, this);

    synchronized (this)
    {
      if (_listeningTo.size() == 0) _outputStreams.clear();
    }

  }

  synchronized public void flush()
  {
    /*
     * zip through them all and flush..
     */
    for (ILogger logger : _outputStreams.values())
      logger.flush();
  }

  /**
   * @see org.jactr.instrument.IInstrument#initialize()
   */
  public void initialize()
  {
    // noop
  }

}
