package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.TimeTrigger;
import org.w3c.dom.Element;

public class TimeTriggerHandler implements INodeHandler<ITrigger>
{
  public String getTagName()
  {
    return "time-trigger";
  }

  public ITrigger process(Element element, IExperiment experiment)
  {
    return new TimeTrigger(element, experiment);
  }

  public boolean shouldDecend()
  {
    return true;
  }
}