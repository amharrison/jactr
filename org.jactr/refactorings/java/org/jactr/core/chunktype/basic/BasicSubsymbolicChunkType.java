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
import java.util.Collections;

import org.jactr.core.chunktype.ISubsymbolicChunkType;
import org.jactr.core.chunktype.four.ISubsymbolicChunkType4;
import org.jactr.core.module.declarative.four.learning.IBaseLevelActivationEquation;

/**
 * default impl
 * 
 * @author harrison
 * @created February 5, 2003
 */
public class BasicSubsymbolicChunkType implements ISubsymbolicChunkType,
    ISubsymbolicChunkType4
{


  IBaseLevelActivationEquation       _baseLevelActivationEquation;

  AbstractChunkType                  _parentChunkType;

  boolean                            _isEncoded;

  /**
   * Constructor for the DefaultSubsymbolicChunkType5 object
   * 
   * @param parentChunkType
   *            Description of Parameter
   * @since
   */
  public BasicSubsymbolicChunkType(AbstractChunkType parentChunkType)
  {
    _parentChunkType = parentChunkType;
  }

  /**
   * Sets the parameter attribute of the DefaultSubsymbolicChunkType5 object
   * 
   * @param key
   *            The new parameter value
   * @param value
   *            The new parameter value
   * @since
   */
  public void setParameter(String key, String value)
  {

  }

  /**
   * Gets the parameter attribute of the DefaultSubsymbolicChunkType5 object
   * 
   * @param key
   *            Description of Parameter
   * @return The parameter value
   * @since
   */
  public String getParameter(String key)
  {
    return null;
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
  @SuppressWarnings("unchecked")
  public Collection<String> getSetableParameters()
  {
    return Collections.EMPTY_LIST;
  }

  /**
   * Description of the Method
   * 
   * @since
   */
  public void dispose()
  {
    _parentChunkType = null;
    _baseLevelActivationEquation = null;
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

  }
}