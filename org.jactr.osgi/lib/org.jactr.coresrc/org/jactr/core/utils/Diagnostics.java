package org.jactr.core.utils;

/*
 * default logging
 */
import java.util.function.BiConsumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.time.impl.BasicClock;
import org.jactr.core.model.ModelTerminatedException;

/**
 * static class with various diagnostic tools. Exposes the following system
 * properties: "jactr.diagnostics.timeWindow" (TimeWindow name),
 * "jactr.diagnostics.terminateOnFail" (false)
 * 
 * @author harrison
 */
public class Diagnostics
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(Diagnostics.class);

  static enum TimeWindow {
    DAY(86400.0), WEEK(604800.0), MONTH(2628000.0), YEAR(31536000.0), ADULTHOOD(
        567648000.0), CENTURY(3153600000.0);

    double _value;

    TimeWindow(double value)
    {
      _value = value;
    }

    public double getSeconds()
    {
      return _value;
    }
  }

  static private TimeWindow                          DEFAULT_WINDOW;

  static private BiConsumer<Double, Double>          DEFAULT_PRECISION_FAILURE;

  static private BiConsumer<Double, TimeWindow>      DEFAULT_SANITY_FAILURE;

  static public final BiConsumer<Double, Double>     PRECISION_IGNORE    = (c,
                                                                             p) -> {
                                                                           String msg = String
                                                                               .format(
                                                                                   "Precision violation. Desired [%.5f], got [%.5f]",
                                                                                   c,
                                                                                   p);
                                                                           LOGGER
                                                                               .debug(msg);
                                                                         };

  static public final BiConsumer<Double, Double>     PRECISION_STOP      = (c,
                                                                             p) -> {
                                                                           String msg = String
                                                                               .format(
                                                                                   "Precision violation. Desired [%.5f], got [%.5f]",
                                                                                   c,
                                                                                   p);
                                                                           LOGGER
                                                                               .error(msg);
                                                                           throw new ModelTerminatedException(
                                                                               msg);
                                                                         };

  static public final BiConsumer<Double, Double>     PRECISION_TERMINATE = (c,
                                                                             p) -> {
                                                                           String msg = String
                                                                               .format(
                                                                                   "Precision violation. Desired [%.5f], got [%.5f]",
                                                                                   c,
                                                                                   p);
                                                                           LOGGER
                                                                               .error(msg);
                                                                           System
                                                                               .exit(-1);
                                                                         };

  static public final BiConsumer<Double, TimeWindow> SANITY_IGNORE       = (c,
                                                                             w) -> {
                                                                           String msg = String
                                                                               .format(
                                                                                   "Age violation. [%.5f] exceeded [%s:%.5f]",
                                                                                   c,
                                                                                   w,
                                                                                   w.getSeconds());
                                                                           LOGGER
                                                                               .debug(msg);
                                                                         };

  static public final BiConsumer<Double, TimeWindow> SANITY_STOP         = (c,
                                                                             w) -> {
                                                                           String msg = String
                                                                               .format(
                                                                                   "Age violation. [%.5f] exceeded [%s:%.5f]",
                                                                                   c,
                                                                                   w,
                                                                                   w.getSeconds());
                                                                           LOGGER
                                                                               .error(msg);
                                                                           throw new ModelTerminatedException(
                                                                               msg);
                                                                         };

  static public final BiConsumer<Double, TimeWindow> SANITY_TERMINATE    = (c,
                                                                             w) -> {
                                                                           String msg = String
                                                                                   .format(
                                                                                       "Age violation. [%.5f] exceeded [%s:%.5f]",
                                                                                       c,
                                                                                       w,
                                                                                   w.getSeconds());
                                                                           LOGGER
                                                                               .error(msg);
                                                                           System
                                                                               .exit(-1);
                                                                         };

  static
  {
    String property = System.getProperty("jactr.diagnostics.timeWindow");
    try
    {
      if (property == null) property = "YEAR";

      DEFAULT_WINDOW = TimeWindow.valueOf(property);
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format(
            "Failed to set timeWindow for %s, using YEAR. ", property), e);
      DEFAULT_WINDOW = TimeWindow.YEAR;
    }

    String strat = System.getProperty("jactr.diagnostics.onFail", "stop");

    if (strat.equalsIgnoreCase("terminate"))
    {
      DEFAULT_PRECISION_FAILURE = PRECISION_TERMINATE;
      DEFAULT_SANITY_FAILURE = SANITY_TERMINATE;
    }
    else if (strat.equalsIgnoreCase("stop"))
    {
      DEFAULT_PRECISION_FAILURE = PRECISION_STOP;
      DEFAULT_SANITY_FAILURE = SANITY_STOP;
    }
    else
    {
      DEFAULT_PRECISION_FAILURE = PRECISION_IGNORE;
      DEFAULT_SANITY_FAILURE = SANITY_IGNORE;
    }

  }

  /**
   * @param precisionFailure
   *          called when there is a failure. passed clock time, actual
   *          precision
   * @param clock
   */
  static public void precisionTest(double clock,
      BiConsumer<Double, Double> precisionFailure)
  {
    double next = Math.nextAfter(clock, Double.POSITIVE_INFINITY);
    double delta = Math.abs(next - clock);
    if (delta > BasicClock.getPrecision())
    {
      LOGGER
          .fatal(String
              .format(
                  "Precision violation, minimum increment : %.5f, requested precision: %.5f.",
                  delta, BasicClock.getPrecision()));
      if (precisionFailure != null) precisionFailure.accept(clock, delta);
    }
  }

  /**
   * @param clock
   * @param window
   * @param windowFailure
   * @param precisionFailure
   */
  static public void timeSanityCheck(double clock, TimeWindow window,
      BiConsumer<Double, TimeWindow> windowFailure,
      BiConsumer<Double, Double> precisionFailure)
  {
    if (clock > window.getSeconds())
    {
      LOGGER.error(String.format(
          "Time sanity check failed. %.2f exceeds %.2f (%s).", clock,
          window.getSeconds(), window.toString()));
      if (windowFailure != null) windowFailure.accept(clock, window);
    }

    precisionTest(clock, precisionFailure);
  }

  static public void timeSanityCheck(double clock)
  {
    timeSanityCheck(clock, DEFAULT_WINDOW, DEFAULT_SANITY_FAILURE,
        DEFAULT_PRECISION_FAILURE);
  }

}
