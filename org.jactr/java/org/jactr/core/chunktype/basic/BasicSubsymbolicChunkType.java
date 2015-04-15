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
package org.jactr.core.chunktype.basic;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.four.ISubsymbolicChunkType4;
import org.jactr.core.utils.DefaultAdaptable;

/**
 * default impl
 * 
 * @author harrison
 * @created February 5, 2003
 */
public class BasicSubsymbolicChunkType extends DefaultAdaptable implements
    ISubsymbolicChunkType, ISubsymbolicChunkType4
{
  IChunkType                   _parentChunkType;

  boolean                      _isEncoded;

  Map<String, String>          _unknownParameters;

  /**
   * Constructor for the DefaultSubsymbolicChunkType5 object
   * 
   * @param parentChunkType
   *          Description of Parameter
   * @since
   */
  public BasicSubsymbolicChunkType()
  {
    _unknownParameters = new TreeMap<String, String>();
  }

  public void bind(IChunkType wrapper, Collection<IChunkType> parents)
  {
    _parentChunkType = wrapper;
  }

  /**
   * Sets the parameter attribute of the DefaultSubsymbolicChunkType5 object
   * 
   * @param key
   *          The new parameter value
   * @param value
   *          The new parameter value
   * @since
   */
  public void setParameter(String key, String value)
  {
    _unknownParameters.put(key, value);
  }

  /**
   * Gets the parameter attribute of the DefaultSubsymbolicChunkType5 object
   * 
   * @param key
   *          Description of Parameter
   * @return The parameter value
   * @since
   */
  public String getParameter(String key)
  {
    return _unknownParameters.get(key);
  }

  /**
   * Gets the possibleParameters attribute of the DefaultSubsymbolicChunkType5
   * object
   * 
   * @return The possibleParameters value
   * @since
   */
  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  /**
   * Gets the setableParameters attribute of the DefaultSubsymbolicChunkType5
   * object
   * 
   * @return The setableParameters value
   * @since
   */

  public Collection<String> getSetableParameters()
  {
    return _unknownParameters.keySet();
  }

  /**
   * Description of the Method
   * 
   * @since
   */
  public void dispose()
  {
    _parentChunkType = null;
    _unknownParameters.clear();
  }

  /**
   * Gets the encoded attribute of the DefaultSubsymbolicChunkType5 object
   * 
   * @return The encoded value
   */
  public boolean isEncoded()
  {
    return _isEncoded;
  }

  public void encode()
  {
    // noop
    _isEncoded = true;
  }

  public boolean isDisposed()
  {
    return false;
  }
}