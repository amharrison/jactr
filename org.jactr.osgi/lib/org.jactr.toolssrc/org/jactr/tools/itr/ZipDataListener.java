package org.jactr.tools.itr;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.entry.iterative.IIterativeRunListener;
import org.jactr.entry.iterative.TerminateIterativeRunException;

/**
 * dump the output of an iterative run to a zip archive
 * 
 * @author harrison
 */
public class ZipDataListener implements IIterativeRunListener, IParameterized
{
  /**
   * Logger definition
   */
  static private transient Log LOGGER         = LogFactory
                                                  .getLog(ZipDataListener.class);

  static public final String   IGNORE_ROOT    = "IgnoreRootDir";

  private ZipOutputStream      _outputStream;

  private int                  _archiveCount  = 0;

  private File                 _currentArchive;

  private FilenameFilter       _filter        = new FilenameFilter() {

                                                public boolean accept(File dir,
                                                    String filename)
                                                {
                                                  /*
                                                   * only care about the
                                                   * filename ignore .zip
                                                   */
                                                  return !(filename
                                                      .startsWith("runArchive") && filename
                                                      .endsWith(".zip"));
                                                }
                                              };

  private boolean              _ignoreRootDir = false;

  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException
  {

  }

  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {

    /*
     * the executors may still be churning away..
     */
    Set<String> executors = new TreeSet<String>();
    ExecutorServices.getExecutorNames(executors);
    if (LOGGER.isDebugEnabled())
      LOGGER.debug(String.format("Waiting for executors %s to finish up",
          executors));

    for (String name : executors)
      try
    {
      ExecutorService service = ExecutorServices.getExecutor(name);
        if (service == null || service.isShutdown() || service.isTerminated())
          continue;

        if (ExecutorServices.waitFor(service, 10000))
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(String.format("%s finished load", name));
      }
      else if (LOGGER.isDebugEnabled())
        LOGGER.debug(String.format("%s took too long", name));
    }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to wait for %s", name), e);
      }

    if (_outputStream != null)
    {
      File root = new File(System.getProperty("user.dir"));
      File working = ACTRRuntime.getRuntime().getWorkingDirectory();
      try
      {
        /*
         * max out at one gig.
         */
        if (_currentArchive.length() > 1073741824) newArchive();

        archiveAndDeleteContents(root.getAbsolutePath(), working, _filter,
            _outputStream);
      }
      catch (IOException e)
      {
        if (LOGGER.isErrorEnabled())
          LOGGER.error(
              "Could not archive and delete " + working.getAbsolutePath(), e);
        _outputStream = null;
      }
    }

  }

  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors)
      throws TerminateIterativeRunException
  {
    // NoOp
  }

  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException
  {
    // NoOp
  }

  private void newArchive() throws IOException
  {
    if (_outputStream != null) try
    {
      _outputStream.flush();
      _outputStream.closeEntry();
      _outputStream.close();
    }
    catch (Exception e)
    {
      // meh
    }

    _currentArchive = new File(System.getProperty("user.dir"), String.format(
        "runArchive-%d.zip", _archiveCount));
    _outputStream = new ZipOutputStream(new BufferedOutputStream(
        new FileOutputStream(_currentArchive)));

    _archiveCount++;
  }

  /**
   * create the zip file
   */
  public void start(int totalRuns) throws TerminateIterativeRunException
  {
    try
    {
      newArchive();
    }
    catch (IOException e)
    {
      if (LOGGER.isErrorEnabled())
        LOGGER.error("Could not output zip stream ", e);
      _outputStream = null;
    }
  }

  /**
   * clean up
   */
  public void stop()
  {
    if (_outputStream != null && !_ignoreRootDir)
    {
      File root = new File(System.getProperty("user.dir"));
      try
      {
        /*
         * max out at one gig.
         */
        if (_currentArchive.length() > 1073741824) newArchive();

        archiveAndDeleteContents(root.getAbsolutePath(), root, _filter,
            _outputStream);
        _outputStream.closeEntry();
        _outputStream.close();

        if (_currentArchive.length() == 0) _currentArchive.delete();
      }
      catch (IOException e)
      {
        if (LOGGER.isErrorEnabled())
          LOGGER.error(
              "Could not archive and delete " + root.getAbsolutePath(), e);
      }
      finally
      {
        _outputStream = null;
      }
    }
  }

  /**
   * archive the contents of root into the zipoutputstream
   * 
   * @param root
   * @param filter
   * @param zos
   * @throws IOException
   */
  protected void archiveAndDeleteContents(String pathRoot, File root,
      FilenameFilter filter, ZipOutputStream zos) throws IOException
  {

    byte[] buffer = new byte[4096];
    for (File file : root.listFiles(filter))
      if (file.isDirectory())
      {
        archiveAndDeleteContents(pathRoot, file, filter, zos);
        file.delete();
      }
      else
      {
        String name = file.getAbsolutePath();
        name = name.substring(pathRoot.length());
        ZipEntry entry = new ZipEntry(name);
        if (name.toLowerCase().endsWith(".zip"))
          entry.setMethod(ZipEntry.STORED);
        else
          entry.setMethod(ZipEntry.DEFLATED);
        entry.setTime(file.lastModified());

        zos.putNextEntry(entry);
        /*
         * snag the contents..
         */
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
            file), 4096);

        int len = 0;
        while ((len = bis.read(buffer)) != -1)
          zos.write(buffer, 0, len);

        bis.close();
        zos.closeEntry();

        file.delete();
      }
  }

  public String getParameter(String key)
  {
    return null;
  }

  public Collection<String> getPossibleParameters()
  {
    return Collections.EMPTY_LIST;
  }

  public Collection<String> getSetableParameters()
  {
    return Collections.EMPTY_LIST;
  }

  public void setParameter(String key, String value)
  {
    if (IGNORE_ROOT.equalsIgnoreCase(key))
      _ignoreRootDir = Boolean.parseBoolean(value);
  }

  public void preLoad(int currentRunIndex, int totalRuns)
      throws TerminateIterativeRunException
  {
    // TODO Auto-generated method stub

  }

}
