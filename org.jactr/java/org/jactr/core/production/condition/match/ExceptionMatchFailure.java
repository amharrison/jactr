package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.ICondition;

public class ExceptionMatchFailure extends AbstractMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ExceptionMatchFailure.class);

  private final Throwable            _thrown;

  private final String               _throwersName;

  public ExceptionMatchFailure(ICondition condition, String throwersName,
      Throwable thrown)
  {
    super(condition);
    _thrown = thrown;
    _throwersName = throwersName;
  }

  public String getThrowersName()
  {
    return _throwersName;
  }

  public Throwable getExcpetion()
  {
    return _thrown;
  }

  @Override
  public String toString()
  {
    return String.format("%s threw an exception %s", _throwersName,
        _thrown.getMessage());
  }

}
