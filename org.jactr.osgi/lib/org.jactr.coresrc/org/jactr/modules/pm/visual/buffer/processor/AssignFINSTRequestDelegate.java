/*
 * Created on Jul 11, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm.visual.buffer.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;

public class AssignFINSTRequestDelegate extends AbstractRequestDelegate
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(AssignFINSTRequestDelegate.class);

  public AssignFINSTRequestDelegate(IChunkType assignFINSTChunkType)
  {
    super(assignFINSTChunkType);
  }

  

  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    if (LOGGER.isWarnEnabled()) LOGGER.warn("NOT IMPLEMENTED YET");
    return false;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    // TODO Auto-generated method stub
    return null;
  }

}
