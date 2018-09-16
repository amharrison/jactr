package org.jactr.tools.analysis.production.endstates.impl;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.analysis.production.SequenceAnalyzer;
import org.jactr.tools.analysis.production.endstates.BufferEndState;
import org.jactr.tools.analysis.production.endstates.BufferEndStates;
import org.jactr.tools.analysis.production.endstates.IBufferEndStateComputer;

/**
 * computes the end state for a given buffer where the action is a modify and
 * possibly a condition (well, there'd better be a condition).
 * 
 * @author harrison
 */
public class ModifyEndStateComputer implements IBufferEndStateComputer
{

  public Collection<BufferEndState> computePossibleEndStatesFor(
      BufferEndStates endStates, String bufferName, SequenceAnalyzer analyzer)
  {
    String lowerBufferName = bufferName.toLowerCase();

    CommonTree modify = endStates.getMapOfTrees(JACTRBuilder.MODIFY_ACTION)
        .get(lowerBufferName);
    if (modify == null) return Collections.emptyList();

    CommonTree condition = endStates
        .getMapOfTrees(JACTRBuilder.MATCH_CONDITION).get(lowerBufferName);
    if (condition == null) return Collections.emptyList();

    /*
     * now we snag all the non variable slots
     */
    Map<Integer, Collection<CommonTree>> allCSlots = BufferStateUtilities
        .getSlots(condition);
    Collection<CommonTree> cSlots = BufferStateUtilities
        .getNonVariables(allCSlots);
    Collection<CommonTree> mSlots = BufferStateUtilities
        .getAssignments(BufferStateUtilities.getSlots(modify));

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

    for (CommonTree cSlot : cSlots)
      if (!addedSlotNames.contains(ASTSupport.getName(cSlot).toLowerCase()))
      {
        endState.addSlotHypothesis(cSlot);
        addedSlotNames.add(ASTSupport.getName(cSlot).toLowerCase());
      }

    /*
     * one last step. We ignored the variablized slots in the condition above.
     * Now we need to go through them because any variable is actually the same
     * as != null
     */
    if (allCSlots.containsKey(JACTRBuilder.VARIABLE))
      for (CommonTree cSlot : allCSlots.get(JACTRBuilder.VARIABLE))
      {
        String name = ASTSupport.getName(cSlot);
        if (!addedSlotNames.contains(name.toLowerCase()))
        {
          for (CommonTree expanded : BufferStateUtilities.expandVariable(name,
              BufferStateUtilities.getContent(cSlot), endStates))
             endState.addSlotHypothesis(expanded);

          addedSlotNames.add(name.toLowerCase());
        }
      }

    if (endState.isEmpty()) return Collections.emptyList();

    return Collections.singleton(endState);
  }

}
