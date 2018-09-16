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
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;

/**
 * @author developer
 */
public class RealTimeFactorPerformanceListener implements
    IIterativeRunListener, IParameterized
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER     = LogFactory
                                                    .getLog(RealTimeFactorPerformanceListener.class);

  static public final String         ROW_HEADER = "RowHeader";

  static public final String         BLOCK_SIZE = "BlockSize";

  static public final String         FILE_NAME  = "FileName";

  protected long                     _blockRealTimeSum;

  protected long                     _blockSimTimeSum;

  protected int                      _blockSize;

  protected String                   _rowHeader = "";

  protected String                   _fileName  = "";

  protected StringBuilder            _row       = new StringBuilder();

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

    if (currentRunIndex % _blockSize == 0)
    {
      double realTimeFactor = (double) _blockSimTimeSum /
          (double) _blockRealTimeSum;
      _row.append((realTimeFactor / _blockSize)).append("\t");
      _blockRealTimeSum = 0;
      _blockSimTimeSum=0;
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
    if (currentRunIndex == 1)
    {
      if (_blockSize == 0) _blockSize = Math.max(totalRuns / 10, 1);
      _row.append(_rowHeader).append("\t");
    }
    /*
     * attach our listener..
     */
    IModelListener ml = new ModelListenerAdaptor() {

      private long   _realCycleStartTime = 0;

      private double _simCycleStartTime  = 0;

      protected void updateSimulatedTime(ModelEvent me)
      {
        _realCycleStartTime = me.getSystemTime();

        /*
         * how much simulated time has elapsed since the start of the last
         * cycle?
         */
        long simDelta = (long) ((me.getSimulationTime() - _simCycleStartTime) * 1000);

        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Simulated time delta : " + simDelta);

        _simCycleStartTime = me.getSimulationTime();

        /*
         * we need something to synchronize on.. why not the row?
         */
        synchronized (_row)
        {
          _blockSimTimeSum += simDelta;
        }
      }

      @Override
      public void cycleStarted(ModelEvent me)
      {
        updateSimulatedTime(me);
      }

      @Override
      public void modelStopped(ModelEvent me)
      {
        updateSimulatedTime(me);
        me.getSource().removeListener(this);
      }

      @Override
      public void cycleStopped(ModelEvent me)
      {
        /*
         * how much real time has elapsed
         */
        long realDelta = me.getSystemTime() - _realCycleStartTime;
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Real time delta : " + realDelta);
        synchronized (_row)
        {
          _blockRealTimeSum += realDelta;
        }
      }
    };

    for (IModel model : models)
      model.addListener(ml, ExecutorServices.INLINE_EXECUTOR);
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
