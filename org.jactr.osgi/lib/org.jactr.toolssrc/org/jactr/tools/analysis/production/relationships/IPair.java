package org.jactr.tools.analysis.production.relationships;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.analysis.production.endstates.BufferEndState;

public interface IPair
{
  public BufferEndState getEndState();
  public CommonTree getCondition();
}
