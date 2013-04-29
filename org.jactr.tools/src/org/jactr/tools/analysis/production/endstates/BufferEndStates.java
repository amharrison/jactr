package org.jactr.tools.analysis.production.endstates;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;

public class BufferEndStates
{
  /**
   * Logger definition
   */
  static private final transient Log              LOGGER       = LogFactory
                                                                   .getLog(BufferEndStates.class);

  static int[]                                    _commonTypes = new int[] {
      JACTRBuilder.QUERY_CONDITION, JACTRBuilder.MATCH_CONDITION,
      JACTRBuilder.ADD_ACTION, JACTRBuilder.MODIFY_ACTION,
      JACTRBuilder.REMOVE_ACTION                              };

  private Map<String, Collection<BufferEndState>> _bufferEndStates;

  private Map<Integer, Map<String, CommonTree>>   _commonMaps;
  
  private CommonTree _production;

  public BufferEndStates(CommonTree production)
  {
    _production = production;
    _bufferEndStates = new TreeMap<String, Collection<BufferEndState>>();

    _commonMaps = new TreeMap<Integer, Map<String, CommonTree>>();
    for(int type : _commonTypes)
      _commonMaps.put(type, ASTSupport.getMapOfTrees(production, type));
  }
  
  public CommonTree getProduction()
  {
    return _production;
  }

  public void addEndState(BufferEndState endState)
  {
    String bufferName = endState.getBufferName().toLowerCase();
    if (!_bufferEndStates.containsKey(bufferName))
      _bufferEndStates.put(bufferName, new ArrayList<BufferEndState>());
    _bufferEndStates.get(bufferName).add(endState);
  }
  
  public Map<String, Collection<BufferEndState>> getEndStates()
  {
    return Collections.unmodifiableMap(_bufferEndStates);
  }

  /**
   * return the map of asts for all the default conditions (query & match) and 
   * actions (add, remove, modify) so that they need not be calculated every time
   * by every buffer state computer
   * @param type
   * @return
   */
  public Map<String, CommonTree> getMapOfTrees(int type)
  {
    return _commonMaps.get(type);
  }
}
