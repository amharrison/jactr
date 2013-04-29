/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.chunk.five;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISubsymbolicChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunk.IllegalChunkStateException;
import org.jactr.core.chunk.basic.AbstractChunk;
import org.jactr.core.chunk.basic.BasicSymbolicChunk;
import org.jactr.core.chunk.event.ChunkEvent;
import org.jactr.core.chunktype.IChunkType;

/**
 * The DefaulChunk class implements the basic structure for the IChunk
 * interface.
 * 
 * @author harrison
 * @created December 6, 2002
 */
public class DefaultChunk5 extends AbstractChunk
{

  /**
   * Description of the Field
   * 
   * @since
   */
  protected ISubsymbolicChunk  _subsymbolicChunk;

  /**
   * Description of the Field
   * 
   * @since
   */
  protected ISymbolicChunk     _symbolicChunk;

  /**
   * Constructor for the DefaultChunk5 object
   */
  public DefaultChunk5(IChunkType parentChunkType)
  {
    super(parentChunkType.getModel());
    setSymbolicChunk(new BasicSymbolicChunk(this, parentChunkType));
    setSubsymbolicChunk(new DefaultSubsymbolicChunk5(this));
  }

  /**
   * Sets the ISubsymbolicChunk attribute of the DefaultChunk5 object
   * 
   * @param ssc
   *            The new ISubsymbolicChunk value
   * @since
   */
  protected void setSubsymbolicChunk(ISubsymbolicChunk ssc)
  {
    _subsymbolicChunk = ssc;
  }

  /**
   * Sets the ISymbolicChunk attribute of the DefaultChunk5 object
   * 
   * @param sc
   *            The new ISymbolicChunk value
   * @since
   */
  protected void setSymbolicChunk(ISymbolicChunk sc)
  {
    _symbolicChunk = sc;
  }

  /**
   * Gets the ISubsymbolicChunk attribute of the DefaultChunk5 object
   * 
   * @return The ISubsymbolicChunk value
   * @since
   */
  @Override
  public ISubsymbolicChunk getSubsymbolicChunk()
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    return _subsymbolicChunk;
  }

  /**
   * Gets the ISymbolicChunk attribute of the DefaultChunk5 object
   * 
   * @return The ISymbolicChunk value
   * @since
   */
  @Override
  public ISymbolicChunk getSymbolicChunk()
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    return _symbolicChunk;
  }

  /**
   * dispatch events from this chunk outward - events are only dispatched if
   * this chunk is valid - i.e. has both fully constructed symbolic and
   * subsymbolic components. this means that chunktype, slot_add, slot_remove
   * events will never get passed and are therefor deprecated
   */
  @Override
  public void dispatch(ChunkEvent event)
  {
    if (hasBeenDisposed())
      throw new IllegalChunkStateException(this + " has been disposed!");
    if (_symbolicChunk != null && _subsymbolicChunk != null)
      super.dispatch(event);
  }

  /**
   * Description of the Method
   * 
   * @return Description of the Returned Value
   * @since
   */
  @Override
  public String toString()
  {
    if (_symbolicChunk == null) return super.toString();

    return _symbolicChunk.getName();
  }

  /**
   * Description of the Method
   * 
   * @param mergie
   *            Description of the Parameter
   */

  @Override
  public void replaceContents(IChunk masterChunk)
  {
    super.replaceContents(masterChunk);
    /*
     * dispose of the old contents
     */
    _symbolicChunk.dispose();
    _subsymbolicChunk.dispose();

    /*
     * and snag the new contents
     */
    _symbolicChunk = masterChunk.getSymbolicChunk();
    _subsymbolicChunk = masterChunk.getSubsymbolicChunk();
  }

  /**
   * Description of the Method
   * 
   * @since
   */
  @Override
  synchronized public void dispose()
  {
    super.dispose();

    _symbolicChunk.dispose();
    _subsymbolicChunk.dispose();
    _symbolicChunk = null;
    _subsymbolicChunk = null;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result +
        ((_subsymbolicChunk == null) ? 0 : _subsymbolicChunk.hashCode());
    result = prime * result +
        ((_symbolicChunk == null) ? 0 : _symbolicChunk.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof DefaultChunk5)) return false;
    final DefaultChunk5 other = (DefaultChunk5) obj;
    if (_subsymbolicChunk == null)
    {
      if (other._subsymbolicChunk != null) return false;
    }
    else if (!_subsymbolicChunk.equals(other._subsymbolicChunk)) return false;
    if (_symbolicChunk == null)
    {
      if (other._symbolicChunk != null) return false;
    }
    else if (!_symbolicChunk.equals(other._symbolicChunk)) return false;
    return true;
  }

}