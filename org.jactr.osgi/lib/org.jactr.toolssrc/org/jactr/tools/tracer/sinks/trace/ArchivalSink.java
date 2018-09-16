package org.jactr.tools.tracer.sinks.trace;

/*
 * default logging
 */
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.util.LockUtilities;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.collections.FastListFactory;
import org.jactr.tools.tracer.ITraceSink;
import org.jactr.tools.tracer.sinks.trace.internal.TraceFileManager;
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
  static private final transient Log    LOGGER        = LogFactory
                                                          .getLog(ArchivalSink.class);

  private volatile boolean              _isActive     = true;

  private Executor                      _executor     = ExecutorServices.INLINE_EXECUTOR;

  private ReentrantReadWriteLock        _lock         = new ReentrantReadWriteLock();

  private Map<String, TraceFileManager> _fileManagers = new TreeMap<String, TraceFileManager>();

  public ArchivalSink()
  {
    initializeCleanup();

    // _executor = ExecutorServices.getExecutor("archivalSink");
    // if (_executor == null)
    // {
    // _executor = Executors.newSingleThreadExecutor();
    // ExecutorServices.addExecutor("archivalSink", (ExecutorService)
    // _executor);
    // }

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
        // no need to lock
        _fileManagers.values().forEach((fm) -> {
          try
          {
            fm.flush();
            fm.close();
          }
          catch (Exception e)
          {
            LOGGER.error("Failed to close file manager ", e);
          }
        });

        _fileManagers.clear();
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

    String model = event.getModelName();
    TraceFileManager tfm = null;

    try
    {
      tfm = LockUtilities.runLocked(
          _lock.writeLock(),
          () -> {
            TraceFileManager fm = _fileManagers.get(model.toLowerCase());
            if (fm == null)
            {
              fm = new TraceFileManager(new File(getOutputDirectory(), model
                  .toLowerCase()));
              fm.open();
              _fileManagers.put(model.toLowerCase(), fm);
            }
            return fm;
          });
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to create trace file manager");
    }

    if (tfm != null)
    {
      final TraceFileManager fTFM = tfm;

      _executor.execute(new Runnable() {

        public void run()
        {
          _isActive = fTFM.record(event);
        }

      });
    }
    else if (LOGGER.isWarnEnabled())
      LOGGER.warn(String.format("Could not save events for %s", model));
  }

  public void flush() throws Exception
  {
    _executor.execute(new Runnable() {

      public void run()
      {
        _isActive = true;
        try
        {
          List<TraceFileManager> managers = FastListFactory.newInstance();

          LockUtilities.runLocked(_lock.writeLock(), () -> {
            managers.addAll(_fileManagers.values());
          });

          managers.forEach((fm) -> {
            try
            {
              fm.flush();
            }
            catch (Exception e)
            {
              _isActive = false;
              LOGGER.error("Failed to flush fileManager", e);
            }
          });
        }
        catch (Exception e)
        {
          _isActive = false;
          LOGGER.error("Failed to flush", e);
        }
      }
    });
  }

  public boolean isOpen()
  {
    return _isActive;
  }

}
