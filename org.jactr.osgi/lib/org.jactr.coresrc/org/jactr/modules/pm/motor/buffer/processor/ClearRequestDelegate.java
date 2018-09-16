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
package org.jactr.modules.pm.motor.buffer.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.SimpleRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.motor.IMotorModule;

/**
 * reset the motor system - merely calls IMotorModule.reset()
 * 
 * @author developer
 */
public class ClearRequestDelegate extends SimpleRequestDelegate
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(ClearRequestDelegate.class);

  public ClearRequestDelegate(IChunkType clearChunkType)
  {
    super(clearChunkType);
  }

  public boolean request(@SuppressWarnings("unused") IRequest request,
      IActivationBuffer buffer, double requestTime)
  {
      ((IMotorModule) buffer.getModule()).reset();
    return true;
  }

  public void clear()
  {
    // noop
  }

}
