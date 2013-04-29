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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.SimpleRequestDelegate;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;

/**
 * @author developer
 */
public class AuralSearchRequestDelegate extends SimpleRequestDelegate
{
  /**
   * logger definition
   */
  static private final Log       LOGGER = LogFactory
                                            .getLog(AuralSearchRequestDelegate.class);

  protected ITimedEvent          _pendingBufferStuff;

  protected IAuralLocationBuffer _locationBuffer;

  public AuralSearchRequestDelegate(IChunkType audioEvent,
      IAuralLocationBuffer buffer)
  {
    super(audioEvent);
    _locationBuffer = buffer;
  }

  /**
   * @see org.jactr.core.buffer.delegate.IRequestDelegate#request(IRequest,
   *      IActivationBuffer)
   */
  public boolean request(IRequest request, IActivationBuffer buffer, double requestTime)
  {
    ChunkTypeRequest ctr = (ChunkTypeRequest) request;
    IAuralLocationBuffer locBuffer = (IAuralLocationBuffer) buffer;

    if (locBuffer.isBufferStuffPending()) locBuffer.cancelBufferStuff();

    boolean isStuffRequest = false;
    for (ISlot slot : new ArrayList<ISlot>(ctr.getSlots()))
      if (IPerceptualBuffer.IS_BUFFER_STUFF_REQUEST.equals(slot.getName())
          && Boolean.TRUE.equals(slot.getValue()))
      {
        isStuffRequest = true;
        ctr.removeSlot(slot);
        break;
      }

    IAuralModule module = (IAuralModule) buffer.getModule();
    module.scanAuditoryField(ctr, requestTime, isStuffRequest);

    return true;
  }

  public void clear()
  {
    //noop
  }
}
