/*
 * Created on May 7, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.production.action;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.production.CannotInstantiateException;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.production.VariableBindings;

/**
 * An action, that when fired will block the model thread until a chunk appears
 * in the goal buffer
 * 
 * @author developer
 */
public class SleepAction implements IAction
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(SleepAction.class);

  /**
   * @see org.jactr.core.production.action.IAction#bind(VariableBindings)
   */
  public IAction bind(VariableBindings variableBindings)
      throws CannotInstantiateException
  {
    return this;
  }

  /**
   * @see org.jactr.core.production.action.IAction#dispose()
   */
  public void dispose()
  {

  }

  /**
   * wait until the goal buffer isn't empty
   * 
   * @see org.jactr.core.production.action.IAction#fire(org.jactr.core.production.IInstantiation, double)
   */
  public double fire(IInstantiation instantiation, double firingTime)
  {
    IActivationBuffer goalBuffer = instantiation.getModel()
        .getActivationBuffer(IActivationBuffer.GOAL);

    if (goalBuffer.getSourceChunk() == null)
    {
      final Lock goalLock = new ReentrantLock();
      final Condition gotAGoal = goalLock.newCondition();

      /*
       * merely signal when the goal buffer gets something
       */
      IActivationBufferListener listener = new ActivationBufferListenerAdaptor() {
        @Override
        public void sourceChunkAdded(ActivationBufferEvent event)
        {
          try
          {
            goalLock.lock();
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("Signaling goal insertion");
            gotAGoal.signalAll();
          }
          finally
          {
            goalLock.unlock();
          }
        }
      };

      /*
       * attach the listener with the inline executor - this ensures that
       * regardless of what thread adds the source chunk to the buffer we will
       * be notified
       */
      goalBuffer.addListener(listener, ExecutorServices.INLINE_EXECUTOR);

      try
      {
        goalLock.lock();
        while (goalBuffer.getSourceChunk() == null)
        {
          if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for goal");
          gotAGoal.await();
        }
      }
      catch (Exception e)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Could not wait for goal ", e);
      }

      if (LOGGER.isDebugEnabled()) LOGGER.debug("Resuming from wait");

      goalLock.unlock();

      /*
       * remove the listener
       */
      goalBuffer.removeListener(listener);
    }
    else if (LOGGER.isDebugEnabled())
      LOGGER.debug("Goal is already present, no need to sleep");

    return 0;
  }
}
