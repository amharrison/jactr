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

import java.util.concurrent.Executor;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.event.ChunkTypeEvent;
import org.jactr.core.chunktype.event.IChunkTypeListener;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.model.IModel;

public abstract class AbstractChunkType implements IChunkType
{


  private String                                                _comment;

  private IModel                                                _model;

  private boolean                                               _isEncoded;

  protected ACTREventDispatcher<IChunkType, IChunkTypeListener> _eventDispatcher;

  public AbstractChunkType(IModel model)
  {
    super();
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

}
