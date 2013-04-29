/*
 * Created on Jul 2, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.aural.buffer.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.SimpleRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;

/**
 * @author developer
 */
public class AttendToRequestDelegate extends SimpleRequestDelegate
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(AttendToRequestDelegate.class);

  protected String         _locationSlotName;

  /**
   * @param soundChunkType
   */
  public AttendToRequestDelegate(IChunkType soundChunkType,
      String locationSlotName)
  {
    super(soundChunkType);
    _locationSlotName = locationSlotName;
  }

  /**
   * @see org.jactr.core.buffer.delegate.IRequestDelegate#request(IRequest,
   *      IActivationBuffer)
   */
  public boolean request(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    IAuralActivationBuffer auralBuffer = (IAuralActivationBuffer) buffer;
    IAuralModule module = (IAuralModule) auralBuffer.getModule();

    /*
     * first off, we need to make sure the event slot is specified if the
     * chunktype used is sound, we check event, if it is attend-to, we check
     * where
     */
    IChunk audioEvent = null;
    for (IConditionalSlot cSlot : ctr.getConditionalSlots())
      if (cSlot.getCondition() == IConditionalSlot.EQUALS
          && cSlot.getName().equalsIgnoreCase(_locationSlotName))
      {
        audioEvent = (IChunk) cSlot.getValue();
        break;
      }

    module.encodeAuditoryChunkAt(audioEvent, requestTime);
    return true;
  }

  public void clear()
  {
    //noop
  }
}
