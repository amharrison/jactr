/*
 * Created on Jul 15, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm.buffer;

import org.jactr.core.buffer.IRequestableBuffer;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.IDelegatedRequestableBuffer;
import org.jactr.core.buffer.six.IStatusBuffer;
import org.jactr.core.chunk.IChunk;

public interface IPerceptualBuffer extends IStatusBuffer, IActivationBuffer,
    IRequestableBuffer, IDelegatedRequestableBuffer,
    IEventTrackingActivationBuffer
{

  static public final String IS_BUFFER_STUFF_REQUEST = "isBufferStuffRequest";
  
  static public final String MODALITY_SLOT    = "modality";

  static public final String EXECUTION_SLOT   = "execution";

  static public final String PREPARATION_SLOT = "preparation";

  static public final String PROCESSOR_SLOT   = "processor";

  public boolean isModalityFree();

  public boolean isProcessorFree();

  public boolean isPreparationFree();

  public boolean isExecutionFree();

  public boolean isModalityBusy();

  public boolean isProcessorBusy();

  public boolean isPreparationBusy();

  public boolean isExecutionBusy();

  public void setModalityChunk(IChunk chunk);

  public void setExecutionChunk(IChunk chunk);

  public void setPreparationChunk(IChunk chunk);

  public void setProcessorChunk(IChunk chunk);

}
