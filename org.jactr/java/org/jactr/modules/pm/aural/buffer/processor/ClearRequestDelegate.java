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
package org.jactr.modules.pm.aural.buffer.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;

/**
 * reset the visual system - merely calls IVisualModule.reset()
 * 
 * @author developer
 */
public class ClearRequestDelegate extends AbstractRequestDelegate
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(ClearRequestDelegate.class);

  public ClearRequestDelegate(IChunkType clearChunkType)
  {
    super(clearChunkType);
    /*
     * if we used blocking timed events, we'd need to release the block, most
     * likely from the visual module's reset event. it's easier just to do this
     */
    setUseBlockingTimedEvents(false);
    // we will actually clear at the end of the cycle, and not 50ms after that
    // setDelayStart(false);
  }

  @Override
  protected double computeCompletionTime(double startTime, IRequest request,
      IActivationBuffer buffer)
  {
    return startTime;
  }

  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    /*
     * we permit clearing when busy, so always accept
     */
    return true;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    IAuralActivationBuffer actBuffer = (IAuralActivationBuffer) buffer;
    IAuralLocationBuffer locBuffer = ((IAuralModule) actBuffer
        .getModule()).getAuralLocationBuffer();

    IChunk busy = buffer.getModel().getDeclarativeModule().getBusyChunk();

    actBuffer.setStateChunk(busy);
    actBuffer.setModalityChunk(busy);
    actBuffer.setPreparationChunk(busy);
    actBuffer.setProcessorChunk(busy);
    actBuffer.setExecutionChunk(busy);

    locBuffer.setStateChunk(busy);
    locBuffer.setModalityChunk(busy);
    locBuffer.setPreparationChunk(busy);
    locBuffer.setProcessorChunk(busy);
    locBuffer.setExecutionChunk(busy);
    return null;
  }

  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    ((IAuralModule) buffer.getModule()).reset(isFullReset(request));
  }

  private boolean isFullReset(IRequest request)
  {
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    for (ISlot slot : ctr.getSlots())
      if (slot.getName().equalsIgnoreCase("all")
          && slot.getValue() instanceof Boolean && (Boolean) slot.getValue())
        return true;
    return false;
  }
}
