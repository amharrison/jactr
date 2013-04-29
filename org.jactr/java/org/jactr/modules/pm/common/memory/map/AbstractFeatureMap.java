package org.jactr.modules.pm.common.memory.map;

/*
 * default logging
 */
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

public abstract class AbstractFeatureMap<T> implements IFeatureMap<T>
{
  /**
   * Logger definition
   */
  static private final transient Log                            LOGGER      = LogFactory
                                                                                .getLog(AbstractFeatureMap.class);

  @SuppressWarnings("unchecked")
  private ACTREventDispatcher<IFeatureMap, IFeatureMapListener> _dispatcher = new ACTREventDispatcher<IFeatureMap, IFeatureMapListener>();

  private IPerceptualMemory                                     _memory;

  private final String                                          _crPropertyName;

  private final String                                          _requestSlotName;

  private Lock                                                  _lock       = new ReentrantLock();

  public AbstractFeatureMap(String requestSlotName, String crPropertyName)
  {
    _crPropertyName = crPropertyName;
    _requestSlotName = requestSlotName;
  }

  protected String getRelevantSlotName()
  {
    return _requestSlotName;
  }

  protected String getRelevantPropertyName()
  {
    return _crPropertyName;
  }

  public void addListener(IFeatureMapListener listener, Executor executor)
  {
    _dispatcher.addListener(listener, executor);
  }

  public void removeListener(IFeatureMapListener listener)
  {
    _dispatcher.removeListener(listener);
  }

  protected boolean hasListeners()
  {
    return _dispatcher.hasListeners();
  }

  protected void dispatch(FeatureMapEvent event)
  {
    _dispatcher.fire(event);
  }

  public void setPerceptualMemory(IPerceptualMemory memory)
  {
    _memory = memory;
  }

  public IPerceptualMemory getPerceptualMemory()
  {
    return _memory;
  }

  protected Lock getLock()
  {
    return _lock;
  }

  abstract protected void clearInternal();

  final public void clear()
  {
    _lock.lock();
    try
    {
      clearInternal();
    }
    finally
    {
      _lock.unlock();
    }
  }

  public void dispose()
  {
    clear();
  }

  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest)
  {
    String slotName = getRelevantSlotName();
    if (slotName == null) return;

    T value = getCurrentValue(identifier);

    if (value != null)
      mutableRequest.addSlot(new BasicSlot(slotName, value));
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn("No " + slotName + " information for " + identifier);
  }

  final public T getInformation(IIdentifier identifier)
  {
    _lock.lock();
    try
    {
      return getCurrentValue(identifier);
    }
    finally
    {
      _lock.unlock();
    }
  }

  final public void getCandidateRealObjects(ChunkTypeRequest request,
      Set<IIdentifier> container)
  {
    _lock.lock();
    try
    {
      getCandidates(request, container);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * is {@link #getRelevantSlotName()} is not null, this will check to see if
   * the request contains that slot name.
   * 
   * @param request
   * @return
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#isInterestedIn(org.jactr.core.production.request.ChunkTypeRequest)
   */
  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    if (_requestSlotName != null) for (ISlot slot : request.getSlots())
      if (slot.getName().equalsIgnoreCase(_requestSlotName)) return true;

    return _requestSlotName == null;
  }

  final public void afferentObjectAdded(IAfferentObject object)
  {
    IIdentifier identifier = object.getIdentifier();

    T data = extractInformation(object);
    _lock.lock();
    try
    {
      addInformation(identifier, data);
    }
    finally
    {
      _lock.unlock();
    }

    if (data != null)
    {
      objectAdded(object, data);
      if (hasListeners())
        dispatch(new FeatureMapEvent(this, ACTRRuntime.getRuntime().getClock(
            getPerceptualMemory().getModule().getModel()).getTime(),
            FeatureMapEvent.Type.ADDED, Collections.singleton(identifier)));
    }
  }

  /**
   * call back after object information has been added, assuming data was not
   * null
   * 
   * @param object
   * @param data
   */
  protected void objectAdded(IAfferentObject object, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Added " + object.getIdentifier() + " = " + data);
  }

  final public void afferentObjectRemoved(IAfferentObject object)
  {
    IIdentifier identifier = object.getIdentifier();
    T data = null;
    _lock.lock();
    try
    {
      data = removeInformation(identifier);
    }
    finally
    {
      _lock.unlock();
    }

    if (data != null)
    {
      objectRemoved(object, data);
      if (hasListeners())
        dispatch(new FeatureMapEvent(this, ACTRRuntime.getRuntime().getClock(
            getPerceptualMemory().getModule().getModel()).getTime(),
            FeatureMapEvent.Type.REMOVED, Collections.singleton(identifier)));
    }
  }

  /**
   * callback
   * 
   * @param object
   * @param data
   */
  protected void objectRemoved(IAfferentObject object, T data)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Removed " + object.getIdentifier() + " = " + data);
  }

  final public void afferentObjectUpdated(IAfferentObject object,
      IObjectDelta delta)
  {
    // relevant property did not change
    if (_crPropertyName != null
        && !delta.getChangedProperties().contains(_crPropertyName)) return;

    IIdentifier identifier = object.getIdentifier();
    T data = extractInformation(object);
    T oldData = null;
    _lock.lock();
    try
    {
      oldData = removeInformation(identifier);
      addInformation(identifier, data);
    }
    finally
    {
      _lock.unlock();
    }

    objectUpdated(object, oldData, data);

    if (data != null && !data.equals(oldData) && hasListeners())
      dispatch(new FeatureMapEvent(this, ACTRRuntime.getRuntime().getClock(
          getPerceptualMemory().getModule().getModel()).getTime(),
          FeatureMapEvent.Type.UPDATED, Collections.singleton(identifier)));
  }

  protected void objectUpdated(IAfferentObject object, T oldData, T newData)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(this + " Updated " + object.getIdentifier() + "  " + oldData
          + " " + newData);
  }

  abstract protected T getCurrentValue(IIdentifier identifier);

  abstract protected T extractInformation(IAfferentObject afferentObject);

  abstract protected T removeInformation(IIdentifier identifier);

  abstract protected void addInformation(IIdentifier identifier, T data);

  abstract protected void getCandidates(ChunkTypeRequest request,
      Set<IIdentifier> results);
}
