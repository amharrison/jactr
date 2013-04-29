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
public class MockOnStart extends BridgeOnStart
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(MockOnStart.class);

  Thread                             _experimentThread;

  /**
   * @see org.jactr.tools.bridge.BridgeOnStart#createGoalFeeder()
   */
  @Override
  public GoalFeeder createGoalFeeder()
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Creating mock goal feeder");
    return new MockGoalFeeder();
  }

  /**
   * @see org.jactr.tools.bridge.BridgeOnStart#createResponseCollector()
   */
  @Override
  public ResponseCollector createResponseCollector()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("creating mock response collector");
    return new MockResponseCollector();
  }

  /**
   * @see org.jactr.tools.bridge.BridgeOnStart#initialize()
   */
  @Override
  public void initialize()
  {
    MockExperiment exp = new MockExperiment(true);
    ACTRRuntime.getRuntime().setApplicationData(exp);
    /*
     * start the experiment - we usually have to do this on a separate thread
     */
    _experimentThread = new Thread(exp);
    _experimentThread.start();
  }

  protected Thread getExperimentThread()
  {
    return _experimentThread;
  }
}
