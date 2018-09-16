package org.jactr.core.runtime.controller.impl;

/*
 * default logging
 */
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;

public class ModelListener extends ModelListenerAdaptor
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER         = LogFactory
                                                        .getLog(ModelListener.class);

  final private RuntimeState         _state;

  final private Lock                 _lock          = new ReentrantLock();

  private boolean                    _shouldSuspend = false;

  private Condition                  _condition     = _lock.newCondition();

  

  public ModelListener(RuntimeState state)
  {
    _state = state;
  }

  public void setShouldSuspend(boolean shouldSuspend)
  {
    try
    {
      _lock.lock();
      _shouldSuspend = shouldSuspend;

      _condition.signalAll();
    }
    finally
    {
      _lock.unlock();
    }
  }
  

  private boolean shouldSuspend()
  {
    try
    {
      _lock.lock();
      return _shouldSuspend;
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void suspendModel(IModel model)
  {
    boolean suspended = shouldSuspend();

    if (suspended)
      model.dispatch(new ModelEvent(model, ModelEvent.Type.SUSPENDED));

    try
    {
      _lock.lock();
      while (_shouldSuspend)
        _condition.await();
    }
    catch (InterruptedException e)
    {
      // ignore and return
      LOGGER.warn("Interrupted, expecting termination ", e);
    }
    finally
    {
      _lock.unlock();
    }

    if (suspended)
      model.dispatch(new ModelEvent(model, ModelEvent.Type.RESUMED));
  }

  @Override
  public void cycleStarted(ModelEvent me)
  {
    suspendModel(me.getSource());
  }

  @Override
  public void modelStarted(ModelEvent me)
  {
  }

  @Override
  public void modelResumed(ModelEvent me)
  {
    _state.resumed(me.getSource());
  }

  @Override
  public void modelSuspended(ModelEvent me)
  {
    _state.suspended(me.getSource());
  }

  @Override
  public void modelDisconnected(ModelEvent me)
  {
  }
}
