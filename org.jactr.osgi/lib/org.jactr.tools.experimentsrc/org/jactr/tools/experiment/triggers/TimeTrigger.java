package org.jactr.tools.experiment.triggers;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.IClock;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.w3c.dom.Element;

/**
 * trigger that fires after relative or absolute time has elapsed
 * 
 * @author harrison
 */
public class TimeTrigger implements ITrigger
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER        = LogFactory
                                                       .getLog(TimeTrigger.class);

  static public final String         RELATIVE_ATTR = "relative";

  static public final String         ABSOLUTE_ATTR = "absolute";

  private final Collection<IAction>  _actions      = new ArrayList<IAction>();

  private Collection<ITrigger>       _triggers     = new ArrayList<ITrigger>();

  private CompletableFuture<Double>  _trigger;

  private boolean                    _isRelative   = false;

  private double                     _fireAt       = 0;

  private IExperiment                _experiment;

  public void add(IAction action)
  {
    _actions.add(action);
  }

  public void add(ITrigger trigger)
  {
    _triggers.add(trigger);
  }

  public TimeTrigger(Element trigger, IExperiment experiment)
  {
    _experiment = experiment;
    try
    {
      if (trigger.hasAttribute(RELATIVE_ATTR))
      {
        _isRelative = true;
        _fireAt = Double.parseDouble(experiment
            .getVariableResolver()
            .resolve(trigger.getAttribute(RELATIVE_ATTR),
                experiment.getVariableContext()).toString());
      }
      else
        _fireAt = Double.parseDouble(experiment
            .getVariableResolver()
            .resolve(trigger.getAttribute(ABSOLUTE_ATTR),
                experiment.getVariableContext()).toString());
    }
    catch (NumberFormatException e)
    {
      throw new IllegalArgumentException("Could not get valid number from "
          + RELATIVE_ATTR + " or " + ABSOLUTE_ATTR + " attributes of "
          + trigger.getTagName(), e);
    }
  }

  public TimeTrigger(double timeDelay, boolean delayIsRelative,
      IExperiment experiment)
  {
    _experiment = experiment;
    _isRelative = delayIsRelative;
    _fireAt = timeDelay;
  }

  public boolean isArmed()
  {
    return _trigger != null;
  }

  public void setArmed(boolean arm)
  {
    if (isArmed() && arm) return;
    if (!isArmed() && !arm) return;

    if (!arm && _trigger != null)
    {
      if (!_trigger.isDone() && !_trigger.isCancelled())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(String.format("Canceling trigger future [%s]", _trigger));
        _trigger.cancel(false);
      }

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("disarmed [%s]", _trigger));

      _trigger = null;
    }
    else if (arm && _trigger == null)
    {
      IClock clock = _experiment.getClock();
      if (clock == null)
        throw new IllegalStateException(
            "No clock has been assigned to the experiment");

      double start = _fireAt;
      double now = clock.getTime();
      if (_isRelative) start += now;

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Time trigger should fire @ " + start + " isRelative:"
            + _isRelative + " now:" + now);

      Runnable runner = () -> {
        /*
         * zip through and fire all the actions
         */
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("timed trigger hit @ %.4f",
              clock.getTime()));
        try
        {
          _trigger = null;
          for (ITrigger trigger : _triggers)
            trigger.setArmed(true);

          for (IAction action : _actions)
            action.fire(_experiment.getVariableContext());
        }
        catch (Exception e)
        {
          LOGGER.error(String.format("Failed to fire after time update "), e);
        }

      };

      _trigger = clock.waitForTime(start);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("Using trigger future [%s]", _trigger));

      _trigger.handle((d, t) -> {
        if (t != null && !(t instanceof CancellationException))
          LOGGER.error(
              String.format("[%s] Failed to wait for time? ", _trigger), t);
        return null;
      });

      _trigger.thenRun(runner);
    }
  }
}
