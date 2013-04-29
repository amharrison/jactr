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
package org.jactr.core.chunktype.basic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.chunktype.event.IChunkTypeListener;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.DefaultAdaptable;

public class DefaultChunkType extends DefaultAdaptable implements
    IChunkType
{
  private String                                                _comment;

  private IModel                                                _model;

  private boolean                                               _isEncoded;

  private Map<String, Object>                                   _metaData;

  protected ACTREventDispatcher<IChunkType, IChunkTypeListener> _eventDispatcher;

  protected ISymbolicChunkType _symbolicChunkType;

  protected ISubsymbolicChunkType _subsymbolicChunkType;

  public DefaultChunkType(IModel model)
  {
    super();
    _metaData = new TreeMap<String, Object>();
    _model = model;
    _eventDispatcher = new ACTREventDispatcher<IChunkType, IChunkTypeListener>();
  }

  synchronized public void encode()
  {
    if (_isEncoded) return;
    getSymbolicChunkType().encode();
    getSubsymbolicChunkType().encode();
    _isEncoded = true;

    if (hasListeners())
      dispatch(new ChunkTypeEvent(this, ChunkTypeEvent.Type.ENCODED));
  }

  public IModel getModel()
  {
    return _model;
  }

  public boolean isEncoded()
  {
    return _isEncoded;
  }

  public boolean hasListeners()
  {
    return _eventDispatcher.hasListeners();
  }

  /**
   * Adds a feature to the IChunkTypeListener attribute of the DefaultChunkType5
   * object
   */
  public void addListener(IChunkTypeListener cl, Executor executor)
  {
    _eventDispatcher.addListener(cl, executor);
  }

  /**
   */
  public void removeListener(IChunkTypeListener cl)
  {
    _eventDispatcher.removeListener(cl);
  }

  /**
   * Description of the Method
   */
  public void dispatch(ChunkTypeEvent event)
  {
    _eventDispatcher.fire(event);
  }

  /**
   * 
   */
  public String getComment()
  {
    return _comment;
  }

  /**
   * Sets the comment attribute of the DefaultChunkType5 object
   */
  public void setComment(String comment)
  {
    _comment = comment;
  }

  /**
   * 
   */
  public boolean isA(IChunkType ct)
  {
    if (getSymbolicChunkType() != null) return getSymbolicChunkType().isA(ct);
    return false;
  }

  /**
   * 
   */
  @Override
  public String toString()
  {
    if (getSymbolicChunkType() != null)
      return getSymbolicChunkType().getName();
    return super.toString();
  }

  /**
   * lexically compare chunktypes
   */
  public int compareTo(IChunkType o)
  {
    return getSymbolicChunkType().getName().compareTo(
        o.getSymbolicChunkType().getName());
  }

  /**
   * Description of the Method
   * 
   * @since
   */

  public void dispose()
  {
    /*
     * we do the null check since dispose might actually be called twice. first
     * by the parent chunk type and then again by the dec module
     */

    if (_eventDispatcher != null) _eventDispatcher.clear();
    _eventDispatcher = null;
    _model = null;
    _symbolicChunkType = null;
    _subsymbolicChunkType = null;
    _metaData.clear();
  }

  /**
   * Description of the Method
   * 
   * @param obj
   *            Description of Parameter
   * @return Description of the Returned Value
   * @since
   */
  public boolean equals(IChunkType obj)
  {
    return obj == this;
  }

  @Override
  public Object getAdapter(Class adapterClass)
  {
    if (ISymbolicChunkType.class.equals(adapterClass))
      return getSymbolicChunkType();
    else if (ISubsymbolicChunkType.class.equals(adapterClass))
      return getSubsymbolicChunkType();
    else
      return super.getAdapter(adapterClass);
  }

  public Object getMetaData(String key)
  {
    return _metaData.get(key);
  }

  /**
   * Sets the MetaData attribute of the MetaContainer object
   * 
   * @param key
   *          The new MetaData value
   * @param value
   *          The new MetaData value
   * @since
   */
  public void setMetaData(String key, Object value)
  {
    _metaData.put(key, value);
  }

  /**
   * return all the keys
   * 
   * @return
   */
  public Collection<String> getMetaDataKeys()
  {
    return Collections.unmodifiableCollection(_metaData.keySet());
  }

  public void bind(ISymbolicChunkType symbolic, ISubsymbolicChunkType subsymbolic)
  {
    _symbolicChunkType = symbolic;
    _subsymbolicChunkType = subsymbolic;
  }

  /**
   * Gets the symbolicChunkType attribute of the DefaultChunkType5 object
   * 
   * @return The symbolicChunkType value
   * @since
   */
  public ISymbolicChunkType getSymbolicChunkType()
  {
    return _symbolicChunkType;
  }

  /**
   * Gets the subsymbolicChunkType attribute of the DefaultChunkType5 object
   * 
   * @return The subsymbolicChunkType value
   * @since
   */
  public ISubsymbolicChunkType getSubsymbolicChunkType()
  {
    return _subsymbolicChunkType;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime
        * result
        + (_subsymbolicChunkType == null ? 0 : _subsymbolicChunkType
            .hashCode());
    result = prime * result
        + (_symbolicChunkType == null ? 0 : _symbolicChunkType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof DefaultChunkType)) return false;
    final DefaultChunkType other = (DefaultChunkType) obj;
    if (_subsymbolicChunkType == null)
    {
      if (other._subsymbolicChunkType != null) return false;
    }
    else if (!_subsymbolicChunkType.equals(other._subsymbolicChunkType))
      return false;
    if (_symbolicChunkType == null)
    {
      if (other._symbolicChunkType != null) return false;
    }
    else if (!_symbolicChunkType.equals(other._symbolicChunkType))
      return false;
    return true;
  }
}
