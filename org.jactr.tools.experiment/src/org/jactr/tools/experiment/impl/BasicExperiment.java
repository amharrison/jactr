package org.jactr.tools.experiment.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IClock;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.impl.VariableResolver.IResolver;
import org.jactr.tools.experiment.lock.LockManager;
import org.jactr.tools.experiment.misc.ExperimentUtilities;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ICompoundTrial;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.triggers.EndTrigger;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.NamedTriggerManager;
import org.jactr.tools.experiment.triggers.StartTrigger;
import org.jactr.tools.experiment.triggers.TimeTrigger;
import org.w3c.dom.Document;

public class BasicExperiment implements IExperiment
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(BasicExperiment.class);

  private List<ITrial>               _allTrials;

  private List<ITrial>               _pendingTrials;

  private final NamedTriggerManager  _manager;

  private String                     _name;

  private final VariableResolver     _resolver;

  private final LockManager          _lockManager;

  private IDataLogger                _collector;

  private double                     _startTime;

  private double                     _stopTime;

  private StartTrigger               _startTrigger;

  private EndTrigger                 _endTrigger;

  private Collection<ITrigger>       _triggers;

  private final IVariableContext     _variableContext = new VariableContext();

  private volatile ITrial            _currentTrial;

  private volatile boolean           _shouldStop      = false;

  private IClock                     _clock           = null;

  public BasicExperiment()
  {
    _lockManager = new LockManager();
    _manager = new NamedTriggerManager();
    _resolver = new VariableResolver();
    _allTrials = new ArrayList<ITrial>();
    _pendingTrials = new ArrayList<ITrial>();
    _triggers = new ArrayList<ITrigger>();

    /**
     * ${DataPath}
     */
    _resolver.add(new VariableResolver.IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("DataPath");
      }

      public Object resolve(String key, IVariableContext context)
      {
        return getDataCollector().getPath();
      }

    });

    /**
     * ${trial}
     */
    _resolver.add(new VariableResolver.IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("trial");
      }

      public Object resolve(String key, IVariableContext context)
      {
        ITrial trial = getTrial();
        if (trial != null) return trial.getId();
        return null;
      }

    });

    /**
     * ${time}
     */
    _resolver.add(new VariableResolver.IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("time");
      }

      public Object resolve(String key, IVariableContext context)
      {
        return String.format("%1$f", getTime());
      }
    });

    /**
     * ${delta}
     */
    _resolver.add(new VariableResolver.IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("delta");
      }

      public Object resolve(String key, IVariableContext context)
      {
        double start = getStartTime();
        ITrial trial = getTrial();
        if (trial != null) start = trial.getStartTime();

        return String.format("%1$f", (getTime() - start));
      }

    });

    _resolver.add(new IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("self");
      }

      public Object resolve(String key, IVariableContext context)
      {
        try
        {
          return context.get("=model").toString();
        }
        catch (Exception e)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Could not get current model ", e);
          return null;
        }
      }

    });

    _resolver.add(new IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("others");
      }

      public Object resolve(String key, IVariableContext context)
      {
        try
        {
          IModel model = (IModel) context.get("=model");
          ArrayList<IModel> models = new ArrayList<IModel>(ACTRRuntime
              .getRuntime().getModels());
          models.remove(model);

          StringBuilder sb = new StringBuilder();
          for (IModel m : models)
            sb.append(m.getName()).append(",");
          if (sb.length() > 0) sb.delete(sb.length() - 1, sb.length());
          return sb.toString();
        }
        catch (Exception e)
        {
          return null;
        }
      }

    });

    _resolver.add(new IResolver() {

      public boolean isRelevant(String key)
      {
        return key.equalsIgnoreCase("all");
      }

      public Object resolve(String key, IVariableContext context)
      {
        try
        {
          ArrayList<IModel> models = new ArrayList<IModel>(ACTRRuntime
              .getRuntime().getModels());

          StringBuilder sb = new StringBuilder();
          for (IModel m : models)
            sb.append(m.getName()).append(",");
          if (sb.length() > 0) sb.delete(sb.length() - 1, sb.length());
          return sb.toString();
        }
        catch (Exception e)
        {
          return null;
        }
      }

    });
  }

  public IVariableContext getVariableContext()
  {
    return _variableContext;
  }

  public LockManager getLockManager()
  {
    return _lockManager;
  }

  public void setStartTrigger(StartTrigger trigger)
  {
    _startTrigger = trigger;
  }

  public void setEndTrigger(EndTrigger trigger)
  {
    _endTrigger = trigger;
  }

  public double getTime()
  {
    return getClock().getTime();
  }

  public VariableResolver getVariableResolver()
  {
    return _resolver;
  }

  public String getName()
  {
    return _name;
  }

  public void addTrial(ITrial trial)
  {
    _allTrials.add(trial);
  }

  public void configure(Document document)
  {
    /*
     * snag the parser to use..
     */
    _name = document.getDocumentElement().getAttribute("name");
    String parserClass = document.getDocumentElement().getAttribute("parser");
    if (parserClass.length() == 0)
      parserClass = ExperimentParser.class.getName();
    try
    {
      ExperimentParser parser = (ExperimentParser) getClass().getClassLoader()
          .loadClass(parserClass).newInstance();

      configureParser(parser);

      parser.configure(document, this);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Failed to parse configuration ", e);
    }

    if (Boolean.parseBoolean(document.getDocumentElement().getAttribute(
        "shuffle"))) Collections.shuffle(_allTrials);
  }

  /**
   * if you need to add experiment specific parse handlers, do so here
   * 
   * @param parser
   */
  protected void configureParser(ExperimentParser parser)
  {

  }

  public ITrial getTrial(boolean recursive)
  {
    ITrial rtn = _currentTrial;
    while (recursive && rtn instanceof ICompoundTrial)
    {
      ITrial last = rtn;
      rtn = ((ICompoundTrial) rtn).getCurrentTrial();
      if (rtn == null)
      {
        rtn = last;
        break;
      }
    }
    return rtn;
  }

  public ITrial getTrial()
  {
    return getTrial(true);
  }

  public NamedTriggerManager getTriggerManager()
  {
    return _manager;
  }

  public void stop()
  {
    _shouldStop = true;
    ITrial trial = getTrial(false);
    if (trial != null && trial.isRunning()) trial.stop();
  }

  public void start()
  {
    new Thread(new Runnable() {

      public void run()
      {
        started();
        _pendingTrials.addAll(_allTrials);
        ITrial next = null;

        while ((next = getNextTrial()) != null && !_shouldStop)
        {
          _currentTrial = next;
          try
          {
            _currentTrial.start();
            _currentTrial.waitForStop();
          }
          catch (Exception e)
          {
            LOGGER.error("Failed to execute trial " + _currentTrial.getId(), e);
          }

          _currentTrial = null;
        }

        stopped();
      }

    }, "experiment-thread").start();
  }

  protected ITrial getNextTrial()
  {
    if (_pendingTrials.size() != 0) return _pendingTrials.remove(0);
    return null;
  }

  public void setNextTrial(ITrial trial)
  {
    _pendingTrials.remove(trial);
    _pendingTrials.add(0, trial);
  }

  public List<ITrial> getTrials()
  {
    return Collections.unmodifiableList(_allTrials);
  }

  /**
   * calls trigger if available
   */
  protected void started()
  {
    _startTime = getTime();

    if (_startTrigger != null) _startTrigger.setArmed(true);

    for (ITrigger trigger : _triggers)
      trigger.setArmed(true);
  }

  /**
   * called at the end of processing
   */
  protected void stopped()
  {
    _stopTime = getTime();

    for (ITrigger trigger : _triggers)
      trigger.setArmed(false);
    if (_endTrigger != null) _endTrigger.setArmed(true);
  }

  public void addTrigger(ITrigger trigger)
  {
    _triggers.add(trigger);
    if (trigger instanceof TimeTrigger)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(String
                .format("Use of timed triggers in experiment block is not recommended as the model's clock may not be set yet"));
    }
  }

  public double getStartTime()
  {
    return _startTime;
  }

  public double getStopTime()
  {
    return _stopTime;
  }

  public IDataLogger getDataCollector()
  {
    return _collector;
  }

  public void setDataCollector(IDataLogger collector)
  {
    _collector = collector;
  }

  @Override
  public IClock getClock()
  {
    return _clock;
  }

  @Override
  public void setClock(IClock clock)
  {
    _clock = clock;

  }
}
