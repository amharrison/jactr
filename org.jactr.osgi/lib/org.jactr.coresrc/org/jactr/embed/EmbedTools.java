package org.jactr.embed;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.BreakpointType;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.jactr.core.runtime.controller.debug.event.BreakpointEvent;
import org.jactr.core.runtime.controller.debug.event.IBreakpointListener;

public class EmbedTools
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EmbedTools.class);

  /**
   * Utility function that returns a completable future for when the model
   * executes until this point and time.{@link DebugController#resume()} must be
   * called afterwards.
   * 
   * @param controller
   * @param when
   * @return
   */
  static public CompletableFuture<Double> runUntil(DebugController controller,
      double when)
  {
    CompletableFuture<Double> future = runUntil(controller, (be, w) -> {
      return be.getSource().getAge() >= w;
    }, (be) -> be.getSource().getAge(), (m) -> when, BreakpointType.TIME);

    return future;
  }

  /**
   * Utility function that returns a completable future for when the model
   * executes until this point in cycles.{@link DebugController#resume()} must
   * be called afterwards.
   * 
   * @param controller
   * @param cycle
   * @return
   */
  static public CompletableFuture<Long> runUntil(DebugController controller,
      long cycle)
  {
    CompletableFuture<Long> future = runUntil(controller, (be, c) -> be
        .getSource().getProceduralModule().getNumberOfProductionsFired() >= c,
        (be) -> be.getSource().getProceduralModule()
            .getNumberOfProductionsFired(), (m) -> cycle, BreakpointType.CYCLE);

    return future;
  }

  /**
   * run a single cycle. In this instance, the controller must be suspended or
   * not even started in order to work correctly.
   * 
   * @param controller
   * @return
   */
  static public CompletableFuture<Long> runOneCycle(DebugController controller)
  {
    CompletableFuture<Long> future = runUntil(controller, (be, c) -> be
        .getSource().getProceduralModule().getNumberOfProductionsFired() >= c,
        (be) -> be.getSource().getProceduralModule()
            .getNumberOfProductionsFired(), (m) -> m.getProceduralModule()
            .getNumberOfProductionsFired() + 1, BreakpointType.CYCLE);
    return future;
  }

  /**
   * @param controller
   * @param hasReached
   *          uses breakpoint and triggerEvent value to determine if breakpoint
   *          was reached
   * @param completionValue
   *          this is the value that is sent on completion of the future
   * @param triggerEvent
   *          the value that is used in breakpoint tests
   * @param type
   * @return
   */
  static protected <T> CompletableFuture<T> runUntil(
      DebugController controller, BiPredicate<BreakpointEvent, T> hasReached,
      Function<BreakpointEvent, T> completionValue,
      Function<IModel, T> triggerEvent, BreakpointType type)
  {
    CompletableFuture<T> future = new CompletableFuture<T>();
    /*
     * when the break point is reached (configured below), we verify that the
     * right point has been reached, then complete the future. Since there can
     * be multiple models, we need multiple listeners
     */
    Collection<IBreakpointListener> bpls = new ArrayList<IBreakpointListener>();
    // retained for cleanup
    Collection<Object> triggerValues = new ArrayList<Object>();

    for (IModel model : ACTRRuntime.getRuntime().getModels())
    {
      // the test could be model specific
      T triggerValue = triggerEvent.apply(model);
      triggerValues.add(triggerValue);

      IBreakpointListener bl = (be) -> {
        if (hasReached.test(be, triggerValue))
          future.complete(completionValue.apply(be));
      };

      bpls.add(bl);

      /*
       * add the listener
       */
      controller.addListener(bl, ExecutorServices.INLINE_EXECUTOR);

      /*
       * add the breakpoint
       */
      controller.addBreakpoint(model, type, triggerValue);

      /*
       * if the runtime is already running, it is possible to reach the
       * breakpoint before we've finished installing.. hmmm
       */
    }

    /*
     * when the future is completed, this will be called, cleaning up after
     * ourselves.
     */
    future.thenRun(() -> {

      /*
       * make sure we remove all the breakpoints after firing
       */
      // inefficient, but this shouldn't be run a bunch, at least not quickly
        for (IModel model : ACTRRuntime.getRuntime().getModels())
          for (Object triggerValue : triggerValues)
            controller.removeBreakpoint(model, type, triggerValue);

        /*
         * and the listeners too
         */
        for (IBreakpointListener listener : bpls)
          controller.removeListener(listener);
      });

    /**
     * make sure that we are running
     */
    if (!controller.isRunning())
      controller.start();
    else if (controller.isSuspended()) controller.resume();

    return future;
  }
}
