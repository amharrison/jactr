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
public class MockOnStop extends BridgeOnStop
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(MockOnStop.class);

  /**
   * @see org.jactr.tools.bridge.BridgeOnStop#cleanUp()
   */
  @Override
  public void cleanUp()
  {
    try
    {
      ((MockOnStart) ACTRRuntime.getRuntime().getOnStart())
          .getExperimentThread().join();
    }
    catch (InterruptedException e)
    {
      LOGGER.error("Exception ", e);
    }
  }

  /**
   * @see org.jactr.tools.bridge.BridgeOnStop#cleanUp(org.jactr.tools.bridge.GoalFeeder)
   */
  @Override
  public void cleanUp(GoalFeeder feeder)
  {
    /*
     * nor any need to dispose of the feeder
     */
  }

  /**
   * @see org.jactr.tools.bridge.BridgeOnStop#cleanUp(org.jactr.tools.bridge.ResponseCollector)
   */
  @Override
  public void cleanUp(ResponseCollector collector)
  {
    /*
     * hell, I really didn't need to do anything with this.. but why not be
     * complete
     */
  }

}
