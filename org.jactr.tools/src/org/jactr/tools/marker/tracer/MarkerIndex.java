package org.jactr.tools.marker.tracer;

/*
 * default logging
 */
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.tools.marker.IMarker;

public class MarkerIndex
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(MarkerIndex.class);

  private final Set<String>          _writtenTypes;

  private DataOutputStream           _typeStream;

  private DataOutputStream           _indexStream;

  private final File                 _outputDirectory;

  public MarkerIndex(File outputDirectory)
  {
    _outputDirectory = outputDirectory;
    _writtenTypes = new TreeSet<String>();
    try
    {
      openStreams();
    }
    catch (IOException e)
    {
      LOGGER.warn("File exception, not writing marker indicies ", e);
    }
  }

  public void opened(IMarker marker)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Writing opening of %s", marker.getName()));

    updateTypeStream(marker);

    updateMarkerIndex(marker, false);
  }

  public void closed(IMarker marker)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Writing closing of %s", marker.getName()));

    updateMarkerIndex(marker, true);
  }

  protected void updateTypeStream(IMarker marker)
  {
    String type = marker.getType();
    /*
     * write the types encountered
     */
    if (_writtenTypes.add(type) && _typeStream != null) try
    {
      _typeStream.writeUTF(type);
      _typeStream.flush();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to write to type stream ", e);
    }
  }

  protected void updateMarkerIndex(IMarker marker, boolean isClose)
  {
    /*
     * we write the boolean {open/close} id, type, name, and time
     */
    if (_indexStream != null) try
    {
      _indexStream.writeBoolean(isClose);
      _indexStream.writeLong(marker.getId());
      _indexStream.writeUTF(marker.getType());
      _indexStream.writeUTF(marker.getName());
      if (isClose)
        _indexStream.writeDouble(marker.getEndTime());
      else
        _indexStream.writeDouble(marker.getStartTime());

      if (isClose) _indexStream.flush();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to write to marker index stream ", e);
    }

  }

  private void openStreams() throws FileNotFoundException
  {
    _typeStream = new DataOutputStream(new FileOutputStream(new File(
        _outputDirectory, "marker.types")));
    _indexStream = new DataOutputStream(new FileOutputStream(new File(
        _outputDirectory, "marker.index")));
  }

  public void dispose()
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Flushing markerIndex"));
    try
    {
      if (_typeStream != null)
      {
        _typeStream.flush();
        _typeStream.close();
      }
      _typeStream = null;

      if (_indexStream != null)
      {
        _indexStream.flush();
        _indexStream.close();
      }
      _indexStream = null;
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to dispose of MarkerIndex ", e);
    }
  }
}
