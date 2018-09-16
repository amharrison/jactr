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
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.production.request.IRequest;
import org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6;
import org.jactr.modules.pm.common.buffer.AbstractRequestDelegate;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.buffer.IVisualActivationBuffer;
import org.jactr.modules.pm.visual.buffer.processor.AssignFINSTRequestDelegate;
import org.jactr.modules.pm.visual.buffer.processor.AttendToRequestDelegate;
import org.jactr.modules.pm.visual.buffer.processor.ClearRequestDelegate;
import org.jactr.modules.pm.visual.buffer.processor.SetDefaultSearchRequestDelegate;
import org.jactr.modules.pm.visual.buffer.processor.StartTrackingRequestDelegate;

/**
 * @author developer
 */
public class DefaultVisualActivationBuffer6 extends AbstractPMActivationBuffer6
    implements IVisualActivationBuffer,ICompilableBuffer
{
  /**
   * logger definition
   */
  static public final Log           LOGGER = LogFactory
                                               .getLog(DefaultVisualActivationBuffer6.class);

  protected IChunkType              _visualChunkType;

  protected AttendToRequestDelegate _moveAttentionDelegate;

  protected AttendToRequestDelegate _attendToDelegate;
  
  protected ICompilableContext _compilableContext = new VisualCompilableContext();

  public DefaultVisualActivationBuffer6(IVisualModule module)
  {
    super(IVisualModule.VISUAL_BUFFER, module);
  }
  
  @Override
  protected Collection<IChunk> clearInternal()
  {
    _moveAttentionDelegate.clear();
    _attendToDelegate.clear();
    return super.clearInternal();
  }

  @Override
  protected void grabReferences()
  {
    try
    {
      _visualChunkType = getModel().getDeclarativeModule().getChunkType(
          IVisualModule.VISUAL_CHUNK_TYPE).get();
    }
    catch (Exception e)
    {
      LOGGER.error("Could not get " + IVisualModule.VISUAL_CHUNK_TYPE
          + " chunk type", e);
    }

    installDefaultChunkPatternProcessors();

    super.grabReferences();
  }


  protected void installDefaultChunkPatternProcessors()
  {
    IModel model = getModel();
    try
    {
      /*
       * allows the model to +visual> oldVisualChunk, effectively imagining a
       * visual rep
       */
      AddChunkRequestDelegate del = new AddChunkRequestDelegate();
      del.setAsynchronous(true);
      addRequestDelegate(del);

      /*
       * the rest of these are chunktype specific requests
       */
      _moveAttentionDelegate = new AttendToRequestDelegate(
          (IVisualModule) getModule(), model.getDeclarativeModule()
              .getChunkType(IVisualModule.MOVE_ATTENTION_CHUNK_TYPE).get(),
          IVisualModule.SCREEN_POSITION_SLOT);

      addRequestDelegate(_moveAttentionDelegate);

      /*
       * this is the more general version of the attending request. this is
       * noncanonical but has been refactored to be more applicable across
       * different modules (much like clear was refactored)
       */
      _attendToDelegate = new AttendToRequestDelegate(
          (IVisualModule) getModule(), model.getDeclarativeModule()
              .getChunkType("attend-to").get(), "where");
      addRequestDelegate(_attendToDelegate);

      addRequestDelegate(new ClearRequestDelegate(model.getDeclarativeModule()
          .getChunkType(IVisualModule.CLEAR_CHUNK_TYPE).get()));
      addRequestDelegate(new AssignFINSTRequestDelegate(model
          .getDeclarativeModule().getChunkType(
              IVisualModule.ASSIGN_FINST_CHUNK_TYPE).get()));
      addRequestDelegate(new StartTrackingRequestDelegate(model
          .getDeclarativeModule().getChunkType(
              IVisualModule.START_TRACKING_CHUNK_TYPE).get()));

      addRequestDelegate(new SetDefaultSearchRequestDelegate(model
          .getDeclarativeModule().getChunkType("set-default-visual-search")
          .get()));

      addRequestDelegate(new AbstractRequestDelegate(_visualChunkType) {


        @Override
        protected void finishRequest(IRequest request,
            IActivationBuffer buffer, Object startValue)
        {
          
          
        }

        @Override
        protected boolean isValid(IRequest request, IActivationBuffer buffer)
            throws IllegalArgumentException
        {
          IModel model = buffer.getModel();
          if (Logger.hasLoggers(model) || LOGGER.isWarnEnabled())
          {
            String message = "You should not be adding visual-objects to the buffer, use move-attention instead";
            if (LOGGER.isWarnEnabled()) LOGGER.warn(message);
            Logger.log(model, Logger.Stream.VISUAL, message);
          }
          return false;
        }

        @Override
        protected Object startRequest(IRequest request, IActivationBuffer buffer, double requestTime)
        {
          // TODO Auto-generated method stub
          return null;
        }

      });
    }
    catch (Exception e)
    {
      LOGGER.error("Could not install chunk pattern processors", e);
    }
  }

  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return chunkType.isA(_visualChunkType);
  }

  @Override
  protected IChunk addSourceChunkInternal(IChunk chunkToInsert)
  {
    if (chunkToInsert.hasBeenDisposed())
    {
      /*
       * unusual situation where between the scan/encode time and now the
       * visualChunk has been disposed of because it was no longer visible
       */
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Chunk to insert has been disposed of setting error");

      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.VISUAL,
            "visual chunk has disappeared, error");

      /*
       * set the error
       */
      return addSourceChunk(getErrorChunk());
    }

    return super.addSourceChunkInternal(chunkToInsert);
  }

  /**
   * set the source chunk and flag the FINST for the visual object as attended
   * @param chunk
   * @see org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6#setSourceChunkInternal(org.jactr.core.chunk.IChunk)
   */
  @Override
  protected void setSourceChunkInternal(IChunk chunk)
  {
    super.setSourceChunkInternal(chunk);

    IVisualModule module = (IVisualModule) getModule();
    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.VISUAL, getName()
          + " current visual object " + getSourceChunk());

    if (chunk == null) // turn off tracking if its on
      module.setTrackedVisualChunk(null);
    else
      // we need to mark this chunk as having been attended
      module.assignFINST(getSourceChunk());
  }

}
