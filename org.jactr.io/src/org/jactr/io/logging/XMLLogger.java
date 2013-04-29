/*
 * Created on Apr 15, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.logging;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.logging.ILogger;
import org.jactr.core.logging.LogEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;

/**
 * dumps model log information to an xml fiile
 * 
 * @author developer
 */
public class XMLLogger implements IInstrument, ILogger, IParameterized
{
  /**
   * logger definition
   */
  static private final Log   LOGGER    = LogFactory.getLog(XMLLogger.class);

  static public final String FILE_NAME = "FileName";

  private String             _fileName;

  private PrintWriter        _output;

  private Collection<IModel> _models;

  public XMLLogger()
  {
    setParameter(FILE_NAME, "log.xml");
    _models = new ArrayList<IModel>();
  }

  /**
   * @see org.jactr.instrument.IInstrument#initialize()
   */
  public void initialize()
  {
  }

  /**
   * @see org.jactr.instrument.IInstrument#install(org.jactr.core.model.IModel)
   */
  public void install(IModel model)
  {
    Logger.addLogger(model, this);
    _models.add(model);
  }

  /**
   * @see org.jactr.instrument.IInstrument#uninstall(org.jactr.core.model.IModel)
   */
  public void uninstall(IModel model)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Uninstalling logger");
    Logger.removeLogger(model, this);
    _models.remove(model);
    if (_output != null && _models.size() != 0)
    {
      _output.println("</log-data>");
      _output.flush();
      _output = null;
    }
  }

  /**
   * @see org.jactr.core.logging.ILogger#log(org.jactr.core.logging.LogEvent)
   */
  public void log(LogEvent logEvent)
  {
    try
    {
      StringBuilder sb = new StringBuilder("\t<log model=\"");
      sb.append(logEvent.getModel().getName()).append("\" when=\"");
      sb.append(logEvent.getSimulationTime()).append("\" stream=\"");
      sb.append(logEvent.getStreamName()).append("\"><![CDATA[");
      sb.append(logEvent.getMessage()).append("]]></log>");
      _output.println(sb.toString());
    }
    catch (Exception e)
    {
      LOGGER.error("Screwed up ", e);
    }
  }
  
  public void flush()
  {
    _output.flush();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (FILE_NAME.equalsIgnoreCase(key)) return _fileName;
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    return Collections.singleton(FILE_NAME);
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (FILE_NAME.equalsIgnoreCase(key)) try
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Setting output to " + value);
      _output = new PrintWriter(new FileWriter(value, true));
      _output.println("<log-data>");
      _fileName = value;
    }
    catch (Exception e)
    {
      LOGGER.error("Could not create file name " + value, e);
      _output = new PrintWriter(System.err);
    }

  }

}
