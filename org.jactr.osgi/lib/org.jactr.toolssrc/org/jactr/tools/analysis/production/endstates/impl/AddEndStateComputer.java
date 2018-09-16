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
public class AddEndStateComputer implements IBufferEndStateComputer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(AddEndStateComputer.class);

  public Collection<BufferEndState> computePossibleEndStatesFor(
      BufferEndStates endStates, String bufferName, SequenceAnalyzer analyzer)
  {
    String lowerBufferName = bufferName.toLowerCase();

    Map<String, CommonTree> addActions = endStates
        .getMapOfTrees(JACTRBuilder.ADD_ACTION);

    /*
     * no add actions at all
     */
    if (!addActions.containsKey(lowerBufferName))
      return Collections.emptyList();

    CommonTree addAction = addActions.get(lowerBufferName);

    Collection<CommonTree> mSlots = BufferStateUtilities
        .getAssignments(BufferStateUtilities.getSlots(addAction));

    BufferEndState endState = new BufferEndState(bufferName);

    /*
     * we will add all the modified slots, and then all the conditional slots
     * that weren't overwritten by the modified slots
     */
    Set<String> addedSlotNames = new TreeSet<String>();
    for (CommonTree mSlot : mSlots)
    {
      String name = ASTSupport.getName(mSlot);
      addedSlotNames.add(name.toLowerCase());
      if (BufferStateUtilities.contentIsType(mSlot, JACTRBuilder.VARIABLE))
      {
        for (CommonTree expanded : BufferStateUtilities.expandVariable(name,
            BufferStateUtilities.getContent(mSlot), endStates))
          endState.addSlotHypothesis(expanded);
      }
      else
        endState.addSlotHypothesis(mSlot);
    }

    /*
     * lets check the content of the add, is it a variable, chunk or chuktype.
     * ADD_ACTION(bufferName content slots)
     */
    CommonTree content = (CommonTree) addAction.getChild(1);
    int type = content.getType();

    if (type == JACTRBuilder.CHUNK_TYPE_IDENTIFIER)
    {
      /*
       * if the add is a chunk pattern (chunk type is provided) we should add
       * any default slot values that have no already been overridden TODO add
       * default slot values from chunk type
       */
      for (CommonTree slot : analyzer.getChunkTypeSlots(content.getText()))
      {
        String slotName = ASTSupport.getName(slot).toLowerCase();
        if (!addedSlotNames.contains(slotName))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Adding chunktype slot " + slot.toStringTree());
          endState.addSlotHypothesis(slot);
          addedSlotNames.add(slotName);
        }
      }
    }
    else if (type == JACTRBuilder.CHUNK_IDENTIFIER)
    {
      if (LOGGER.isWarnEnabled())
        LOGGER.warn(" currently ignoring adds of precise chunks");
    }
    else if (type == JACTRBuilder.VARIABLE)
    {
      /*
       * a previously bound buffer..
       */
      String otherBuffer = content.getText().substring(1).toLowerCase();
      Map<String, CommonTree> matches = endStates
          .getMapOfTrees(JACTRBuilder.MATCH_CONDITION);

      if (matches.containsKey(otherBuffer))
      {

        Map<Integer, Collection<CommonTree>> allCSlots = BufferStateUtilities
            .getSlots(matches.get(otherBuffer));

        Collection<CommonTree> cSlots = BufferStateUtilities
            .getNonVariables(allCSlots);

        /*
         * first grab all the conditions for non-variable slots
         */
        for (CommonTree cSlot : cSlots)
          if (!addedSlotNames.contains(ASTSupport.getName(cSlot).toLowerCase()))
          {
            endState.addSlotHypothesis(cSlot);
            addedSlotNames.add(ASTSupport.getName(cSlot).toLowerCase());
          }

        /*
         * one last step. We ignored the variablized slots in the condition
         * above. Now we need to go through them because any variable is
         * actually the same as != null, and might also correspond to a bound
         * variable
         */
        if (allCSlots.containsKey(JACTRBuilder.VARIABLE))
          for (CommonTree cSlot : allCSlots.get(JACTRBuilder.VARIABLE))
          {
            String name = ASTSupport.getName(cSlot);
            if (!addedSlotNames.contains(name.toLowerCase()))
            {
              for (CommonTree expanded : BufferStateUtilities.expandVariable(
                  name, BufferStateUtilities.getContent(cSlot), endStates))
                endState.addSlotHypothesis(expanded);

              addedSlotNames.add(name.toLowerCase());
            }
          }
      }
    }
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No clue how to handle an add of " + content + " (" + type
          + ")");

    ArrayList<BufferEndState> rtn = new ArrayList<BufferEndState>();

    if (!endState.isEmpty()) rtn.add(endState);
    rtn.add(new SimpleBufferEndState(bufferName, "state", BufferEndState.FREE,
        BufferEndState.ERROR, BufferEndState.BUSY));
    rtn.add(new SimpleBufferEndState(bufferName, "state", BufferEndState.BUSY,
        BufferEndState.FREE, BufferEndState.ERROR));
    rtn.add(new SimpleBufferEndState(bufferName, "state", BufferEndState.ERROR,
        BufferEndState.BUSY, BufferEndState.FREE));

    return rtn;
  }
}
