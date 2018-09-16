package org.jactr.tools.analysis.production.relationships;

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
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.tools.analysis.production.endstates.BufferEndState;
import org.jactr.tools.analysis.production.endstates.BufferEndStates;

public class GeneralRelationshipComputer implements IRelationshipComputer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(GeneralRelationshipComputer.class);

  public IRelationship computeRelationship(BufferEndStates headEndStates,
      BufferEndStates tailEndStates)
  {
    Map<String, CommonTree> conditions = tailEndStates
        .getMapOfTrees(JACTRBuilder.MATCH_CONDITION);
    Map<String, CommonTree> queries = tailEndStates
        .getMapOfTrees(JACTRBuilder.QUERY_CONDITION);

    Map<String, Collection<IPair>> positiveConditions = new TreeMap<String, Collection<IPair>>();
    Map<String, Collection<IPair>> ambiguousConditions = new TreeMap<String, Collection<IPair>>();
    Map<String, Collection<IPair>> negativeConditions = new TreeMap<String, Collection<IPair>>();

    Set<String> buffers = new TreeSet<String>(conditions.keySet());
    buffers.addAll(queries.keySet());

    Map<String, Collection<BufferEndState>> endStateMap = headEndStates
        .getEndStates();

    DefaultRelationship relationship = new DefaultRelationship(headEndStates
        .getProduction(), tailEndStates.getProduction());

    for (String bufferName : buffers)
    {
      Collection<IPair> positive = new ArrayList<IPair>();
      positiveConditions.put(bufferName, positive);

      Collection<IPair> ambiguous = new ArrayList<IPair>();
      ambiguousConditions.put(bufferName, ambiguous);

      Collection<IPair> negative = new ArrayList<IPair>();
      negativeConditions.put(bufferName, negative);

      CommonTree condition = conditions.get(bufferName);
      if (condition != null)
        evaluateEndStates(bufferName, endStateMap.get(bufferName), condition,
            positive, ambiguous, negative);

      CommonTree query = queries.get(bufferName);
      if (query != null)
        evaluateEndStates(bufferName, endStateMap.get(bufferName), query,
            positive, ambiguous, negative);

      // minimum positive is the minim number of positive pairs for a potential
      // match
      double minimumPositive = 1;
      double divisor = 1;
      double score = 0;
      if (endStateMap.containsKey(bufferName))
        divisor = endStateMap.get(bufferName).size();

      if (query != null && condition != null) minimumPositive = 2;

      if (positive.size() >= minimumPositive)
        score = positive.size() / divisor;
      else
      {
        double ambigDivisor = Math.max(1, ambiguous.size());
        score = - negative.size() / ambigDivisor
            / divisor;
      }
      
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Computed "+bufferName+" score = "+score);
      relationship.setScore(bufferName, score);

      relationship.setAmbiguousRelationships(bufferName, ambiguous);
      relationship.setNegativeRelationships(bufferName, negative);
      relationship.setPositiveRelationships(bufferName, positive);
    }

    return relationship;
  }

  private void evaluateEndStates(String bufferName,
      Collection<BufferEndState> headEndStates, CommonTree check,
      Collection<IPair> positive, Collection<IPair> ambiguous,
      Collection<IPair> negative)
  {
    /*
     * the head production had no operations on the current buffer
     */
    if (headEndStates == null) return;
    boolean isQuery = check.getType() == JACTRBuilder.QUERY_CONDITION;

    for (BufferEndState endState : headEndStates)
      if ((endState.isQuery() && isQuery) || (!endState.isQuery() && !isQuery))
      {
        int rel = endState.computeRelationship(check);

        IPair pair = new DefaultPair(endState, check);

        if (rel > 0)
          positive.add(pair);
        else if (rel < 0)
          negative.add(pair);
        else
          ambiguous.add(pair);
      }

  }
}
