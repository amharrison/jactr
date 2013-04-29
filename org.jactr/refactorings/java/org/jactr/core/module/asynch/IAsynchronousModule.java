package org.jactr.core.module.asynch;

/*
 * default logging
 */
import java.util.concurrent.Executor;

import org.jactr.core.module.IModule;
import org.jactr.core.queue.timedevents.BlockingTimedEvent;

public interface IAsynchronousModule extends IModule
{

  static public final String STRICT_SYNCHRONIZATION_PARAM = "EnableStrictSynchronization";
  
  public Executor getExecutor();

  public boolean isStrictSynchronizationEnabled();

  public void setStrictSynchronizationEnabled(boolean enableStrict);

  /**
   * create a new synchronization point. This synchronization point will block
   * the model from further firing at blockAtTime. The block will not be
   * released until {@link BlockingTimedEvent#abort()} is called. <br>
   * <br>
   * If {@link #isStrictSynchronizationEnabled()} is true, the event will be
   * created and queued. If false, the event will be created, immediately
   * aborted, and <b>not</b> queued. <br>
   * <br>
   * This behavior was decided upon so that the module code need not concern
   * itself with {@link #isStrictSynchronizationEnabled()} at all. Modules that
   * wish to synchronize should just create the {@link BlockingTimedEvent} via
   * this call and make their module request. Upon completion of the request,
   * the block is aborted.
   * 
   * @param startTime
   * @param blockAtTime
   * @return
   */
  public BlockingTimedEvent synchronizedTimedEvent(double startTime,
      double blockAtTime);

}
