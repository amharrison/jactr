/*
 * Created on Apr 11, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.entry.iterative;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.concurrent.locks.ReadWriteLock;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.event.IACTRRuntimeListener;
import org.jactr.io.environment.EnvironmentParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author developer
 */
public class IterativeMain
{
  /**
   * logger definition
   */
  static private final Log  LOGGER        = LogFactory
                                              .getLog(IterativeMain.class);

  static private final long SECONDS       = 1000;

  static private final long MINUTES       = SECONDS * 60;

  static private final long HOURS         = MINUTES * 60;

  private boolean           _aggressiveGC = true;

  static private String duration(long startTime, long stopTime)
  {
    long delta = stopTime - startTime;
    long h = delta / HOURS;
    long m = (delta - h * HOURS) / MINUTES;
    long s = (delta - h * HOURS - m * MINUTES) / SECONDS;
    long ms = delta - h * HOURS - m * MINUTES - s * SECONDS;
    StringBuilder sb = new StringBuilder("" + h);
    sb.append(":");
    if (m < 10) sb.append("0");
    sb.append(m).append(":");
    if (s < 10) sb.append("0");
    sb.append(s).append(".");
    if (ms < 100) sb.append("0");
    if (ms < 10) sb.append("0");
    sb.append(ms);
    return sb.toString();
  }

  /**
   * load the environment file from url
   * 
   * @param url
   * @return
   */
  public Document loadEnvironment(URL url) throws IOException, SAXException,
      ParserConfigurationException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = factory.newDocumentBuilder();
    Document doc = parser.parse(url.openStream());
    return doc;
  }

  public void iteration(final int index, int total, Document environment,
      URL envURL, final Collection<IIterativeRunListener> listeners,
      final PrintWriter log) throws TerminateIterativeRunException
  {

    ExecutorServices.initialize();

    if (LOGGER.isDebugEnabled())
    {
      long totalMem = Runtime.getRuntime().totalMemory();
      long freeMem = Runtime.getRuntime().freeMemory();
      LOGGER.debug("Running iteration " + index + "/" + total + " [" + freeMem
          / 1024 + "k free : " + (totalMem - freeMem) / 1024 + "k used of "
          + totalMem / 1024 + "k]");
    }

    for (IIterativeRunListener listener : listeners)
      try
      {
        listener.preLoad(index, total);
      }
      catch (Exception e)
      {
        LOGGER.error("listener " + listener + " threw an exception ", e);
      }

    /*
     * first we use the environment to load all the model descriptors
     */
    EnvironmentParser ep = new EnvironmentParser();
    Collection<CommonTree> modelDescriptors = ep.getModelDescriptors(
        environment, envURL);

    for (IIterativeRunListener listener : listeners)
      try
      {
        listener.preBuild(index, total, modelDescriptors);
      }
      catch (TerminateIterativeRunException tire)
      {
        throw tire;
      }
      catch (Exception e)
      {
        LOGGER.error("listener " + listener + " threw an exception ", e);
      }

    /*
     * now we actually do that vodoo that we do to set up the environment this
     * will build the models, instruments, and set up common reality..
     */
    ep.process(environment, modelDescriptors);

    modelDescriptors.clear();

    ACTRRuntime runtime = ACTRRuntime.getRuntime();

    Collection<IModel> models = runtime.getModels();

    for (IModel model : models)
      model.addListener(new ModelListenerAdaptor() {

        long    startTime    = 0;

        long    simStartTime = 0;

        boolean closed       = false;

        @Override
        public void modelStarted(ModelEvent event)
        {
          startTime = event.getSystemTime();
          simStartTime = (long) (event.getSimulationTime() * 1000);
        }

        protected String header(ModelEvent event)
        {
          StringBuilder sb = new StringBuilder("  <model name=\"");
          sb.append(event.getSource()).append("\" simulated=\"");
          sb.append(duration(simStartTime,
              (long) (event.getSimulationTime() * 1000)));
          sb.append("\" actual=\"");
          sb.append(duration(startTime, event.getSystemTime()));
          sb.append("\" factor=\"");

          double factor = (event.getSimulationTime() * 1000 - simStartTime)
              / (event.getSystemTime() - startTime);
          NumberFormat format = NumberFormat.getNumberInstance();
          format.setMaximumFractionDigits(3);
          sb.append(format.format(factor)).append("\"");
          return sb.toString();
        }

        @Override
        public void modelStopped(ModelEvent event)
        {
          if (!closed) synchronized (log)
          {
            log.println(header(event) + "/>");
          }
        }

        @Override
        public void exceptionThrown(ModelEvent event)
        {
          synchronized (log)
          {
            closed = true;
            log.println(header(event) + ">");
            event.getException().printStackTrace(log);
            log.println("  </model>");

            for (IIterativeRunListener listener : listeners)
              try
              {
                listener.exceptionThrown(index, event.getSource(),
                    event.getException());
              }
              catch (TerminateIterativeRunException tire)
              {

              }

            /*
             * from here we try to stop the runtime, but do not block.. that
             * would be disasterous
             */
            IController controller = ACTRRuntime.getRuntime().getController();
            controller.stop();
          }
        }

      }, ExecutorServices.INLINE_EXECUTOR);

    for (IIterativeRunListener listener : listeners)
      try
      {
        listener.preRun(index, total, models);
      }
      catch (TerminateIterativeRunException tire)
      {
        throw tire;
      }
      catch (Exception e)
      {
        LOGGER.error("listener " + listener + " threw an exception ", e);
      }

    try
    {
      /*
       * start 'er up!
       */
      IController controller = runtime.getController();

      /*
       * we do the model check in case the listener is monkeying around with the
       * models (adding, removing) the controller will not ever complete if no
       * models are executed.
       */
      if (models.size() != 0) try
      {
        controller.start().get();
        controller.complete().get();
      }
      catch (InterruptedException ie)
      {
        LOGGER.error("Interrupted while waiting for completion", ie);
      }

      /*
       * all done - time to notify and clean up
       */
      for (IIterativeRunListener listener : listeners)
        try
        {
          listener.postRun(index, total, models);
        }
        catch (TerminateIterativeRunException tire)
        {
          throw tire;
        }
        catch (Exception e)
        {
          LOGGER.error("listener " + listener + " threw an exception ", e);
        }
    }
    catch (TerminateIterativeRunException tire)
    {
      throw tire;
    }
    catch (Throwable e)
    {
      throw new RuntimeException("Failed to run iteration " + index + " ", e);
    }
    finally
    {
      cleanUp(runtime);
    }
  }

  protected void cleanUp(ACTRRuntime runtime)
  {

    if (runtime.getConnector().isRunning()) try
    {
      runtime.getConnector().stop();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to cleanly stop reality connector ", e);
    }

    Collection<IModel> models = new ArrayList<IModel>(runtime.getModels());
    for (IModel model : models)
      try
      {
        runtime.removeModel(model);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not remove " + model, e);
      }

    if (runtime.getModels().size() != 0)
      if (LOGGER.isErrorEnabled())
        LOGGER.error("Not all models were removed " + runtime.getModels());

    /*
     * remove any runtime listeners that were installed
     */
    for (IACTRRuntimeListener runtimeListener : runtime.getListeners())
      runtime.removeListener(runtimeListener);

    /*
     * kill the executors
     */
    ExecutorServices.shutdown(10000);

    /*
     * now dipose
     */
    for (IModel model : models)
    {
      ReadWriteLock lock = model.getLock();
      lock.writeLock().lock();
      try
      {
        model.dispose();
      }
      catch (Exception e)
      {
        LOGGER.error("Could not dispose of " + model, e);
      }
      finally
      {
        lock.writeLock().unlock();
      }
    }

    runtime.setOnStart(null);
    runtime.setOnStop(null);
    runtime.setApplicationData(null);
    runtime.setController(null);
  }

  protected Collection<IIterativeRunListener> createListeners(Document document)
  {
    ArrayList<IIterativeRunListener> listeners = new ArrayList<IIterativeRunListener>();
    NodeList nl = document.getElementsByTagName("iterative-listener");
    for (int i = 0; i < nl.getLength(); i++)
      try
      {
        IIterativeRunListener listener = (IIterativeRunListener) EnvironmentParser
            .instantiate((Element) nl.item(i), "IIterativeRunListener");
        listeners.add(listener);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not create run listener from " + nl.item(i), e);
      }
    return listeners;
  }

  protected int getIterations(Document document)
  {
    NodeList nl = document.getElementsByTagName("iterative");
    if (nl.getLength() != 1)
      throw new RuntimeException("Must specify the iterative tag");
    try
    {
      return Integer
          .parseInt(((Element) nl.item(0)).getAttribute("iterations"));
    }
    catch (NumberFormatException nfe)
    {
      throw new RuntimeException("iterative tag must have iterations attribute");
    }
  }

  /**
   * @param url
   * @param listener
   * @param iterations
   * @throws Exception
   */
  public void run(URL url)
  {
    Collection<IIterativeRunListener> listeners = Collections.EMPTY_LIST;

    try
    {
      Document environment = loadEnvironment(url);
      int iterations = getIterations(environment);
      listeners = createListeners(environment);

      String id = System.getProperty("iterative-id");
      if (id == null)
        id = "";
      else
        id = "-" + id;

      File rootDir = ACTRRuntime.getRuntime().getWorkingDirectory();
      PrintWriter log = new PrintWriter(new FileWriter("iterative-log" + id
          + ".xml"));
      log.println("<iterative-run total=\"" + iterations + "\">");

      DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT,
          DateFormat.LONG);

      boolean shouldExit = false;

      for (IIterativeRunListener listener : listeners)
        try
        {
          listener.start(iterations);
        }
        catch (TerminateIterativeRunException tire)
        {
          shouldExit = true;
        }
        catch (Exception e)
        {
          if (LOGGER.isErrorEnabled())
            LOGGER.error(listener + " threw an exception on start ", e);
        }

      long startTime = System.currentTimeMillis();

      for (int i = 1; i <= iterations; i++)
      {
        if (shouldExit) break;

        /*
         * create a new working directory
         */
        File workingDir = new File(rootDir, "run-" + i);
        workingDir.mkdirs();

        ACTRRuntime.getRuntime().setWorkingDirectory(workingDir);

        log.println(" <run itr=\"" + i + "\" start=\""
            + format.format(new Date()) + "\">");

        try
        {
          /*
           * let's do it.
           */
          if (_aggressiveGC) System.gc();

          iteration(i, iterations, environment, url, listeners, log);

          if (_aggressiveGC) System.gc();
        }
        catch (TerminateIterativeRunException tire)
        {
          shouldExit = true;
        }
        catch (Exception e1)
        {
          log.print(" <exception><![CDATA[");
          e1.printStackTrace(log);
          log.print("]]></exception>");

          for (IIterativeRunListener listener : listeners)
            try
            {
              listener.exceptionThrown(i, null, e1);
            }
            catch (TerminateIterativeRunException tire)
            {
              shouldExit = true;
            }
            catch (Exception e2)
            {
              LOGGER.error(listener
                  + " threw an exception on exception notification ", e2);
            }
        }

        log.println(" </run>");
        log.flush();

        if (workingDir.list().length == 0) workingDir.delete();
      }

      String duration = duration(startTime, System.currentTimeMillis());

      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Total running time : " + duration);

      log.println(" <duration value=\"" + duration + "\"/>");
      log.println("</iterative-run>");
      log.close();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to run fully", e);
      for (IIterativeRunListener listener : listeners)
        try
        {
          // we ignore the return value here since we're exiting anyway
          listener.exceptionThrown(0, null, e);
        }
        catch (TerminateIterativeRunException tire)
        {
          // silently swallow since we're already exiting
        }
        catch (Exception e2)
        {
          LOGGER.error(listener
              + " threw an exception during exception notification ", e2);
        }

    }
    finally
    {
      for (IIterativeRunListener listener : listeners)
        try
        {
          listener.stop();
        }
        catch (Exception e)
        {
          LOGGER.error(listener + " threw an exception on stop ", e);
        }
    }
  }

  static public void main(String[] args)
  {
    try
    {
      URL url = new URL(args[0]);
      IterativeMain entryPoint = new IterativeMain();
      entryPoint.run(url);
    }
    catch (Exception e)
    {
      LOGGER.error("Could not run", e);
    }
  }
}
