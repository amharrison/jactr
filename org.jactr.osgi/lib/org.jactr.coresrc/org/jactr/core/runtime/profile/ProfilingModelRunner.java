/*
 * Created on Dec 4, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.runtime.profile;

import java.util.concurrent.ExecutorService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.ICycleProcessor;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.DefaultModelRunner;
/**
 * tracks various performance metrics
 * @author developer
 *
 */
public class ProfilingModelRunner extends DefaultModelRunner
{
  /**
   logger definition
   */
  static private final Log LOGGER = LogFactory
  .getLog(ProfilingModelRunner.class);
 
  
  private long _totalActualCycles = 0;
  private long _totalActualTimeSpentWaiting = 0;
  private long _totalActualTimeSpentProcessing = 0;
  
  private long _totalActualCycleTime=0;
  
  private long _realCycleStartTime = 0;
  private long _realClockStartTime = 0;
  private long _realEventStartTime = 0;
  
  
  private long _totalRealEventTime = 0;
  
  private double _simulatedClockStartTime = 0;
  
  private double _totalSimulatedTime = 0;
  
  /**
   * @param service
   * @param model
   * @param cycleRunner
   */
  public ProfilingModelRunner(ExecutorService service, IModel model, ICycleProcessor cycleRunner)
  {
    super(service, model, cycleRunner);
  }


  @Override
  protected void startUp()
  {
    super.startUp();
    _simulatedClockStartTime = ACTRRuntime.getRuntime().getClock(_model).getTime();
    if(Double.isInfinite(_simulatedClockStartTime) || Double.isNaN(_simulatedClockStartTime))
      _simulatedClockStartTime = 0;
  }
  
  @Override
  protected void preEventFiring()
  {
    _realEventStartTime = System.nanoTime();
  }
  
  protected void postEventFiring()
  {
    _totalRealEventTime += System.nanoTime() - _realEventStartTime;
    _realEventStartTime = 0;
  }
  
  @Override
  protected void preCycle(double currentSimulatedTime)
  {
    _totalActualCycles++;
    _realCycleStartTime = System.nanoTime();
  }
  
  @Override
  protected void postCycle(double nextTime)
  {
    long delta = System.nanoTime() - _realCycleStartTime;
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Cycle took "+delta+"ns");
    
    _totalActualTimeSpentProcessing += delta;
    _totalSimulatedTime = ACTRRuntime.getRuntime().getClock(_model).getTime();
  }
  
  protected void preClock()
  {
    _realClockStartTime = System.nanoTime();
  }
  
  protected void postClock(double currentSimulatedTime)
  {
    long now = System.nanoTime();
    
    if(_realClockStartTime!=0)
      _totalActualTimeSpentWaiting += now - _realClockStartTime;
    
    _totalActualCycleTime += now - _realCycleStartTime;
    _realClockStartTime = 0;
    _realCycleStartTime = 0;
    
    if (LOGGER.isDebugEnabled())
      {
        LOGGER.debug("RealCycles:"+getTotalCycles()+" CycleTime:"+getTotalCycleTime()+" EventTime:"+getActualEventTime()+" CycleProcess:"+getActualCycleTime()+" ClockWait:"+getActualWaitTime());
        LOGGER.debug("SimCycles:"+_model.getCycle()+" SimTime:"+getSimulatedTime()+" RealTime:"+getRealTimeFactor());
      }
  }
  
  public long getTotalCycles()
  {
    return _totalActualCycles;
  }
  
  
  /**
   * comprised of both actual processing time and waiting time
   * @return
   */
  public double getTotalCycleTime()
  {
    return _totalActualCycleTime/1000000000d;
  }
  
  /**
   * return just the time spent in cycle()
   * @return
   */
  public double getActualCycleTime()
  {
    return _totalActualTimeSpentProcessing/1000000000d;
  }
  
  /**
   * return just the time spent waiting for the clock
   * @return
   */
  public double getActualWaitTime()
  {
    return _totalActualTimeSpentWaiting/1000000000d;
  }
  
  public double getActualEventTime()
  {
    return _totalRealEventTime/1000000000d;
  }
  
  public double getSimulatedTime()
  {
    return _totalSimulatedTime;
  }
  
  public double getRealTimeFactor()
  {
    return getSimulatedTime()/getTotalCycleTime();
  }
}


