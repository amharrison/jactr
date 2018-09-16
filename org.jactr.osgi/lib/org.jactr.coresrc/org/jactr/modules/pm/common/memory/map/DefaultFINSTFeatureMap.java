/*
 * Created on Jun 26, 2007 Copyright (C) 2001-2007, Anthony Harrison
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
package org.jactr.modules.pm.common.memory.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.event.ACTREventDispatcher;
import org.jactr.core.model.IModel;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.queue.timedevents.AbstractTimedEvent;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.BasicSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.FastSetFactory;
import org.jactr.modules.pm.common.memory.IPerceptualMemory;

/**
 * FINST tracking feature map that is used by both the aural and visual modules.
 * Unlike all the other {@link IFeatureMap}s, this one is accessed by both the
 * common reality and model threads. As such, it is necessary to use a
 * {@link ReentrantReadWriteLock} to protect the data. Commonreality accesses
 * this feature map (like all the other feature maps) through the
 * {@link #afferentObjectAdded(IAfferentObject)},
 * {@link #afferentObjectRemoved(IAfferentObject)},
 * {@link #afferentObjectUpdated(IAfferentObject, IObjectDelta)} methods. The
 * model thread accesses it indirectly through the {@link FlagAsOld} timed event
 * which is posted by any call to
 * {@link #flagAsAttended(IIdentifier, IChunk, double)},
 * {@link #flagAsNew(IIdentifier, IChunk, double)}, or
 * {@link #flagAsOld(IIdentifier, IChunk)}.<br>
 * <br>
 * Without this thread synchronization, deadlock or concurrent modifications
 * would occur.<br>
 * <br>
 * There is still much improvement that can be made in terms of the granularity
 * and speed, the current thread safety is course and rather slow. [the old,
 * unprotected version ran allowed models to run at 500x real time, this version
 * brings it down to 250x] <br>
 * <br>
 * Note: this does not fire events
 * 
 * @author developer
 */
public class DefaultFINSTFeatureMap implements IFINSTFeatureMap
{
  /**
   * logger definition
   */
  static private final Log                                      LOGGER      = LogFactory
                                                                                .getLog(DefaultFINSTFeatureMap.class);

  private int                                                   _maximumFINSTs;

  private String                                                _attendedSlotName;

  IModel                                                        _model;

  IChunk                                                        _newChunk;

  Map<IIdentifier, FINST>                                       _attendedIdentifiers;

  Map<IIdentifier, FINST>                                       _oldIdentifiers;

  Map<IIdentifier, FINST>                                       _newIdentifiers;

  @SuppressWarnings("unchecked")
  private ACTREventDispatcher<IFeatureMap, IFeatureMapListener> _dispatcher = new ACTREventDispatcher<IFeatureMap, IFeatureMapListener>();

  private IPerceptualMemory                                     _memory;

  private ReentrantReadWriteLock                                _lock       = new ReentrantReadWriteLock();

  public DefaultFINSTFeatureMap(IModel model, String attendedSlotName)
  {
    _attendedSlotName = attendedSlotName;
    _model = model;
    _newChunk = model.getDeclarativeModule().getNewChunk();

    _newIdentifiers = new HashMap<IIdentifier, FINST>();
    _oldIdentifiers = new HashMap<IIdentifier, FINST>();
    _attendedIdentifiers = new HashMap<IIdentifier, FINST>();
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

  /**
   * @return
   * @see org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap#getNew()
   */
  public void getNew(Set<IIdentifier> destination)
  {
    try
    {
      _lock.readLock().lock();
      destination.addAll(_newIdentifiers.keySet());
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public boolean isNew(IIdentifier identifier)
  {
    try
    {
      _lock.readLock().lock();
      return _newIdentifiers.containsKey(identifier);
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public void getOld(Set<IIdentifier> destination)
  {
    try
    {
      _lock.readLock().lock();

      destination.addAll(_oldIdentifiers.keySet());
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public boolean isOld(IIdentifier identifier)
  {
    try
    {
      _lock.readLock().lock();

      return _oldIdentifiers.containsKey(identifier);
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public void getAttended(Set<IIdentifier> destination)
  {
    try
    {
      _lock.readLock().lock();

      destination.addAll(_attendedIdentifiers.keySet());
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public boolean isAttended(IIdentifier identifier)
  {
    try
    {
      _lock.readLock().lock();

      return _attendedIdentifiers.containsKey(identifier);
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  protected FINST getFINST(IIdentifier identifier)
  {
    try
    {
      _lock.readLock().lock();

      FINST finst = _newIdentifiers.get(identifier);
      if (finst == null) finst = _oldIdentifiers.get(identifier);
      if (finst == null) finst = _attendedIdentifiers.get(identifier);
      return finst;
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  public int getMaximumFINSTs()
  {
    return _maximumFINSTs;
  }

  public void setMaximumFINSTs(int max)
  {
    _maximumFINSTs = max;
  }

  private void assignFINST(FINST finst)
  {
    IIdentifier identifier = finst.getIdentifier();
    if (isAttended(identifier))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(identifier + " already has a finst assigned");
      // do nothing..
      return;
    }

    try
    {
      _lock.writeLock().lock();

      while (_attendedIdentifiers.size() >= getMaximumFINSTs())
      {
        FINST oldFinst = findOldestFINST();
        if (oldFinst != null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("FINST limit reached, removing oldest : "
                + oldFinst.getIdentifier());
          flagAsOld(oldFinst.getIdentifier(), oldFinst.getAfferentChunk());
        }
      }

      _attendedIdentifiers.put(identifier, finst);
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("assigning finst to " + identifier);
  }

  /**
   * find the oldest finst, this should only be called from assignFinst as it
   * requires write lock
   * 
   * @return
   */
  private FINST findOldestFINST()
  {
    try
    {
      _lock.writeLock().lock();

      FINST oldest = null;
      double time = Double.POSITIVE_INFINITY;
      for (FINST finst : _attendedIdentifiers.values())
        if (finst.getTime() < time) oldest = finst;
      return oldest;
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap#flagAsAttended(org.commonreality.identifier.IIdentifier,
   *      org.jactr.core.chunk.IChunk)
   */
  public void flagAsAttended(IIdentifier identifier, IChunk chunk,
      double duration)
  {
    FINST finst = null;

    FlagAsOld fao = this.new FlagAsOld(identifier, chunk, duration);
    try
    {
      _lock.writeLock().lock();

      finst = removeFINST(identifier);

      if (finst == null) finst = _newIdentifiers.remove(identifier);

      // already attended?
      if (finst == null) finst = removeFINST(identifier);

      if (finst == null)
      {
        LOGGER.warn("Flagging " + identifier
            + " as attended, but it was never new or old");
        finst = new FINST(identifier, chunk);
      }

      finst.setAfferentChunk(chunk);

      assignFINST(finst);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Old: " + _oldIdentifiers.keySet() + " New: "
            + _newIdentifiers.keySet() + " Att:"
            + _attendedIdentifiers.keySet());

      finst.setFlagAsOld(fao);
    }
    finally
    {
      _lock.writeLock().unlock();
    }


    // add the timed event to expire the attended
    _model.getTimedEventQueue().enqueue(fao);

    if (hasListeners())
      dispatch(new FeatureMapEvent(this, ACTRRuntime.getRuntime()
          .getClock(getPerceptualMemory().getModule().getModel()).getTime(),
          FeatureMapEvent.Type.UPDATED, Collections.singleton(identifier)));
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap#flagAsNew(org.commonreality.identifier.IIdentifier,
   *      org.jactr.core.chunk.IChunk)
   */
  public void flagAsNew(IIdentifier identifier, IChunk chunk, double duration)
  {
    FINST finst = null;
    FlagAsOld fao = this.new FlagAsOld(identifier, chunk, duration);
    try
    {
      _lock.writeLock().lock();

      finst = removeFINST(identifier);

      if (finst == null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Created new finst object to track " + identifier);
        finst = this.new FINST(identifier, chunk);
      }
      else
        finst.setAfferentChunk(chunk);

      _newIdentifiers.put(identifier, finst);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Old: " + _oldIdentifiers.keySet() + " New: "
            + _newIdentifiers.keySet() + " Att:"
            + _attendedIdentifiers.keySet());

      finst.setFlagAsOld(fao);
    }
    finally
    {
      _lock.writeLock().unlock();
    }


    // add the timed event to expire the new
    _model.getTimedEventQueue().enqueue(fao);

    if (hasListeners())
      dispatch(new FeatureMapEvent(this, ACTRRuntime.getRuntime()
          .getClock(getPerceptualMemory().getModule().getModel()).getTime(),
          FeatureMapEvent.Type.UPDATED, Collections.singleton(identifier)));
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap#flagAsOld(org.commonreality.identifier.IIdentifier,
   *      org.jactr.core.chunk.IChunk)
   */
  public void flagAsOld(IIdentifier identifier, IChunk chunk)
  {
    try
    {
      _lock.writeLock().lock();

      FINST finst = removeFINST(identifier);

      if (finst == null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Flagging " + identifier
            + " as old, but it was never new or attended");
        finst = new FINST(identifier, chunk);
      }

      finst.setFlagAsOld(null);
      _oldIdentifiers.put(identifier, finst);

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Old: " + _oldIdentifiers.keySet() + " New: "
            + _newIdentifiers.keySet() + " Att:"
            + _attendedIdentifiers.keySet());
    }
    finally
    {
      _lock.writeLock().unlock();
    }

    if (hasListeners())
      dispatch(new FeatureMapEvent(this, ACTRRuntime.getRuntime()
          .getClock(getPerceptualMemory().getModule().getModel()).getTime(),
          FeatureMapEvent.Type.UPDATED, Collections.singleton(identifier)));
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFINSTFeatureMap#reset()
   */
  public void reset()
  {
    Collection<FINST> finsts = null;
    try
    {
      _lock.readLock().lock();
      finsts = new ArrayList<FINST>(_attendedIdentifiers.values());

    }
    finally
    {
      _lock.readLock().unlock();
    }

    for (FINST finst : finsts)
      flagAsOld(finst.getIdentifier(), finst.getAfferentChunk());
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#clear()
   */
  public void clear()
  {
    try
    {
      _lock.writeLock().lock();

      _newIdentifiers.clear();
      _oldIdentifiers.clear();
      _attendedIdentifiers.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#dispose()
   */
  public void dispose()
  {
    clear();
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#fillSlotValues(ChunkTypeRequest,
   *      org.commonreality.identifier.IIdentifier, IChunk, ChunkTypeRequest)
   */
  public void fillSlotValues(ChunkTypeRequest mutableRequest,
      IIdentifier identifier, IChunk encodedChunk,
      ChunkTypeRequest originalSearchRequest)
  {
    boolean isNew = false;
    boolean isAttended = false;

    FINST finst = getFINST(identifier);

    isNew = isNew(identifier);
    isAttended = isAttended(identifier);

    if (finst != null) if (isNew)
      mutableRequest.addSlot(new BasicSlot(_attendedSlotName, _newChunk));
    else if (isAttended)
      mutableRequest.addSlot(new BasicSlot(_attendedSlotName, Boolean.TRUE));
    else
      mutableRequest.addSlot(new BasicSlot(_attendedSlotName, null));
  }

  public FINSTState getInformation(IIdentifier identifier)
  {
    try
    {
      _lock.readLock().lock();
      if (_newIdentifiers.containsKey(identifier)) return FINSTState.NEW;
      if (_oldIdentifiers.containsKey(identifier)) return FINSTState.OLD;
      if (_attendedIdentifiers.containsKey(identifier))
        return FINSTState.ATTENDED;

      return FINSTState.UNKNOWN;
    }
    finally
    {
      _lock.readLock().unlock();
    }
  }

  /**
   * @see org.jactr.modules.pm.common.memory.map.IFeatureMap#getCandidateRealObjects(ChunkTypeRequest,
   *      Set)
   */
  public void getCandidateRealObjects(ChunkTypeRequest request,
      Set<IIdentifier> container)
  {

    Set<IIdentifier> tmp = FastSetFactory.newInstance();
    boolean firstInsertion = true;
    for (IConditionalSlot slot : request.getConditionalSlots())
      if (slot.getName().equalsIgnoreCase(_attendedSlotName))
      {
        tmp.clear();
        Object value = slot.getValue();
        switch (slot.getCondition())
        {
          case IConditionalSlot.NOT_EQUALS:
            not(value, tmp);
            break;
          default:
            equals(value, tmp);
            break;
        }

        if (firstInsertion)
        {
          container.addAll(tmp);
          firstInsertion = false;
        }
        else
          container.retainAll(tmp);
      }

    FastSetFactory.recycle(tmp);
  }

  private void equals(Object value, Set<IIdentifier> container)
  {
    if (_newChunk.equals(value))
      getNew(container);
    else if (Boolean.TRUE.equals(value))
      getAttended(container);
    else if (Boolean.FALSE.equals(value) || value == null) getOld(container);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("==" + value + " returning " + container);
  }

  private void not(Object value, Set<IIdentifier> container)
  {
    if (_newChunk.equals(value))
    {
      // all but new
      getAttended(container);
      getOld(container);
    }
    else if (Boolean.TRUE.equals(value))
    {
      getOld(container);
      getNew(container);
    }
    else if (Boolean.FALSE.equals(value) || value == null)
    {
      getNew(container);
      getAttended(container);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("!=" + value + " returning " + container);
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectAdded(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectAdded(IAfferentObject object)
  {
    // NoOp
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectRemoved(org.commonreality.object.IAfferentObject)
   */
  public void afferentObjectRemoved(IAfferentObject object)
  {
    IIdentifier identifier = object.getIdentifier();
    FINST finst = removeFINST(identifier);

    if (finst != null && finst.getFlagAsOld() != null)
      finst.getFlagAsOld().abort();

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("removed " + identifier + " old:" + _oldIdentifiers.keySet()
          + " new:" + _newIdentifiers.keySet() + " att:"
          + _attendedIdentifiers.keySet());
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#afferentObjectUpdated(org.commonreality.object.IAfferentObject,
   *      org.commonreality.object.delta.IObjectDelta)
   */
  public void afferentObjectUpdated(IAfferentObject object, IObjectDelta delta)
  {
    // NoOp?
  }

  /**
   * @see org.jactr.modules.pm.common.afferent.IAfferentObjectListener#isInterestedIn(org.commonreality.object.IAfferentObject)
   */
  public boolean isInterestedIn(IAfferentObject object)
  {
    return false;
  }

  public boolean isInterestedIn(ChunkTypeRequest request)
  {
    for (ISlot slot : request.getSlots())
      if (slot.getName().equalsIgnoreCase(_attendedSlotName)) return true;
    return false;
  }

  private FINST removeFINST(IIdentifier identifier)
  {
    try
    {
      _lock.writeLock().lock();

      if (isAttended(identifier))
        return _attendedIdentifiers.remove(identifier);

      if (isOld(identifier)) return _oldIdentifiers.remove(identifier);

      if (isNew(identifier)) return _newIdentifiers.remove(identifier);

      return null;
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  private class FlagAsOld extends AbstractTimedEvent
  {
    protected IIdentifier _identifier;

    protected IChunk      _afferentChunk;

    public FlagAsOld(IIdentifier identifier, IChunk afferentChunk, double offset)
    {
      _identifier = identifier;
      _afferentChunk = afferentChunk;
      double start = ACTRRuntime.getRuntime().getClock(_model).getTime();
      double end = start + offset;
      setTimes(start, end);
    }

    @Override
    public void fire(double currentTime)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("firing " + this);
      if (!hasFired() && !hasAborted())
      {
        super.fire(currentTime);
        flagAsOld(_identifier, _afferentChunk);
      }
    }

    @Override
    public String toString()
    {
      StringBuilder sb = new StringBuilder("(");
      sb.append(getClass().getSimpleName());
      sb.append(": start=").append(getStartTime());
      sb.append(" end=").append(getEndTime());
      sb.append(" id=").append(_identifier);
      sb.append(" chunk=").append(_afferentChunk);
      sb.append(" hash=").append(hashCode());
      sb.append(")");
      return sb.toString();
    }
  }

  protected class FINST
  {
    protected double      _time;

    protected IChunk      _afferentChunk;

    protected IIdentifier _identifier;

    protected FlagAsOld   _timedEvent;

    public FINST(IIdentifier identifier, IChunk afferentChunk)
    {
      _identifier = identifier;
      setAfferentChunk(afferentChunk);
    }

    public void setFlagAsOld(FlagAsOld timedEvent)
    {
      if (_timedEvent != null && !_timedEvent.hasAborted()
          && !_timedEvent.hasFired())
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("Aborting old timed event " + _timedEvent);
        _timedEvent.abort();
      }
      _timedEvent = timedEvent;
    }

    public FlagAsOld getFlagAsOld()
    {
      return _timedEvent;
    }

    public IChunk getAfferentChunk()
    {
      return _afferentChunk;
    }

    public void setAfferentChunk(IChunk afferentChunk)
    {
      _afferentChunk = afferentChunk;
      _time = ACTRRuntime.getRuntime().getClock(_model).getTime();
    }

    public IIdentifier getIdentifier()
    {
      return _identifier;
    }

    public double getTime()
    {
      return _time;
    }
  }

  public void normalizeRequest(ChunkTypeRequest request)
  {

  }

}
