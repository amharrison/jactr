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
package org.jactr.modules.pm.aural.buffer.six;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.delegate.ExpandChunkRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.buffer.processor.AuralSearchRequestDelegate;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6;

/**
 * Supports clearing unique fields of the audio-event permiting merging.
 * 
 * @author developer
 */
public class DefaultAuralLocationBuffer extends AbstractPMActivationBuffer6
    implements IAuralLocationBuffer
{
  /**
   * logger definition
   */
  static private final Log LOGGER           = LogFactory
                                                .getLog(DefaultAuralLocationBuffer.class);

  protected ITimedEvent    _pendingScan;

  protected boolean        _stuffPending;

  protected boolean        _nullUniqueSlots = true;

  /**
   * @param name
   * @param model
   * @param module
   */
  public DefaultAuralLocationBuffer(IAuralModule module)
  {
    super(IAuralModule.AURAL_LOCATION_BUFFER, module);
  }

  public void setCompressAudioEventsEnabled(boolean compressAudioEvents)
  {
    _nullUniqueSlots = compressAudioEvents;
  }

  public boolean isCompressAudioEventsEnabled()
  {
    return _nullUniqueSlots;
  }

  @Override
  public void enqueueTimedEvent(ITimedEvent timedEvent)
  {
    _pendingScan = timedEvent;
    super.enqueueTimedEvent(timedEvent);
  }

  @Override
  public void initialize()
  {
    super.initialize();

  }

  @Override
  protected void grabReferences()
  {
    /*
     * this will expand any inserted chunk requests to chunktype requests. this
     * allows the modeler to do a +visual-location> =oldLoc which will then
     * start a new search
     */
    addRequestDelegate(new ExpandChunkRequestDelegate(false));
    addRequestDelegate(new AuralSearchRequestDelegate(
        (IAuralModule) getModule()));
    super.grabReferences();
  }

  @Override
  protected void setSourceChunkInternal(IChunk chunk)
  {
    super.setSourceChunkInternal(chunk);

    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.AURAL, getName()
          + " current audio event " + chunk);
  }

  /**
   * @see org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6#isValidChunkType(org.jactr.core.chunktype.IChunkType)
   */
  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return chunkType != null
        && chunkType.isA(((IAuralModule) getModule()).getAudioEventChunkType());
  }

  public void checkForBufferStuff()
  {
    IAuralModule aModule = (IAuralModule) getModule();

    /*
     * can't stuff if full, requested, unrequested
     */
    if (!isBufferEmpty())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getName() + ".buffer is not empty("
            + getSlot("buffer").getValue() + "), can't stuff");
      return;
    }

    /*
     * or busy
     */
    if (isStateBusy())
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(getName() + ".state is busy, can't stuff");
      return;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Setting search pattern for buffer stuff");

    /*
     * there is some stuffing to do.. lets do a basic search for anything new,
     * near the center of view note: this is not the same as lisp which looks
     * for the newest left most (WTF?).
     */

    ChunkTypeRequest locationBufferStuffPattern = new ChunkTypeRequest(
        aModule.getAudioEventChunkType());
    locationBufferStuffPattern.addSlot(new BasicSlot(
        IAuralModule.ATTENDED_STATUS_SLOT, getModel().getDeclarativeModule()
            .getNewChunk()));
    locationBufferStuffPattern.addSlot(new BasicSlot(
        IPerceptualBuffer.IS_BUFFER_STUFF_REQUEST, true));

    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.AURAL, "Attempting a stuff search with "
          + locationBufferStuffPattern);

    _stuffPending = true;

    request(locationBufferStuffPattern, getModel().getAge());
  }

  public boolean isBufferStuffPending()
  {
    return _pendingScan != null && _stuffPending;
  }

  public void cancelBufferStuff()
  {
    if (isBufferStuffPending())
    {
      _pendingScan.abort();
      _pendingScan = null;
      _stuffPending = false;
    }
  }

  /**
   * overriden so that we can null out the time values for better merge
   * behavior.
   */
  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    super.removeSourceChunkInternal(chunkToRemove);
    if (isCompressAudioEventsEnabled())
    {
      try
      {
        ISymbolicChunk sc = chunkToRemove.getSymbolicChunk();
        ((IMutableSlot) sc.getSlot(IAuralModule.ONSET_SLOT)).setValue(null);
        ((IMutableSlot) sc.getSlot(IAuralModule.OFFSET_SLOT)).setValue(null);
      }
      catch (Exception e)
      {
        LOGGER.debug(
            "Failed to clear audio-event contents. Leaving untouched. ", e);
      }
      return true;
    }
    return false;
  }
}
