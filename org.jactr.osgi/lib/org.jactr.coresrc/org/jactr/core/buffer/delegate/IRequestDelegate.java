/*
 * Created on Feb 6, 2007
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.buffer.delegate;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.production.request.IRequest;

/**
 * Delegate for request processing.
 * @author developer
 *
 */
public interface IRequestDelegate
{

    
  /**
   * do the module specific processing
   * @param request
   * @param buffer
   * @return true if this was successful (will propogate the buffer event)
   */
  public boolean request(IRequest request, IActivationBuffer buffer, double requestTime);
  
  public boolean willAccept(IRequest request);
  
  /**
   * clear/cancel any pending requests
   */
  public void clear();
}


