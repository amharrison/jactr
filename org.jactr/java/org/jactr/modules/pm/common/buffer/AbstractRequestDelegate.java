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
package org.jactr.modules.pm.common.buffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AsynchronousRequestDelegate;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.slot.IMutableSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.buffer.IPerceptualBuffer;

/**
 * abstract chunk pattern processor that has it's handles(ChunkPattern)
 * implemented basically, if the pattern is of the same chunk type as that used
 * to initialize the processor, handles returns true be sure to set the buffer
 * state slot value!!
 * 
 * @author developer
 */
public abstract class AbstractRequestDelegate extends AsynchronousRequestDelegate
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory
                                     .getLog(AbstractRequestDelegate.class);

  protected IChunkType    _chunkType;

  public AbstractRequestDelegate(IChunkType chunkType)
  {
    _chunkType = chunkType;
    setAsynchronous(true);
  }


  public boolean willAccept(IRequest request)
  {
    if (request instanceof ChunkTypeRequest)
    {
      ChunkTypeRequest ctr = (ChunkTypeRequest) request;
      return _chunkType != null && ctr.getChunkType().isA(_chunkType);
    }
    return false;
  }
}
