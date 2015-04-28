package org.jactr.tools.goalfeeder;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.queue.timedevents.TerminationTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.goalfeeder.injector.IGoalInjector;
import org.jactr.tools.goalfeeder.responder.IGoalResponder;
import org.jactr.tools.goalfeeder.timedevents.GatingTimedEvent;

/**
 * Deprecated. This class (and package) was never able to function as intended. <br/>
 * <br/>
 * The goal feeder is a support class to facilitate bridging experiments and
 * models. This is to be used when your experiment cannot directly control the
 * clock, or does not use common reality (which, if it did, it would have
 * control of the clock). <br>
 * <br>
 * It has a few elements: the goal feeder, goal constructors, goal responders,
 * and respond action. <br>
 * <br>
 * The goal feeder collects constructors and responders. it is responsible for
 * inserting new goals into the model's goal buffer in response to some
 * experimental component. The actual goal chunk is built by the goal
 * constructor, using the experimental task.<br>
 * <br>
 * When a model executes the respond action, the contents of the goal buffer are
 * passed to the goal responders and one of them actually executes the specific
 * response<br>
 * <br>
 * The respond action expects that the goal feeder be attached to the runtime
 * application data. <br>
 * <br>
 * It is up to the modeler to ensure that nextGoal() is called. The idea is that
 * the experiment will have some listener type interface, when a task is
 * started, the listener will receive the notification and then fire nextGoal().<br>
 * <br>
 * Where you really need to think is in the area of time control. Since the
 * experiment does not control the clock, the model can run at break-neck speed
 * before the experiment actually gets going. The solution to this is to use
 * BlockingTimedEvents. These are inserted with each call to nextGoal(), the
 * goal constructor can control how much time is permitted to elapse before the
 * model is explicitly blocked to allow the experiment to catch up.<br>
 * <br>
 * If your model just stops dead in its tracks, it's most likely because a goal
 * constructor returned 0 for the maxTimeElapse when it should have returned
 * something greater<br>
 * 
 * @author harrison
 */
@Deprecated
public abstract class GoalFeeder<T>
{
  /**
   * Logger definition
   */
  static private transient Log            LOGGER = LogFactory
                                                     .getLog(GoalFeeder.class);

  private Collection<IGoalInjector<T>> _constructors;

  private Collection<IGoalResponder>      _responders;

  private IModel                          _model;

  private GatingTimedEvent                _currentGate;

  private boolean                         _throwExceptionOnInvalidResponse;

  /**
   * will call assembleConstructors, responders. if block initially is true, an
   * initial blocking timed event will be inserted to prevent the model from
   * running beyond the first nextGoal call
   * 
   * @param model
   * @param blockInitially
   */
  public GoalFeeder(IModel model, boolean throwExceptionOnInvalidResponse)
  {
    _constructors = assembleInjectors();
    _responders = new ArrayList<IGoalResponder>();

    for (IGoalInjector<T> cons : _constructors)
      if (cons.getResponder() != null) _responders.add(cons.getResponder());

    _model = model;

    _throwExceptionOnInvalidResponse = throwExceptionOnInvalidResponse;

    /*
     * keep the model from running until we are ready
     */
    insertAndRelease(0.0);
  }

  /**
   * called when there is nothing left to do
   */
  public void done()
  {
    double now = ACTRRuntime.getRuntime().getClock(_model).getTime();
    _model.getTimedEventQueue().enqueue(new TerminationTimedEvent(now, now));

    /*
     * release the last block.. allowing the model to finish running.
     */
    release();
  }

  public IModel getModel()
  {
    return _model;
  }

  /**
   * instantiate all the constructors to be used to generate the goals
   * 
   * @return
   */
  abstract protected Collection<IGoalInjector<T>> assembleInjectors();

  /**
   * return the goal constructor that may be able to build a goal chunk for this
   * task
   * 
   * @param experimentTask
   * @return can be null
   */
  public IGoalInjector<T> getInjector(T experimentTask)
  {
    for (IGoalInjector<T> constructor : _constructors)
      if (constructor.handles(experimentTask)) return constructor;
    return null;
  }

  /**
   * Bug: currently will always return null until some more
   * refactoring is done
   * @param experimentTask
   * @return the goal chunk created and set or null
   */
  public IChunk nextGoal(final T experimentTask)
  {
    final IGoalInjector<T> injector = getInjector(experimentTask);
    if (injector == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("No constructor found for " + experimentTask);
      return null;
    }

    /*
     * we now wait for the model to reach the current time block.. However, if
     * there is no gating timed event available (an injector did not require
     * one), we will need to insert one. This ensures that the model is blocked
     * before we attempt to do anything
     */
    while (!waitForModel())
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Inserting internal gate");
      insertAndRelease(0);
    }

    IChunk goalChunk = injector.injectGoal(experimentTask, _model,
        GoalFeeder.this);

   
    GatingTimedEvent gte = injector.createGate(ACTRRuntime.getRuntime()
        .getClock(_model).getTime(), experimentTask);
    
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(injector + " returned " + goalChunk + " and " + gte
          + " for " + experimentTask);

    if (gte != null) insertAndRelease(gte);

    return goalChunk;
  }

  protected void insertAndRelease(double maxTimeToElapse)
  {
    double now = ACTRRuntime.getRuntime().getClock(_model).getTime();
    GatingTimedEvent gte = new GatingTimedEvent(now, now + maxTimeToElapse,
        this);
    insertAndRelease(gte);
  }

  synchronized protected void insertAndRelease(GatingTimedEvent gte)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Inserting new gate " + gte);
    _model.getTimedEventQueue().enqueue(gte);

    release();

    _currentGate = gte;
  }

  synchronized protected void release()
  {
    if (_currentGate != null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Releasing previous gate " + _currentGate);
      _currentGate.abort();
      _currentGate = null;
    }
  }

  /**
   * returns true if there was a gate to wait on
   * 
   * @return
   */
  protected boolean waitForModel()
  {
    if (_currentGate == null) return false;

      try
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Waiting for model to reach gate");
        _currentGate.waitForModel();
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Model reached gate, resuming");
      }
      catch (InterruptedException e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("Interrupted while waiting for model to reach gate, resuming");
      }

    return _currentGate.isBlocked();
  }

  /**
   * generate a response given this goal chunk
   * 
   * @param chunk
   */
  public void respond(IInstantiation instantiation)
  {
    for (IGoalResponder responder : _responders)
      if (responder.handles(instantiation))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Will use " + responder + " to respond to " +
              instantiation);
        responder.respond(instantiation);
        return;
      }

    String message = "No clue how to respond to " + instantiation +
        ", no responder claimed responsibility";

    if (Logger.hasLoggers(_model))
      Logger.log(_model, Logger.Stream.EXCEPTION, message);

    LOGGER.warn(message);

    if (_throwExceptionOnInvalidResponse) throw new RuntimeException(message);
  }

}
