package org.jactr.tools.stat;

/*
 * default logging
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.instrument.IInstrument;

/**
 * stat tracker that records the number of chunks, types and productions, as
 * well as running time (simulated and actual). The tracker samples at a
 * configurable rate of cycle firings.
 * 
 * @author harrison
 */
public class StatTracker implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER        = LogFactory
                                                       .getLog(StatTracker.class);
  
  static public final String CYCLES_PARAM = "SampleFrequency";

  private long                       _cycleCount;

  private long                       _cycleSamples = 100;
  
  private Map<IModel, StatTrackingListener> _listeners;

  public StatTracker()
  {
    _listeners = new HashMap<IModel, StatTrackingListener>();
  }

  public void initialize()
  {
    // TODO Auto-generated method stub

  }

  public void install(IModel model)
  {
    try
    {
      PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(
          new File(ACTRRuntime.getRuntime().getWorkingDirectory(), model
              .getName()
              + "-stats.txt"))));
      
      StatTrackingListener listener = this.new StatTrackingListener(output);
      model.addListener(listener, ExecutorServices
          .getExecutor(ExecutorServices.BACKGROUND));
      
      _listeners.put(model, listener);
    }
    catch (Exception e)
    {

    }
  }

  public void uninstall(IModel model)
  {
    model.removeListener(_listeners.get(model));
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
    return Arrays.asList(CYCLES_PARAM);
  }

  public void setParameter(String key, String value)
  {
    if(CYCLES_PARAM.equalsIgnoreCase(key))
      _cycleSamples = Integer.parseInt(value);
  }
  

  private class StatTrackingListener extends ModelListenerAdaptor
  {

    private PrintWriter _outputStream;

    private double      _simCycleStartTime  = 0;

    private long        _realCycleStartTime = 0;

    public StatTrackingListener(PrintWriter outputStream)
    {
      _outputStream = outputStream;
    }

    private void updateTimes(ModelEvent me)
    {
      double realRunTime = (me.getSystemTime() - _realCycleStartTime) / 1000.0;

      double simRunTime = me.getSimulationTime() - _simCycleStartTime;

      _realCycleStartTime = me.getSystemTime();
      _simCycleStartTime = me.getSimulationTime();

      output(me.getSource(), _cycleCount, realRunTime, simRunTime);
    }

    public void cycleStarted(ModelEvent me)
    {
      if (_cycleCount % _cycleSamples == 0) updateTimes(me);

      _cycleCount++;
    }

    public void modelStopped(ModelEvent me)
    {
      updateTimes(me);
      _outputStream.flush();
    }
    
    public void modelStarted(ModelEvent me)
    {
      _outputStream.println("Cycle\tChunks\tTypes\tProductions\tRealT\tSimT");
      _realCycleStartTime = System.currentTimeMillis();
    }

    protected void output(IModel model, long cycle, double realTime,
        double simTime)
    {
      try
      {
        long chunks = model.getDeclarativeModule().getNumberOfChunks();
        long chunkTypes = model.getDeclarativeModule().getChunkTypes().get()
            .size();
        long productions = model.getProceduralModule().getProductions().get()
            .size();

        _outputStream.println(cycle + "\t" + chunks + "\t" + chunkTypes + "\t"
            + productions + "\t" + realTime + "\t" + simTime);
      }
      catch (Exception e)
      {
        /**
         * Error : error
         */
        LOGGER.error("could not output stats ", e);
      }
    }
  }

 
}
