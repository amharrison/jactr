package org.jactr.tools.analysis.production.endstates;

/*
 * default logging
 */
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.tools.analysis.production.SequenceAnalyzer;

public interface IBufferEndStateComputer
{

  /**
   * 
   * return a collection of all the possible buffer end states for a given production and buffer. The production sequence analyzer
   * will handle the adding of the end states to the buffer end states structure. It should NOT be done
   * by the computer.
   * @param endStates contains the production ast, as well as cached maps of the actions and conditions
   * @param bufferName
   * @return
   */
  public Collection<BufferEndState> computePossibleEndStatesFor(BufferEndStates endStates, String bufferName, SequenceAnalyzer analyzer); 
}
