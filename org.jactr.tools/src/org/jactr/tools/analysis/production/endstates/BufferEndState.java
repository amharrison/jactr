package org.jactr.tools.analysis.production.endstates;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.tools.analysis.production.endstates.impl.BufferStateUtilities;

public class BufferEndState
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER   = LogFactory
                                                  .getLog(BufferEndState.class);

  static protected ASTSupport        _support = new ASTSupport();

  static public ASTSupport getSupport()
  {
    return _support;
  }

  static final public CommonTree NULL     = _support.create(
                                              JACTRBuilder.IDENTIFIER, "null");

  static final public CommonTree NIL      = _support.create(
                                              JACTRBuilder.IDENTIFIER, "nil");

  static final public CommonTree TRUE     = _support.create(
                                              JACTRBuilder.IDENTIFIER, "true");

  static final public CommonTree FALSE    = _support.create(
                                              JACTRBuilder.IDENTIFIER, "false");

  static final public CommonTree EMPTY    = _support.create(
                                              JACTRBuilder.IDENTIFIER, "empty");

  static final public CommonTree FULL     = _support.create(
                                              JACTRBuilder.IDENTIFIER, "full");

  static final public CommonTree FREE     = _support.create(
                                              JACTRBuilder.IDENTIFIER, "free");

  static final public CommonTree BUSY     = _support.create(
                                              JACTRBuilder.IDENTIFIER, "busy");

  static final public CommonTree ERROR    = _support.create(
                                              JACTRBuilder.IDENTIFIER, "error");

  private String                 _bufferName;

  private String                 _conditionedOn;

  private boolean                _isQuery = false;

  private Collection<CommonTree> _hypothesizedStates;

  public BufferEndState(String bufferName, String conditionedOn)
  {
    _bufferName = bufferName;
    _conditionedOn = conditionedOn;
    _hypothesizedStates = new ArrayList<CommonTree>();
  }

  public BufferEndState(String bufferName)
  {
    this(bufferName, null);
  }

  public boolean isQuery()
  {
    return _isQuery;
  }

  public void setIsQuery(boolean isQuery)
  {
    _isQuery = true;
  }

  public String getConditionedOn()
  {
    return _conditionedOn;
  }

  public String getBufferName()
  {
    return _bufferName;
  }

  public boolean isEmpty()
  {
    return _hypothesizedStates.isEmpty();
  }

  public void addSlotHypothesis(CommonTree slot)
  {
    _hypothesizedStates.add(slot);
  }

  /**
   * compute the relationship between the conditionOrQueries slots and this end
   * state.. note: we currently ignore chunktype..
   * 
   * @param conditionOrQuery
   * @return
   */
  public int computeRelationship(CommonTree conditionOrQuery)
  {
    int relationship = 0;
    for (CommonTree cSlot : ASTSupport.getTrees(conditionOrQuery,
        JACTRBuilder.SLOT))
    {
      for (CommonTree eSlot : _hypothesizedStates)
        if (BufferStateUtilities.slotIsApplicable(eSlot, cSlot)
            && !BufferStateUtilities
                .contentIsType(cSlot, JACTRBuilder.VARIABLE))
        {
          BufferStateUtilities.Consistent consistency = BufferStateUtilities
              .isConsistentWith(eSlot, cSlot);
          if (consistency == BufferStateUtilities.Consistent.NO)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(cSlot.toStringTree()
                  + " is inconsistent with end state " + eSlot.toStringTree());
            return -1;
          }
          else if (consistency == BufferStateUtilities.Consistent.YES)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug(cSlot.toStringTree()
                  + " is consistent with end state " + eSlot.toStringTree()
                  + ", continuing with comparisons");
            relationship++;
          }
        }
    }
    return relationship;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder("[");
    sb.append(getBufferName());
    if (_conditionedOn != null)
      sb.append("(").append(_conditionedOn).append(")");
    sb.append(":");
    for (CommonTree slot : _hypothesizedStates)
      sb.append(slot.toStringTree()).append(" ");
    sb.append("]");
    return sb.toString();
  }
}
