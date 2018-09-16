package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.NamedTrigger;
import org.w3c.dom.Element;

public class TriggerHandler implements INodeHandler<ITrigger>
{
  public String getTagName()
  {
    return "trigger";
  }

  public ITrigger process(Element element, IExperiment experiment)
  {
    NamedTrigger trigger = new NamedTrigger(element.getAttribute("name"),
        experiment);
    return trigger;
  }

  public boolean shouldDecend()
  {
    return true;
  }
}