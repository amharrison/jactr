package org.jactr.io.compiler.impl;

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
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.compiler.AbstractReportableUnitCompiler;

/**
 * unit compiler that checks productions whenever strict harvesting will be
 * applied.
 * 
 * @author harrison
 */
public class StrictHarvestingUnitCompiler extends
    AbstractReportableUnitCompiler
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(StrictHarvestingUnitCompiler.class);

  private Set<String>                _strictBuffers;

  public StrictHarvestingUnitCompiler()
  {
    setRelevantTypes(JACTRBuilder.PRODUCTION, JACTRBuilder.BUFFER);
    _strictBuffers = new TreeSet<String>();
  }

  @Override
  protected void compile(CommonTree node)
  {
    int type = node.getType();
    if (type == JACTRBuilder.PRODUCTION)
      checkProduction(node);
    else if (type == JACTRBuilder.BUFFER) checkBuffer(node);
  }

  /**
   * we scan through all the match conditions and check for an add, remove or
   * modify. if none exists, we report the message, storing the exception for
   * later, when we will actually check the buffer and see if strict harvesting
   * is enabled or not
   * 
   * @param node
   */
  protected void checkProduction(CommonTree node)
  {
    Set<String> addedBuffers = ASTSupport.getMapOfTrees(node,
        JACTRBuilder.ADD_ACTION).keySet();
    Set<String> removedBuffers = ASTSupport.getMapOfTrees(node,
        JACTRBuilder.REMOVE_ACTION).keySet();
    Set<String> modifiedBuffers = ASTSupport.getMapOfTrees(node,
        JACTRBuilder.MODIFY_ACTION).keySet();

    for (CommonTree check : ASTSupport.getTrees(node,
        JACTRBuilder.MATCH_CONDITION))
    {
      String bufferName = ASTSupport.getName(check).toLowerCase();
      
      if(!_strictBuffers.contains(bufferName)) continue;
      if (addedBuffers.contains(bufferName)) continue;
      if (removedBuffers.contains(bufferName)) continue;
      if (modifiedBuffers.contains(bufferName)) continue;

      // no add, mod or remove.. report.
      report(
          bufferName
              + " was matched against, but not manipulated. Strict harvesting will remove after production fires",
          check, null);
    }
  }

  /**
   * now we check the parameters for this buffer, looking for
   * StrictHarvestingEnabled. if it is not true, we remove the pending
   * exceptions from the appropriate collection.
   * 
   * @param buffer
   */
  protected void checkBuffer(CommonTree buffer)
  {
    String bufferName = ASTSupport.getName(buffer).toLowerCase();

    Map<String, CommonTree> parameters = ASTSupport.getMapOfTrees(buffer,
        JACTRBuilder.PARAMETER);

    CommonTree parameter = parameters.get("strictharvestingenabled");

    /*
     * ignoring goal (never strict)
     * not defined, assume true
     */
    if (!bufferName.equals("goal")
        && ((parameter == null) || Boolean.parseBoolean(parameter.getChild(1)
            .getText())))
    {
      _strictBuffers.add(bufferName);
    }
    else
    {
      _strictBuffers.remove(bufferName);
    }
  }
}
