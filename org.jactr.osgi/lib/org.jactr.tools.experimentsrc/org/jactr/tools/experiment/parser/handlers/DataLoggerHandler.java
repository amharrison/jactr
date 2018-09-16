package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IDataLogger;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.impl.XMLDataLogger;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;

public class DataLoggerHandler implements INodeHandler<ITrial>
{
  public String getTagName()
  {
    return "data-logger";
  }

  public ITrial process(Element element, IExperiment experiment)
  {
    String className = element.getAttribute("class");
    // String path = experiment.getVariableResolver()
    // .resolve(element.getAttribute("path"), experiment.getVariableContext())
    // .toString();

    String path = experiment.getVariableResolver().resolveValues(
        element.getAttribute("path"), experiment.getVariableContext());

    // String file = experiment.getVariableResolver()
    // .resolve(element.getAttribute("file"), experiment.getVariableContext())
    // .toString();

    String file = experiment.getVariableResolver().resolveValues(
        element.getAttribute("file"), experiment.getVariableContext());

    IDataLogger collector = null;

    try
    {
      collector = (IDataLogger) getClass().getClassLoader()
          .loadClass(className).newInstance();
    }
    catch (Exception e)
    {
      ExperimentParser.LOGGER.error("Could not create " + className
          + ", using default xml ", e);
      collector = new XMLDataLogger();
      path = experiment.getVariableResolver()
          .resolve("${actrWorkingDir}/data", experiment.getVariableContext())
          .toString();
    }

    collector.setExperiment(experiment);
    collector.setPath(path, file);
    experiment.setDataCollector(collector);

    return null;
  }

  public boolean shouldDecend()
  {
    return false;
  }
}