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

package org.jactr.core.chunktype.five;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.basic.AbstractChunkType;
import org.jactr.core.chunktype.basic.BasicSubsymbolicChunkType;
import org.jactr.core.chunktype.basic.BasicSymbolicChunkType;
import org.jactr.core.model.IModel;

/**
 * Description of the Class
 * 
 * @author harrison
 * @created February 5, 2003
 */
public class DefaultChunkType5 extends AbstractChunkType
{

  /**
   * Description of the Field
   * 
   * @since
   */
  protected ISymbolicChunkType    _symbolicChunkType;

  /**
   * Description of the Field
   * 
   * @since
   */
  protected ISubsymbolicChunkType _subsymbolicChunkType;

  /**
   * Constructor for the DefaultChunkType5 object
   * 
   * @since
   */
  public DefaultChunkType5(IModel model)
  {
    this(model, null);
  }

  public DefaultChunkType5(IModel model, IChunkType parent)
  {
    super(model);
    setSymbolicChunkType(new BasicSymbolicChunkType(this, parent));
    setSubsymbolicChunkType(new BasicSubsymbolicChunkType(this));
  }

  @Override
  public void dispose()
  {
    if (_subsymbolicChunkType != null) _subsymbolicChunkType.dispose();
    if (_symbolicChunkType != null) _symbolicChunkType.dispose();
    _subsymbolicChunkType = null;
    _symbolicChunkType = null;
    super.dispose();
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

  /**
   * Sets the symbolicChunkType attribute of the DefaultChunkType5 object
   * 
   * @param sct
   *            The new symbolicChunkType value
   * @since
   */
  protected void setSymbolicChunkType(ISymbolicChunkType sct)
  {
    _symbolicChunkType = sct;
  }

  /**
   * Sets the subsymbolicChunkType attribute of the DefaultChunkType5 object
   * 
   * @param ssct
   *            The new subsymbolicChunkType value
   * @since
   */
  protected void setSubsymbolicChunkType(ISubsymbolicChunkType ssct)
  {
    _subsymbolicChunkType = ssct;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime
        * result
        + ((_subsymbolicChunkType == null) ? 0 : _subsymbolicChunkType
            .hashCode());
    result = prime * result
        + ((_symbolicChunkType == null) ? 0 : _symbolicChunkType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (!(obj instanceof DefaultChunkType5)) return false;
    final DefaultChunkType5 other = (DefaultChunkType5) obj;
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
