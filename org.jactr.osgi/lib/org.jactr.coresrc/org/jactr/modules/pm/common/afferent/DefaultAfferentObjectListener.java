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
package org.jactr.modules.pm.common.afferent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.commonreality.object.manager.IAfferentObjectManager;
import org.commonreality.object.manager.event.IAfferentListener;
import org.commonreality.object.manager.event.IObjectEvent;
import org.commonreality.object.manager.event.ObjectEvent;
import org.commonreality.time.IClock;
import org.eclipse.collections.impl.factory.Maps;
import org.eclipse.collections.impl.factory.Sets;
import org.jactr.core.utils.collections.CachedCollection;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.collections.FastMapFactory;

/**
 * default afferent listener that instead of routing events directly, queues
 * them up and then posts a process event to handle the events. This way, if
 * afferent events are coming faster than the listener can actually process
 * them, they get queued so that only the most recent update needs to be
 * processed
 * 
 * @author developer
 */
public class DefaultAfferentObjectListener implements IAfferentListener,
    Runnable
{

  static private transient Log                LOGGER          = LogFactory
                                                                  .getLog(DefaultAfferentObjectListener.class);

  private Executor                            _executor;

  private Collection<IAfferentObjectListener> _listeners;

  private IAgent                              _agent;

  private Set<IAfferentObject>                _addedObjects;

  private Set<IAfferentObject>                _removedObjects;

  private Map<IAfferentObject, IObjectDelta>  _updatedDeltas;

  private double                              _lastChangeTime = -1;

  private AtomicInteger                       _pendingUpdates = new AtomicInteger();

  public DefaultAfferentObjectListener(IAgent agent, Executor executor)
  {
    _listeners = new CachedCollection<IAfferentObjectListener>(
        new ArrayList<IAfferentObjectListener>());
    _agent = agent;
    _executor = executor;
    _addedObjects = Sets.mutable.empty();
    _removedObjects = Sets.mutable.empty();
    _updatedDeltas = Maps.mutable.empty();
  }

  protected Executor getExecutor()
  {
    return _executor;
  }

  protected IAgent getAgent()
  {
    return _agent;
  }

  public void add(IAfferentObjectListener encoder)
  {
    _listeners.add(encoder);
  }

  public void remove(IAfferentObjectListener encoder)
  {
    _listeners.remove(encoder);
  }

  public void processExistingObjects()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Signalling add for potentially missed objects");

    IAfferentObjectManager manager = getAgent().getAfferentObjectManager();
    Collection<IAfferentObject> objects = new HashSet<IAfferentObject>();
    for (IIdentifier identifier : manager.getIdentifiers())
    {
      IAfferentObject object = manager.get(identifier);
      if (object != null)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("existing object %s", identifier));
        objects.add(object);
      }
    }

    if (objects.size() != 0)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Signalling with mock event");
      objectsAdded(new ObjectEvent<IAfferentObject, IAfferentListener>(
          IObjectEvent.Type.ADDED, objects));
    }
  }

  /**
   * actually process the new adds, updates, and removes
   */
  final public void run()
  {
    List<IAfferentObject> added = FastListFactory.newInstance();
    List<IAfferentObject> removed = FastListFactory.newInstance();
    Map<IAfferentObject, IObjectDelta> deltas = FastMapFactory.newInstance();

    synchronized (_addedObjects)
    {
      added.addAll(_addedObjects);
      _addedObjects.clear();
    }

    synchronized (_removedObjects)
    {
      removed.addAll(_removedObjects);
      _removedObjects.clear();
    }

    synchronized (_updatedDeltas)
    {
      deltas.putAll(_updatedDeltas);
      _updatedDeltas.clear();
    }

    objectsAdded(added);
    _pendingUpdates.addAndGet(-added.size());

    objectsUpdated(deltas);
    _pendingUpdates.addAndGet(-deltas.size());

    objectsRemoved(removed);
    _pendingUpdates.addAndGet(-removed.size());

    FastListFactory.recycle(added);
    FastListFactory.recycle(removed);
    FastMapFactory.recycle(deltas);

    /*
     * if this is being run right after shutdown has staretd, getClock could be
     * null.
     */
    IClock clock = _agent.getClock();
    if (clock != null)
      _lastChangeTime = clock.getTime();
  }

  /**
   * number of updates queued
   * 
   * @return
   */
  public int getPendingUpdates()
  {
    return _pendingUpdates.get();
  }

  public double getLastChangeTime()
  {
    return _lastChangeTime;
  }

  /**
   * @see org.commonreality.object.manager.event.IObjectListener#objectsAdded(org.commonreality.object.manager.event.IObjectEvent)
   */
  public void objectsAdded(IObjectEvent<IAfferentObject, ?> addEvent)
  {
    // for (IAfferentObject object : addEvent.getObjects())
    // objectAdded(object);
    boolean shouldQueue = false;
    synchronized (_addedObjects)
    {
      shouldQueue = _addedObjects.size() == 0;
      Collection<IAfferentObject> added = addEvent.getObjects();
      _addedObjects.addAll(added);
      _pendingUpdates.addAndGet(added.size());
    }

    if (shouldQueue) _executor.execute(this);
  }

  protected void objectsAdded(Collection<IAfferentObject> objects)
  {
    for (IAfferentObject object : objects)
      objectAdded(object);
  }

  protected void objectAdded(IAfferentObject object)
  {
    for (IAfferentObjectListener encoder : _listeners)
      if (encoder.isInterestedIn(object))
        try
        {
          encoder.afferentObjectAdded(object);
        }
        catch (Exception e)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(encoder + " failed to encode afferent object "
                + object.getIdentifier() + " ", e);
        }
  }

  /**
   * @see org.commonreality.object.manager.event.IObjectListener#objectsRemoved(org.commonreality.object.manager.event.IObjectEvent)
   */
  public void objectsRemoved(IObjectEvent<IAfferentObject, ?> removeEvent)
  {
    // for (IAfferentObject object : removeEvent.getObjects())
    // objectRemoved(object);
    boolean shouldQueue = false;
    synchronized (_removedObjects)
    {
      // we only queue if there are still pending removes. the next cycle will
      // catch
      // the new removals
      shouldQueue = _removedObjects.size() == 0;
      Collection<IAfferentObject> removed = removeEvent.getObjects();
      _removedObjects.addAll(removed);
      _pendingUpdates.addAndGet(removed.size());
    }

    if (shouldQueue) _executor.execute(this);
  }

  protected void objectsRemoved(Collection<IAfferentObject> objects)
  {
    for (IAfferentObject object : objects)
      objectRemoved(object);
  }

  protected void objectRemoved(IAfferentObject object)
  {
    for (IAfferentObjectListener encoder : _listeners)
      if (encoder.isInterestedIn(object))
        try
        {
          encoder.afferentObjectRemoved(object);
        }
        catch (Exception e)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(encoder + " failed to remove object "
                + object.getIdentifier() + " ", e);
        }
  }

  /**
   * @see org.commonreality.object.manager.event.IObjectListener#objectsUpdated(org.commonreality.object.manager.event.IObjectEvent)
   */
  public void objectsUpdated(IObjectEvent<IAfferentObject, ?> updateEvent)
  {
    boolean shouldQueue = false;
    IAfferentObjectManager manager = _agent.getAfferentObjectManager();
    synchronized (_updatedDeltas)
    {
      shouldQueue = _updatedDeltas.size() == 0;
      for (IObjectDelta delta : updateEvent.getDeltas())
      {
        IIdentifier id = delta.getIdentifier();
        IAfferentObject object = manager.get(id);
        IObjectDelta oldDelta = _updatedDeltas.get(object);
        if (oldDelta != null)
          oldDelta.merge(delta);
        else
        {
          _updatedDeltas.put(object, delta.copy());
          _pendingUpdates.incrementAndGet();
        }
      }
    }

    if (shouldQueue) _executor.execute(this);
  }

  protected void objectsUpdated(Map<IAfferentObject, IObjectDelta> deltas)
  {
    for (Map.Entry<IAfferentObject, IObjectDelta> delta : deltas.entrySet())
      objectUpdated(delta.getKey(), delta.getValue());
  }

  protected void objectUpdated(IAfferentObject object, IObjectDelta delta)
  {
    for (IAfferentObjectListener encoder : _listeners)
      if (encoder.isInterestedIn(object))
        try
        {
          encoder.afferentObjectUpdated(object, delta);
        }
        catch (Exception e)
        {
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(encoder + " failed to propogate update of "
                + object.getIdentifier(), e);
        }
  }

}
