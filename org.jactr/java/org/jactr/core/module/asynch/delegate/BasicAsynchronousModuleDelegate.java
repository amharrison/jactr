package org.jactr.core.module.asynch.delegate;

/*
 * default logging
 */
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.module.AbstractModule;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.IRequest;
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
public abstract class BasicAsynchronousModuleDelegate<M extends IAsynchronousModule, R>
    implements IAsynchronousModuleDelegate<M, R>
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BasicAsynchronousModuleDelegate.class);

  private M                          _module;

  private R                          _errorResult;

  public BasicAsynchronousModuleDelegate(M module, R cantProcessResult)
  {
    _module = module;
    _errorResult = cantProcessResult;
  }

  public M getModule()
  {
    return _module;
  }

  

  public CompletableFuture<R> process(final IRequest request,
      final double requestTime, final Object... parameters)
  {
    boolean canProcess = shouldProcess(request, parameters);

    if (canProcess)
    {
      CompletableFuture<R> rtn = AbstractModule.delayedFuture(
          new Callable<R>() {

        public R call() throws Exception
        {
          R result = _errorResult;

          try
          {
            result = processInternal(request, requestTime, parameters);
          }
          catch (Exception e)
          {
            /**
             * Error : error
             */
            LOGGER.error("Failed to process pattern request ", e);
          }

          processInternalCompleted(request, result, parameters);

          return result;
        }

      }, getModule().getExecutor());
      return rtn;
    }
    else
    {
      /*
       * nothing can be done, return right now
       */
      processInternalCompleted(request, _errorResult, parameters);
      return AbstractModule.immediateReturn(_errorResult);
    }
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
  protected void processInternalCompleted(IRequest request, R result, Object...parameters)
  {

  }

  

  /**
   * Called on the initiating thread (i.e. model thread), this checks the module
   * and the buffers to be sure that the processing can proceed. If true, it
   * will queue {@link #processInternal(ChunkPattern)} on the asynchronous
   * executor.
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
  abstract protected R processInternal(IRequest request, double requestTime, Object... parameters);

 
}
