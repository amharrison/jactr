/*
 * Created on Sep 22, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.production.condition;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.model.IModel;
import org.jactr.core.utils.ModelerException;

/**
 * @bug why is this not an interface with an abstract implementation like
 *      IBufferAction?
 */
public abstract class AbstractBufferCondition extends AbstractSlotCondition
    implements IBufferCondition
{
  /**
   * Logger definition
   */
  static final private Log LOGGER = LogFactory
                                      .getLog(AbstractBufferCondition.class);

  private String           _bufferName;

  public AbstractBufferCondition(String bufferName)
  {
    super();
    _bufferName = bufferName;
  }

  public String getBufferName()
  {
    return _bufferName;
  }

  @Override
  protected String createToString()
  {
    return "[" + _bufferName + ":" + super.createToString() + "]";
  }

  protected IActivationBuffer getActivationBuffer(IModel model)
  {
    IActivationBuffer buffer = model.getActivationBuffer(_bufferName);
    if (buffer == null)
    {
      LOGGER.error("IActivationBuffer named " + _bufferName + " was not found");
      throw new ModelerException(_bufferName + " is not a valid buffer.", null,
          "Are you sure " + _bufferName
              + " is correct? Has the buffer been installed?");
    }
    return buffer;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + (_bufferName == null ? 0 : _bufferName.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AbstractBufferCondition other = (AbstractBufferCondition) obj;
    if (_bufferName == null)
    {
      if (other._bufferName != null) return false;
    }
    else if (!_bufferName.equals(other._bufferName)) return false;
    return true;
  }

}
