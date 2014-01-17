/*
 * Created on Jul 7, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm.visual.buffer.six;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IllegalActivationBufferStateException;
import org.jactr.core.buffer.delegate.ExpandChunkRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;
import org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer;
import org.jactr.modules.pm.visual.buffer.processor.SetDefaultSearchRequestDelegate;
import org.jactr.modules.pm.visual.buffer.processor.VisualSearchRequestDelegate;

/**
 * since visual-location and visual buffers share the same state information
 * (stored in the visual buffer) so care must be taken to use the visual buffers
 * state information and not visual location's
 * 
 * @author developer
 */
public class DefaultVisualLocationBuffer6 extends AbstractPMActivationBuffer6
    implements IVisualLocationBuffer, ICompilableBuffer
{
  /**
   * logger definition
   */
  static public final Log               LOGGER = LogFactory
                                                   .getLog(DefaultVisualLocationBuffer6.class);

  protected IChunk                      _currentVisualLocation;

  protected IChunk                      _lastVisualLocation;

  protected IChunkType                  _visualLocationChunkType;

  protected IVisualActivationBuffer     _visualActivationBuffer;

  protected VisualSearchRequestDelegate _scanDelegate;

  protected ChunkTypeRequest            _defaultSearchRequest;

  protected ICompilableContext _compilableContext = new VisualCompilableContext();


  public DefaultVisualLocationBuffer6(IVisualActivationBuffer visualBuffer,
      IVisualModule module)
  {
    super(IVisualModule.VISUAL_LOCATION_BUFFER, module);
    _visualActivationBuffer = visualBuffer;

  }

  @Override
  public void initialize()
  {
    super.initialize();
  }

  @Override
  protected void grabReferences()
  {
    try
    {
      _visualLocationChunkType = getModel().getDeclarativeModule()
          .getChunkType(IVisualModule.VISUAL_LOCATION_CHUNK_TYPE).get();

      _defaultSearchRequest = new ChunkTypeRequest(_visualLocationChunkType);
      _defaultSearchRequest.addSlot(new BasicSlot(
          IVisualModule.ATTENDED_STATUS_SLOT, getModel().getDeclarativeModule()
              .getNewChunk()));

      addRequestDelegate(new SetDefaultSearchRequestDelegate(getModel()
          .getDeclarativeModule().getChunkType("set-default-visual-search")
          .get()));
    }
    catch (Exception e)
    {
      LOGGER.error("Could not get chunktype "
          + IVisualModule.VISUAL_LOCATION_CHUNK_TYPE, e);
    }
    installDefaultChunkPatternProcessors();
    super.grabReferences();
  }

  @Override
  protected Collection<IChunk> clearInternal()
  {
    Collection<IChunk> source = super.clearInternal();

    _currentVisualLocation = null;

    if (_scanDelegate.isBufferStuffPending())
      _scanDelegate.cancelBufferStuff();

    clearLastLocation();
    return source;
  }

  /**
   * since the visual location chunks are reused, we want to turn off the copy
   * mechanism
   * 
   * @see org.jactr.core.buffer.six.AbstractActivationBuffer6#copyChunkOnInsertion()
   */
  @Override
  protected boolean shouldCopyOnInsertion(IChunk sourceChunk)
  {
    return false;
  }

  /**
   * returns the current visual location in the buffer or the last attended
   * location
   * 
   * @return
   * @see org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer#getCurrentVisualLocation()
   */
  public IChunk getCurrentVisualLocation()
  {
    if (_currentVisualLocation != null) return _currentVisualLocation;
    return _lastVisualLocation;
  }

  @Override
  protected void setSourceChunkInternal(IChunk chunk)
  {
    if (chunk != null
        && !chunk.isA(((IVisualModule) getModule())
            .getVisualLocationChunkType()))
      throw new IllegalActivationBufferStateException(getName()
          + " may only contain visual-location chunks not : "
          + chunk.getSymbolicChunk().getChunkType());

    super.setSourceChunkInternal(chunk);
    _currentVisualLocation = getSourceChunk();

    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.VISUAL, getName()
          + " current visual location " + _currentVisualLocation);

    if (chunk != null && !chunk.equals(_lastVisualLocation))
      clearLastLocation();
  }

  protected void clearLastLocation()
  {
    if (_lastVisualLocation != null)
      synchronized (_lastVisualLocation)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER
              .debug("clearing temporary slot values of prior visual-location "
                  + _lastVisualLocation);

        /*
         * it was in the buffer, zero the slot values that aren't persistent
         */
        ISymbolicChunk sc = _lastVisualLocation.getSymbolicChunk();
        for (ISlot slot : sc.getSlots())
          if (!slot.getName().equals(IVisualModule.SCREEN_X_SLOT)
              && !slot.getName().equals(IVisualModule.SCREEN_Y_SLOT)) try
          {
            IMutableSlot ms = (IMutableSlot) sc.getSlot(slot.getName());
            ms.setValue(null);
          }
          catch (Exception e)
          {

          }

        _lastVisualLocation.setMetaData(
            IPerceptualMemory.SEARCH_RESULT_IDENTIFIER_KEY, null);
      }

    _lastVisualLocation = null;
  }

  @Override
  protected boolean removeSourceChunkInternal(IChunk chunkToRemove)
  {
    if (super.removeSourceChunkInternal(chunkToRemove))
    {
      /*
       * we store this so that the next time a visual-location is added, the
       * temporary slot values are cleared. We want to clear them so that we
       * don't carry around a bunch of detritus - but we can't do it here
       * because the encoding functions in the visual module or other related
       * modules ( i.e. configural or manipulative in ACT-R/S) will rely upon
       * the objects slot in the visual-location, which will likely be removed
       * from the vis-loc buffer before encoding
       */
      _lastVisualLocation = chunkToRemove;
      return true;
    }
    return false;
  }

  protected void installDefaultChunkPatternProcessors()
  {
    /*
     * this will expand any inserted chunk requests to chunktype requests. this
     * allows the modeler to do a +visual-location> =oldLoc which will then
     * start a new search
     */
    addRequestDelegate(new ExpandChunkRequestDelegate(false));

    /*
     * set default is installed first so that it takes priority over the search,
     * since both use the same chunktype hierarchy
     */
    try
    {
      addRequestDelegate(new SetDefaultSearchRequestDelegate(getModel()
          .getDeclarativeModule().getChunkType("set-default-visual-search")
          .get()));
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to install handler for set-default-visual-search ",
          e);
    }

    _scanDelegate = new VisualSearchRequestDelegate(
        (IVisualModule) getModule(), false);
    addRequestDelegate(_scanDelegate);
  }

  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return chunkType.isA(_visualLocationChunkType);
  }

  /**
   * we stuff the buffer if this buffer is empty and free AND the visual buffer
   * is free. If they are, we create a basic visual search request, set the
   * state (ours, not the visual buffers) to unrequested, and pass the request
   * on to the visual location chunk pattern processor
   * 
   * @see org.jactr.modules.pm.visual.buffer.IVisualLocationBuffer#checkForBufferStuff()
   */
  public void checkForBufferStuff()
  {
    /*
     * can't stuff if full, requested or unrequested
     */
    if (!isBufferEmpty()) return;

    /*
     * cant stuff during busy or error, only free
     */
    if (!isStateFree()) return;

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Setting search pattern for buffer stuff");

    getModule();
    ChunkTypeRequest defaultRequest = getDefaultSearch();
    ChunkTypeRequest locationBufferStuffPattern = new ChunkTypeRequest(
        defaultRequest.getChunkType(), defaultRequest.getSlots());

    // @bug this should be based on current
    // locationBufferStuffPattern.addSlot(new BasicSlot(
    // IVisualModule.NEAREST_SLOT, module.getVisualMemory()
    // .getVisualLocationChunkAt(0, 0)));

    locationBufferStuffPattern.addSlot(new BasicSlot(
        IPerceptualBuffer.IS_BUFFER_STUFF_REQUEST, true));

    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.VISUAL, "Attempting a stuff search with "
          + locationBufferStuffPattern);

    request(locationBufferStuffPattern, ACTRRuntime.getRuntime().getClock(
        getModel()).getTime());
  }

  /**
   * so that we don't try to encode the visual locations which are taken care of
   * by the visual map..
   * 
   * @see org.jactr.core.buffer.AbstractActivationBuffer#handlesEncoding()
   */
  @Override
  public boolean handlesEncoding()
  {
    return true;
  }

  public boolean isBufferStuffPending()
  {
    return _scanDelegate.isBufferStuffPending();
  }

  public void cancelBufferStuff()
  {
    _scanDelegate.cancelBufferStuff();
  }

  public void setDefaultSearch(ChunkTypeRequest request)
  {
    _defaultSearchRequest = request;
  }

  public ChunkTypeRequest getDefaultSearch()
  {
    return _defaultSearchRequest;
  }
  
  @Override
  public ICompilableContext getCompilableContext()
  {
    return _compilableContext;
  }
}
