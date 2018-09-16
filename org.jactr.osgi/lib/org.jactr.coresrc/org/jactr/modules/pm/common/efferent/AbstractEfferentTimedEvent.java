package org.jactr.modules.pm.common.efferent;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.efferent.IEfferentCommand;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.IBufferBasedTimedEvent;

/**
 * timed event meant to deal with efferent commands. since efferent commands
 * might not enjoy the strict timings that act-r expects, these timed events
 * have the ability to drift forward in time until the efferent command's state
 * is appropriate.
 * 
 * @author harrison
 */
public abstract class AbstractEfferentTimedEvent<E extends IEfferentCommand> extends
    AbstractTimedEvent implements IBufferBasedTimedEvent
{
  /**
   * Logger definition
   */
  static private final transient Log              LOGGER = LogFactory
                                                             .getLog(AbstractEfferentTimedEvent.class);

  final private Future<E>                         _commandFuture;

  final private Set<IEfferentCommand.ActualState> _driftStates;

  final private IActivationBuffer                 _buffer;

  public AbstractEfferentTimedEvent(double start, double end,
      Future<E> commandFuture, IActivationBuffer buffer)
  {
    super(start, end);
    _commandFuture = commandFuture;
    _driftStates = new HashSet<IEfferentCommand.ActualState>();
    _buffer = buffer;
  }

  protected Future<E> getFuture()
  {
    return _commandFuture;
  }
  
  protected Set<IEfferentCommand.ActualState> getDriftStates()
  {
    return _driftStates;
  }

  /**
   * return a deep copy of this event.
   * @return
   * @see java.lang.Object#clone()
   */
  @Override
  abstract protected AbstractEfferentTimedEvent<E> clone();

  public IActivationBuffer getBuffer()
  {
    return _buffer;
  }

  public IChunk getBoundChunk()
  {
    return null;
  }


  public void setDriftStates(IEfferentCommand.ActualState... states)
  {
    _driftStates.addAll(Arrays.asList(states));
  }

  @Override
  final public void abort()
  {
    super.abort();
    aborted(_commandFuture);
  }

  @Override
  final public void fire(double currentTime)
  {
    super.fire(currentTime);
    if (!_commandFuture.isDone())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("future has not completed, must drift");
      drift(currentTime, _commandFuture);
    }
    try
    {
      IEfferentCommand command = _commandFuture.get();
      if (_driftStates.contains(command.getActualState()))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug(command.getActualState() + " is drift state, must drift");
        
        drift(currentTime, _commandFuture);
      }
      else
        fired(currentTime, _commandFuture);
    }
    catch (Exception e)
    {
      fired(currentTime, _commandFuture);
    }
  }

  /**
   * 
   */
  protected void aborted(Future<E> commandFuture)
  {

  }
  
  

  protected void drift(double currentTime, Future<E> commandFuture)
  {
    AbstractEfferentTimedEvent<E> clone = clone();
    IModel model = getBuffer().getModel();
    clone.setTimes(getStartTime(), getEndTime()
        + model.getProceduralModule().getDefaultProductionFiringTime());
    model.getTimedEventQueue().enqueue(clone);
    drifted(clone);
  }

  protected void drifted(AbstractEfferentTimedEvent<E> driftedEvent)
  {

  }

  protected void fired(double currentTime, Future<E> commandFuture)
  {

  }
}
