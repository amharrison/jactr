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
package org.jactr.tools.bridge;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * @author developer
 */
@Deprecated
public abstract class BridgeOnStop implements Runnable
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(BridgeOnStop.class);

  /**
   * called after goal feeder and response collector
   */
  abstract public void cleanUp();

  /**
   * called after response collector
   */
  abstract public void cleanUp(GoalFeeder feeder);

  /**
   * called when this is to be disposed of
   * 
   * @param collector
   */
  abstract public void cleanUp(ResponseCollector collector);

  /**
   * @see java.lang.Runnable#run()
   */
  public void run()
  {
    cleanUp(ResponseCollector.getResponseCollector());
    ResponseCollector.setResponseCollector(null);

    cleanUp(GoalFeeder.getGoalFeeder());
    GoalFeeder.setGoalFeeder(null);

    cleanUp();

    ACTRRuntime.getRuntime().setOnStart(null);
    ACTRRuntime.getRuntime().setOnStop(null);
  }

}
