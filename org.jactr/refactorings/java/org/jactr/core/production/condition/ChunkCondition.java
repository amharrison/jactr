/*
 * Created on Sep 22, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.production.condition;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.model.IModel;
import org.jactr.core.slot.ISlot;

/**
 * match the specifics of a chunk in a named buffer
 * 
 * @author harrison
 */
public class ChunkCondition extends ChunkTypeCondition
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ChunkCondition.class);

  private IChunk                     _chunk;

  
  public ChunkCondition(String bufferName, IChunk chunk)
  {
    this(bufferName, chunk, chunk.getSymbolicChunk().getSlots());
  }
  /**
   * @param bufferName
   */
  public ChunkCondition(String bufferName, IChunk chunk, Collection<? extends ISlot> slots)
  {
    super(bufferName, chunk.getSymbolicChunk().getChunkType(), slots);
    _chunk = chunk;
  }

  /**
   * @see org.jactr.core.utils.Duplicateable#duplicate()
   */
  public IChunk getChunk()
  {
    return _chunk;
  }
  
  
  public ChunkCondition clone(IModel model, Map<String,Object> bindings) throws CannotMatchException
  {
    /*
     * check the provisional chunk binding
     */
    IChunk testChunk = (IChunk) bindings.get("="+getBufferName());
    
    if(testChunk==null)
      throw new CannotMatchException("No chunk to match in  "+getBufferName());
    
    return new ChunkCondition(getBufferName(), getChunk(), getRequest().getSlots());
  }

  /**
   * bind and create a new ChunkCondition with the variables fully resolved
   * 
   * @see org.jactr.core.production.condition.ICondition#bind(org.jactr.core.model.IModel,
   *      Map)
   */
//  public ICondition bind(IModel model, Map<String, Object> variableBindings)
//      throws CannotMatchException
//  {
//    IActivationBuffer buffer = getActivationBuffer(model);
//    IChunk equalivalentChunk = buffer.contains(_chunk);
//    if (equalivalentChunk==null)
//      throw new CannotMatchException(getBufferName() + " does not contain " +
//          _chunk);
//
//    /*
//     * @bug if this condition had status slots, we wouldn't check them..
//     */
//    if (LOGGER.isDebugEnabled())
//      LOGGER.debug("Binding chunkCondition " + equalivalentChunk + " bindings " +
//          variableBindings);
//    
//    Collection<IConditionalSlot> slots = new ArrayList<IConditionalSlot>(getConditionalSlots());
//    if(buffer instanceof IStatusBuffer)
//      ((IStatusBuffer)buffer).matchesStatus(slots, variableBindings, false);
//      
//
//    ChunkCondition cc = new ChunkCondition(getBufferName(), equalivalentChunk);
//    cc.duplicateSlots(slots);
//    cc.checkAndBindSlots(equalivalentChunk, variableBindings);
//    variableBindings.put("=" + getBufferName().toLowerCase(), equalivalentChunk);
//
//    return cc;
//  }
  
  public int bind(IModel model, Map<String, Object> variableBindings, boolean isIterative) throws CannotMatchException
  {
    /*
     * check the provisional chunk binding
     */
    IChunk testChunk = (IChunk) variableBindings.get("="+getBufferName());
    
    if(testChunk==null)
      throw new CannotMatchException("No chunk to match in  "+getBufferName());
    
    return getRequest().bind(testChunk, model, variableBindings, isIterative);
  }

}
