package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;

public class DataCollectorInitHandler implements INodeHandler<ITrial>
{
  public String getTagName()
  {
    return "data-collector-init";
  }

  public ITrial process(Element element, IExperiment experiment)
  {
    //runnable
    String className = element.getAttribute("class");
   
    try
    {
      Runnable runner = (Runnable) getClass().getClassLoader().loadClass(
          className).newInstance();
      
      runner.run();
    }
    catch (Exception e)
    {
      ExperimentParser.LOGGER.error(
          "Could not create " + className + ", data-collector not initialized", e);
    }


    return null;
  }

  public boolean shouldDecend()
  {
    return false;
  }
}