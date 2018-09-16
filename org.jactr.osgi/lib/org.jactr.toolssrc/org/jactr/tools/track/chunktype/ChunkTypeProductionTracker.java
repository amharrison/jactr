package org.jactr.tools.track.chunktype;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.event.ActivationBufferEvent;
import org.jactr.core.buffer.event.ActivationBufferListenerAdaptor;
import org.jactr.core.buffer.event.IActivationBufferListener;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.IModelListener;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.module.procedural.event.IProceduralModuleListener;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IInstantiation;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.RollingFileWriter;
import org.jactr.core.utils.StringUtilities;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.core.utils.parameter.ParameterHandler;
import org.jactr.instrument.IInstrument;

public class ChunkTypeProductionTracker implements IInstrument, IParameterized
{
  /**
   * Logger definition
   */
  static private transient Log      LOGGER        = LogFactory
                                                      .getLog(ChunkTypeProductionTracker.class);

  static public final String        FILENAME      = "FileName";

  static public final String        MAX_FILE_SIZE = "MaxFileSize";

  static public final String        MAX_BACKUPS   = "NumberOfBackups";

  private IModelListener            _modelListener;

  private IProceduralModuleListener _proceduralListener;

  private IActivationBufferListener _goalListener;

  private String                    _fileName     = "production.txt";

  private int                       _backups      = 3;

  private long                      _maxSize      = 1;                                          // meg

  private PrintWriter               _output;

  private Sequence                  _currentSequence;

  public ChunkTypeProductionTracker()
  {
    _proceduralListener = new ProceduralModuleListenerAdaptor() {

      @Override
      public void productionFired(ProceduralModuleEvent pme)
      {
        IInstantiation instantiation = (IInstantiation) pme.getProduction();
        double now = pme.getSimulationTime();

        if (_currentSequence == null)
        {
          IChunk goal = pme.getSource().getModel().getActivationBuffer(
              IActivationBuffer.GOAL).getSourceChunk();
          if (goal != null)
            _currentSequence = new Sequence(goal);
          else
            _currentSequence = new Sequence();
        }

        // remove the last terminal time
        if (_currentSequence._firingTimes.size() != 0)
          _currentSequence._firingTimes.remove(_currentSequence._firingTimes
              .size() - 1);

        _currentSequence._firingTimes.add(now);
        _currentSequence._firingTimes.add(now
            + instantiation.getSubsymbolicProduction().getFiringTime());

        _currentSequence._productions.add(instantiation.getProduction()
            .toString());
      }
    };

    _goalListener = new ActivationBufferListenerAdaptor() {
      @Override
      public void sourceChunkAdded(ActivationBufferEvent abe)
      {
        if (_currentSequence != null)
        {
          _currentSequence._terminaGoalString = StringUtilities.toString(abe
              .getSourceChunks().iterator().next());
          dump(_currentSequence);
        }
        _currentSequence = new Sequence(abe.getSourceChunks().iterator().next());
      }

      @Override
      public void sourceChunkRemoved(ActivationBufferEvent abe)
      {
        if (_currentSequence != null)
        {
          _currentSequence._terminaGoalString = StringUtilities.toString(abe
              .getSourceChunks().iterator().next());
          dump(_currentSequence);
        }
        _currentSequence = null;
      }
    };

    _modelListener = new ModelListenerAdaptor() {
      public void exceptionThrown(ModelEvent me)
      {
        if (_currentSequence != null) dump(_currentSequence);
        _currentSequence = null;
      }
    };

  }

  public void initialize()
  {
  }

  public void install(IModel model)
  {

    model.addListener(_modelListener, ExecutorServices.INLINE_EXECUTOR);
    model.getActivationBuffer(IActivationBuffer.GOAL).addListener(
        _goalListener, ExecutorServices.INLINE_EXECUTOR);
    model.getProceduralModule().addListener(_proceduralListener,
        ExecutorServices.INLINE_EXECUTOR);
  }

  public void uninstall(IModel model)
  {
    model.removeListener(_modelListener);
    model.getActivationBuffer(IActivationBuffer.GOAL).removeListener(
        _goalListener);
    model.getProceduralModule().removeListener(_proceduralListener);
  }

  protected void dump(Sequence sequence)
  {
    StringBuilder times = new StringBuilder();
    StringBuilder productions = new StringBuilder();
    NumberFormat format = NumberFormat.getNumberInstance();

    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;

    for (Double time : sequence._firingTimes)
    {
      times.append(format.format(time)).append("\t");
      if (time < min) min = time;
      if (time > max) max = time;
    }

    for (String production : sequence._productions)
      productions.append(production).append("\t");

    _output.println(sequence._chunkType);
    _output.println(format.format(Math.max(0, max - min)));
    _output.println("\t" + sequence._initialGoalString);
    _output.println("\t" + sequence._terminaGoalString);
    _output.println("\t" + times.toString());
    _output.println("\t" + productions.toString());

    _output.println();
    _output.println();
    _output.flush();
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
    return Arrays.asList(FILENAME, MAX_FILE_SIZE, MAX_BACKUPS);
  }

  public void setParameter(String key, String value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Setting parameter " + key + "=" + value);

    if (FILENAME.equalsIgnoreCase(key))
      _fileName = value;
    else if (MAX_BACKUPS.equalsIgnoreCase(key))
      _backups = ParameterHandler.numberInstance().coerce(value).intValue();
    else if (MAX_FILE_SIZE.equalsIgnoreCase(key))
      _maxSize = ParameterHandler.numberInstance().coerce(value).longValue();

    try
    {
      _output = new PrintWriter(new RollingFileWriter(ACTRRuntime.getRuntime()
          .getWorkingDirectory(), _fileName, _maxSize * 1024 * 1024,_backups));
    }
    catch (Exception e)
    {
      _output = new PrintWriter(System.err);
    }
  }

  private class Sequence
  {
    String             _chunkType;

    String             _initialGoalString;

    String             _terminaGoalString;

    Collection<String> _productions;

    List<Double>       _firingTimes;

    public Sequence(IChunk goalChunk)
    {
      this();
      _chunkType = goalChunk.getSymbolicChunk().getChunkType().toString();
      _initialGoalString = StringUtilities.toString(goalChunk);
    }

    public Sequence()
    {
      _chunkType = "<empty>";
      _initialGoalString = "<empty>";
      _productions = new ArrayList<String>();
      _firingTimes = new ArrayList<Double>();
    }
  }
}
