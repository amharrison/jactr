package org.jactr.tools.analysis.production.endstates.impl;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.analysis.production.endstates.BufferEndState;
import org.jactr.tools.analysis.production.endstates.BufferEndStates;

public class BufferStateUtilities
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(BufferStateUtilities.class);

  /**
   * {@value #YES} means that given the values and the conditions the slots
   * have, they COULD be equivalent (but still might not be)<br>
   * {@value #NO} means that given the values and the conditions, the two slots
   * can never be equivalent <br>
   * {@value #AMBIGUOUS} means that the given values and conditions are not
   * sufficient to make any judgement.<br>
   * 
   * @author harrison
   */
  static public enum Consistent {
    YES, NO, AMBIGUOUS
  };

  /**
   * this will search through all the conditions and queries looking for more
   * information about variableName. When it finds another slot that matches to
   * that variable name, all other non-variable conditions for that slot are
   * saved and remapped to the provided slotName. ex: <br>
   * <code>
   *  (p
   *   =goal>
   *   isa fact
   *   - arg1 Z
   *   arg1 =value
   *  
   *  ==>
   *   +retrieval>
   *    isa other-fact
   *    argA =value
   *    )
   * </code>
   * <br>
   * We know that other-fact.argA cannot be null OR Z.<br>
   * Ideally this method should be recursively because some mean modeler might
   * use a whole slew of variable indirections within a single production.. but
   * that will be for version 2.
   * 
   * @param slotName
   * @param variableName
   * @param endStates
   * @return
   */
  static public Collection<CommonTree> expandVariable(String slotName,
      String variableName, BufferEndStates endStates)
  {
    Collection<CommonTree> rtn = new ArrayList<CommonTree>();
    Set<String> slotNamesToExpand = new TreeSet<String>();
    for (CommonTree condition : endStates.getMapOfTrees(
        JACTRBuilder.MATCH_CONDITION).values())
    {
      slotNamesToExpand.clear();
      Map<Integer, Collection<CommonTree>> allSlots = getSlots(condition);
      /*
       * zip through the variables to see if anyone references the equality of
       * the provided variable name..
       */

      if (allSlots.containsKey(JACTRBuilder.VARIABLE))
        for (CommonTree slot : allSlots.get(JACTRBuilder.VARIABLE))
          if (conditionIs(slot, IConditionalSlot.EQUALS)
              && variableName.equalsIgnoreCase(getContent(slot)))
            slotNamesToExpand.add(ASTSupport.getName(slot).toLowerCase());

      /*
       * now we have all the names of the slots that reference variableName
       * within this condition. We need to go through once more to grab all the
       * actual references to this slot (other than the variables)
       */
      for (CommonTree slot : getNonVariables(allSlots))
      {
        String name = ASTSupport.getName(slot);
        if (slotNamesToExpand.contains(name.toLowerCase()))
          rtn.add(BufferEndState.getSupport().createSlot(slotName,
              (CommonTree)slot.getChild(1), (CommonTree)slot.getChild(2)));
      }
    }
    
    /*
     * sometimes we cant resolve much of anything, but we do know that
     * the the slot name is not going to be null/nil...
     */
    if(rtn.size()==0)
    {
      rtn.add(BufferEndState.getSupport().createSlot(slotName,
          IConditionalSlot.NOT_EQUALS, BufferEndState.NULL));
      rtn.add(BufferEndState.getSupport().createSlot(slotName,
          IConditionalSlot.NOT_EQUALS, BufferEndState.NIL));
    }

    return rtn;
  }

  /**
   * return a map of collections of slot ASTs contained by the condition or
   * action. the map is keyed on the type of content in the slot, typically
   * {@link JACTRBuilder#IDENTIFIER}, {@link JACTRBuilder#VARIABLE}, or
   * {@link JACTRBuilder#NUMBER}
   * 
   * @param conditionOrAction
   * @return
   */
  static public Map<Integer, Collection<CommonTree>> getSlots(
      CommonTree conditionOrAction)
  {
    Map<Integer, Collection<CommonTree>> rtn = new TreeMap<Integer, Collection<CommonTree>>();
    for (CommonTree slot : ASTSupport.getTrees(conditionOrAction,
        JACTRBuilder.SLOT))
    {
      // slot(name condition content)
      int contentType = slot.getChild(2).getType();
      Collection<CommonTree> slots = rtn.get(contentType);
      if (slots == null)
      {
        slots = new ArrayList<CommonTree>();
        rtn.put(contentType, slots);
      }
      slots.add(slot);
    }
    return rtn;
  }

  /**
   * return all slots that arent variables
   * 
   * @param mapOfSlots
   * @return
   */
  static public Collection<CommonTree> getNonVariables(
      Map<Integer, Collection<CommonTree>> mapOfSlots)
  {
    ArrayList<CommonTree> rtn = new ArrayList<CommonTree>();
    for (int type : new int[] { JACTRBuilder.IDENTIFIER, JACTRBuilder.NUMBER,
        JACTRBuilder.STRING })
    {
      Collection<CommonTree> slots = mapOfSlots.get(type);
      if (slots != null) rtn.addAll(slots);
    }
    return rtn;
  }

  /**
   * return all slots that are equality conditions (aka assignments), even
   * variables
   * 
   * @param mapOfTrees
   * @return
   */
  static public Collection<CommonTree> getAssignments(
      Map<Integer, Collection<CommonTree>> mapOfTrees)
  {
    ArrayList<CommonTree> rtn = new ArrayList<CommonTree>();
    for (Collection<CommonTree> slots : mapOfTrees.values())
      for (CommonTree slot : slots)
        if (conditionIs(slot, IConditionalSlot.EQUALS)) rtn.add(slot);
    return rtn;
  }

  static public boolean contentIsType(CommonTree slot, int type)
  {
    return slot.getChild(2).getType() == type;
  }

  static public String getContent(CommonTree slot)
  {
    return slot.getChild(2).getText();
  }

  /**
   * uses {@link IConditionalSlot} condition constants, not {@link JACTRBuilder}
   * 
   * @param slot
   * @param condition
   * @return
   */
  static public boolean conditionIs(CommonTree slot, int condition)
  {
    int cond = slot.getChild(1).getType();
    if (cond == JACTRBuilder.EQUALS && condition == IConditionalSlot.EQUALS)
      return true;
    if (cond == JACTRBuilder.NOT && condition == IConditionalSlot.NOT_EQUALS)
      return true;
    if (cond == JACTRBuilder.GT && condition == IConditionalSlot.GREATER_THAN)
      return true;
    if (cond == JACTRBuilder.GTE
        && condition == IConditionalSlot.GREATER_THAN_EQUALS) return true;
    if (cond == JACTRBuilder.LT && condition == IConditionalSlot.LESS_THAN)
      return true;
    if (cond == JACTRBuilder.LTE
        && condition == IConditionalSlot.LESS_THAN_EQUALS) return true;

    return false;
  }

  /**
   * compare two slots to see if they are applicable, this is just a name check
   * 
   * @param endStateSlot
   * @param conditionalSlot
   * @return
   */
  static public boolean slotIsApplicable(CommonTree endStateSlot,
      CommonTree conditionalSlot)
  {
    return conditionalSlot.getChild(0).getText().equalsIgnoreCase(
        endStateSlot.getChild(0).getText());
  }

  /**
   * return true if the endstate slot is consistent with the conditional slot.
   * this will check the conditions on both the slots and compare their values
   * accordingly. The values should NEVER be variables in either slot.
   * similarly, before calling this,
   * {@link #slotIsApplicable(CommonTree, CommonTree)} should have returned
   * true.<br>
   * <br>
   * Currently this will return ambiguous if either slot condition is anything
   * but equals or not
   * 
   * @param endStateSlot
   * @param conditionalSlot
   * @throws IllegalStateException
   *             if either is a variable or the condition type is not recognized
   * @return {@link Consistent}
   */
  static public Consistent isConsistentWith(CommonTree endStateSlot,
      CommonTree conditionalSlot)
  {
    int endCondition = endStateSlot.getChild(1).getType();
    int queryCondition = conditionalSlot.getChild(1).getType();

    if (endStateSlot.getChild(2).getType() == JACTRBuilder.VARIABLE
        || conditionalSlot.getChild(2).getType() == JACTRBuilder.VARIABLE)
      throw new IllegalStateException(
          "neither conditional nor end state slots may be variables");

    String endContent = endStateSlot.getChild(2).getText();
    String queryContent = conditionalSlot.getChild(2).getText();

    if (endCondition == JACTRBuilder.EQUALS)
    {
      // both equal
      if (queryCondition == endCondition)
        if (endContent.equalsIgnoreCase(queryContent))
          return Consistent.YES;
        else
          return Consistent.NO;

      if (queryCondition == JACTRBuilder.NOT)
        if (endContent.equalsIgnoreCase(queryContent))
          // a=1 a!=1
          return Consistent.NO;
        else
          // a=1 a!=2
          return Consistent.YES;

      return Consistent.AMBIGUOUS;
    }
    else if (endCondition == JACTRBuilder.NOT)
    {
      // a!=2 a!=3
      if (queryCondition == endCondition) return Consistent.YES;

      if (queryCondition == JACTRBuilder.EQUALS)
        if (endContent.equalsIgnoreCase(queryContent))
          // a!=2 a=2
          return Consistent.NO;
        else
          // a!=2 a=4
          return Consistent.YES;

      return Consistent.AMBIGUOUS;
    }
    else
      return Consistent.AMBIGUOUS;
  }

}
