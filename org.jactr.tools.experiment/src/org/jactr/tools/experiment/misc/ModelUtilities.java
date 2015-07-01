package org.jactr.tools.experiment.misc;

/*
 * default logging
 */
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ModelCycleExecutor;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.six.learning.IProceduralLearningModule6;
import org.jactr.core.queue.timedevents.RunnableTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;

public class ModelUtilities
{
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelUtilities.class);

  /**
   * reward/punish the model using {@link #executeLater(IModel, Runnable)}
   * 
   * @param model
   * @param reward
   * @return
   * @throws IllegalStateException
   */
  static public Future<Boolean> reward(final IModel model, final double reward)
      throws IllegalStateException
  {
    return executeNow(model, new Runnable() {

      public void run()
      {
        IProceduralLearningModule6 plm = (IProceduralLearningModule6) model
            .getModule(IProceduralLearningModule6.class);

        if (plm != null)
          plm.reward(reward);
        else
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(String.format(
                "Cannot reward %1$s, no IProceduralLearningModule6 installed.",
                model));
      }

    });
  }

  /**
   * will execute the runnable on the model thread at the earliest possible
   * moment
   * 
   * @param model
   * @param runnable
   * @return future boolean if the runnable was fired (false if aborted)
   * @throws IllegalStateException
   *           if the model is not running
   */
  static public Future<Boolean> executeLater(IModel model,
      final Runnable onFire, final Runnable onAbort)
      throws IllegalStateException
  {
    IController controller = ACTRRuntime.getRuntime().getController();
    if (controller == null || !controller.isRunning())
      throw new IllegalStateException("Model is not running");

    double now = ACTRRuntime.getRuntime().getClock(model).getTime();
    final ExecuteLater<Boolean> future = new ExecuteLater<Boolean>();

    Runnable fire = new Runnable() {
      public void run()
      {
        try
        {
          onFire.run();
          future.setResult(true);
        }
        catch (Exception e)
        {
          future.setException(e);
        }
      }
    };

    Runnable abort = new Runnable() {

      public void run()
      {
        try
        {
          if (onAbort != null) onAbort.run();

          future.setResult(false);
        }
        catch (Exception e)
        {
          future.setException(e);
        }
      }
    };

    model.getTimedEventQueue().enqueue(
        new RunnableTimedEvent(now, fire, abort));

    return future;
  }
  
  /**
   * post this runnable to execute after the current cycle finishes
   * @param model
   * @param onFire
   * @return
   */
  static public Future<Boolean> executeNow(IModel model, final Runnable onFire)
  {
    IController controller = ACTRRuntime.getRuntime().getController();
    if (controller == null || !controller.isRunning())
      throw new IllegalStateException("Model is not running");
    
    final ExecuteLater<Boolean> future = new ExecuteLater<Boolean>();

    Runnable fire = new Runnable() {
      public void run()
      {
        try
        {
          onFire.run();
          future.setResult(true);
        }
        catch (Exception e)
        {
          future.setException(e);
        }
      }
    };
    
    new ModelCycleExecutor(model, ModelCycleExecutor.When.ASAP).execute(fire);
    
    return future;
  }

  static public Future<Boolean> executeLater(IModel model, Runnable onFire)
      throws IllegalStateException
  {
    return executeLater(model, onFire, null);
  }

  static private class ExecuteLater<T> extends FutureTask<T>
  {
    public ExecuteLater()
    {
      super(new Runnable() {

        public void run()
        {

        }
      }, null);
    }

    public void setResult(T result)
    {
      set(result);
    }

    public void setException(Throwable thrown)
    {
      super.setException(thrown);
    }
  }
}
