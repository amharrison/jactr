package org.jactr.core.utils;

/*
 * default logging
 */
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RollingFileWriter extends Writer
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER           = LogFactory
                                                          .getLog(RollingFileWriter.class);

  final private long                  _maxFileSize;                         // 1

  // M

  final private long                 _maxBackups;

  final private File                 _directory;

  final private String               _fileName;

  final private String               _prefix;

  final private String               _extension;

  private File                       _file;

  private Writer                     _fileWriter;

  
  public RollingFileWriter(File directory, String fileName)
  {
    this(directory, fileName, 1024 * 1024, 3);
  }

  public RollingFileWriter(File directory, String fileName, long maxSize, long backups)
  {
    _maxBackups = backups;
    _maxFileSize = maxSize;
    _directory = directory;
    _fileName = fileName;
    int index = _fileName.lastIndexOf('.');
    if (index >= 0)
    {
      _prefix = _fileName.substring(0, index);
      _extension = _fileName.substring(index + 1);
    }
    else
    {
      _prefix = _fileName;
      _extension = "";
    }
    createStreams();
  }

  private void createStreams()
  {
    _file = new File(_directory, _fileName);
    try
    {
      _fileWriter = new BufferedWriter(new FileWriter(_file));
    }
    catch (IOException ioe)
    {
      LOGGER.error("could not create file writer for " + _file.getName()
          + ", using stderr", ioe);
      _fileWriter = new PrintWriter(System.err, true);
    }
  }

  private SortedMap<Integer, File> findLogs()
  {
    SortedMap<Integer, File> sortedFiles = new TreeMap<Integer, File>();

    for (File file : _directory.listFiles(new FilenameFilter() {

      public boolean accept(File dir, String name)
      {
        return name.startsWith(_prefix) && name.endsWith(_extension);
      }

    }))
      sortedFiles.put(getIndex(file), file);
    return sortedFiles;
  }

  /*
   * close old stream
   */
  private void closeInternalStream()
  {
    try
    {
      _fileWriter.close();
    }
    catch (IOException e)
    {
      LOGGER.error("RollingWriterLogger.rollOver threw IOException : ", e);
    }
    _fileWriter = null;
    _file = null;
  }

  /**
   * return number suffix {name}-{number}.{extension}
   * 
   * @param file
   * @return
   */
  private int getIndex(File file)
  {
    String fileName = file.getName();
    if (_extension.length() != 0)
      fileName = fileName.substring(0, fileName.indexOf(_extension)-1);
    fileName = fileName.substring(_prefix.length());
    try
    {
      return Integer
          .parseInt(fileName.substring(fileName.lastIndexOf('-') + 1));
    }
    catch (Exception e)
    {
      return 0;
    }
  }

  private void rollOver()
  {
    closeInternalStream();
    
    SortedMap<Integer, File> files = findLogs();
    if (files.size() == _maxBackups + 1)
    {
      /*
       * delete the oldest
       */
      Integer last = files.lastKey();
      files.remove(last).delete();
    }

    List<File> reverse = new ArrayList<File>(files.values());
    Collections.reverse(reverse);
    Iterator<File> itr = reverse.iterator();
    /*
     * rename to the next index
     */
    while (itr.hasNext())
    {
      File file = itr.next();
      file.renameTo(new File(_directory, _prefix + "-" + reverse.size() + "."
          + _extension));
      itr.remove();
    }
    
    
  }

  private void checkForRollOver()
  {
    if (_file.length() >= _maxFileSize)
    {
      rollOver();
      createStreams();
    }
  }

  synchronized public void flush()
  {
    try
    {
      _fileWriter.flush();
    }
    catch (IOException ioe)
    {
      LOGGER.error("Could not flush to " + _file.getName(), ioe);
    }

    checkForRollOver();
  }


  @Override
  synchronized public void close() throws IOException
  {
    flush();
    closeInternalStream();
  }

  @Override
  synchronized public void write(char[] cbuf, int off, int len) throws IOException
  {
    _fileWriter.write(cbuf, off, len);
    checkForRollOver();
  }

}
