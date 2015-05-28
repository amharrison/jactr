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
package org.jactr.tools.async.iterative.listener;

import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;
import org.jactr.tools.async.common.NetworkedEndpoint;
import org.jactr.tools.async.iterative.message.DeadLockMessage;
import org.jactr.tools.async.iterative.message.ExceptionMessage;
import org.jactr.tools.async.iterative.message.StatusMessage;
import org.jactr.tools.deadlock.DeadLockDetector;
import org.jactr.tools.deadlock.IDeadLockListener;

/**
 * @author developer
 */
public class NetworkedIterativeRunListener extends NetworkedEndpoint implements
    IIterativeRunListener, IParameterized
{
  /**
   * logger definition
   */
  static private final Log   LOGGER                 = LogFactory
                                                        .getLog(NetworkedIterativeRunListener.class);

  static public final String DEADLOCK_TIMEOUT_PARAM = "DeadlockTimeout";

  private DeadLockDetector   _detector;

  private long               _timeout               = 10000;

  public NetworkedIterativeRunListener()
  {
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#exceptionThrown(int,
   *      org.jactr.core.model.IModel, java.lang.Throwable)
   */
  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {
    /**
     * Error : error
     */
    LOGGER.error("Iteration : [" + index + "](" + model + ") threw ", thrown);

    try
    {
      getSession().write(new ExceptionMessage(index, model, thrown));
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      LOGGER
          .error(
              "NetworkedIterativeRunListener.exceptionThrown threw Exception : ",
              e);
    }

  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#postRun(int, int,
   *      java.util.Collection)
   */
  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    try
    {
      getSession().write(new StatusMessage(false, currentRunIndex, totalRuns));
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("NetworkedIterativeRunListener.postRun threw Exception : ",
          e);
    }

    for (IModel model : models)
      model.uninstall(_detector);
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#preBuild(int, int,
   *      java.util.Collection)
   */
  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors)
      throws TerminateIterativeRunException
  {
    // noop
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#preRun(int, int,
   *      java.util.Collection)
   */
  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    /*
     * send the start
     */
    try
    {
      getSession().write(new StatusMessage(true, currentRunIndex, totalRuns));
    }
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      LOGGER
          .error("NetworkedIterativeRunListener.preRun threw Exception : ", e);
    }
    /*
     * and attach the deadlock detector
     */
    for (IModel model : models)
      model.install(_detector);
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#start(int)
   */
  public void start(int totalRuns) throws TerminateIterativeRunException
  {

    _detector = new DeadLockDetector(new IDeadLockListener() {

      public void deadlockDetected()
      {
        try
        {
          getSession().write(new DeadLockMessage());
        }
        catch (Exception e)
        {
          // TODO Auto-generated catch block
          LOGGER.error(".deadlockDetected threw Exception : ", e);
        }
      }

    }, _timeout);

    try
    {
      connect();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not connect, propogating exception ", e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  /**
   * @see org.jactr.entry.iterative.IIterativeRunListener#stop()
   */
  public void stop()
  {
    try
    {
      disconnect();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not disconnect ", e);
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  @Override
  public Collection<String> getPossibleParameters()
  {
    Collection<String> params = super.getPossibleParameters();
    params.add(DEADLOCK_TIMEOUT_PARAM);
    return params;
  }

  @Override
  public void setParameter(String key, String value)
  {
    if (DEADLOCK_TIMEOUT_PARAM.equalsIgnoreCase(key))
      try
      {
        _timeout = Long.parseLong(value);
      }
      catch (NumberFormatException e)
      {
        _timeout = 10000;
      }
    else
      super.setParameter(key, value);
  }

  public void preLoad(int currentRunIndex, int totalRuns)
      throws TerminateIterativeRunException
  {
    // TODO Auto-generated method stub

  }

}
