package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.ICondition;

public abstract class AbstractMatchFailure implements IMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AbstractMatchFailure.class);

  private ICondition                 _condition;

  public AbstractMatchFailure(ICondition condition)
  {
    setCondition(condition);
  }

  public void setCondition(ICondition condition)
  {
    _condition = condition;
  }

  public ICondition getCondition()
  {
    return _condition;
  }

}
