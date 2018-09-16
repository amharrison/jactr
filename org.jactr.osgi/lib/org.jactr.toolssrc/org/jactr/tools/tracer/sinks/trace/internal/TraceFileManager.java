package org.jactr.tools.tracer.sinks.trace.internal;

/*
 * default logging
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.tracer.transformer.ITransformedEvent;

public class TraceFileManager
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER            = LogFactory
                                                           .getLog(TraceFileManager.class);

  private File                       _outputDirectory;

  private GZIPOutputStream           _gzipOutputStream;

  private ObjectOutputStream         _currentObjectOutputStream;

  private List<ITransformedEvent>    _pendingEvents;

  final private TraceIndex           _traceIndex;

  private double                     _recordWindowSpan = 60;                               // 30sec

  private double                     _recordWindow[]   = { Double.NaN,
      Double.MAX_VALUE                                };

  private boolean                    _longRun          = false;

  private File                       _currentFile;

  public TraceFileManager(File outputDirectory)
  {
    _pendingEvents = FastListFactory.newInstance();
    _traceIndex = new TraceIndex(outputDirectory);
    _outputDirectory = outputDirectory;
    _outputDirectory.mkdirs();

    initialize();
  }

  private void initialize()
  {
    try
    {
      _recordWindowSpan = Double.parseDouble(System
          .getProperty("jactr.sink.archive.windowSize"));
    }
    catch (Exception e)
    {
      _recordWindowSpan = 60;
    }

    try
    {
      _longRun = Boolean.parseBoolean(System
          .getProperty("jactr.sink.archive.longRun"));
    }
    catch (Exception e)
    {
      _longRun = false;
    }
  }

  synchronized public void open()
  {
    // noop
    _traceIndex.open();
  }

  synchronized public void close()
  {
    _traceIndex.close();
    if (_currentObjectOutputStream != null)
    {
      flush();
      closeRecord();
    }
  }

  synchronized public boolean record(ITransformedEvent event)
  {
    boolean createRecord = false;

    double eventTime = event.getSimulationTime();

    if (eventTime >= _recordWindow[1])
      try
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("new time block @ %.2f", eventTime));

        flushInternal(_pendingEvents, _currentObjectOutputStream);
        closeRecord();
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to flush", e);
      }
      finally
      {
        cleanup();
        createRecord = true;
      }

    if (Double.isNaN(_recordWindow[0]) || createRecord)
    {
      // if (Double.isNaN(_recordWindow[0]))
      _recordWindow[0] = eventTime;
      // else
      // _recordWindow[0] = _recordWindow[1];

      _recordWindow[1] = _recordWindow[0] + _recordWindowSpan;

      // if (LOGGER.isDebugEnabled())
      // LOGGER.debug(String.format("Creating new timewindow [%.2f, %.2f]",
      // _recordWindow[0], _recordWindow[1]));
      newRecord();
    }

    _pendingEvents.add(event);
    return true;
  }

  protected File createFile()
  {
    /*
     * for now, it's outputDirectory/hour/minute/00.sessionData
     */
    Calendar birthday = Calendar.getInstance();

    birthday.setTimeInMillis((long) _recordWindow[0] * 1000);

    int[] segments = null;

    if (_longRun)
      segments = new int[] { Calendar.YEAR, Calendar.DAY_OF_YEAR,
          Calendar.HOUR_OF_DAY, Calendar.MINUTE };
    else
      segments = new int[] { Calendar.HOUR_OF_DAY, Calendar.MINUTE };

    File path = _outputDirectory;
    for (int segment : segments)
    {
      path = new File(path, String.format("%1d", birthday.get(segment)));
      path.mkdirs();
    }

    File fp = new File(path, String.format("%1d.sessionData",
        birthday.get(Calendar.SECOND)));

    if (fp.exists())
    {
      String msg = String
          .format(
              "Tracefile %s already exists. This suggests you've got a long running model. Run with -Djactr.sink.archive.longRun=true",
              fp.getAbsolutePath());

      if (LOGGER.isWarnEnabled()) LOGGER.warn(msg);

      throw new ModelTerminatedException(msg);
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format(
          "[%.2f, %.2f] Created new sessionData file %s", _recordWindow[0],
          _recordWindow[1], fp.getAbsolutePath()));

    _currentFile = fp;
    return fp;
  }

  private void newRecord()
  {
    /*
     * create the file.
     */
    File indexFile = createFile();

    try
    {
      _gzipOutputStream = new GZIPOutputStream(new FileOutputStream(indexFile),
          4096);
      _currentObjectOutputStream = new ObjectOutputStream(_gzipOutputStream);

      _currentObjectOutputStream.writeDouble(_recordWindow[0]);
      _currentObjectOutputStream.writeDouble(_recordWindow[1]);

      /*
       * record the range for the index
       */
      _traceIndex.update(indexFile, _recordWindow);
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to create new record ", e);

      closeRecord();
    }
  }

  private void closeRecord()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Closing current session"));
    try
    {
      if (_currentObjectOutputStream != null)
      {
        _currentObjectOutputStream.flush();
        _currentObjectOutputStream.close();
      }

      if (_gzipOutputStream != null)
      {
        _gzipOutputStream.finish();

        _gzipOutputStream.flush();
        _gzipOutputStream.close();
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to clean up", e);
    }
    finally
    {
      _currentObjectOutputStream = null;
      _gzipOutputStream = null;
      _recordWindow[0] = Double.NaN;
      _recordWindow[1] = Double.MAX_VALUE;
    }
  }

  /**
   * dump pending to file
   * 
   * @return true if all is good
   */
  synchronized public boolean flush()
  {
    try
    {
      _traceIndex.flush();

      flushInternal(_pendingEvents, _currentObjectOutputStream);

      return true;
    }
    catch (IOException e)
    {
      LOGGER.error("failed to flush ", e);
      cleanup();
      return false;
    }
  }

  private void flushInternal(Collection<ITransformedEvent> pendingEvents,
      ObjectOutputStream output) throws IOException
  {
    try
    {
      // if (LOGGER.isDebugEnabled())
      // LOGGER
      // .debug(String.format("Flushing %d events", pendingEvents.size()));

      for (ITransformedEvent event : pendingEvents)
      {
        double eventTime = event.getSimulationTime();

        if (eventTime < _recordWindow[0] || eventTime >= _recordWindow[1])
          if (LOGGER.isWarnEnabled())
            LOGGER.warn(String.format(
                "%s (@ %.2f) is outside of current record window (%.2f, %.2f)",
                event.getClass().getName(), eventTime, _recordWindow[0],
                _recordWindow[1]));

        output.writeObject(event);

        // if (event instanceof StringTableMessage && LOGGER.isDebugEnabled())
        // LOGGER.debug(String.format("Wrote (%.2f) %s to [%.2f, %.2f] : %s",
        // event.getSimulationTime(), event, _recordWindow[0],
        // _recordWindow[1], _currentFile.getCanonicalPath()));
      }

      if (output != null) output.flush();

      // _gzipOutputStream.finish();
    }
    finally
    {
      pendingEvents.clear();
    }
  }

  private void cleanup()
  {
    closeRecord();
  }
}
