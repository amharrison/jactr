/*
 * Created on Jul 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.buffer.six;


import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.request.SlotBasedRequest;
import org.jactr.core.slot.INotifyingSlotContainer;

public interface IStatusBuffer extends IActivationBuffer,
    INotifyingSlotContainer
{

  static public final String STATE_SLOT  = "state";

  static public final String BUFFER_SLOT = "buffer";

  static public final String ERROR_SLOT  = "error";

  static public final String ERROR_NOTHING_AVAILABLE_CHUNK   = "error-nothing-available";

  static public final String ERROR_NOTHING_MATCHES_CHUNK     = "error-nothing-matches";

  static public final String ERROR_NO_LONGER_AVAILABLE_CHUNK = "error-no-longer-available";

  static public final String ERROR_CHANGED_TOO_MUCH_CHUNK    = "error-changed-too-much";

  static public final String ERROR_INVALID_INDEX_CHUNK       = "error-invalid-index";

  static public final String ERROR_DELETED_CHUNK             = "error-chunk-deleted";

  static public final String ERROR_UNKNOWN_CHUNK             = "error-unknown";

  public boolean isStateError();

  public boolean isStateFree();

  public boolean isStateBusy();

  public boolean isBufferRequested();

  public boolean isBufferUnrequested();

  public boolean isBufferFull();

  public boolean isBufferEmpty();

  public void setStateChunk(IChunk chunk);

  public void setBufferChunk(IChunk chunk);

  public void setErrorChunk(IChunk chunk);

  public boolean isErrorSet();

  /**
   * Required to permit status buffers to participate in the variable binding
   * process of production instantiation. Since we don't know a priori what
   * the slots the status buffers will contain or their possible values, it is
   * up to the status buffers to attempt to bind variable values
   * 
   * @param request
   * @param bindings
   * @return
   * @throws CannotMatchException
   */
  public int bind(SlotBasedRequest request, VariableBindings bindings,
      boolean isIterative) throws CannotMatchException;

}
