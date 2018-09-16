/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.entry.iterative.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;

/**
 * @author developer
 */
public class GeneralPerformanceListener implements IIterativeRunListener,
    IParameterized
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(GeneralPerformanceListener.class);

  static public final String         ROW_HEADER = "RowHeader";

  static public final String         BLOCK_SIZE = "BlockSize";

  static public final String         FILE_NAME  = "FileName";

  private long                       _startTime  = 0;

  private long                       _blockSum;

  private int                        _blockSize;

  private String                     _rowHeader = "";

  private String                     _fileName  = "";

  private StringBuilder              _row       = new StringBuilder();

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#exceptionThrown(int,
   *      org.jactr.core.model.IModel, java.lang.Throwable)
   */
  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {
    LOGGER.error(model + " threw an exception on run " + index, thrown);
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#postRun(int, int,
   *      java.util.Collection)
   */
  public void postRun(int currentRunIndex, int totalRuns,
      @SuppressWarnings("unused")
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    long delta = System.currentTimeMillis() - _startTime;
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Cycle " + currentRunIndex + "/" + totalRuns + "took " +
          delta + "ms");

    _blockSum += delta;

    if (currentRunIndex % _blockSize == 0)
    {
      _row.append(((double) _blockSum / (double) _blockSize)).append("\t");
      _blockSum = 0;
    }

    if (currentRunIndex == totalRuns && _fileName.length() != 0) try
    {
      /*
       * done, so we dump
       */
      URI root = new File(System.getProperty("user.dir")).toURI();
      File output = new File(root.resolve(_fileName));
      PrintWriter pw = new PrintWriter(new FileWriter(output, true));
      pw.println(_row.toString());
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Dumping row " + _row);
      pw.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not write to " + _fileName, e);
    }
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#preBuild(int, int,
   *      java.util.Collection)
   */
  public void preBuild(@SuppressWarnings("unused")
  int currentRunIndex, @SuppressWarnings("unused")
  int totalRuns, @SuppressWarnings("unused")
  Collection<CommonTree> modelDescriptors) throws TerminateIterativeRunException
  {

  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#preRun(int, int,
   *      java.util.Collection)
   */
  public void preRun(@SuppressWarnings("unused")
  int currentRunIndex, @SuppressWarnings("unused")
  int totalRuns, @SuppressWarnings("unused")
  Collection<IModel> models) throws TerminateIterativeRunException
  {
    _startTime = System.currentTimeMillis();

    if (currentRunIndex == 1)
    {
      if (_blockSize == 0) _blockSize = Math.max(totalRuns / 10, 1);
      _row.append(_rowHeader).append("\t");
    }
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(FILE_NAME);
    rtn.add(ROW_HEADER);
    rtn.add(BLOCK_SIZE);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (FILE_NAME.equalsIgnoreCase(key))
      _fileName = value;
    else if (ROW_HEADER.equalsIgnoreCase(key))
      _rowHeader = value;
    else if (BLOCK_SIZE.equalsIgnoreCase(key))
      _blockSize = ParameterHandler.numberInstance().coerce(value).intValue();

  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#start(int)
   */
  public void start(int totalRuns) throws TerminateIterativeRunException
  {

  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#stop()
   */
  public void stop()
  {

  }

  public void preLoad(int currentRunIndex, int totalRuns) throws TerminateIterativeRunException
  {
    // TODO Auto-generated method stub

  }

}
