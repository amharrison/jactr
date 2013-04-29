package org.jactr.tools.tracer.listeners;

/*
 * default logging
 */
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.logging.ILogger;
import org.jactr.core.logging.LogEvent;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.tools.tracer.transformer.logging.BulkLogEvent;

public class LogTracer extends BaseTraceListener implements ILogger
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LogTracer.class);

  private IModelListener                          _modelListener;

  private IProceduralModuleListener               _proceduralListener;

  private Map<IModel, Map<String, StringBuilder>> _logData;

  public LogTracer()
  {
    _logData = new HashMap<IModel, Map<String, StringBuilder>>();

    _proceduralListener = new ProceduralModuleListenerAdaptor() {
      /*
       * middle flush (non-Javadoc)
       * @see
       * org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor
       * #productionWillFire
       * (org.jactr.core.module.procedural.event.ProceduralModuleEvent)
       */
      @Override
      public void conflictSetAssembled(ProceduralModuleEvent pme)
      {
        flushToSink(pme.getSource().getModel(), pme.getSimulationTime(), false);
      }
    };
    _modelListener = new ModelListenerAdaptor() {
      /*
       * end flush (non-Javadoc)
       * @see
       * org.jactr.core.model.event.ModelListenerAdaptor#cycleStopped(org.jactr
       * .core.model.event.ModelEvent)
       */
      @Override
      public void cycleStopped(ModelEvent me)
      {
        flushToSink(me.getSource(), me.getSimulationTime(), true);
      }
    };
  }

  public void install(IModel model, Executor executor)
  {
    _logData.put(model, new TreeMap<String, StringBuilder>());

    Logger.addLogger(model, this, executor);
    model.addListener(_modelListener, executor);
    model.getProceduralModule().addListener(_proceduralListener, executor);
  }

  public void uninstall(IModel model)
  {
    _logData.remove(model);

    Logger.removeLogger(model, this);
    model.removeListener(_modelListener);
    model.getProceduralModule().removeListener(_proceduralListener);
  }

  synchronized public void log(LogEvent logEvent)
  {
    /*
     * concatenate
     */
    Map<String, StringBuilder> logMap = _logData.get(logEvent.getModel());
    StringBuilder log = logMap.get(logEvent.getStreamName());
    if (log == null)
    {
      log = new StringBuilder();
      logMap.put(logEvent.getStreamName(), log);
    }

    log.append(logEvent.getMessage()).append("\n");
  }

  synchronized protected void flushToSink(IModel model, double time,
      boolean endOfCycle)
  {
    Map<String, StringBuilder> logMap = _logData.get(model);
    BulkLogEvent ble = new BulkLogEvent(model.getName(), time, logMap,
        endOfCycle);
    sink(ble);

    // clear
    for (StringBuilder sb : logMap.values())
      sb.delete(0, sb.length());
  }

  public void flush()
  {
    // noop

  }


}
