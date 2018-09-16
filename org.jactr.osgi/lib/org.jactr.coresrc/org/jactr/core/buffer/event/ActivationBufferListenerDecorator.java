/*
 * Created on Jul 10, 2007 Copyright (C) 2001-2007, Anthony Harrison
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
package org.jactr.core.buffer.event;

import org.jactr.core.event.IParameterEvent;

/**
 * @author developer
 */
public class ActivationBufferListenerDecorator implements
    IActivationBufferListener
{

  private IActivationBufferListener _listener;

  public ActivationBufferListenerDecorator(IActivationBufferListener listener)
  {
    if (listener == null)
      throw new NullPointerException("listener cannot be null");
    _listener = listener;
  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#chunkMatched(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void chunkMatched(ActivationBufferEvent abe)
  {
    _listener.chunkMatched(abe);
  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#requestAccepted(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void requestAccepted(ActivationBufferEvent abe)
  {
    _listener.requestAccepted(abe);
  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#sourceChunkAdded(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void sourceChunkAdded(ActivationBufferEvent abe)
  {
    _listener.sourceChunkAdded(abe);
  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#sourceChunkRemoved(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void sourceChunkRemoved(ActivationBufferEvent abe)
  {
    _listener.sourceChunkRemoved(abe);
  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#sourceChunksCleared(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void sourceChunksCleared(ActivationBufferEvent abe)
  {
    _listener.sourceChunksCleared(abe);
  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#statusSlotChanged(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void statusSlotChanged(ActivationBufferEvent abe)
  {
    _listener.statusSlotChanged(abe);
  }

  /**
   * @see org.jactr.core.event.IParameterListener#parameterChanged(org.jactr.core.event.IParameterEvent)
   */
  @SuppressWarnings("unchecked")
  public void parameterChanged(IParameterEvent pe)
  {
    _listener.parameterChanged(pe);
  }

}
