package org.jactr.tools.analysis.production.relationships;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.analysis.production.endstates.BufferEndState;

public class DefaultPair implements IPair
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultPair.class);

  final private CommonTree _condition;
  final private BufferEndState _endState;
  
  public DefaultPair(BufferEndState endState, CommonTree condition)
  {
    _condition = condition;
    _endState = endState;
  }
  
  public CommonTree getCondition()
  {
    return _condition;
  }

  public BufferEndState getEndState()
  {
    return _endState;
  }

}
