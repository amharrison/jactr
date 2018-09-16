package org.jactr.tools.analysis.production.endstates.impl;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

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
 * creates an end state for productions that have an explicit remove, OR a match
 * but no modify. The end state returned is a query end state
 * 
 * @author harrison
 */
public class RemovalEndStateComputer implements IBufferEndStateComputer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RemovalEndStateComputer.class);

  public Collection<BufferEndState> computePossibleEndStatesFor(
      BufferEndStates endStates, String bufferName,
      SequenceAnalyzer analyzer)
  {
    String lowerBufferName = bufferName.toLowerCase();

    Map<String, CommonTree> actions = endStates
        .getMapOfTrees(JACTRBuilder.REMOVE_ACTION);

    if (actions.containsKey(lowerBufferName))
    {
      /*
       * need to check for an add too.. in case they did -retrieval +retrieval
       */
      Map<String, CommonTree> addActions = endStates
          .getMapOfTrees(JACTRBuilder.ADD_ACTION);

      if (!addActions.containsKey(lowerBufferName))
        return Collections.singleton((BufferEndState) new SimpleBufferEndState(
            bufferName, "buffer", BufferEndState.EMPTY, BufferEndState.FULL));

      /*
       * there is an add!! the bastard. we will ignore it and hope that the
       * AddEndStateComputer can handle it..
       */
      return Collections.emptyList();
    }

    /*
     * no removal on this buffer, dig deeper by checking for match and modify
     */
    /*
     * goal never has strict harvesting enabled
     */
    if (lowerBufferName.equals("goal")) return Collections.emptyList();

    actions = endStates.getMapOfTrees(JACTRBuilder.MODIFY_ACTION);
    if (actions.containsKey(lowerBufferName)
        && endStates.getMapOfTrees(JACTRBuilder.MATCH_CONDITION).containsKey(
            lowerBufferName))
      return Collections.singleton((BufferEndState) new SimpleBufferEndState(
          bufferName, "If " + bufferName + ".StrictHaverstingEnabled = true",
          "buffer", BufferEndState.EMPTY, BufferEndState.FULL));

    return Collections.emptyList();
  }
}
