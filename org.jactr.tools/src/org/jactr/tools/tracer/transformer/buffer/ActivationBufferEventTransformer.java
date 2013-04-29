/*
 * Created on Feb 22, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.tracer.transformer.buffer;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.IACTREvent;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.resolver.ASTResolver;
import org.jactr.tools.tracer.transformer.IEventTransformer;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

/**
 * @author developer
 */
public class ActivationBufferEventTransformer implements IEventTransformer
{
  /**
   * logger definition
   */
  static private final Log LOGGER   = LogFactory
                                        .getLog(ActivationBufferEventTransformer.class);

  private ASTSupport       _support = new ASTSupport();

  /**
   * @see org.jactr.tools.tracer.transformer.IEventTransformer#transform(IACTREvent)
   */
  public ITransformedEvent transform(IACTREvent actrEvent)
  {
    ActivationBufferEvent bufferEvent = (ActivationBufferEvent) actrEvent;
    CommonTree ast = null;
    boolean handled = false;
    try
    {
      switch (bufferEvent.getType())
      {
        case SOURCE_ADDED:
          ast = _support.create(JACTRBuilder.CHUNKS);
          for (IChunk chunk : bufferEvent.getSourceChunks())
            try
            {
              if (!chunk.hasBeenDisposed())
                ast.addChild(ASTResolver.toAST(chunk, true));
            }
            catch (Exception e)
            {
              if (LOGGER.isDebugEnabled()) LOGGER.debug("chunk disposed", e);
            }
          handled = true;
          break;
        case SOURCE_REMOVED:
          ast = _support.create(JACTRBuilder.CHUNKS);
          for (IChunk chunk : bufferEvent.getSourceChunks())
            try
            {
              ast.addChild(_support.create(JACTRBuilder.CHUNK_IDENTIFIER, chunk
                  .getSymbolicChunk().getName()));
            }
            catch (Exception e)
            {
              if (LOGGER.isDebugEnabled())
                LOGGER.debug("chunk has been disposed ", e);
            }
          handled = true;
          break;
        case STATUS_SLOT_CHANGED:
          String value = "null";
          if (bufferEvent.getNewSlotValue() != null)
            value = bufferEvent.getNewSlotValue().toString();
          ast = _support.createParameter(bufferEvent.getSlotName(), value);
          handled = true;
          break;
      }
    }
    catch (Exception e)
    {
      LOGGER.debug("Failed to process event ", e);
      // handled = false;
    }

    if (handled)
      return new TransformedBufferEvent(bufferEvent.getSource().getModel()
          .getName(), bufferEvent.getSource().getName(), bufferEvent
          .getSystemTime(), bufferEvent.getSimulationTime(), bufferEvent
          .getType(), ast);

    return null;
  }

}
