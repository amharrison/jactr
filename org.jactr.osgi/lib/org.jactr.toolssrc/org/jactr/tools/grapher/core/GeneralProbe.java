package org.jactr.tools.grapher.core;

/*
 * default logging
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.tools.grapher.core.container.IProbeContainer;
import org.jactr.tools.grapher.core.message.NetworkPackager;
import org.jactr.tools.grapher.core.parser.Parser;
import org.jactr.tools.grapher.core.probe.IPollingProbe;
import org.jactr.tools.grapher.core.probe.IProbe;
import org.jactr.tools.grapher.core.selector.ModelSelector;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.listeners.ITraceListener;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class GeneralProbe implements ITraceListener, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log   LOGGER      = LogFactory
                                                       .getLog(GeneralProbe.class);

  static public final String           CONFIG      = "ConfigFile";

  private Collection<ModelSelector>    _modelSelectors;

  private Map<IModel, Collection<IProbeContainer>> _topLevelContainers;

  private double                       _timeWindow = 1;
  
  private NetworkPackager              _packager;
  
  private ITraceSink _sink;

  public GeneralProbe()
  {
    _modelSelectors = new ArrayList<ModelSelector>();
    _topLevelContainers = new HashMap<IModel, Collection<IProbeContainer>>();
    _packager = new NetworkPackager();
  }

  public void setTraceSink(ITraceSink sink)
  {
    _sink = sink;
  }
  

  public void install(IModel model, Executor executor)
  {
    // we ignore the recommended executor and use inline

    for (ModelSelector selector : _modelSelectors)
      if (selector.matches(model))
      {
        IProbeContainer container = selector.install(model, null);
        Collection<IProbeContainer> probeContainers = _topLevelContainers
            .get(model);
        if (probeContainers == null)
        {
          probeContainers = new ArrayList<IProbeContainer>();
          _topLevelContainers.put(model, probeContainers);
        }
        probeContainers.add(container);

        // we need to poll/process inline
        model.addListener(new ProbeProcessor(container),
            ExecutorServices.INLINE_EXECUTOR);
      }
  }

  public void uninstall(IModel model)
  {
    
  }

  protected void processProbes(IProbeContainer topLevelContainer, double when)
  {
    Map<String, Object> data = new TreeMap<String, Object>();
    processContainer(topLevelContainer, data);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("ProcessedData : " + data);

    final Collection<ITransformedEvent> events = FastListFactory.newInstance();
    events.addAll(_packager.process(topLevelContainer.getName(), data, when,
        _timeWindow));

    /*
     * push the networking onto the background..
     */
    if (events.size() > 0)
      ExecutorServices.getExecutor(ExecutorServices.BACKGROUND).execute(
          new Runnable() {
            public void run()
            {
              for (ITransformedEvent event : events)
                _sink.add(event);
            }
          });
  }

  protected void processContainer(IProbeContainer container,
      Map<String, Object> data)
  {
    String name = container.getName();
    Map<String, Object> myData = new TreeMap<String, Object>();
    data.put(name, myData);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Processing probes " + name);

    Set<String> additions = new TreeSet<String>();
    Set<String> removed = new TreeSet<String>();

    List<IProbe> probes = FastListFactory.newInstance();
    /*
     * take care of the top probes first
     */
    for (IProbe probe : container.getProbes(probes))
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Processing probe " + probe.getTrackedName());

      if (probe instanceof IPollingProbe) ((IPollingProbe) probe).update();

      Map<String, Object> changes = new TreeMap<String, Object>();
      if (probe.getChanges(additions, changes, removed))
      {
        myData.put(probe.getTrackedName(), changes);
        additions.clear();
        removed.clear();
      }
    }

    FastListFactory.recycle(probes);

    List<IProbeContainer> children = FastListFactory.newInstance();

    /*
     * descend
     */
    for (IProbeContainer child : container.getChildren(children))
      processContainer(child, myData);

    FastListFactory.recycle(children);
  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return getSetableParameters();
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.singleton(CONFIG);
  }

  public void setParameter(String key, String value)
  {
    if (CONFIG.equalsIgnoreCase(key))
      try
      {
        URL resource = getClass().getClassLoader().getResource(value);
        if (resource == null)
          throw new IllegalArgumentException(
              "Could not find file on classpath " + value);
        Parser parser = new Parser(resource);
        _timeWindow = parser.getTimeWindow();
        _modelSelectors = parser.buildModelSelectors();
      }
      catch (Exception e)
      {
        LOGGER.error("Could not configure general probe ", e);
        _timeWindow = 1;
        _modelSelectors.clear();
        _topLevelContainers.clear();
      }
  }

  private class ProbeProcessor extends ModelListenerAdaptor
  {
    private double         _nextCheckTime = Double.MIN_VALUE;

    public IProbeContainer _container;

    public ProbeProcessor(IProbeContainer container)
    {
      _container = container;
    }

    @Override
    public void cycleStopped(ModelEvent event)
    {
      double time = event.getSimulationTime();
      if (time < _nextCheckTime) return;

      processProbes(_container, time);

      _nextCheckTime = time + _timeWindow;
    }
    
    @Override
    public void modelStopped(ModelEvent event)
    {
      processProbes(_container, event.getSimulationTime());
    }
  }

 

  
}
