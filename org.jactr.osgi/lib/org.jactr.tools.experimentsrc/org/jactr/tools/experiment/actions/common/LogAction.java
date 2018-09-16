package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */

import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;

public class LogAction implements IAction
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER    = LogFactory
                                                     .getLog(LogAction.class);

  private final String                 _message;

  private final IExperiment            _experiment;

  private Consumer<String>             _logMessageConsumer;

  static public final Consumer<String> STDOUT    = (msg) -> System.out
                                                     .println(msg);

  static public final Consumer<String> STDERR    = (msg) -> System.err
                                                     .println(msg);

  static public final Consumer<String> ERROR_LOG = (msg) -> LOGGER.error(msg);

  static public final Consumer<String> NULL      = (msg) -> {
                                                 };

  /**
   * will check LogAction.destination system property for override log consumer,
   * otherwise, stdout
   * 
   * @param message
   * @param experiment
   */
  public LogAction(String message, IExperiment experiment)
  {
    _message = message;
    _experiment = experiment;

    Consumer<String> consumer = STDOUT;
    String destination = System.getProperty("LogAction.destination");
    if (destination != null) if (destination.equalsIgnoreCase("err"))
      consumer = LogAction.STDERR;
    else if (destination.equalsIgnoreCase("null")) consumer = LogAction.NULL;

    _logMessageConsumer = consumer;
  }

  public LogAction(String message, IExperiment experiment, boolean logError)
  {
    this(message, experiment, logError ? ERROR_LOG : STDOUT);
  }

  public LogAction(String message, IExperiment experiment,
      Consumer<String> logConsumer)
  {
    _message = message;
    _experiment = experiment;
    _logMessageConsumer = logConsumer;
  }

  public void fire(IVariableContext context)
  {
    // StringBuilder sb = new StringBuilder(_message);
    // int startIndex = sb.indexOf(resolver.getPrefix(), 0);
    // while (startIndex != -1)
    // {
    // int endIndex = sb.indexOf(resolver.getSuffix(), startIndex);
    // if (endIndex == -1) break;
    // String replacement = resolver.resolve(
    // sb.substring(startIndex, endIndex + 1), context).toString();
    // sb.replace(startIndex, endIndex + 1, replacement);
    // startIndex = sb.indexOf(resolver.getPrefix(),
    // startIndex + replacement.length());
    // }

    // if we know people want the noop, don't even process anything
    if (_logMessageConsumer != NULL)
    {
      VariableResolver resolver = _experiment.getVariableResolver();
      String resolved = resolver.resolveValues(_message, context);
      _logMessageConsumer.accept(resolved);
    }
  }

}
