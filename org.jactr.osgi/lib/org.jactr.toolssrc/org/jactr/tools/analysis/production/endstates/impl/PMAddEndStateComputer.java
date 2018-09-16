package org.jactr.tools.analysis.production.endstates.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.analysis.production.SequenceAnalyzer;
import org.jactr.tools.analysis.production.endstates.BufferEndState;
import org.jactr.tools.analysis.production.endstates.BufferEndStates;
import org.jactr.tools.analysis.production.endstates.IBufferEndStateComputer;

/**
 * deal with add operations..
 * 
 * @author harrison
 */
public class PMAddEndStateComputer implements IBufferEndStateComputer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PMAddEndStateComputer.class);

  private Set<String>                _bufferNames;

  public PMAddEndStateComputer(String... bufferNames)
  {
    _bufferNames = new TreeSet<String>();
    for (String bufferName : bufferNames)
      _bufferNames.add(bufferName.toLowerCase());
  }

  public Collection<BufferEndState> computePossibleEndStatesFor(
      BufferEndStates endStates, String bufferName,
      SequenceAnalyzer analyzer)
  {
    String lowerBufferName = bufferName.toLowerCase();

    if (!_bufferNames.contains(lowerBufferName))
      return Collections.emptyList();

    Map<String, CommonTree> addActions = endStates
        .getMapOfTrees(JACTRBuilder.ADD_ACTION);

    /*
     * no add actions at all
     */
    if (!addActions.containsKey(lowerBufferName))
      return Collections.emptyList();

    ArrayList<BufferEndState> rtn = new ArrayList<BufferEndState>();

    /*
     * each of the three states for each of prep,proc, and exec
     */
    for (String slot : new String[] { "preparation", "processing", "execution" })
    {
      rtn.add(new SimpleBufferEndState(bufferName, slot,
          BufferEndState.FREE, BufferEndState.ERROR, BufferEndState.BUSY));
      rtn.add(new SimpleBufferEndState(bufferName, slot,
          BufferEndState.BUSY, BufferEndState.ERROR, BufferEndState.FREE));
      rtn.add(new SimpleBufferEndState(bufferName, slot,
          BufferEndState.ERROR, BufferEndState.BUSY, BufferEndState.FREE));
    }

    return rtn;
  }
}
