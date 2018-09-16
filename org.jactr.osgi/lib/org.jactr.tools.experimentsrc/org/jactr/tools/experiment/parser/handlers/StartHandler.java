package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.triggers.ITrigger;
import org.jactr.tools.experiment.triggers.StartTrigger;
import org.w3c.dom.Element;

public class StartHandler implements INodeHandler<ITrigger>
{
  public String getTagName()
  {
    return "start";
  }

  public ITrigger process(Element element, IExperiment experiment)
  {
    return new StartTrigger(experiment);
  }

  public boolean shouldDecend()
  {
    return true;
  }
}