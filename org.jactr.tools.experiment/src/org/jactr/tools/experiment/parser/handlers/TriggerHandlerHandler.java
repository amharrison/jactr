package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.w3c.dom.Element;

public class TriggerHandlerHandler implements INodeHandler<ITrial>
{
  /**
   * 
   */
  private final ExperimentParser experimentParser;

  /**
   * @param experimentParser
   */
  public TriggerHandlerHandler(ExperimentParser experimentParser)
  {
    this.experimentParser = experimentParser;
  }

  public String getTagName()
  {
    return "trigger-handler";
  }

  public ITrial process(Element element, IExperiment experiment)
  {
    /*
     * instantiate a new action handler..
     */
    String className = element.getAttribute("class");
    try
    {
      INodeHandler<ITrigger> handler = (INodeHandler<ITrigger>) getClass()
          .getClassLoader().loadClass(className).newInstance();
      this.experimentParser.addTriggerHandler(handler);
    }
    catch (Exception e)
    {
      ExperimentParser.LOGGER.error("Could not create new trigger-handler for " + className,
          e);
    }
    return null;
  }

  public boolean shouldDecend()
  {
    return false;
  }
}