/*
 * Created on Jan 5, 2006
 * Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.buffer.six;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IRequestableBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.logging.IMessageBuilder;
import org.jactr.core.logging.Logger;
import org.jactr.core.module.IModule;
import org.jactr.core.production.request.IRequest;
public abstract class AbstractRequestableBuffer6 extends AbstractActivationBuffer6
    implements IRequestableBuffer
{
  
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
      .getLog(AbstractRequestableBuffer6.class);

  
  public AbstractRequestableBuffer6(String name, IModule module)
  {
    super(name, module);
  }

    
  public boolean request(IRequest request, double requestTime) throws IllegalArgumentException
  {
    boolean processed = false;
    try
    {
      getLock().writeLock().lock();
      processed = requestInternal(request, requestTime);
    }
    finally
    {
      getLock().writeLock().unlock();
    }
    
    if (processed)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("addChunkPatternInternal returned true, dispatching");
      if (getEventDispatcher().hasListeners())
        getEventDispatcher().fire(new ActivationBufferEvent(this, request));

      if(Logger.hasLoggers(getModel()))
      {
        IMessageBuilder mb = Logger.messageBuilder();
        mb.append(getName()).append(" added pattern ")
            .append(String.format("%s", request));

        Logger.log(getModel(), Logger.Stream.BUFFER, mb);
      }
    }
    
    return processed;
  }
  
  /**
   * actually do the work of the request. this is called within the write lock
   * @param request
   * @return
   * @throws IllegalArgumentException
   */
  abstract protected boolean requestInternal(IRequest request, double requestTime) throws IllegalArgumentException;
}


