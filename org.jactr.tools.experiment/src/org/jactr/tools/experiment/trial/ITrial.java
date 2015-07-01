package org.jactr.tools.experiment.trial;

import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.triggers.ITrigger;

/*
 * default logging
 */

public interface ITrial
{
  
  public String getId();

  public void setStartTrigger(ITrigger trigger);
  public void setEndTrigger(ITrigger trigger);
  
  public void addTrigger(ITrigger trigger);
  
  public boolean isRunning();
  
  /**
   * start a trial that isnt running. should only be called by
   * the experiment thread
   */
  public void start();
  
  /**
   * request that a running trial be stopped. can be called by any thread
   */
  public void stop();
  
  public void waitForStop();
  
  public double getStartTime();
  public double getStopTime();
  
  public IExperiment getExperiment();
}
