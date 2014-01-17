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
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.modules.pm.aural.IAuralModule;
import org.jactr.modules.pm.aural.buffer.IAuralActivationBuffer;
import org.jactr.modules.pm.aural.buffer.IAuralLocationBuffer;
import org.jactr.modules.pm.aural.buffer.processor.AttendToRequestDelegate;
import org.jactr.modules.pm.aural.buffer.processor.ClearRequestDelegate;
import org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6;

/**
 * @author developer
 */
public class DefaultAuralActivationBuffer extends AbstractPMActivationBuffer6
    implements IAuralActivationBuffer
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(DefaultAuralActivationBuffer.class);

  private IAuralLocationBuffer _locationBuffer;

  /**
   * @param name
   * @param model
   * @param module
   */
  public DefaultAuralActivationBuffer(IAuralModule module,
      IAuralLocationBuffer locationBuffer)
  {
    super(IAuralModule.AURAL_BUFFER, module);
    _locationBuffer = locationBuffer;
  }

  @Override
  public void initialize()
  {
    super.initialize();
  }

  @Override
  protected void grabReferences()
  {
    IAuralModule module = (IAuralModule) getModule();
    
    
    addRequestDelegate(new AddChunkRequestDelegate());
    
    addRequestDelegate(new ClearRequestDelegate(module
        .getClearChunkType()));

    addRequestDelegate(new AttendToRequestDelegate(module, module
        .getSoundChunkType(), IAuralModule.EVENT_SLOT));

    /*
     * here is the more general attending request.
     */
    try
    {
      addRequestDelegate(new AttendToRequestDelegate(module, module
          .getModel().getDeclarativeModule().getChunkType("attend-to").get(),
          "where"));
    }
    catch (Exception e)
    {
      LOGGER.error("could not install general attending processor ", e);
    }
    super.grabReferences();
  }

  @Override
  protected void setSourceChunkInternal(IChunk chunk)
  {
    super.setSourceChunkInternal(chunk);

    IModel model = getModel();
    if (Logger.hasLoggers(model))
      Logger.log(model, Logger.Stream.AURAL, getName() + " current sound " +
          chunk);
  }

  /**
   * @see org.jactr.modules.pm.common.buffer.AbstractPMActivationBuffer6#isValidChunkType(org.jactr.core.chunktype.IChunkType)
   */
  @Override
  protected boolean isValidChunkType(IChunkType chunkType)
  {
    return chunkType != null &&
        chunkType.isA(((IAuralModule) getModule()).getSoundChunkType());
  }

  public IAuralLocationBuffer getAuralLocationBuffer()
  {
    return _locationBuffer;
  }

}
