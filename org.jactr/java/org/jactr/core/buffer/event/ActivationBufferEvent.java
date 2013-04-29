/*
 * Created on Oct 13, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.buffer.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.runtime.ACTRRuntime;

public class ActivationBufferEvent extends
    AbstractACTREvent<IActivationBuffer, IActivationBufferListener> implements
    IParameterEvent<IActivationBuffer, IActivationBufferListener>
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(ActivationBufferEvent.class);

  static public enum Type {
    SOURCE_ADDED, SOURCE_REMOVED, SOURCE_CLEARED, REQUEST_ACCEPTED, PARAMETER_CHANGED, STATUS_SLOT_CHANGED, CHUNK_MATCHED
  }

  private Collection<IChunk> _sourceChunks;

  private IRequest       _request;

  /**
   * for parameter/status slot changes
   */
  private String             _itemName;

  private Object             _newValue;

  private Object             _oldValue;

  private Type               _type;

  @SuppressWarnings("unchecked")
  protected ActivationBufferEvent(IActivationBuffer buffer, Type type)
  {
    super(buffer, ACTRRuntime.getRuntime().getClock(buffer.getModel())
        .getTime());
    _type = type;
    _sourceChunks = Collections.EMPTY_LIST;
  }

  /**
   * for SOURCE_CLEARED
   * 
   * @param buffer
   * @param clearedChunks
   */
  public ActivationBufferEvent(IActivationBuffer buffer,
      Collection<IChunk> clearedChunks)
  {
    this(buffer, Type.SOURCE_CLEARED);
    _sourceChunks = Collections.unmodifiableCollection(new ArrayList<IChunk>(clearedChunks));
  }

  /**
   * for SOURCE_ADDED & REMOVED
   * 
   * @param buffer
   * @param type
   * @param sourceChunk
   */
  public ActivationBufferEvent(IActivationBuffer buffer, Type type,
      IChunk sourceChunk)
  {
    this(buffer, type);
    _sourceChunks = Collections.unmodifiableCollection(Collections.singleton(sourceChunk));
  }

  public ActivationBufferEvent(IActivationBuffer buffer, IRequest request)
  {
    this(buffer, Type.REQUEST_ACCEPTED);
    _request = request;
  }

  /**
   * for PARAMETER_CHANGED or STATUS_SLOT_CHANGED
   */
  public ActivationBufferEvent(IActivationBuffer buffer, Type type,
      String name, Object oldValue, Object newValue)
  {
    this(buffer, type);
    _itemName = name;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  public Type getType()
  {
    return _type;
  }

  public Collection<IChunk> getSourceChunks()
  {
    return _sourceChunks;
  }

  public IRequest getRequest()
  {
    return _request;
  }

  @Override
  public void fire(IActivationBufferListener listener)
  {
    switch (getType())
    {
      case PARAMETER_CHANGED:
        listener.parameterChanged(this);
        break;
      case REQUEST_ACCEPTED:
        listener.requestAccepted(this);
        break;
      case SOURCE_ADDED:
        listener.sourceChunkAdded(this);
        break;
      case SOURCE_REMOVED:
        listener.sourceChunkRemoved(this);
        break;
      case SOURCE_CLEARED:
        listener.sourceChunksCleared(this);
        break;
      case STATUS_SLOT_CHANGED:
        listener.statusSlotChanged(this);
        break;
      case CHUNK_MATCHED:
        listener.chunkMatched(this);
        break;
      default:
        LOGGER.warn("No clue what to do with event type " + this.getType());
    }

  }

  public String getSlotName()
  {
    return _itemName;
  }

  public Object getOldSlotValue()
  {
    return _oldValue;
  }

  public Object getNewSlotValue()
  {
    return _newValue;
  }

  public Object getNewParameterValue()
  {
    return _newValue;
  }

  public Object getOldParameterValue()
  {
    return _oldValue;
  }

  public String getParameterName()
  {
    return _itemName;
  }

}
