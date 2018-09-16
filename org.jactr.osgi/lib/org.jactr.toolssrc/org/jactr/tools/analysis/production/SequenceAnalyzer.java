package org.jactr.tools.analysis.production;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.BufferUtilities;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.antlr3.parser.xml.JACTRParser.declarativeMemory_return;
import org.jactr.tools.analysis.production.endstates.BufferEndState;
import org.jactr.tools.analysis.production.endstates.BufferEndStates;
import org.jactr.tools.analysis.production.endstates.IBufferEndStateComputer;
import org.jactr.tools.analysis.production.endstates.impl.BufferStateUtilities;
import org.jactr.tools.analysis.production.relationships.IRelationship;
import org.jactr.tools.analysis.production.relationships.IRelationshipComputer;
import org.jactr.tools.analysis.production.relationships.ProductionRelationships;

public class SequenceAnalyzer
{
  /**
   * Logger definition
   */
  static private final transient Log                 LOGGER = LogFactory
                                                                .getLog(SequenceAnalyzer.class);

  /**
   * tracks chunktypes (key) and all their children
   */
  protected Map<String, Set<String>>                 _chunkTypeChildren;

  protected Map<String, CommonTree>                  _chunkTypes;

  protected Map<CommonTree, ProductionRelationships> _relationships;

  protected Collection<IBufferEndStateComputer>      _endStateComputers;

  protected Collection<IRelationshipComputer>        _relationshipComputers;

  public SequenceAnalyzer()
  {
    _chunkTypeChildren = new TreeMap<String, Set<String>>();
    _chunkTypes = new TreeMap<String, CommonTree>();
    _relationships = new HashMap<CommonTree, ProductionRelationships>();
    _endStateComputers = new ArrayList<IBufferEndStateComputer>();
    _relationshipComputers = new ArrayList<IRelationshipComputer>();
  }

  public void reset()
  {
    _chunkTypeChildren.clear();
    _relationships.clear();
  }

  public void add(IBufferEndStateComputer computer)
  {
    _endStateComputers.add(computer);
  }

  public void add(IRelationshipComputer computer)
  {
    _relationshipComputers.add(computer);
  }
  
  public ProductionRelationships getRelationships(CommonTree production)
  {
    return _relationships.get(production);
  }
  
  public Map<CommonTree, ProductionRelationships> getAllRelationships()
  {
    return new HashMap<CommonTree, ProductionRelationships>(_relationships);
  }

  /**
   * we track all the chunktypes and their children so that we can check
   * chunktype conditions
   * 
   * @param chunkType
   */
  public void addChunkType(CommonTree chunkType)
  {
    String chunkTypeName = ASTSupport.getName(chunkType).toLowerCase();

    _chunkTypes.put(chunkTypeName, chunkType);

    CommonTree parent = ASTSupport.getFirstDescendantWithType(chunkType,
        JACTRBuilder.PARENT);

    if (parent == null)
    {
      if (!_chunkTypeChildren.containsKey(chunkTypeName))
        _chunkTypeChildren.put(chunkTypeName, new TreeSet<String>());
    }
    else
    {
      String parentName = parent.getText().toLowerCase();
      if (!_chunkTypeChildren.containsKey(parentName))
        _chunkTypeChildren.put(parentName, new TreeSet<String>());
      _chunkTypeChildren.get(parentName).add(chunkTypeName);
    }
  }

  public void addProduction(CommonTree production)
  {
    String productionName = ASTSupport.getName(production).toLowerCase();
    BufferEndStates endStates = computeBufferEndStates(production);
    ProductionRelationships relations = new ProductionRelationships(
        productionName, endStates);
    _relationships.put(production, relations);

    for (Map.Entry<CommonTree, ProductionRelationships> entry : _relationships
        .entrySet())
    {
      CommonTree other = entry.getKey();
      ProductionRelationships otherRels = entry.getValue();

      /*
       * we compute the relationships in both directions, adding the
       * relationships to each (but only once if we are comparing to ourselves)
       */
      for (IRelationship relation : computeRelationship(production, other))
      {
        relations.addRelationship(relation);
        // no need to add twice, we'll do it again below
        if (production != other) otherRels.addRelationship(relation);
      }

      /*
       * and the other direction
       */
      for (IRelationship relation : computeRelationship(other, production))
      {
        relations.addRelationship(relation);
        // no need to add twice, we already added above
        if (production != other) otherRels.addRelationship(relation);
      }
    }
  }

  protected BufferEndStates computeBufferEndStates(CommonTree production)
  {
    String productionName = ASTSupport.getName(production);

    Set<String> bufferConditionNames = new TreeSet<String>();

    for (String bufferName : ASTSupport.getMapOfTrees(production,
        JACTRBuilder.QUERY_CONDITION).keySet())
      bufferConditionNames.add(bufferName);

    for (String bufferName : ASTSupport.getMapOfTrees(production,
        JACTRBuilder.MATCH_CONDITION).keySet())
      bufferConditionNames.add(bufferName);

    BufferEndStates endStates = new BufferEndStates(production);

    /*
     * we compute the buffer end states for each of the buffers checked in the
     * lhs
     */
    for (String bufferName : bufferConditionNames)
      for (IBufferEndStateComputer computer : _endStateComputers)
        for (BufferEndState endState : computer.computePossibleEndStatesFor(
            endStates, bufferName, this))
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(productionName + " end state for " + bufferName
                + " = " + endState);
          endStates.addEndState(endState);
        }

    return endStates;
  }

  protected Collection<IRelationship> computeRelationship(CommonTree head,
      CommonTree tail)
  {
    Collection<IRelationship> rtn = new ArrayList<IRelationship>();
    for (IRelationshipComputer computer : _relationshipComputers)
    {
      IRelationship relationship = computer.computeRelationship(_relationships
          .get(head).getEndStates(), _relationships.get(tail).getEndStates());

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(ASTSupport.getName(head) + " " + relationship.getScore()
            + " " + ASTSupport.getName(tail));

      rtn.add(relationship);
    }

    return rtn;
  }

  public Collection<CommonTree> getChunkTypeSlots(String chunkTypeName)
  {
    Map<String, CommonTree> slots = new TreeMap<String, CommonTree>();
    while (chunkTypeName != null)
    {
      CommonTree chunkType = _chunkTypes.get(chunkTypeName.toLowerCase());
      if (chunkType == null)
      {
        chunkTypeName = null;
        continue;
      }

      /*
       * snag the named slots contained in the first slots container in the
       * chunk type. We cant just use getMapOfTrees(chunkType, SLOT) as that
       * will traverse all the chunks too
       */
      Map<String, CommonTree> cSlots = ASTSupport.getMapOfTrees(ASTSupport
          .getFirstDescendantWithType(chunkType, JACTRBuilder.SLOTS),
          JACTRBuilder.SLOT);

      for (String slotName : cSlots.keySet())
        if (!slots.containsKey(slotName))
        {
          CommonTree slot = cSlots.get(slotName);
          String content = BufferStateUtilities.getContent(slot);
          if (!content.equalsIgnoreCase("nil")
              && !content.equalsIgnoreCase("null"))
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("chunk type " + chunkTypeName + " contributed "
                  + slot.toStringTree());
            slots.put(slotName, slot);
          }
        }

      /*
       * the second child, if it is a PARENT is the name of the parent
       */
      Tree node = chunkType.getChild(1);
      if (node.getType() == JACTRBuilder.PARENT)
        chunkTypeName = node.getText();
      else
        chunkTypeName = null; // terminate
    }

    return slots.values();
  }
}
