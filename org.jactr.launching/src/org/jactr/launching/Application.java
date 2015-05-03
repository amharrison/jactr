package org.jactr.launching;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.event.ACTRRuntimeAdapter;
import org.jactr.core.runtime.event.ACTRRuntimeEvent;
import org.jactr.core.runtime.event.IACTRRuntimeListener;
import org.jactr.entry.Main;
import org.jactr.entry.iterative.IterativeMain;
import org.osgi.framework.Bundle;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(Application.class);

  /**
   * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
   */
  public Object start(IApplicationContext context) throws Exception
  {

    String[] arguments = (String[]) context.getArguments().get(
        IApplicationContext.APPLICATION_ARGS);

    Options options = new Options();

    /*
     * this option is supposed to be consumed by the launcher, but it is passing
     * it along for some unknown reason
     */
    // options.addOption(new Option("2","launcher.secondThread", false, "MAC
    // Only : force launcher to use second thread"));
    options.addOption(new Option("e", "environment", true,
        "Load the contents of the environment config file"));

    options.addOption(new Option("r", "run", true,
        "Run the configured environment"));

    options.addOption(new Option("i", "iterative", true,
        "Run the iterative environment"));
    
    options.addOption(new Option("p","permissions", true, "modify permissions"));

    addIrrelevantOptions(options);

    CommandLineParser parser = new GnuParser();

    CommandLine cmd = parser.parse(options, arguments, false);

    String environmentFile = "";
    boolean shouldRun = false;
    boolean isIterative = false;
    
    if(cmd.hasOption('p')) Activator.getDefault().modifyPermissions(cmd.getOptionValue('p'));

    if (cmd.hasOption('e')) environmentFile = cmd.getOptionValue('e');

    if (cmd.hasOption('r'))
    {
      shouldRun = true;
      environmentFile = cmd.getOptionValue('r');
    }

    if (cmd.hasOption('i'))
    {
      isIterative = true;
      environmentFile = cmd.getOptionValue('i');
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Loading " + environmentFile + " run:" + shouldRun);

    URL url = getClass().getClassLoader().getResource(environmentFile);

    try
    {
      if (url == null) url = URI.create(environmentFile).toURL();

      context.applicationRunning();

      if (isIterative)
        runIterative(url);
      else
        runNormal(url, shouldRun);
    }
    catch (Exception e)
    {
      String msg = "Unable to execute runtime from " + environmentFile;
      LOGGER.error(msg, e);
      Activator.getDefault().getLog().log(
          new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, msg, e));
      return -1;
    }

    return IApplication.EXIT_OK;
  }

  /**
   * this adds the irrelevant options that for whatever buggy reason eclipse
   * does not consume
   * 
   * @param options
   */
  private void addIrrelevantOptions(Options options)
  {
    for (String opt : new String[] { "consoleLog", "showlocation" })
      options.addOption(new Option(opt, false, "unconsumed eclipse option"));
    
    for (String opt : new String[] { "keyring" })
      options.addOption(new Option(opt, true, "unconsumed eclipse option"));
  }

  /**
   * run using the iterative entry point
   * 
   * @param url
   * @throws Exception
   */
  protected void runIterative(URL url) throws Exception
  {
    IterativeMain main = new IterativeMain();
    main.run(url);
  }

  /**
   * run using the standard entry point
   * 
   * @param url
   * @param shouldRun
   */
  protected void runNormal(URL url, boolean shouldRun) throws Exception
  {
    Main jactrEntryPoint = new Main();
    ACTRRuntime runtime = jactrEntryPoint.createRuntime(url);

    if (shouldRun)
    {
      if (LOGGER.isDebugEnabled()) LOGGER.debug("Starting");
      jactrEntryPoint.run(runtime);
    }
    else
      waitForStart(runtime);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Waiting for completion");
    jactrEntryPoint.waitForRuntime(runtime);

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Cleaning up");
    jactrEntryPoint.cleanUp(runtime);
  }
  
  private void waitForStart(ACTRRuntime runtime) throws Exception
  {
    final Lock lock = new ReentrantLock();
    final Condition condition = lock.newCondition();
    IACTRRuntimeListener listener = new ACTRRuntimeAdapter(){

      public void runtimeStarted(ACTRRuntimeEvent event)
      {
        try
        {
          lock.lock();
          condition.signalAll();
        }
        finally
        {
          lock.unlock();
        }
        
      }
      
    };
    
    runtime.addListener(listener, ExecutorServices.INLINE_EXECUTOR);
    
    /*
     * wait to start
     */
    try
    {
      lock.lock();
      while(!runtime.getController().isRunning())
        condition.await();
    }
    finally
    {
      lock.unlock();
    }
    
    
    runtime.removeListener(listener);
  }

  /**
   * @see org.eclipse.equinox.app.IApplication#stop()
   */
  public void stop()
  {
    // TODO implement Application.stop
    if (LOGGER.isWarnEnabled())
      LOGGER.warn("Application.stop is not implemented");

  }

  @SuppressWarnings("unchecked")
  static public void main(String[] argv)
  {
    final Map args = new HashMap();
    args.put(IApplicationContext.APPLICATION_ARGS, argv);
    IApplicationContext context = new IApplicationContext() {

      public void applicationRunning()
      {

      }

      public Map getArguments()
      {
        return args;
      }

      public String getBrandingApplication()
      {
        return null;
      }

      public Bundle getBrandingBundle()
      {
        return null;
      }

      public String getBrandingDescription()
      {
        return null;
      }

      public String getBrandingId()
      {
        return null;
      }

      public String getBrandingName()
      {
        return null;
      }

      public String getBrandingProperty(String key)
      {
        return null;
      }

      public void setResult(Object result, IApplication application)
      {
        // TODO Auto-generated method stub

      }

    };

    Application app = new Application();
    try
    {
      app.start(context);
    }
    catch (Exception e)
    {
      LOGGER.error("Application.main threw Exception : ", e);
      System.exit(-1);
    }

    System.exit(0);
  }
}
