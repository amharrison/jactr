/*
 * Created on Jun 10, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.chunk.basic;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.event.IChunkListener;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.event.IParameterListener;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.parameter.IParameterized;

/**
 * ChunkData contains blocks of code that are common to most chunk
 * implementations. <bR>
 * <bt> TODO this class needs to use the locks it has
 * 
 * @author developer
 */
public class ChunkData
{
  private ACTREventDispatcher<IChunk, IChunkListener>             _eventDispatcher;

  private ACTREventDispatcher<IParameterized, IParameterListener> _parameterEventDispatcher;

  private boolean                                                 _hasBeenDisposed;

  private String                                                  _comment;

  private Map<String, Object>                                     _metaData;

  private boolean                                                 _isEncoded = false;

  private boolean                                                 _isMutable = false;

  private IModel                                                  _model;

  final private ReentrantReadWriteLock                            _lock      = new ReentrantReadWriteLock();

  public ChunkData(IModel model)
  {
    _parameterEventDispatcher = new ACTREventDispatcher<IParameterized, IParameterListener>();
    _eventDispatcher = new ACTREventDispatcher<IChunk, IChunkListener>();
    _metaData = new TreeMap<String, Object>();
    _model = model;
  }

  public void dispose()
  {
    _eventDispatcher.clear();
    _eventDispatcher = null;
    _parameterEventDispatcher.clear();
    _parameterEventDispatcher = null;
    _metaData = null;
    _hasBeenDisposed = true;
    _isEncoded = false;
    _isMutable = false;
    _model = null;
  }

  public ACTREventDispatcher<IParameterized, IParameterListener> getParameterDispatcher()
  {
    return _parameterEventDispatcher;
  }

  public ACTREventDispatcher<IChunk, IChunkListener> getChunkDispatcher()
  {
    return _eventDispatcher;
  }

  public Map<String, Object> getMetaData()
  {
    return _metaData;
  }

  public void setEncoded(boolean encoded)
  {
    _isEncoded = encoded;
  }

  public boolean isEncoded()
  {
    return _isEncoded;
  }

  public boolean isDisposed()
  {
    return _hasBeenDisposed;
  }

  public void setMutable(boolean mutable)
  {
    _isMutable = mutable;
  }

  public boolean isMutable()
  {
    return _isMutable;
  }

  public IModel getModel()
  {
    return _model;
  }

  public String getComment()
  {
    return _comment;
  }

  public void setComment(String comment)
  {
    _comment = comment;
  }

  public Lock readLock()
  {
    return _lock.readLock();
  }

  public Lock writeLock()
  {
    return _lock.writeLock();
  }

  public ReentrantReadWriteLock getLock()
  {
    return _lock;
  }
}
