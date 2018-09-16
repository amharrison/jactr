package org.jactr.tools.experiment.bootstrap;

/*
 * default logging
 */
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.model.ModelTerminatedException;
import org.jactr.core.model.event.ModelEvent;
import org.jactr.core.model.event.ModelListenerAdaptor;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.scripting.ScriptingManager;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.dc.DataCollector;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * starts the experiment
 * 
 * @author harrison
 */
public class StartModelExperiments implements Runnable
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER             = LogFactory
                                                            .getLog(StartModelExperiments.class);

  static public final String         CLASS_ATTR         = "class";

  static public final String         CONFIGURATION_FILE = "ConfigurationFile";

  static public final String         SUBJECT_ID         = "SubjectId";

  /*
   * for experiment variable context
   */
  static public final String         EXPERIMENT_MODEL   = "experiment.model";

  /*
   * for model metadata
   */
  static public final String         MODELS_EXPERIMENT  = "models.experiment";

  public void run()
  {
    /*
     * exposes model's experiment as jactrExperiment
     */
    ScriptingManager.install(new ScriptConfig());

    Collection<IExperiment> experiments = new ArrayList<IExperiment>();

    /*
     * we create a separate experiment for each model
     */
    for (IModel model : ACTRRuntime.getRuntime().getModels())
    {
      String subjectId = DataCollector.getSubjectId(model);

      IExperiment experiment = loadExperiment(
          System.getProperty(CONFIGURATION_FILE), (ex) -> {
            /*
             * always set the subjectId of the experiment
             */
            ex.getVariableContext().set(SUBJECT_ID, subjectId);
            // provisional clock setting
          ex.setClock(ACTRRuntime.getRuntime().getClock(model));
          ex.getVariableContext().set(EXPERIMENT_MODEL, model);
          model.setMetaData(MODELS_EXPERIMENT, ex);
        });

      experiment.getVariableResolver().addAlias("actrWorkingDir",
          ACTRRuntime.getRuntime().getWorkingDirectory().getAbsolutePath());

      model.addListener(new ModelListenerAdaptor() {
        @Override
        public void modelStarted(ModelEvent me)
        {
          // actual clock set
          experiment.setClock(ACTRRuntime.getRuntime().getClock(model));
        }
      }, null);

      experiments.add(experiment);
    }

    /*
     * start the experiments
     */
    experiments.forEach((e) -> e.start());

  }

  /**
   * instantiate and load the experiment from the config file at location
   * 
   * @param location
   * @return
   */
  static public IExperiment loadExperiment(String location,
      Consumer<IExperiment> config)
  {
    if (location == null)
      throw new ModelTerminatedException(
          "No experiment configuration file found. Cannot continue");

    Document document = loadConfiguration(location);

    String className = document.getDocumentElement().getAttribute(CLASS_ATTR);

    try
    {
      IExperiment experiment = (IExperiment) StartModelExperiments.class
          .getClassLoader().loadClass(className).newInstance();

      if (config != null) config.accept(experiment);

      experiment.configure(document);
      return experiment;
    }
    catch (InstantiationException e)
    {
      throw new IllegalStateException(e);
    }
    catch (IllegalAccessException e)
    {
      throw new IllegalStateException(e);
    }
    catch (ClassNotFoundException e)
    {
      throw new IllegalStateException(e);
    }
  }

  static private Document loadConfiguration(String location)
  {
    /*
     * first get the url. we try resource first, then fully resolved, then
     * relative
     */
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try
    {
      DocumentBuilder parser = factory.newDocumentBuilder();
      return parser.parse(resolveURL(location).openStream());
    }
    catch (ParserConfigurationException e)
    {
      throw new IllegalStateException(e);
    }
    catch (SAXException e)
    {
      throw new IllegalStateException(e);
    }
    catch (IOException e)
    {
      throw new IllegalStateException(e);
    }
  }

  static private URL resolveURL(String location)
  {
    URL rtn = StartModelExperiments.class.getClassLoader()
        .getResource(location);
    if (rtn != null) return rtn;

    try
    {
      return new URL(location);
    }
    catch (MalformedURLException e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug(
            location + " is not a url, attempting relative resolution", e);
    }

    URI root = new File(System.getProperty("user.dir")).toURI();
    try
    {
      URI resolved = root.resolve(location);
      return resolved.toURL();
    }
    catch (Exception e)
    {
      if (LOGGER.isDebugEnabled())
        LOGGER.debug("Could not resolve " + location + " relative to " + root);
    }

    throw new IllegalArgumentException("Could not resolve " + location
            + " to a valid url. Is org.jactr.tools.experiment an Eclipse-RegisterBuddy? Is location on the classpath?");
  }
}
