package org.jactr.core.module.asynch.delegate;

/*
 * default logging
 */
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.queue.timedevents.BlockingTimedEvent;

/**
 * abstract asynch delegate. This will manage the {@link BlockingTimedEvent}
 * such that the synchronization will occurr minimumProcessingTime after the
 * start of the request.
 * 
 * @author harrison
 * @param <M>
 * @param <R>
 */
public abstract class AbstractAsynchronousModuleDelegate<M extends IAsynchronousModule, R>
    implements IAsynchronousModuleDelegate<M, R>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractAsynchronousModuleDelegate.class);

  private M                          _module;

  private double                     _minimumProcessingTime;

  private R                          _errorResult;

  public AbstractAsynchronousModuleDelegate(M module,
      double minimumProcessingTime, R cantProcessResult)
  {
    _module = module;
    _minimumProcessingTime = minimumProcessingTime;
    _errorResult = cantProcessResult;
  }

  public M getModule()
  {
    return _module;
  }
  
  public double getMinimumProcessingTime()
  {
    return _minimumProcessingTime;
  }

  public Future<R> process(final IRequest request, double processTime,
      final Object... parameters)
  {
    double now = processTime;
    double blockAt = now + _minimumProcessingTime;

    boolean canProcess = shouldProcess(request, parameters);

    if (canProcess)
    {
      final BlockingTimedEvent synchronizationBlock = getModule()
          .synchronizedTimedEvent(now, blockAt);
      
      blockingTimedEventCreated(synchronizationBlock);

      Future<R> rtn = AbstractModule.delayedFuture(new Callable<R>() {

        public R call() throws Exception
        {
          R result = _errorResult;

          try
          {
            result = processInternal(request, parameters);
          }
          catch (Exception e)
          {
            /**
             * Error : error
             */
            LOGGER.error("Failed to process pattern request ", e);
          }

          double startTime = synchronizationBlock.getStartTime();
          double harvestAt = computeHarvestTime(request, result, startTime,
              parameters);

          if (LOGGER.isDebugEnabled())
            LOGGER.debug(getModule() + " requested, started at " + startTime
                + " will harvest " + result + " at " + harvestAt);

          processInternalCompleted(request, result, startTime, harvestAt);

          ITimedEvent harvest = createHarvestTimedEvent(startTime, harvestAt, request, result, parameters);

          enqueue(harvest);

          /*
           * processing is complete, release the block
           */
          if (!synchronizationBlock.hasAborted()) synchronizationBlock.abort();
          
          return result;
        }

      }, getModule().getExecutor());
      return rtn;
    }
    else
      /*
       * nothing can be done, return right now
       */
      return AbstractModule.immediateReturn(_errorResult);
  }
  
  
  protected ITimedEvent createHarvestTimedEvent(double start, double end, final IRequest request, final R result, final Object ... parameters )
  {
    return new AbstractTimedEvent(start, end) {
      @Override
      public void fire(double currentTime)
      {
        super.fire(currentTime);
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(getModule() + " harvesting " + result
              + " at " + currentTime);

        finalizeProcessing(request, result, parameters);
      }
    };
  }

  /**
   * called on the asynch thread on the creation of the blocking timed event.
   * this is useful if you need to snag a reference to it.
   * 
   * @param bte
   */
  protected void blockingTimedEventCreated(BlockingTimedEvent bte)
  {

  }

  /**
   * called on the asynch thread after processInternal has completed. primarily
   * used to output some logging info..
   * 
   * @param request
   * @param result
   * @param startTime
   * @param harvestAt
   */
  protected void processInternalCompleted(IRequest request, R result,
      double startTime, double harvestAt)
  {


  }

  /**
   * method to queue the timed event. By default
   * getModule().getModel().getTimedEventQueue().enqueue()
   * 
   * @param timedEvent
   */
  protected void enqueue(ITimedEvent timedEvent)
  {
    getModule().getModel().getTimedEventQueue().enqueue(timedEvent);
  }

  /**
   * Called on the initiating thread (i.e. model thread), this checks the module
   * and the buffers to be sure that the processing can proceed. If true, it
   * will queue {@link #processInternal(ChunkPattern)} on the asynchronous
   * executor and then execute {@link #finalizeProcessing(ChunkPattern, Object)}
   * on the model thread once all is completed
   * 
   * @param request
   * @return
   */
  abstract protected boolean shouldProcess(IRequest request,
      Object... parameters);

  /**
   * called on the asynchronous thread, this does the actual processing,
   * returning some result. This method should handle all its own exceptions.
   * 
   * @param request
   * @return
   */
  abstract protected R processInternal(IRequest request,
      Object... parameters);

  /**
   * called on the model thread, this handles the clean up
   * 
   * @param request
   * @param result
   */
  abstract protected void finalizeProcessing(IRequest request, R result,
      Object... parameters);

  /**
   * returns the time at which this result should be made available. In other
   * words, after {@link #processInternal(ChunkPattern)} finishes, a new
   * {@link ITimedEvent} is queued that will fire at the returned time. Upon
   * firing it will call {@link #finalizeProcessing(ChunkPattern, Object)}.
   * This is called on the asynch thread
   * 
   * @param request
   * @param result
   * @param startTime
   * @return
   */
  abstract protected double computeHarvestTime(IRequest request, R result,
      double startTime, Object... parameters);
}
