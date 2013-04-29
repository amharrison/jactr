package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.IBufferCondition;

public class EmptyBufferMatchFailure extends AbstractMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EmptyBufferMatchFailure.class);

  public EmptyBufferMatchFailure(IBufferCondition condition)
  {
    super(condition);
  }

  @Override
  public String toString()
  {
    return String.format("%s is empty, cannot match.",
        ((IBufferCondition) getCondition()).getBufferName());
  }
}
