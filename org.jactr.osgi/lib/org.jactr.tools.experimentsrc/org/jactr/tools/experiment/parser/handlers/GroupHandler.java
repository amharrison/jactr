package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.trial.impl.CompoundTrial;
import org.w3c.dom.Element;

public class GroupHandler implements INodeHandler<ITrial>
{
  /**
   * 
   */
  private final ExperimentParser experimentParser;

  /**
   * @param experimentParser
   */
  public GroupHandler(ExperimentParser experimentParser)
  {
    this.experimentParser = experimentParser;
  }

  public String getTagName()
  {
    return "group";
  }

  public ITrial process(Element element, IExperiment experiment)
  {
    String id = element.getAttribute("id");
    CompoundTrial trial = new CompoundTrial(id + (this.experimentParser._trialCount++),
        experiment);
    try
    {
      trial.setChoose(Integer.parseInt(element.getAttribute("choose")));
    }
    catch (Exception e)
    {
      trial.setChoose(0);
    }
    trial.setShuffle(Boolean.parseBoolean(element.getAttribute("shuffle")));

    return trial;
  }

  public boolean shouldDecend()
  {
    return true;
  }
}