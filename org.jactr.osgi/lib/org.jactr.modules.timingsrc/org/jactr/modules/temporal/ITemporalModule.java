package org.jactr.modules.temporal;

/*
 * default logging
 */
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.module.IModule;

/**
 * stub interface for Taatgen & van Rijn's temporal module
 * @author harrison
 *
 */
public interface ITemporalModule extends IModule
{
  
  static public final String TEMPORAL_LOG = "TEMPORAL";
  
  /**
   * return the buffer that contains the time chunk
   * @return
   */
  public IActivationBuffer getBuffer();
  
  /**
   * reset the temporal module, removing any timer and reseting
   * the tick duration to its initial value
   */
  public void reset();
  
  /**
   * start a timer by adding a time chunk to the buffer
   */
  public void startTimer(int initialTicks);
  
  /**
   * return the current # of ticks.
   * @return
   */
  public long getTicks();
}
