/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
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

package org.jactr.core.queue.timedevents;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * BlockingTimedEvent sits on the TimedEventQueue and when it fires, it will
 * block the running of the IModel. This is used to prevent a IModel running
 * beyond when it should. Once the BlockingTimedEvent.abort() method is called,
 * the block will pass and the IModel will resume.
 * 
 * @author harrison
 * @created April 18, 2003
 */
public class BlockingTimedEvent extends AbstractTimedEvent
{

  static private transient final Log LOGGER = LogFactory
                                                .getLog(BlockingTimedEvent.class);

  static private long                _totalSleepTime;

  static private long                _totalSleepInstances;

  private Object _owner;
  
  /**
   * Constructor for the BlockingTimedEvent object
   * 
   * @param sTime
   *            Description of the Parameter
   * @param eTime
   *            Description of the Parameter
   */
  public BlockingTimedEvent(Object owner, double sTime, double eTime)
  {
    super(sTime, eTime);
    _owner = owner;
  }
  
  public Object getOwner()
  {
    return _owner;
  }

  /**
   * Description of the Method
   * 
   * @param now
   *            Description of the Parameter
   */
  @Override
  public synchronized void fire(double now)
  {
    long startTime = System.currentTimeMillis();
    try
    {
      while (!hasAborted())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("BlockingTimedEvent("+_owner+") not cleared @ "+getEndTime()+", waiting");
        
        wait();
      }
    }
    catch (InterruptedException e)
    {
      LOGGER.debug("BlockingTimedEvent.fire ("+_owner+") interrupted", e);
      LOGGER.warn("Interrupted, expecting termination ", e);
    }
    finally
    {
      long delta = System.currentTimeMillis() - startTime;

      if (LOGGER.isDebugEnabled())
      {
        LOGGER.debug("BlockingTimedEvent ("+_owner+") waited for " + delta
            + "ms for release");

        synchronized (BlockingTimedEvent.class)
        {
          _totalSleepInstances++;
          _totalSleepTime += delta;

          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Average : "
                + _totalSleepTime / _totalSleepInstances + "ms across "
                + _totalSleepInstances + " instances");
        }
      }
    }
  }

  /**
   * Description of the Method
   */
  @Override
  public synchronized void abort()
  {
    super.abort();
    notifyAll();
  }

}
