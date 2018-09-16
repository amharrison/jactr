/*
 * Created on Aug 24, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.buffer.event;

import org.jactr.core.event.IParameterEvent;

/**
 * @author harrison TODO To change the template for this generated type comment
 *         go to Window - Preferences - Java - Code Style - Code Templates
 */
public class ActivationBufferListenerAdaptor implements
    IActivationBufferListener
{

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#sourceChunkAdded(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void sourceChunkAdded(@SuppressWarnings("unused")
  ActivationBufferEvent abe)
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#sourceChunkRemoved(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void sourceChunkRemoved(@SuppressWarnings("unused")
  ActivationBufferEvent abe)
  {
    // TODO Auto-generated method stub

  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#sourceChunksCleared(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void sourceChunksCleared(@SuppressWarnings("unused")
  ActivationBufferEvent abe)
  {
    // TODO Auto-generated method stub

  }

  public void requestAccepted(@SuppressWarnings("unused")
  ActivationBufferEvent abe)
  {

  }

  public void statusSlotChanged(@SuppressWarnings("unused")
  ActivationBufferEvent abe)
  {

  }

  @SuppressWarnings("unchecked")
  public void parameterChanged(@SuppressWarnings("unused")
  IParameterEvent pe)
  {

  }

  /**
   * @see org.jactr.core.buffer.event.IActivationBufferListener#chunkMatched(org.jactr.core.buffer.event.ActivationBufferEvent)
   */
  public void chunkMatched(@SuppressWarnings("unused")
  ActivationBufferEvent abe)
  {

  }

}
