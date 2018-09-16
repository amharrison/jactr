package org.jactr.modules.pm.motor.buffer.event;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.motor.IMotorModule;
import org.jactr.modules.pm.motor.command.IMovement;

public class MotorTimedEvent extends AbstractTimedEvent
{
  /**
   * Logger definition
   */
  static private final transient Log        LOGGER       = LogFactory
                                                             .getLog(MotorTimedEvent.class);

  private IMovement                         _movement;

  private IMotorModule                      _motor;

  private double                            _driftOffset = 0.05;

  private Set<IEfferentCommand.ActualState> _driftSet;

  private MotorTimedEvent                   _driftedTimedEvent;

  public MotorTimedEvent(IMovement movement, IMotorModule module,
      double driftOffset, Collection<IEfferentCommand.ActualState> driftStates,
      double startTime, double endTime)
  {
    _movement = movement;
    _motor = module;
    _driftOffset = driftOffset;
    _driftSet = new HashSet<IEfferentCommand.ActualState>(driftStates);
    setTimes(startTime, endTime);
  }

  public Set<IEfferentCommand.ActualState> getDriftStates()
  {
    return Collections.unmodifiableSet(_driftSet);
  }

  public double getDriftOffset()
  {
    return _driftOffset;
  }

  public IMovement getMovement()
  {
    return _movement;
  }

  synchronized public MotorTimedEvent getDriftedTimedEvent()
  {
    MotorTimedEvent mte = this;
    while (mte._driftedTimedEvent != null)
      mte = mte._driftedTimedEvent;
    return mte;
  }

  @Override
  public void fire(double currentTime)
  {
    synchronized (this)
    {
      super.fire(currentTime);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Firing motor event for movement : "
          + getMovement().getChunkTypeRequest() + " @ " + currentTime);

    IEfferentCommand command = getMotorCommand();
    /*
     * command may be null, if CR has not responded yet.
     */
    if (command == null)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("IEfferentCommand not available, drifting");
      drift();
    }
    else
    {
      IEfferentCommand.ActualState state = command.getActualState();
      if (_driftSet.contains(state))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("State is " + state + " drifting");
        drift();
      }
      else
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("State is " + state + " officially firing "
              + command.getResult());
        fireInternal(currentTime);
      }
    }
  }

  protected void fireInternal(double currentTime)
  {

  }

  private void drift()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Drifting event to " + (getEndTime() + _driftOffset));

    MotorTimedEvent newEvent = instantiateDrift(_movement, _motor,
        _driftOffset, _driftSet);

    _driftedTimedEvent = newEvent;

    _motor.getBuffer().enqueueTimedEvent(newEvent);
  }

  protected MotorTimedEvent instantiateDrift(IMovement movement,
      IMotorModule module, double driftOffset,
      Collection<IEfferentCommand.ActualState> driftSet)
  {
    return new MotorTimedEvent(movement, module, driftOffset, driftSet,
        getStartTime(), getEndTime() + driftOffset);
  }

  protected IEfferentCommand getMotorCommand()
  {
    IIdentifier commandId = _movement.getCommandIdentifier();
    IEfferentCommand command = ACTRRuntime.getRuntime().getConnector()
        .getAgent(_motor.getModel()).getEfferentCommandManager().get(commandId);
    return command;
  }
}
