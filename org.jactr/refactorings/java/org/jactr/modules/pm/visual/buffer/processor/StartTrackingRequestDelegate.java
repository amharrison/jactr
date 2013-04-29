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
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.visual.IVisualModule;

public class StartTrackingRequestDelegate extends AbstractRequestDelegate
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(StartTrackingRequestDelegate.class);

  public StartTrackingRequestDelegate(IChunkType chunkType)
  {
    super(chunkType);
  }

 

  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    IChunk visualChunk = buffer.getSourceChunk();
    IVisualModule visualModule = (IVisualModule) buffer.getModule();
    visualModule.setTrackedVisualChunk(visualChunk);
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    IModel model = buffer.getModel();
    IVisualModule visualModule = (IVisualModule) buffer.getModule();
    if (isBusy(buffer))
    {
      String message = buffer.getName()
          + " is not free - jam is possible, aborting!";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);

      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, message);

      return false;
    }
    
    IChunk visualChunk = buffer.getSourceChunk();
    if (visualChunk == null
        || !visualChunk.isA(visualModule.getVisualChunkType()))
    {
      String message = "Invalid tracking request, buffer is empty or doesn't contain a visual object";
      if (LOGGER.isDebugEnabled()) LOGGER.debug(message);
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL, message);
      return false;
    }

    

    return true;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
  {
   
    return null;
  }

}
