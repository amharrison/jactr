/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.declarative.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.event.AbstractACTREvent;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.runtime.ACTRRuntime;

public class DeclarativeModuleEvent extends
    AbstractACTREvent<IDeclarativeModule, IDeclarativeModuleListener> implements
    IParameterEvent<IDeclarativeModule, IDeclarativeModuleListener>
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(DeclarativeModuleEvent.class);

  static public enum Type {
    CHUNK_TYPE_CREATED, CHUNK_TYPE_ADDED, CHUNK_TYPES_MERGED, CHUNK_CREATED, CHUNK_ADDED, CHUNKS_MERGED, CHUNK_REMOVED, CHUNK_TYPE_REMOVED, CHUNK_DISPOSED, CHUNK_TYPE_DISPOSED, PARAMETER_CHANGED
  };

  protected Type                   _type;

  protected Collection<IChunk>     _chunks;

  protected Collection<IChunkType> _chunkTypes;

  protected String                 _parameterName;

  protected Object                 _oldValue;

  protected Object                 _newValue;

  protected DeclarativeModuleEvent(IDeclarativeModule source)
  {
    super(source);
    setSimulationTime(ACTRRuntime.getRuntime().getClock(source.getModel())
        .getTime());
    _chunks = new ArrayList<IChunk>(5);
    _chunkTypes = new ArrayList<IChunkType>(5);
  }

  public DeclarativeModuleEvent(IDeclarativeModule source, Type type,
      IChunkType chunkType)
  {
    this(source);
    _type = type;
    _chunkTypes.add(chunkType);
  }

  public DeclarativeModuleEvent(IDeclarativeModule source, Type type,
      IChunk chunk)
  {
    this(source);
    _type = type;
    _chunks.add(chunk);
  }

  public DeclarativeModuleEvent(IDeclarativeModule source, Type type,
      IChunk... chunks)
  {
    this(source);
    _type = type;
    for (IChunk chunk : chunks)
      _chunks.add(chunk);
  }

  public DeclarativeModuleEvent(IDeclarativeModule source, Type type,
      IChunkType... chunkTypes)
  {
    this(source);
    _type = type;
    for (IChunkType chunkType : chunkTypes)
      _chunkTypes.add(chunkType);
  }

  public DeclarativeModuleEvent(IDeclarativeModule source,
      String parameterName, Object oldValue, Object newValue)
  {
    this(source);
    _type = Type.PARAMETER_CHANGED;
    _parameterName = parameterName;
    _oldValue = oldValue;
    _newValue = newValue;
  }

  public Type getType()
  {
    return _type;
  }

  public IChunk getChunk()
  {
    return _chunks.iterator().next();
  }

  public IChunkType getChunkType()
  {
    return _chunkTypes.iterator().next();
  }

  public Collection<IChunk> getChunks()
  {
    return Collections.unmodifiableCollection(_chunks);
  }

  public Collection<IChunkType> getChunkTypes()
  {
    return Collections.unmodifiableCollection(_chunkTypes);
  }

  @Override
  public void fire(final IDeclarativeModuleListener listener)
  {
    switch (this.getType())
    {
      case CHUNK_CREATED:
        listener.chunkCreated(this);
        break;
      case CHUNK_ADDED:
        listener.chunkAdded(this);
        break;
      case CHUNK_REMOVED:
        if (listener instanceof IDeclarativeModuleListener2)
          ((IDeclarativeModuleListener2) listener).chunkRemoved(this);
        break;
      case CHUNKS_MERGED:
        listener.chunksMerged(this);
        break;
      case CHUNK_DISPOSED:
        listener.chunkDisposed(this);
        break;
      case CHUNK_TYPE_CREATED:
        listener.chunkTypeCreated(this);
        break;
      case CHUNK_TYPE_ADDED:
        listener.chunkTypeAdded(this);
        break;
      case CHUNK_TYPE_REMOVED:
        if (listener instanceof IDeclarativeModuleListener2)
          ((IDeclarativeModuleListener2) listener).chunkTypeRemoved(this);
        break;
      case CHUNK_TYPES_MERGED:
        listener.chunkTypesMerged(this);
        break;
      case CHUNK_TYPE_DISPOSED:
        listener.chunkTypeDisposed(this);
        break;
      case PARAMETER_CHANGED:
        listener.parameterChanged(this);
        break;
      default:
        if (LOGGER.isWarnEnabled())
          LOGGER.warn("No clue what to do with event type " + this.getType());
    }
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
    return _parameterName;
  }
}
