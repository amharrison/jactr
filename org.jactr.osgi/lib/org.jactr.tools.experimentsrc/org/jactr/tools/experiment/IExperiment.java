package org.jactr.tools.experiment;

import java.util.List;

import org.commonreality.time.IClock;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.lock.LockManager;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.triggers.EndTrigger;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.NamedTriggerManager;
import org.jactr.tools.experiment.triggers.StartTrigger;
import org.w3c.dom.Document;

/*
 * default logging
 */

/**
 * experiment interface
 * @author harrison
 *
 */
public interface IExperiment
{

  public double getStartTime();
  public double getStopTime();
  public double getTime();
  
  /**
   * configure the experiment from the xml doc
   * @param document
   */
  public void configure(Document document);
  
  public void addTrial(ITrial trial);
  
  public IVariableContext getVariableContext();
  
  /**
   * current trial
   * @return
   */
  public ITrial getTrial();
  public List<ITrial> getTrials();
  public void setNextTrial(ITrial trial);
  
  public LockManager getLockManager();
  
  public NamedTriggerManager getTriggerManager();
  
  public VariableResolver getVariableResolver();
  
  public IDataLogger getDataCollector();
  
  public void setDataCollector(IDataLogger collector);
  
  public void start();
  
  /**
   * request to stop
   */
  public void stop();
  
  public void setStartTrigger(StartTrigger trigger);
  
  public void setEndTrigger(EndTrigger trigger);
  
  public void addTrigger(ITrigger trigger);
  
  public IClock getClock();
  
  public void setClock(IClock clock);
}
