package org.jactr.tools.tracer.sinks.trace.internal;

/*
 * default logging
 */
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * documents what files contain what time ranges, plus the general make up of
 * the contents
 * 
 * @author harrison
 */
public class TraceIndex
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TraceIndex.class);

  private DataOutputStream           _outputStream;

  private File                       _outputDirectory;

  public TraceIndex(File outputDirectory)
  {
    _outputDirectory = outputDirectory;
  }

  public void update(File archiveFile, double[] timeWindow)
  {
    try
    {
      String archiveFileName = archiveFile.getAbsolutePath();
      // strip the output directory
      archiveFileName = archiveFileName.substring(_outputDirectory
          .getAbsolutePath().toString().length());

      _outputStream.writeDouble(timeWindow[0]);
      _outputStream.writeDouble(timeWindow[1]);
      _outputStream.writeUTF(archiveFileName);

      _outputStream.flush();

      if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("wrote [%.2f, %.2f] %s", timeWindow[0],
            timeWindow[1], archiveFileName));
    }
    catch (Exception e)
    {
      LOGGER.error("Could not write update to index file ", e);
    }
  }

  public void flush()
  {
    try
    {
      if (_outputStream != null) _outputStream.flush();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to flush ", e);
    }
  }

  public void open()
  {
    try
    {
      _outputStream = new DataOutputStream(
          new BufferedOutputStream(new FileOutputStream(new File(
              _outputDirectory, "sessionData.index")), 4096));
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to open index file", e);
    }
  }

  public void close()
  {
    try
    {
      _outputStream.flush();
      _outputStream.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to close ", e);
    }
    finally
    {
      _outputStream = null;
    }
  }

}
