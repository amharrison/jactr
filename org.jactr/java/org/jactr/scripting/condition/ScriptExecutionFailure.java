package org.jactr.scripting.condition;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.match.AbstractMatchFailure;
import org.jactr.core.production.condition.match.IMatchFailure;

/**
 * should be used when scripts evaluate to false or otherwise fail (but not
 * because of another {@link IMatchFailure} that better describes the cause.
 * 
 * @author harrison
 */
public class ScriptExecutionFailure extends AbstractMatchFailure
{

  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ScriptExecutionFailure.class);

  private final String               _message;

  public ScriptExecutionFailure(String message)
  {
    super(null);
    _message = message;
  }

  @Override
  public String toString()
  {
    return _message;
  }
}
