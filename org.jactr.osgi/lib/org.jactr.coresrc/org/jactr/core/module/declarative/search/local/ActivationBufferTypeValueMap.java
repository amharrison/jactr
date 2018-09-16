/*
 * Created on Oct 12, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.module.declarative.search.local;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.module.declarative.search.map.AbstractTypeValueMap;
import org.jactr.core.module.declarative.search.map.DefaultValueMap;
import org.jactr.core.module.declarative.search.map.ITypeValueMap;
import org.jactr.core.module.declarative.search.map.IValueMap;

public class ActivationBufferTypeValueMap<I> extends AbstractTypeValueMap<IActivationBuffer, I> implements ITypeValueMap<IActivationBuffer, I>
{

  private DefaultValueMap<IActivationBuffer,I> _valueMap = new DefaultValueMap<IActivationBuffer,I>();
  


  public IValueMap<IActivationBuffer,I> getValueMap()
  {
    return _valueMap;
  }

  public boolean isValueRelevant(Object value)
  {
    return value instanceof IActivationBuffer;
  }

  
  @Override
  public IActivationBuffer asKeyType(Object value)
  {
    if(value instanceof IActivationBuffer)
      return (IActivationBuffer)value;
    return null;
  }

}


