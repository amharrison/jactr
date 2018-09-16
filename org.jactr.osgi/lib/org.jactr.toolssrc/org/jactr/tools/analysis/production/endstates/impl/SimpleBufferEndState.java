package org.jactr.tools.analysis.production.endstates.impl;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.tools.analysis.production.endstates.BufferEndState;

/**
 * 
 * 
 * @author harrison
 */
public class SimpleBufferEndState extends BufferEndState
{

  /**
   * first commontree is the equality, everything else is the not
   * @param bufferName
   * @param slotName
   * @param content
   */
  public SimpleBufferEndState(String bufferName, String conditionedOn, String slotName, CommonTree ... content)
  {
    super(bufferName, conditionedOn);
    
    setIsQuery(true);
    /*
     * if the buffer is empty, or not full
     */
    addSlotHypothesis(_support.createSlot(slotName, IConditionalSlot.EQUALS,
        content[0]));
    
    for(int i=1; i<content.length;i++)
      addSlotHypothesis(_support.createSlot(slotName, IConditionalSlot.NOT_EQUALS,
          content[i]));
  }
  
  public SimpleBufferEndState(String bufferName, String slotName, CommonTree ... content)
  {
    this(bufferName, null, slotName, content);
  }
}
