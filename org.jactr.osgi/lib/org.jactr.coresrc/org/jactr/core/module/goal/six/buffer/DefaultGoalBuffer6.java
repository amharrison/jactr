/*
 * Created on Aug 18, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.module.goal.six.buffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AddChunkRequestDelegate;
import org.jactr.core.buffer.delegate.AddChunkTypeRequestDelegate;
import org.jactr.core.buffer.delegate.AsynchronousRequestDelegate;
import org.jactr.core.buffer.delegate.DefaultDelegatedRequestableBuffer6;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.module.procedural.five.learning.ICompilableBuffer;
import org.jactr.core.module.procedural.five.learning.ICompilableContext;
import org.jactr.core.module.procedural.six.learning.DefaultCompilableContext;
import org.jactr.core.production.request.IRequest;

/**
 * default goal buffer with a capacity of one
 * 
 * @author developer
 */
public class DefaultGoalBuffer6 extends DefaultDelegatedRequestableBuffer6
    implements ICompilableBuffer
{
  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultGoalBuffer6.class);

  ICompilableContext _compilableContext = new DefaultCompilableContext(true, true, false, false);
	
  
  public DefaultGoalBuffer6(String name, IModule module)
  {
    super(name, module);
    setG(20);
    setActivation(1);

    /*
     * by default we accept +goal> =chunk and +goal> isa chunk as add operations
     * immediately
     */

    AsynchronousRequestDelegate ard = new AddChunkRequestDelegate() {
      @Override
      protected double computeCompletionTime(double startTime,
          IRequest request, IActivationBuffer buffer)
      {
        // ensures that adds occur with modify & remove, otherwise,
        // they'd be off by defAct time
        // TODO won't setDelayStart(false) do the same?
        return startTime;
      }
    };
    ard.setAsynchronous(true);
    ard.setUseBlockingTimedEvents(false);
    addRequestDelegate(ard);

    ard = new AddChunkTypeRequestDelegate() {
      @Override
      protected double computeCompletionTime(double startTime,
          IRequest request, IActivationBuffer buffer)
      {
        // ensures that adds occur with modify & remove
        return startTime;
      }
    };
    ard.setAsynchronous(true);
    ard.setUseBlockingTimedEvents(false);
    addRequestDelegate(ard);

    /*
     * but we ignore +goal> slot =value
     */
    // addRequestDelegate(new IgnoreSlotRequestDelegate());
  }

  @Override
  protected IChunk addSourceChunkInternal(IChunk chunk)
  {
    IChunk inserted = super.addSourceChunkInternal(chunk);

    if (inserted != null)
    {
      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.GOAL, "Setting goal to "
            + getSourceChunk());
    }

    return inserted;
  }
  
  @Override
  protected boolean removeSourceChunkInternal(IChunk chunk)
  {
    boolean removed = super.removeSourceChunkInternal(chunk);
    if(removed)
    {
      IModel model = getModel();
      if (Logger.hasLoggers(model))
        Logger.log(model, Logger.Stream.GOAL, "Removed goal "
            + chunk);
    }
    return removed;
  }

  public ICompilableContext getCompilableContext()
  {
    return _compilableContext;
  }

}
