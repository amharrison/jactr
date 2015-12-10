package org.jactr.tools.experiment.trial.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.trial.ICompoundTrial;
import org.jactr.tools.experiment.trial.ITrial;

public class CompoundTrial extends Trial implements ICompoundTrial
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(CompoundTrial.class);

  private final List<ITrial>         _trials;

  private boolean                    _shuffle = false;

  private int                        _choose  = 0;

  private volatile ITrial            _currentTrial;

  public CompoundTrial(String id, IExperiment experiment)
  {
    super(id, experiment);
    _trials = new ArrayList<ITrial>();
  }

  public void setShuffle(boolean shuffle)
  {
    _shuffle = shuffle;
  }

  public boolean isShuffling()
  {
    return _shuffle;
  }

  public void setChoose(int choose)
  {
    _choose = choose;
  }

  protected void runTrial(ITrial trial)
  {
    try
    {
      _currentTrial = trial;
      _currentTrial.start();
      _currentTrial.waitForStop();
    }
    finally
    {
      _currentTrial = null;
    }
  }

  /**
   * called after the component trial is run, if true, it will be removed from
   * the set of trials that are iterated over.
   * 
   * @param trial
   * @return
   */
  protected boolean shouldRemove(ITrial trial)
  {
    return false;
  }

  protected void runInternal(List<ITrial> trials)
  {
    Iterator<ITrial> iterator = trials.iterator();
    while (iterator.hasNext() && !shouldStop())
    {
      ITrial trial = iterator.next();
      runTrial(trial);
      if (shouldRemove(trial)) iterator.remove();
    }
  }

  @Override
  protected void runInternal()
  {
    super.runInternal();

    List<ITrial> selectedTrials = select(_shuffle, _choose);
    runInternal(selectedTrials);

    stop();
  }

  public ITrial getCurrentTrial()
  {
    return _currentTrial;
  }

  @Override
  public void stop()
  {
    if (_currentTrial != null) _currentTrial.stop();

    super.stop();
  }

  protected List<ITrial> select(boolean shuffle, int choose)
  {
    ArrayList<ITrial> rtn = new ArrayList<ITrial>(_trials);

    if (shuffle) Collections.shuffle(rtn);

    if (choose > 0) while (rtn.size() > choose)
      rtn.remove(0);

    return rtn;
  }

  public void add(ITrial trial)
  {
    _trials.add(trial);
  }

  public Collection<ITrial> getTrials()
  {
    return new ArrayList<ITrial>(_trials);
  }
}
