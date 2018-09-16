package org.jactr.modules.pm.visual.scene;

/*
 * default logging
 */
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.commonreality.modalities.visual.DefaultVisualPropertyHandler;
import org.commonreality.modalities.visual.IVisualPropertyHandler;
import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;
import org.commonreality.object.manager.event.IAfferentListener;
import org.commonreality.object.manager.event.IObjectEvent;
import org.jactr.modules.pm.common.memory.map.FeatureMapEvent;
import org.jactr.modules.pm.common.memory.map.IFeatureMapListener;
import org.jactr.modules.pm.visual.IVisualModule;

/**
 * Scene change listener that implements both {@link IVisualFeatureMapListener} to
 * track changes to the {@link IVisualModule}'s {@link IVisualMap}'s feature maps, and
 * {@link IAfferentListener} which allows the listener to circumvent the visual module
 * and go straight to the source of the percepts. <br>
 * <br>
 * The listener works by collecting the {@link IIdentifier}s of all the percepts
 * that change between calls to {@link #reset()}, call to {@link #check()} will
 * recompute the change ratio ( {@link #getChangeRatio()} ).
 * <br>
 * 
 * @author harrison
 */
public class SceneChangeListener implements IFeatureMapListener,
    IAfferentListener
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER           = LogFactory
                                                            .getLog(SceneChangeListener.class);

  final private Set<IIdentifier>       _allIdentifiers;

  final private Set<IIdentifier>       _changedIdentifiers;

  final private Set<IIdentifier>       _removedIdentifiers;

  private volatile double              _changeRatio     = 0;

  final private ReentrantLock          _lock            = new ReentrantLock();

  final private IVisualPropertyHandler _propertyHandler = new DefaultVisualPropertyHandler();

  public SceneChangeListener()
  {
    _allIdentifiers = new HashSet<IIdentifier>();
    _changedIdentifiers = new HashSet<IIdentifier>();
    _removedIdentifiers = new HashSet<IIdentifier>();
  }

  /**
   * will be called when any new percept is added, we check to see if it
   * is a visual percept and log it
   * @param addEvent
   * @see org.commonreality.object.manager.event.IObjectListener#objectsAdded(org.commonreality.object.manager.event.IObjectEvent)
   */
  public void objectsAdded(IObjectEvent<IAfferentObject, ?> addEvent)
  {
    for (IAfferentObject object : addEvent.getObjects())
      if (_propertyHandler.hasModality(object))
        changed(object.getIdentifier());
  }

  /**
   * called when a percept is removed. if it is visual, we log it
   * @param removeEvent
   * @see org.commonreality.object.manager.event.IObjectListener#objectsRemoved(org.commonreality.object.manager.event.IObjectEvent)
   */
  public void objectsRemoved(IObjectEvent<IAfferentObject, ?> removeEvent)
  {
    for (IAfferentObject object : removeEvent.getObjects())
      if (_propertyHandler.hasModality(object))
        removed(object.getIdentifier());
  }

  /**
   * called when a percept changes, we determine if it is relevant and log it
   * @param updateEvent
   * @see org.commonreality.object.manager.event.IObjectListener#objectsUpdated(org.commonreality.object.manager.event.IObjectEvent)
   */
  public void objectsUpdated(IObjectEvent<IAfferentObject, ?> updateEvent)
  {
    try
    {
      _lock.lock();
      for (IObjectDelta delta : updateEvent.getDeltas())
      {
        IIdentifier id = delta.getIdentifier();
        if (_allIdentifiers.contains(id) || _changedIdentifiers.contains(id))
          changed(id);
      }
    }
    finally
    {
      _lock.unlock();
    }
  }

  

 

 
  /**
   * log the id of the changed percept. while this is only called on the CR
   * thread, we need to use the lock since {@link #reset()} can be called from
   * anywhere
   * 
   * @param id
   */
  private void changed(IIdentifier id)
  {
    try
    {
      _lock.lock();
      _changedIdentifiers.add(id);
    }
    finally
    {
      _lock.unlock();
    }
  }
  
  private void changed(Collection<IIdentifier> ids)
  {
    try
    {
      _lock.lock();
      _changedIdentifiers.addAll(ids);
    }
    finally
    {
      _lock.unlock();
    }
  }

  private void removed(Collection<IIdentifier> ids)
  {
    try
    {
      _lock.lock();
      _removedIdentifiers.addAll(ids);
    }
    finally
    {
      _lock.unlock();
    }
  }
  
  /**
   * log the id of the removed percept
   * 
   * @param id
   */
  private void removed(IIdentifier id)
  {
    try
    {
      _lock.lock();
      _removedIdentifiers.add(id);
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * reset establishes a new baseline. This may be called by any thread, so it
   * is locked
   */
  protected void reset()
  {
    try
    {
      _lock.lock();

      _allIdentifiers.addAll(_changedIdentifiers);
      _allIdentifiers.removeAll(_removedIdentifiers);

      _removedIdentifiers.clear();
      _changedIdentifiers.clear();

      _changeRatio = 0;
    }
    finally
    {
      _lock.unlock();
    }
  }

  /**
   * calculate and return the change ratio since the last {@link #reset()}
   * 
   * @return
   */
  protected double check()
  {
    /*
     * since we're just doing size checks, there's no need to synchronize
     */
    _changeRatio = Math.min(
        (double) (_changedIdentifiers.size() + _removedIdentifiers.size())
            / (double) _allIdentifiers.size(), 1);
    return _changeRatio;
  }

  public double getChangeRatio()
  {
    return _changeRatio;
  }

  public void featureAdded(FeatureMapEvent event)
  {
    changed(event.getIdentifiers());
    
  }

  public void featureRemoved(FeatureMapEvent event)
  {
    removed(event.getIdentifiers());
    
  }

  public void featureUpdated(FeatureMapEvent event)
  {
    changed(event.getIdentifiers());
  }

}
