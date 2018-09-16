/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.entry;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReadWriteLock;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.io.IOUtilities;
import org.jactr.io.antlr3.compiler.CompilationWarning;
import org.jactr.io.antlr3.misc.CommonTreeException;
import org.jactr.io.environment.EnvironmentParser;

/**
 * jACT-R start up application Usage: jactr --compile modelFile.jactr execution:
 * jactr --environment environment.env jactr --run modelFile.compiled jactr
 * --run modelFile.compiled --onStart class --onStop class jactr --log exit
 * codes: -3 unknown -2 compilation error -1 configuration error 0 success
 * 
 * @author harrison
 * @created July 19, 2001
 */
public class Main
{

  static private final Log LOGGER = LogFactory.getLog(Main.class);

  /**
   * @param cmd
   */
  public Main()
  {
  }

  /**
   * create the deafault environment for a run.. possibly setting up the
   * onStart/Stop
   * 
   * @return
   */
  public ACTRRuntime configureRuntime(ACTRRuntime runtime, CommandLine cmd)
  {

    // now we try to attach the onStart onStop
    if (cmd.hasOption('s'))
    {
      String className = cmd.getOptionValue('s');
      try
      {
        Class runnableClass = getClass().getClassLoader().loadClass(className);
        Runnable runner = (Runnable) runnableClass.newInstance();
        runtime.setOnStart(runner);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not load the onStart class " + className, e);
        System.err.println("Could not load the onStart class " + className);
        e.printStackTrace(System.err);
        System.exit(-1);
      }
    }

    if (cmd.hasOption('p'))
    {
      String className = cmd.getOptionValue('p');
      try
      {
        Class runnableClass = getClass().getClassLoader().loadClass(className);
        Runnable runner = (Runnable) runnableClass.newInstance();
        runtime.setOnStop(runner);
      }
      catch (Exception e)
      {
        LOGGER.error("Could not load the onStop class " + className, e);
        System.err.println("Could not load the onStop class " + className);
        e.printStackTrace(System.err);
        System.exit(-1);
      }
    }

    return runtime;
  }

  /**
   * set up the logging for the models
   * 
   * @param cmd
   * @return
   */
  public ACTRRuntime configureLogging(ACTRRuntime runtime, CommandLine cmd)
  {
    if (cmd.hasOption('l')) for (IModel model : runtime.getModels())
    {
      /*
       * route all the named logs to System.out
       */
      DefaultModelLogger dml = new DefaultModelLogger();
      // and attach to the available models
      model.install(dml);

      String[] logs = cmd.getOptionValues('l');
      for (String logName : logs)
      {
        Logger.Stream.valueOf(logName);
        dml.setParameter(logName, "out");
      }

    }
    return runtime;
  }

  public void waitForRuntime(ACTRRuntime runtime)
  {
    IController controller = runtime.getController();
    try
    {
      controller.complete().get();
    }
    catch (InterruptedException ie)
    {
      LOGGER.error(ie);
    }
    catch (ExecutionException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("Main.waitForRuntime threw ExecutionException : ", e);
    }
  }

  public void run(ACTRRuntime runtime)
  {

    IController controller = runtime.getController();
    if (controller == null)
    {
      controller = new DefaultController();
      runtime.setController(controller);
    }

    try
    {
      controller.start().get();
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("Main.run threw InterruptedException : ", e);
    }
    catch (ExecutionException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("Main.run threw ExecutionException : ", e);
    }
  }

  public void cleanUp(ACTRRuntime runtime)
  {

    if (runtime.getConnector().isRunning()) try
    {
      runtime.getConnector().stop();
    }
    catch (Exception e)
    {
      LOGGER.error("Failed to shutdown connector ", e);
    }

    /*
     * detach the current controller before we exit
     */
    runtime.setController(null);

    /*
     * and clean up
     */
    ArrayList<IModel> toDispose = new ArrayList<IModel>();
    for (IModel model : new ArrayList<IModel>(runtime.getModels()))
    {
      runtime.removeModel(model);
      toDispose.add(model);
    }

    /*
     * will kill the background threads..
     */
    ExecutorServices.shutdown(0);

    /*
     * and dispose. Why not dispose with the remove? listeners may still be
     * active even after the runtime has stopped. We dispose after the service
     * has been shutdown, making sure that all work has been completed
     */
    for (IModel model : toDispose)
    {
      ReadWriteLock lock = model.getLock();
      lock.writeLock().lock();

      try
      {
        model.dispose();
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to dispose of %s cleanly. ", model),
            e);
      }
      finally
      {
        lock.writeLock().unlock();
      }
    }
  }

  public ACTRRuntime createRuntime(URL environmentConfigFile) throws Exception
  {
    EnvironmentParser parser = new EnvironmentParser();
    parser.parse(environmentConfigFile);
    return ACTRRuntime.getRuntime();
  }

  public ACTRRuntime createRuntime(CommandLine cmd) throws Exception
  {
    ACTRRuntime runtime = null;
    if (cmd.hasOption('e'))
    {
      String envFile = cmd.getOptionValue('e');
      File fp = new File(envFile);
      runtime = createRuntime(fp.toURL());
    }

    return runtime;
  }

  /**
   * compile the models
   * 
   * @param cmd
   */
  public void compile(CommandLine cmd)
  {
    boolean error = false;
    if (cmd.hasOption('c'))
    {
      String[] sourceModelFiles = cmd.getOptionValues('c');
      for (String source : sourceModelFiles)
      {
        ArrayList<Exception> warnings = new ArrayList<Exception>();
        ArrayList<Exception> errors = new ArrayList<Exception>();
        CommonTree md = null;

        try
        {
          URL url = new File(source).toURL();
          md = IOUtilities.loadModelFile(url, warnings, errors);
          // compile
          error = !IOUtilities.compileModelDescriptor(md, warnings, errors);

          dumpExceptions(source, warnings);
          dumpExceptions(source, errors);
        }
        catch (Exception e)
        {
          LOGGER.error("Could not load " + source, e);
          System.err.println("Could not load " + source);
          e.printStackTrace(System.err);
          error = true;
        }
      }
    }

    if (error) System.exit(-2);
  }

  protected void dumpExceptions(String sourceFile,
      Collection<Exception> exceptions)
  {
    for (Exception e : exceptions)
    {
      StringBuilder builder = new StringBuilder();
      builder.append("(");
      builder.append(sourceFile);

      if (e instanceof CommonTreeException)
      {
        CommonTreeException ae = (CommonTreeException) e;
        CommonTree node = ae.getStartNode();
        builder.append(":");
        builder.append(node.getLine());
        builder.append(",");
        builder.append(node.getCharPositionInLine());
        builder.append(") ");

        if (ae instanceof CompilationWarning)
          builder.append(" Warning : ");
        else
          builder.append(" Error : ");
      }
      else
        builder.append(") Error : ");

      builder.append(e.getMessage());
      System.err.println(builder.toString());
    }
  }

  public ACTRRuntime loadModels(ACTRRuntime runtime, CommandLine cmd)
  {
    boolean error = false;
    if (cmd.hasOption('r'))
    {
      String[] compiledModelFiles = cmd.getOptionValues('r');
      for (String modelFile : compiledModelFiles)
        try
        {
          URL url = new File(modelFile).toURL();
          Collection<Exception> warnings = new ArrayList<Exception>();
          Collection<Exception> errors = new ArrayList<Exception>();

          CommonTree md = IOUtilities.loadModelFile(url, warnings, errors);
          // compile
          IOUtilities.compileModelDescriptor(md, warnings, errors);

          // construct
          IModel model = IOUtilities.constructModel(md, warnings, errors);

          if (warnings.size() != 0)
          {
            System.err.println(modelFile + " has warnings");
            dumpExceptions(modelFile, warnings);
          }

          if (errors.size() == 0)
            runtime.addModel(model);
          else
          {
            System.err.println(modelFile + " has errors");
            dumpExceptions(modelFile, errors);
          }
        }
        catch (Exception e)
        {
          LOGGER.error("Could not load model from " + modelFile, e);
          System.err.println("Could not load model from " + modelFile);
          e.printStackTrace(System.err);
          error = true;
        }
    }

    if (error) System.exit(-3);

    return runtime;
  }

  /**
   * The main program for the jactr class
   * 
   * @param argv
   *          The command line arguments
   * @since
   */
  public static void main(String[] argv)
  {
    try
    {
      Options options = new Options();
      options.addOption(OptionBuilder.create("h"));

      options.addOption(OptionBuilder.create('c'));

      options.addOption(OptionBuilder.create('e'));

      options.addOption(OptionBuilder.create('r'));

      options.addOption(OptionBuilder.create('s'));
      options.addOption(OptionBuilder.create('p'));

      options.addOption(OptionBuilder.create('l'));

      CommandLineParser parser = new PosixParser();

      CommandLine cmd = parser.parse(options, argv);
      Main env = new Main();

      /*
       * compile
       */
      if (cmd.hasOption('c'))
        env.compile(cmd);
      else if (cmd.hasOption('e') || cmd.hasOption('r'))
      {
        ExecutorServices.initialize();

        ACTRRuntime runtime = env.configureLogging(env.loadModels(
            env.configureRuntime(env.createRuntime(cmd), cmd), cmd), cmd);

        if (cmd.hasOption('r')) env.run(runtime);

        env.waitForRuntime(runtime);

        env.cleanUp(runtime);
      }
      else
      {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("jactr", options);
        System.exit(0);
      }
    }
    catch (Exception e)
    {
      LOGGER.error("Main exception ", e);
      e.printStackTrace();
      System.exit(-3);
    }
    System.exit(0);
  }

}