package org.jactr.tools.experiment.trial;

/*
 * default logging
 */
import java.util.Collection;

public interface ICompoundTrial extends ITrial
{

  public void add(ITrial trial);

  public Collection<ITrial> getTrials();
  
  public ITrial getCurrentTrial();
}
