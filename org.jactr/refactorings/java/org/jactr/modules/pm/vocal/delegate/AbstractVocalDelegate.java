package org.jactr.modules.pm.vocal.delegate;

/*
 * default logging
 */
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.module.asynch.delegate.AbstractAsynchronousModuleDelegate;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.BlockingTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.vocal.AbstractVocalModule;

@Deprecated
public abstract class AbstractVocalDelegate extends
    AbstractAsynchronousModuleDelegate<AbstractVocalModule, IChunk>
{

  /**
   * Logger definition
   */
  static private final transient Log        LOGGER = LogFactory
                                                       .getLog(AbstractVocalDelegate.class);

  private Set<IEfferentCommand.ActualState> _driftStates;

  private Set<IEfferentCommand.ActualState> _earlyTerminationStates;

  private ITimedEvent                       _currentTimedEvent;

  private BlockingTimedEvent                _currentBlockingTimedEvent;

  private IIdentifier                       _currentCommandIdentifier;

  public AbstractVocalDelegate(AbstractVocalModule module,
      double minimumProcessingTime, IChunk cantProcessResult)
  {
    super(module, minimumProcessingTime, cantProcessResult);
    _driftStates = new HashSet<IEfferentCommand.ActualState>();
    _earlyTerminationStates = new HashSet<IEfferentCommand.ActualState>();
  }

  protected void setDriftStates(IEfferentCommand.ActualState... states)
  {
    for (IEfferentCommand.ActualState state : states)
      _driftStates.add(state);
  }

  protected void setEarlyTerminationStates(
      IEfferentCommand.ActualState... states)
  {
    for (IEfferentCommand.ActualState state : states)
      _earlyTerminationStates.add(state);
  }

  protected void blockingTimedEventCreated(BlockingTimedEvent bte)
  {
    _currentBlockingTimedEvent = bte;
  }

  public boolean commandStateChanged(IIdentifier commandIdentifier,
      IEfferentCommand.ActualState state, boolean callingFromFinalize)
  {
    if (_currentCommandIdentifier == null
        || !_currentCommandIdentifier.equals(commandIdentifier))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getClass().getSimpleName() + " : " + commandIdentifier
            + " does not match current command identifier "
            + _currentCommandIdentifier + ", ignoring");
      return false;
    }

    if (_currentBlockingTimedEvent == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER
            .warn(getClass().getSimpleName() + " : "
                + "No blocking timed event for " + commandIdentifier
                + "? Ignoring");
      return false;
    }

    if (_currentTimedEvent == null)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(getClass().getSimpleName() + " : " + "No timed event for "
            + commandIdentifier + "? Ignoring");
      return false;
    }

    boolean isDrift = _driftStates.contains(state);
    if (!_earlyTerminationStates.contains(state) && !isDrift)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getClass().getSimpleName() + " : " + state
            + " is neither early termination nor drift state, ignoring");
      return false;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("State is " + state + " isDrift=" + isDrift);
    
    /*
     * if we are calling from the finalization method, we are only
     * concerned if this is a drift state, otherwise we return
     */
    if(!isDrift && callingFromFinalize)
      return false;

    /*
     * we need to push the timed event back by creating a new one
     */
    VocalTimedEvent vte = (VocalTimedEvent) _currentTimedEvent;
    double now = ACTRRuntime.getRuntime().getClock(getModule().getModel())
        .getTime();
    synchronized (vte)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("TimedEvent is supposed to fire at "+vte.getEndTime());
      if ((!vte.hasFired() && !vte.hasAborted()) || callingFromFinalize)
      {
        if (isDrift)
        {
          enqueue(vte.driftTo(vte.getEndTime() + getMinimumProcessingTime()));
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(getClass().getSimpleName()
                + " : Drifting vocal timed event back to "
                + _currentTimedEvent.getEndTime());
        }
        else
        {
          enqueue(vte.driftTo(now));
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(getClass().getSimpleName()
                + " : Accelerating vocal timed event to "
                + _currentTimedEvent.getEndTime());
        }

        if (!vte.hasAborted() && !vte.hasFired()) vte.abort();
        return true;
      }
      else if (!callingFromFinalize && LOGGER.isDebugEnabled())
        LOGGER.debug(getClass().getSimpleName()
            + " : current vocal timed event has already fired");
    }
    return false;
  }

  final protected void finalizeProcessing(IRequest request, IChunk result,
      Object... parameters)
  {
    AbstractVocalModule module = getModule();
    IModel model = module.getModel();
    IAgent agent = ACTRRuntime.getRuntime().getConnector().getAgent(model);

    IEfferentCommand vocalizationCommand = agent.getEfferentCommandManager()
        .get(getCommandIdentifier());

    if (vocalizationCommand != null)
    {
      /*
       * it is possible that we will not have heard back from CR on the command
       * by the time the timed event fires. so, we should check again to see if
       * we need to drift. if so, return. and let the drifted timed event fire
       * us later
       */
      if (commandStateChanged(getCommandIdentifier(), vocalizationCommand
          .getActualState(), true)) return;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(getClass().getSimpleName() + " Finalizing");
    finalizeProcessingInternal((ChunkTypeRequest)request, result, parameters);

    _currentBlockingTimedEvent = null;
    _currentCommandIdentifier = null;
    _currentTimedEvent = null;
  }

  abstract protected void finalizeProcessingInternal(ChunkTypeRequest pattern,
      IChunk result, Object... parameters);

  final protected void enqueue(ITimedEvent timedEvent)
  {
    setCurrentTimedEvent(timedEvent);
    getModule().getVocalBuffer().enqueueTimedEvent(timedEvent);
  }

  private void setCurrentTimedEvent(ITimedEvent timedEvent)
  {
    _currentTimedEvent = timedEvent;
  }

  protected void setCommandIdentifier(IIdentifier identifier)
  {
    _currentCommandIdentifier = identifier;
  }

  protected IIdentifier getCommandIdentifier()
  {
    return _currentCommandIdentifier;
  }

  final protected ITimedEvent createHarvestTimedEvent(double start, double end,
      IRequest request, IChunk result, Object... parameters)
  {
    return new VocalTimedEvent(start, end, (ChunkTypeRequest) request, result, parameters);
  }

  private class VocalTimedEvent extends AbstractTimedEvent
  {
    private IChunk       _result;

    private Object[]     _parameters;

    private ChunkTypeRequest _pattern;

    public VocalTimedEvent(double start, double end, ChunkTypeRequest pattern,
        IChunk result, Object... parameters)
    {
      super(start, end);
      _result = result;
      _pattern = pattern;
      _parameters = parameters;
    }

    public VocalTimedEvent driftTo(double newEndTime)
    {
      return new VocalTimedEvent(getStartTime(), newEndTime, _pattern, _result,
          _parameters);
    }

    public void fire(double currentTime)
    {
      super.fire(currentTime);
      finalizeProcessing(_pattern, _result, _parameters);
    }
  }
}
