/*
 * Created on Apr 24, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.tracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.IDebugController;
import org.jactr.core.runtime.controller.debug.event.BreakpointEvent;
import org.jactr.core.runtime.controller.debug.event.IBreakpointListener;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;
import org.jactr.tools.misc.ModelsLock;
import org.jactr.tools.tracer.listeners.ITraceListener;
import org.jactr.tools.tracer.sinks.ChainedSink;

/**
 * tracer that can listen and record the actions of all running models. At the
 * start of the run (via ACTRRuntimeEvent.STARTED), the runtime tracer snags the
 * current controller, which must be NetworkedIOHandler
 * 
 * @author developer
 */
public class RuntimeTracer implements IInstrument, IParameterized
{
  /**
   * logger definition
   */
  static public final Log            LOGGER                = LogFactory
                                                               .getLog(RuntimeTracer.class);

  static public final String         EXECUTOR_PARAM        = "Executor";

  static public final String         SINK_CLASS            = "ITraceSinkClass";

  static public final String         LISTENERS             = "ListenerClasses";

  private ITraceSink                 _dataSink;

  private Collection<ITraceListener> _listeners;

  private Set<IModel>                _attachedModels       = new HashSet<IModel>();

  private Map<String, String>        _deferredParameters   = new TreeMap<String, String>();

  private IModelListener             _modelListener        = null;

  private String                     _executorName         = ExecutorServices.BACKGROUND;

  private Executor                   _executor             = ExecutorServices
                                                               .getExecutor(_executorName);

  private static long                SYNCHRONIZATION_THRESHOLD;

  static
  {
    try
    {
      SYNCHRONIZATION_THRESHOLD = Long.parseLong(System.getProperty(
          "jactr.runtimeTracer.synchronizationInterval", "2000"));
    }
    catch (Exception e)
    {
      SYNCHRONIZATION_THRESHOLD = 1000;
    }
  }

  private AtomicLong                 _synchronizationCount = new AtomicLong(0);

  private ModelsLock                 _modelsLock           = new ModelsLock();

  public RuntimeTracer()
  {

    _listeners = new ArrayList<ITraceListener>();

    _modelListener = new ModelListenerAdaptor() {
      @Override
      public void cycleStopped(ModelEvent me)
      {
        if (_synchronizationCount.incrementAndGet() % SYNCHRONIZATION_THRESHOLD == 0)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug(String
                .format("Requesting models to suspend until we can catch up"));
          // trigger the synchronization, but fire it on the our executor
          // so we know we've caught up.
          CompletableFuture<Boolean> allClosed = _modelsLock.close();
          allClosed.thenRunAsync(
              () -> {
                if (LOGGER.isDebugEnabled())
                  LOGGER.debug(String.format("All models stopped. Sinking"));
                if (_dataSink != null)
                  try
                  {
                    _dataSink.flush();
                  }
                  catch (Exception e)
                  {
                    LOGGER.error(
                        ".cycleStopped threw Exception while flushing: ", e);
                  }
                // and release
                _modelsLock.open();
              }, getExecutor());
        }
      }
    };
  }

  public Executor getExecutor()
  {
    return _executor;
  }

  public void setExecutor(String executorName)
  {
    Executor ex = ExecutorServices.getExecutor(executorName);
    if (ex == null)
      throw new RuntimeException("Could not find executor named "
          + executorName);
    _executorName = executorName;
    _executor = ex;
  }

  public void add(ITraceListener listener)
  {
    _listeners.add(listener);
    if (_dataSink != null)
    {
      listener.setTraceSink(_dataSink);
      if (listener instanceof IParameterized)
        applyDeferred((IParameterized) listener);
    }
  }

  protected void applyDeferred(IParameterized parameterized)
  {
    for (Map.Entry<String, String> entry : _deferredParameters.entrySet())
      parameterized.setParameter(entry.getKey(), entry.getValue());
  }

  /**
   * where should we send all the transformed events to?
   * 
   * @param sink
   */
  public void setTraceSink(ITraceSink sink)
  {
    _dataSink = sink;
    for (ITraceListener listener : _listeners)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Setting " + listener + "'s sink to " + sink);
      listener.setTraceSink(sink);
    }
  }

  public void install(IModel model)
  {
    if (_attachedModels.contains(model)) return;

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Installing " + model);

    for (ITraceListener listener : _listeners)
      listener.install(model, getExecutor());

    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);

    _modelsLock.install(model);

    _attachedModels.add(model);
  }

  public void uninstall(IModel model)
  {
    model.removeListener(_modelListener);
    for (ITraceListener listener : _listeners)
      listener.uninstall(model);

    _modelsLock.uninstall(model);
    _attachedModels.remove(model);
  }

  /**
   * we attach two listeners. The first is the runtime listener which when the
   * runtime stops, will force us to flush our data. The other is the a
   * breakpoint listener so that it a breakpoint is reached, we flush.
   * 
   * @see org.jactr.instrument.IInstrument#initialize()
   */
  public void initialize()
  {
    _modelsLock.initialize();
    ACTRRuntime runtime = ACTRRuntime.getRuntime();
    runtime.addListener(new ACTRRuntimeAdapter() {

      @Override
      public void modelAdded(ACTRRuntimeEvent event)
      {
        /*
         * for any late additions. This is nice in principle, but the async
         * controller needs to handle the addition and removal of the models as
         * well
         */
        // install(event.getModel());
      }

      @Override
      public void runtimeStopped(ACTRRuntimeEvent event)
      {
        try
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("Flushing data to data sink");
          _dataSink.flush();
        }
        catch (Exception e)
        {
          LOGGER.error("Could not flush ", e);
        }
      }

    }, getExecutor());

    IController controller = runtime.getController();
    if (controller instanceof IDebugController)
      ((IDebugController) controller).addListener(new IBreakpointListener() {

        public void breakpointReached(BreakpointEvent be)
        {
          try
          {
            _dataSink.flush();
          }
          catch (Exception e)
          {
            LOGGER.error("Could not flush ", e);
          }
        }

      }, getExecutor());
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getParameter(java.lang.String)
   */
  public String getParameter(String key)
  {
    if (LOGGER.isWarnEnabled())
      LOGGER.warn("RuntimeTracer.getParameter is not implemented");
    return null;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getPossibleParameters()
   */
  public Collection<String> getPossibleParameters()
  {
    ArrayList<String> rtn = new ArrayList<String>();
    rtn.add(EXECUTOR_PARAM);
    rtn.add(SINK_CLASS);
    return rtn;
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#getSetableParameters()
   */
  public Collection<String> getSetableParameters()
  {
    return getPossibleParameters();
  }

  /**
   * @see org.jactr.core.utils.parameter.IParameterized#setParameter(java.lang.String,
   *      java.lang.String)
   */
  public void setParameter(String key, String value)
  {
    if (EXECUTOR_PARAM.equalsIgnoreCase(key))
      setExecutor(value);
    else if (SINK_CLASS.equalsIgnoreCase(key))
    {
      List<ITraceSink> sinks = FastListFactory.newInstance();
      String[] sinkClasses = value.split(",");
      for (String sinkClass : sinkClasses)
      {
        sinkClass = sinkClass.trim();
        if (sinkClass.length() > 0)
          try
          {
            sinks.add((ITraceSink) getClass().getClassLoader()
                .loadClass(sinkClass).newInstance());
          }
          catch (Exception e)
          {
            LOGGER.error("Could not create ITraceSink from " + sinkClass, e);
          }
      }

      if (sinks.size() == 0)
        throw new RuntimeException("No sinks could be created");

      if (sinks.size() == 1)
        setTraceSink(sinks.get(0));
      else
      {
        ChainedSink sink = new ChainedSink();
        for (ITraceSink tmp : sinks)
          sink.add(tmp);

        setTraceSink(sink);
      }

      FastListFactory.recycle(sinks);
    }
    else if (LISTENERS.equalsIgnoreCase(key))
      for (String name : value.split(","))
      {
        name = name.trim();
        if (name.length() != 0)
          try
          {
            Class lClass = getClass().getClassLoader().loadClass(name);
            ITraceListener listener = (ITraceListener) lClass.newInstance();

            add(listener);
          }
          catch (Exception e)
          {
            if (LOGGER.isWarnEnabled())
              LOGGER.warn("Could not create new trace listener " + name, e);
          }
      }
    else
    {
      _deferredParameters.put(key, value);
      for (ITraceListener listener : _listeners)
        if (listener instanceof IParameterized)
          ((IParameterized) listener).setParameter(key, value);

      if (_dataSink instanceof IParameterized)
        ((IParameterized) _dataSink).setParameter(key, value);
    }
  }
}
