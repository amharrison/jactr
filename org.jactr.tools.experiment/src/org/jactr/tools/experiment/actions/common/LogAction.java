package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */

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
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LogAction.class);

  private boolean                    _toErr = false;

  private final String               _message;

  private final IExperiment          _experiment;

  public LogAction(String message, IExperiment experiment)
  {
    this(message, experiment, false);
  }
  
  public LogAction(String message, IExperiment experiment, boolean logError)
  {
    _message = message;
    _experiment = experiment;
    _toErr = logError;
  }

  public void fire(IVariableContext context)
  {
    VariableResolver resolver = _experiment.getVariableResolver();
    StringBuilder sb = new StringBuilder(_message);
    int startIndex = sb.indexOf(resolver.getPrefix(), 0);
    while (startIndex != -1)
    {
      int endIndex = sb.indexOf(resolver.getSuffix(), startIndex);
      if (endIndex == -1) break;
      String replacement = resolver.resolve(
          sb.substring(startIndex, endIndex + 1), context).toString();
      sb.replace(startIndex, endIndex + 1, replacement);
      startIndex = sb.indexOf(resolver.getPrefix(),
          startIndex + replacement.length());
    }

    String str = sb.toString();
    
    if (_toErr)
      LOGGER.error(str);

      System.out.println(str);
  }

}
