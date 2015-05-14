package org.jactr.tools.tracer.sinks.trace;

/*
 * default logging
 */
import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.sinks.trace.internal.TraceFileManager;
import org.jactr.tools.tracer.sinks.trace.internal.TraceIndex;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

/**
 * full trace sink saves events to a series of timestamped files that can be
 * used later for playback
 * 
 * @author harrison
 */
public class ArchivalSink implements ITraceSink
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER              = LogFactory
                                                             .getLog(ArchivalSink.class);

  private volatile boolean           _isActive           = true;

  private TraceFileManager           _fileManager;

  private TraceIndex                 _index;

  private Executor                   _executor;

  private double                     _lastSimulationTime = Double.NaN;

  public ArchivalSink()
  {
    File outputDirectory = getOutputDirectory();
    _index = new TraceIndex(outputDirectory);
    _fileManager = new TraceFileManager(outputDirectory, _index);
    _index.open();

    initializeCleanup();

    _executor = ExecutorServices.getExecutor("archivalSink");
    if (_executor == null)
    {
      _executor = Executors.newSingleThreadExecutor();
      ExecutorServices.addExecutor("archivalSink", (ExecutorService) _executor);
    }

  }

  public File getOutputDirectory()
  {
    File outputDirectory = new File(ACTRRuntime.getRuntime()
        .getWorkingDirectory(), "sessionData");
    outputDirectory.mkdirs();
    return outputDirectory;
  }

  /**
   * to maximize recoverability of the data, we install a shutdown hook
   */
  private void initializeCleanup()
  {
    Runnable cleaner = new Runnable() {

      public void run()
      {
        try
        {
          if (_index != null)
          {
            _index.flush();
            _index.close();
          }

          if (_fileManager != null)
          {
            _fileManager.flush();
            _fileManager.close();
          }
        }
        finally
        {
          _index = null;
          _fileManager = null;
        }
      }

    };

    Runtime.getRuntime().addShutdownHook(new Thread(cleaner));
  }

  public void add(final ITransformedEvent event)
  {
    if (event == null)
    {
      LOGGER.error("null message received ", new NullPointerException());
      return;
    }

    double simTime = event.getSimulationTime();

    // if (Double.isNaN(_lastSimulationTime)) _lastSimulationTime = simTime;
    //
    // if (simTime - _lastSimulationTime < -0.000001)
    // LOGGER.warn(String.format(
    // "Time regression detected in event delivery!! (%.6f < %.6f)",
    // simTime, _lastSimulationTime), new RuntimeException());
    // else
    _lastSimulationTime = simTime;

    _executor.execute(new Runnable() {

      public void run()
      {
        _isActive = _fileManager.record(event);
      }

    });
  }

  public void flush() throws Exception
  {
    _executor.execute(new Runnable() {

      public void run()
      {
        _isActive = _fileManager.flush();
        _index.flush();
      }
    });
  }

  public boolean isOpen()
  {
    return _isActive;
  }

}
