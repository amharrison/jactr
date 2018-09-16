package org.jactr.tools.tracer.listeners;

/*
 * default logging
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.identifier.IIdentifier;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.event.IParameterEvent;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.IModule;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.modules.pm.common.event.IPerceptualMemoryModuleEvent;
import org.jactr.modules.pm.common.memory.map.FeatureMapEvent;
import org.jactr.modules.pm.common.memory.map.IFeatureMap;
import org.jactr.modules.pm.common.memory.map.IFeatureMapListener;
import org.jactr.modules.pm.visual.IVisualModule;
import org.jactr.modules.pm.visual.event.IVisualModuleListener;
import org.jactr.modules.pm.visual.event.VisualModuleEvent;
import org.jactr.tools.tracer.transformer.visual.TransformedVisualEvent;
import org.jactr.tools.tracer.transformer.visual.VisualEventTransformer;

public class VisualModuleTracer extends BaseTraceListener
{
  /**
   * Logger definition
   */
  static private final transient Log                         LOGGER = LogFactory
                                                                        .getLog(VisualModuleTracer.class);

  private IModelListener                                     _outputListener;

  private IVisualModuleListener                              _visualListener;

  private IFeatureMapListener                                _featureListener;

  private Executor                                           _traceExecutor;

  private Map<IModel, Collection<TransformedVisualEvent>>    _pendingEvents;

  private Map<IModel, Set<IIdentifier>>                      _addedIds;

  private Map<IModel, Set<IIdentifier>>                      _removedIds;

  private Map<IModel, Map<IIdentifier, Map<String, Object>>> _updates;

  public VisualModuleTracer()
  {
    setEventTransformer(new VisualEventTransformer());

    _pendingEvents = new HashMap<IModel, Collection<TransformedVisualEvent>>();
    _addedIds = new HashMap<IModel, Set<IIdentifier>>();
    _removedIds = new HashMap<IModel, Set<IIdentifier>>();
    _updates = new HashMap<IModel, Map<IIdentifier, Map<String, Object>>>();

    _outputListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStopped(ModelEvent me)
      {
        dumpPendingEvents(me.getSource());
      }
    };

    _visualListener = new IVisualModuleListener() {

      public void perceptAttended(IPerceptualMemoryModuleEvent event)
      {
        TransformedVisualEvent tve = (TransformedVisualEvent) getEventTransformer()
            .transform(event);
        if (tve != null)
          getEvents(((IModule) event.getSource()).getModel()).add(tve);
      }

      public void perceptIndexFound(IPerceptualMemoryModuleEvent event)
      {
        TransformedVisualEvent tve = (TransformedVisualEvent) getEventTransformer()
            .transform(event);
        if (tve != null)
          getEvents(((IModule) event.getSource()).getModel()).add(tve);
      }

      public void trackedObjectMoved(VisualModuleEvent event)
      {
      }

      public void trackingObjectStarted(VisualModuleEvent event)
      {
      }

      public void trackingObjectStopped(VisualModuleEvent event)
      {
      }

      public void parameterChanged(IParameterEvent pe)
      {
      }

      public void moduleReset(IPerceptualMemoryModuleEvent event)
      {

      }

    };

    _featureListener = new IFeatureMapListener() {

      public void featureAdded(FeatureMapEvent event)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Got feature add " + event);

        IModel model = event.getSource().getPerceptualMemory().getModule()
            .getModel();

        IIdentifier id = event.getIdentifiers().iterator().next();
        Set<IIdentifier> added = getAdded(model);
        if (added.contains(id)) return;

        TransformedVisualEvent tve = (TransformedVisualEvent) getEventTransformer()
            .transform(event);
        if (tve != null) getEvents(model).add(tve);
        added.add(id);
      }

      public void featureRemoved(FeatureMapEvent event)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("got feature remove " + event);

        IModel model = event.getSource().getPerceptualMemory().getModule()
            .getModel();

        IIdentifier id = event.getIdentifiers().iterator().next();
        Set<IIdentifier> removed = getRemoved(model);
        if (removed.contains(id)) return;

        TransformedVisualEvent tve = (TransformedVisualEvent) getEventTransformer()
            .transform(event);
        if (tve != null) getEvents(model).add(tve);
        removed.add(id);
      }

      public void featureUpdated(FeatureMapEvent event)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug("got feature update " + event);

        IModel model = event.getSource().getPerceptualMemory().getModule()
            .getModel();

        IIdentifier id = event.getIdentifiers().iterator().next();
        Map<IIdentifier, Map<String, Object>> updates = getUpdates(model);
        Map<String, Object> data = updates.get(id);

        if (data == null)
        {
          data = new TreeMap<String, Object>();

          TransformedVisualEvent tve = (TransformedVisualEvent) getEventTransformer()
              .transform(event);
          if (tve != null) getEvents(model).add(tve);
          updates.put(id, data);
        }

        updateData(event.getSource(), id, data);
      }

    };

  }

  public void install(IModel model, Executor executor)
  {
    _traceExecutor = executor;
    // we dump to sink async..
    model.addListener(_outputListener, _traceExecutor);

    // but since these come in on CR thread, we want to create
    // the transformed event immediately, and let the async dump
    executor = ExecutorServices.INLINE_EXECUTOR;
    IVisualModule vis = (IVisualModule) model.getModule(IVisualModule.class);
    if (vis != null)
    {
      vis.addListener(_visualListener, executor);

      List<IFeatureMap> featureMaps = FastListFactory.newInstance();
      vis.getVisualMemory().getFeatureMaps(featureMaps);

      for (IFeatureMap map : featureMaps)
      {
        if (LOGGER.isDebugEnabled()) LOGGER.debug("Attaching to " + map);
        map.addListener(_featureListener, executor);
      }
      FastListFactory.recycle(featureMaps);
    }
  }

  public void uninstall(IModel model)
  {
    model.removeListener(_outputListener);
    IVisualModule vis = (IVisualModule) model.getModule(IVisualModule.class);
    if (vis != null)
    {
      vis.removeListener(_visualListener);
      List<IFeatureMap> featureMaps = FastListFactory.newInstance();
      vis.getVisualMemory().getFeatureMaps(featureMaps);

      for (IFeatureMap map : featureMaps)
        map.removeListener(_featureListener);

      FastListFactory.recycle(featureMaps);
    }

  }

  synchronized protected Collection<TransformedVisualEvent> getEvents(
      IModel model)
  {
    Collection<TransformedVisualEvent> events = _pendingEvents.get(model);
    if (events == null)
    {
      events = Collections
          .synchronizedList(new ArrayList<TransformedVisualEvent>(100));
      _pendingEvents.put(model, events);
    }
    return events;
  }

  synchronized protected Set<IIdentifier> getAdded(IModel model)
  {
    Set<IIdentifier> added = _addedIds.get(model);
    if (added == null)
    {
      added = Collections.synchronizedSet(new HashSet<IIdentifier>());
      _addedIds.put(model, added);
    }
    return added;
  }

  synchronized protected void getAdded(IModel model, Set<IIdentifier> container)
  {
    Set<IIdentifier> source = getAdded(model);
    synchronized (source)
    {
      container.addAll(source);
    }
  }

  synchronized protected Set<IIdentifier> getRemoved(IModel model)
  {
    Set<IIdentifier> removed = _removedIds.get(model);
    if (removed == null)
    {
      removed = Collections.synchronizedSet(new HashSet<IIdentifier>());
      _removedIds.put(model, removed);
    }
    return removed;
  }

  synchronized protected void getRemoved(IModel model,
      Set<IIdentifier> container)
  {
    Set<IIdentifier> source = getRemoved(model);
    synchronized (source)
    {
      container.addAll(source);
    }
  }

  synchronized protected Map<IIdentifier, Map<String, Object>> getUpdates(
      IModel model)
  {
    Map<IIdentifier, Map<String, Object>> updates = _updates.get(model);
    if (updates == null)
    {
      updates = Collections
          .synchronizedMap(new HashMap<IIdentifier, Map<String, Object>>());
      _updates.put(model, updates);
    }
    return updates;
  }

  protected Map<String, Object> getUpdates(
      Map<IIdentifier, Map<String, Object>> updates, IIdentifier object,
      boolean create)
  {
    Map<String, Object> map = updates.get(object);
    if (map == null) if (create)
    {
      map = new TreeMap<String, Object>();
      updates.put(object, map);
    }
    else
      map = Collections.emptyMap();

    return map;
  }

  protected void updateAllData(IModel model, IIdentifier identifier,
      Map<String, Object> data)
  {
    List<IFeatureMap> featureMaps = FastListFactory.newInstance();
    try
    {
      ((IVisualModule) model.getModule(IVisualModule.class)).getVisualMemory()
          .getFeatureMaps(featureMaps);

      for (IFeatureMap featureMap : featureMaps)
        updateData(featureMap, identifier, data);
    }
    finally
    {
      FastListFactory.recycle(featureMaps);
    }
  }

  protected void updateData(IFeatureMap featureMap, IIdentifier identifier,
      Map<String, Object> data)
  {
    Object info = featureMap.getInformation(identifier);

    if (info instanceof Serializable)
      data.put(featureMap.getClass().getSimpleName(), info);
  }

  synchronized protected void dumpPendingEvents(IModel model)
  {
    Map<IIdentifier, Map<String, Object>> updates = getUpdates(model);
    /*
     * make sure the added have all their data
     */

    // need to make thread safe.
    Collection<IIdentifier> added = getAdded(model);
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Getting full data for " + added.size() + " add events");

    for (IIdentifier add : added)
      updateAllData(model, add, getUpdates(updates, add, true));

    Collection<TransformedVisualEvent> events = getEvents(model);
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Processing " + events.size() + " visual events");

    for (TransformedVisualEvent event : events)
    {
      TransformedVisualEvent.Type type = event.getType();
      /*
       * if it is an add or update, we need to get the collated properties
       */
      if (type == TransformedVisualEvent.Type.ADDED
          || type == TransformedVisualEvent.Type.UPDATED)
        event.getData().putAll(
            getUpdates(updates, event.getIdentifier(), false));

      sink(event);
    }

    added.clear();
    getRemoved(model).clear();
    updates.clear();
    events.clear();
  }
}
