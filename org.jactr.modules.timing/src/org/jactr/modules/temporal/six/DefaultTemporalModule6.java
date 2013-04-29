package org.jactr.modules.temporal.six;

/*
 * default logging
 */
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.IllegalModuleStateException;
import org.jactr.core.module.random.IRandomModule;
import org.jactr.core.module.random.six.DefaultRandomModule;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.TimedEventQueue;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.NumericParameterHandler;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.modules.temporal.ITemporalModule;
import org.jactr.modules.temporal.buffer.DefaultTemporalActivationBuffer;

/**
 * implementation of the temporal module. This module is very basic. By calling
 * {@link #startTimer()} a new time {@link IChunk} is added to the temporal
 * {@link IActivationBuffer}. At the same time, an {@link ITimedEvent} is
 * posted to the {@link TimedEventQueue}. It will expire after the initial tick
 * duration. Uponing firing, it will increment the tick count of the
 * {@link IChunk} and then post a new {@link ITimedEvent} that will expire
 * later. The durations are computed by {@link #computeTickDuration(double)}.
 * The {@link ITimedEvent}s are posted by {@link #nextTickTimedEvent()}.<br>
 * <br>
 * All the modifications by this buffer are done through the {@link ITimedEvent}s
 * (necessarily on the model thread) or as a result of {@link ChunkPattern}
 * requests made to the {@link IActivationBuffer} (again, on the model thread).
 * So, thread safety is not much of a concern here.<br>
 * <br>
 * <br>
 * This module is dynamically resolved via the modules extension point in
 * plugin.xml
 * 
 * @author harrison
 */
public class DefaultTemporalModule6 extends AbstractModule implements
    ITemporalModule, IParameterized
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER                = LogFactory
                                                               .getLog(DefaultTemporalModule6.class);

  static public final String         TIME_MULTIPLIER_PARAM = "TemporalMultiplier";

  static public final String         TIME_NOISE_PARAM      = "TemporalNoise";

  static public final String         TIME_START_PARAM      = "InitialTemporalDuration";

  private IActivationBuffer          _temporalBuffer;

  private IChunkType                 _timeChunkType;

  private double                     _timeMultiplier       = 1.1;

  private double                     _timeNoise            = 0.015;

  private double                     _initialTickTime      = 0.011;

  private IRandomModule              _randomModule;

  private TickTimedEvent             _currentTimedEvent;

  private long                       _count                = 0;

  private long                       _currentTicks         = 0;

  public DefaultTemporalModule6()
  {
    super("temporal");
  }

  /**
   * initialize the module and make sure we've got the time {@link IChunkType}
   * as well as a reference to an {@link IRandomModule}. If not
   * {@link IRandomModule} is installed in the model, a default instance is
   * used.
   * 
   * @see org.jactr.core.module.AbstractModule#initialize()
   */
  @Override
  public void initialize()
  {
    try
    {
      _timeChunkType = getModel().getDeclarativeModule().getChunkType("time")
          .get();
    }
    catch (Exception e)
    {
      throw new IllegalModuleStateException(
          "Could not get time chunktype from declarative memory ", e);
    }

    /*
     * make sure that there is a random module to use
     */
    _randomModule = (IRandomModule) getModel().getModule(IRandomModule.class);
    if (_randomModule == null)
      _randomModule = DefaultRandomModule.getInstance();
  }

  /**
   * create the temporal buffer {@link DefaultTemporalActivationBuffer}
   * 
   * @return
   * @see org.jactr.core.module.AbstractModule#createBuffers()
   */
  @Override
  protected Collection<IActivationBuffer> createBuffers()
  {
    _temporalBuffer = new DefaultTemporalActivationBuffer("temporal", this);
    return Collections.singleton(_temporalBuffer);
  }

  /**
   * return the buffer
   * 
   * @return
   * @see org.jactr.modules.temporal.ITemporalModule#getBuffer()
   */
  public IActivationBuffer getBuffer()
  {
    return _temporalBuffer;
  }

  /**
   * return the current tick count, or 0 if not time chunk is in the buffer
   * 
   * @return
   * @see org.jactr.modules.temporal.ITemporalModule#getTicks()
   */
  public long getTicks()
  {
    return _currentTicks;
  }

  /**
   * reset the clock, and abort any pending {@link ITimedEvent} that is used to
   * increment the ticks
   * 
   * @see org.jactr.modules.temporal.ITemporalModule#reset()
   */
  public void reset()
  {
    if (_currentTimedEvent != null)
    {
      _currentTimedEvent.abort();
      _currentTimedEvent = null;
    }

    _temporalBuffer.clear();
    _currentTicks = 0;
  }

  /**
   * start a timer. The current timer is removed, a new one created and added to
   * the buffer.
   * 
   * @see org.jactr.modules.temporal.ITemporalModule#startTimer()
   */
  public void startTimer(int initialTicks)
  {
    try
    {
      _count++;
      /*
       * is there a tick pending? abort
       */
      if (_currentTimedEvent != null) _currentTimedEvent.abort();

      /*
       * create the chunk and add it to the buffer. If there is a chunk in the
       * buffer already it will be removed first
       */
      IChunk timer = getModel().getDeclarativeModule().createChunk(
          _timeChunkType, "timer-" + _count).get();

      IMutableSlot tickSlot = (IMutableSlot) timer.getSymbolicChunk().getSlot(
          "ticks");
      tickSlot.setValue(new Double(initialTicks));

      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, TEMPORAL_LOG, "Starting clock at " + initialTicks);

      _temporalBuffer.addSourceChunk(timer);

      /*
       * and queue up the ticker
       */
      double previousDuration = 0;

      nextTickTimedEvent(ACTRRuntime.getRuntime().getClock(getModel())
          .getTime(), previousDuration);
    }
    catch (Exception e)
    {
      throw new RuntimeException("Could not start new timer ", e);
    }
  }

  /**
   * compute the duration of the next tick given the previous one. if
   * previousDuration is 0, t0 is recomputed
   * 
   * @param previousDuration
   * @return
   */
  protected double computeTickDuration(double previousDuration)
  {
    double duration = _initialTickTime;
    double s = 5 * _timeNoise * _initialTickTime;

    if (previousDuration > 0)
    {
      duration = _timeMultiplier * previousDuration;
      s = _timeNoise * _timeMultiplier * previousDuration;
    }

    double noise = _randomModule.logisticNoise(s);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Previous duration : " + previousDuration + " current : "
          + duration + " noise : " + noise);

    duration += noise;
    return duration;
  }

  /**
   * internal method to increment the number of ticks of the current source
   * chunk in the buffer. This is called by {@link TickTimedEvent#fire(double)}
   */
  protected void incrementTicks(double startTime, double endTime,
      double duration)
  {

    double ticks = 0;

    /**
     * this is necessary if (endTime-startTime) >= duration, i.e. more than one
     * tick may have elapsed
     */
    while (endTime - startTime >= duration)
    {
      ticks++;
      startTime += duration;
      duration = computeTickDuration(duration);
    }

    _currentTicks += ticks;

    IChunk time = getBuffer().getSourceChunk();

    if (time != null)
    {
      IModel model = getModel();
      IMutableSlot tickSlot = (IMutableSlot) time.getSymbolicChunk().getSlot(
          "ticks");

      double value = ticks + (Double) tickSlot.getValue();
      tickSlot.setValue(new Double(value));

      if (LOGGER.isDebugEnabled() || Logger.hasLoggers(model))
      {
        String msg = "Ticks incremented by " + ticks + " to " + value
            + " next duration : " + duration;
        Logger.log(model, TEMPORAL_LOG, msg);
        LOGGER.debug(msg);
      }
    }
  }

  /**
   * creates a new timed event which will actually perform the tick increments.
   * called by {@link TickTimedEvent#fire(double)} and {@link #startTimer()}
   */
  protected void nextTickTimedEvent(double now, double previousDuration)
  {
    _currentTimedEvent = new TickTimedEvent(now,
        computeTickDuration(previousDuration));
    getModel().getTimedEventQueue().enqueue(_currentTimedEvent);
  }

  /**
   * 
   */
  public String getParameter(String key)
  {
    if (TIME_MULTIPLIER_PARAM.equalsIgnoreCase(key))
      return "" + _timeMultiplier;
    else if (TIME_NOISE_PARAM.equalsIgnoreCase(key))
      return "" + _timeNoise;
    else if (TIME_START_PARAM.equalsIgnoreCase(key))
      return "" + _initialTickTime;

    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Arrays.asList(TIME_MULTIPLIER_PARAM, TIME_NOISE_PARAM,
        TIME_START_PARAM);
  }

  public void setParameter(String key, String value)
  {
    NumericParameterHandler nph = ParameterHandler.numberInstance();
    if (TIME_MULTIPLIER_PARAM.equalsIgnoreCase(key))
      _timeMultiplier = nph.coerce(value).doubleValue();
    else if (TIME_NOISE_PARAM.equalsIgnoreCase(key))
      _timeNoise = nph.coerce(value).doubleValue();
    else if (TIME_START_PARAM.equalsIgnoreCase(key))
      _initialTickTime = nph.coerce(value).doubleValue();
  }

  /**
   * custom timed event that upon {@link #fire(double)} will call
   * {@link DefaultTemporalModule6#incrementTicks()} and
   * {@link DefaultTemporalModule6#nextTickTimedEvent()}
   * 
   * @author harrison
   */
  private class TickTimedEvent extends AbstractTimedEvent
  {

    public TickTimedEvent(double now, double duration)
    {
      super();
      setTimes(now, now + duration);
    }

    @Override
    public void fire(double currentTime)
    {
      super.fire(currentTime);
      /*
       * we use the current time as end time, in case more than duration time
       * has elapsed endtime (could be true if running realtime)
       */
      incrementTicks(getStartTime(), currentTime, getEndTime() - getStartTime());
      nextTickTimedEvent(currentTime, getEndTime() - getStartTime());
    }
  }
}
